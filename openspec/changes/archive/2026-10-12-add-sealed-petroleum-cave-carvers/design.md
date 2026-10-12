## Context

Vanilla flooded caves come from the aquifer, not from a carver: `WorldCarver.getCarveState` asks the
chunk's `Aquifer` what to put in each carved block, and the aquifer answers water, lava, or air. A
carver that wants a different fluid has to decide the block itself.

Constraints from the carving step (`ChunkPyramid`, `NoiseBasedChunkGenerator.applyCarvers`):

- Carving has a block write radius of 0. A chunk runs every carver whose start chunk is within 8
  chunks, and each run writes only into the chunk being generated. Tunnel geometry is reproducible
  because the random source is seeded from the start chunk, but no chunk sees what another chunk
  wrote. A carver therefore cannot "check, then abort" the way `VEFluidDepositFeature` does; it has
  to seal whatever it touches.
- Carvers from different start chunks interleave, so a vanilla cave can run before or after a fluid
  tunnel in the same chunk.
- Vanilla carvers only replace blocks in `minecraft:overworld_carver_replaceables` (base stone, dirt,
  sand, gravel, water, and similar). Bituminous blocks, crude oil, and natural gas are not in it.
- Natural gas blocks are built with `Properties.air()`, so `BlockState.isAir()` is true for them.

The bituminous blocks, the `perimeter_targets` format (a list of `OreConfiguration.TargetBlockState`),
and `LakeMask.perimeterState` come from the `rework-overworld-oil-worldgen` change.

## Goals / Non-Goals

**Goals:**
- Tunnels that are sealed no matter what was carved before them and stay sealed against vanilla
  carvers that run after them.
- No fluid ticks scheduled at generation.
- Leave vanilla caves in new chunks unchanged where they do not meet a petroleum tunnel.

**Non-Goals:**
- Protecting tunnels from structures and features placed after carving.
- Gas over oil layering inside one tunnel. Each tunnel holds one fluid.
- Large tunnels or canyons. Only the small cave shape is in scope.

## Decisions

### A dedicated carver, not a `CaveWorldCarver` subclass
`VEFluidCaveCarver extends WorldCarver<VEFluidCaveCarver.Configuration>`. `CaveWorldCarver` binds its
type parameter to `CaveCarverConfiguration` and carves each ellipsoid as it walks the tunnel. Sealing
needs the whole outline before the shell is placed (next decision), so the carver re-implements the
cave walk (`createRoom` and `createTunnel` math) to record ellipsoids instead of carving them. The
copied math is rewritten with descriptive names, per AGENTS.md.

Alternative: override `carveBlock` on a `CaveWorldCarver` subclass and place the shell per
ellipsoid. Rejected, because ellipsoid N's shell would land inside ellipsoid N+1 before N+1 is
carved. Since the shell is not carver-replaceable, N+1 could then not fill through it, and the tunnel
would be split by bituminous walls.

### Two passes over a recorded outline
`carve` first walks the tunnel system and collects a list of ellipsoids (centre, horizontal radius,
vertical radius, and vanilla's floor-level skip). Then, for the current chunk only:

1. **Fill:** every position inside any ellipsoid becomes the fluid's source block if it is in the
   configuration's `replaceable` set, or is `Blocks.AIR`, `Blocks.CAVE_AIR`, or water. Air is tested
   by block, not with `isAir()`, so a gas tunnel's gas is not treated as open cave by an oil tunnel.
   Other solids stay. Filled positions are set in the `CarvingMask`, as vanilla carvers do.
2. **Shell:** every position that is outside all ellipsoids and shares a face with a position inside
   one is replaced by `LakeMask.perimeterState(perimeterTargets, existing, random)`, unless the
   existing block is in `BlockTags.FEATURES_CANNOT_REPLACE` or no target matches.

The shell test uses geometry only, never the blocks in neighbouring chunks, so two chunks always
agree on which positions are shell.

No position is passed to `markPosForPostprocessing`. Every source block is enclosed, so there is
nothing to tick, and a breach later causes a neighbour update that wakes the fluid.

### No `CarvingMask` marks for the shell
A later vanilla carver skips masked positions, but it already skips bituminous blocks and the fluids
because they are not carver-replaceable. Marking the interior only matches vanilla's behaviour for
carved space. Leaving the shell unmarked avoids touching anything else that reads the mask.

### Configuration extends `CarverConfiguration`
`Configuration extends CarverConfiguration` and declares the three cave floats
(`horizontal_radius_multiplier`, `vertical_radius_multiplier`, `floor_level`) itself, plus
`fluidstate` (`FluidState.CODEC`) and `perimeter_targets`
(`OreConfiguration.TargetBlockState.CODEC.listOf()`, defaulting to an `always_true` Bituminous Gravel
target). It does not extend `CaveCarverConfiguration`, because that class keeps `floorLevel`
package-private. The codec groups `CarverConfiguration.CODEC`, the three floats, and the two new
fields, so the JSON looks like a vanilla cave carver plus two keys. The inherited `lava_level` is
required by the base codec and ignored, because the fluid replaces the lava rule. The JSON sets it
to `{"above_bottom": 8}` to match vanilla.

Alternative: reuse `VELakesFeature.Configuration`. Rejected, because carvers need the probability,
height, and radius fields that `CarverConfiguration` already decodes.

### Smaller tunnel systems
`getRange()` returns 2 instead of vanilla's 4, so a system spans at most 48 blocks from its start.
Each start makes one system instead of vanilla's nested random count: a room with probability 1/4
followed by one to three tunnels (one tunnel when there is no room). Radius multipliers in the shipped
JSON are smaller than vanilla's.

### Generic type id, petroleum configurations
The carver type is `ve_fluid_cave_carver`, not a petroleum name, because it accepts any fluid, like
`ve_fluid_deposit_feature`. "Petroleum" names the capability, the biome modifier, and the shipped
configured carvers, which hold crude oil and natural gas.

### Registration and data
- `world/carver/VECarvers` holds `VE_CARVER_REGISTRY = DeferredRegister.create(Registries.CARVER,
  MODID)` with `ve_fluid_cave_carver`. It is registered in the `VoluminousEnergy` constructor under a
  new `// Register carvers` banner after `// Register features`.
- `worldgen/configured_carver/crude_oil_cave.json`: probability 0.02, y uniform -40 to 30.
  `natural_gas_cave.json`: probability 0.015, y uniform -48 to 16. Both use `yScale` 0.5,
  `horizontal_radius_multiplier` and `vertical_radius_multiplier` uniform 0.5 to 0.9,
  `floor_level` -0.7, `replaceable` `#minecraft:overworld_carver_replaceables`, and the same three
  bituminous targets as `underground_oil_lake.json`. For comparison, vanilla's `cave` uses 0.15.
- `neoforge/biome_modifier/petroleum_caves_overworld.json`: `neoforge:add_carvers`, biomes
  `#minecraft:is_overworld`, step `air`, carvers `[crude_oil_cave, natural_gas_cave]`.

`add_carvers` appends to each biome's carver list. `applyCarvers` seeds each carver by its index in
that list, so appending leaves the vanilla carvers' seeds, and so vanilla caves, unchanged.

## Risks / Trade-offs

- [Structures and features placed after carving (mineshafts, monster rooms, trial chambers, ancient
  cities, geodes) overwrite the shell or the fluid] → Accepted by the maintainer. Gas is the most
  exposed: monster rooms count `isAir()` openings and so can generate inside a gas tunnel.
- [Another mod's carver with a broader replaceable set cuts the shell] → Accepted. It needs that mod
  to list bituminous blocks or VE fluids as replaceable.
- [A tunnel that starts beside chunks generated before the mod was added is cut at that border, and
  can open into whatever the old chunk holds] → Same as vanilla caves at old borders. Only the first
  ring of new chunks is affected.
- [Plugs where a tunnel crosses a vanilla cave look like walls of bitumen in the cave] → Intended.
  They mark that a petroleum tunnel is next to the cave.
- [Starting numbers make tunnels too common or too rare] → Values live in JSON and are tuned during
  the in-game check without code changes.

## Migration Plan

None. Existing chunks are untouched, and new chunks generate tunnels. Removing the biome modifier
JSON, or overriding it in a datapack, turns tunnels off for new chunks.

## Open Questions

- Final probability and radius values, settled during the in-game check.
