## Context

**How vanilla does it.** The vanilla aquifer (`Aquifer.NoiseBasedAquifer`, 1.21.1) is not placed
anywhere. While `NoiseChunk` fills terrain, every block the density function marks as empty asks the
aquifer for its contents:

- The world is divided into a grid of 16×12×16 cells, each with a centre jittered by a positional
  random source.
- A block takes the fluid status of the nearest centre: fluid below that cell's level, air above it.
- Where neighbouring cells have different levels, a pressure term pushes density positive, which
  turns would-be cave into stone. Those are the barriers.
- Fluid types are hard-coded: water, with lava below y -54 or in lava-noise regions.

**Why vanilla can't be extended.** The aquifer is a private final reservoir of `NoiseChunk`, built in
its constructor and captured in a lambda. `createNoiseChunk` and `doFill` are private, and NeoForge
patches no hook into it. Mixins are off the table, and a custom chunk generator would have to copy
`NoiseChunk`, `NoiseBasedChunkGenerator`, and `Aquifer`.

**What a feature can do instead.** A feature can reproduce the aquifer as a function of seed and
position:

- It runs after carvers (`ChunkPyramid`: FEATURES requires CARVERS at radius 1). It can read and
  write blocks one chunk around its own (`blockStateWriteRadius(1)`).
- From `FeaturePlaceContext` it reaches `chunkGenerator()` and
  `level().getLevel().getChunkSource().randomState()`.
- `RandomState.getOrCreateRandomFactory(ResourceLocation)` gives seeded positional randomness.
- `ChunkGenerator.getBaseHeight(x, z, Heightmap.Types.OCEAN_FLOOR_WG, level, randomState)` gives the
  noise terrain height at any column, independent of which chunks are loaded.
- `BiomeSource.getNoiseBiome(qx, qy, qz, randomState.sampler())` gives the biome at any position the
  same way.

**Fluid constraints.** Natural gas blocks use `Properties.air()`, so `isAir()` is true for them.
Bituminous variants come from `LakeMask.perimeterState` and the `perimeter_targets` format of the
`rework-overworld-oil-worldgen` change.

## Goals / Non-Goals

**Goals:**
- Aquifer-sized reservoirs, larger than the about 48-block limit of origin-based features, with no seams
  at chunk borders.
- One configurable feature type covering oil pools, gas caps, and surface pools.
- No fluid ticks scheduled at generation, and no early cost in chunks without a reservoir.

**Non-Goals:**
- Changing vanilla aquifers or terrain density. Barriers are bituminous plugs, not stone pushed into
  the noise.
- Gas over oil layering inside one reservoir. Oil and gas are separate reservoirs, and where they meet they
  are plugged apart.
- Lining solid cave walls with bitumen. Only openings are plugged.
- Protecting reservoirs from structures or features placed after the `lakes` step.

## Decisions

### A reservoir feature instead of a world type or mixin
See proposal.md (Why). A feature also works in existing worlds' new chunks and in modpacks without
asking players to pick a world type.

### Aquifer-style cell grid
`FluidReservoirCells`, a helper beside the feature, divides space into
`cell_width × cell_height × cell_width` grid cells:

- **Centres:** each grid cell's centre is jittered from
  `randomState.getOrCreateRandomFactory(<modid>:petroleum_reservoir_<salt>).at(gridX, gridY, gridZ)`, as
  vanilla does with `aquiferRandom()`.
- **Ownership:** among its 2×3×2 neighbouring grid cells, the same search vanilla uses, a position
  takes its nearest centre and second-nearest centre. Name the pair by grid index (A lower, B
  higher). The position belongs to A when `(distance to B - distance to A) + wobble × noise(x, y, z)
  > 0`, and to B otherwise. Without the noise term the boundary is the flat plane halfway between the
  centres; the noise bends it into a curve, the way vanilla's barrier noise bends aquifer walls.
  Ordering the pair by grid index rather than by distance keeps the rule symmetric, so the boundary
  moves both ways. The noise is sampled only where the two distances differ by less than `wobble`,
  because elsewhere it cannot change the owner.
- **Surface pools:** cells are columns spanning the whole height band, using a 2×2 search.

Each centre's cell is a reservoir when the same random source rolls below `chance` and the biome at the
centre is in `biomes`. Neither depends on loaded chunks, so every chunk agrees.

### Level and floor
For a reservoir cell, the lowest terrain around it, `minSurface`, is the minimum `getBaseHeight`
(`OCEAN_FLOOR_WG`) over the centre column and four columns at ±`cell_width / 2`. This mirrors
vanilla's `SURFACE_SAMPLING_OFFSETS_IN_CHUNKS`.

- **Pool, buried:** the level is uniform in the upper three quarters of the cell's height, capped at
  `minSurface - 8`. The fill covers positions with `y < level`.
- **Cap:** the floor is uniform in the lower three quarters of the cell's height. The fill covers
  positions with `floor ≤ y < minSurface - 8`.
- **Pool reaching the surface:** the level is `min(minSurface, max_y + 1)` and the cell spans
  `min_y` to the level.

Everything is clamped to the configured band. Values are cached per placement call, keyed by grid
cell, so `getBaseHeight` runs five times per reservoir cell per chunk, and never for non-reservoir cells.

### Fill
Over the chunk's 16×16 columns within the band, a position becomes the fluid's source block when its
cell is a reservoir, it is inside the level or floor rule, and its block is `Blocks.AIR`,
`Blocks.CAVE_AIR`, or water. Air is tested by block, not `isAir()`, so an oil reservoir never
overwrites gas. Lava, other fluids, and solids stay. Positions are written with flag 2 and never
scheduled for ticks.

Early out: before touching blocks, the feature lists the grid cells whose centres could own a
position in this chunk. If none is a reservoir, it returns. Most chunks stop here.

### Seal
For every reservoir fluid position `Q` in this chunk, each face neighbour `P` gets a plug when:

- `P` is not through the open face (directly above `Q` for pools, directly below `Q` for caps);
- `P` is open (`!isSolid()` or has a fluid);
- `P` is not already this fluid;
- `P` would not be filled by this reservoir (its cell, rule, and block test fail);
- `P` is not in `BlockTags.FEATURES_CANNOT_REPLACE`.

The plug state is `LakeMask.perimeterState(perimeterTargets, existing, random)`. If no target
matches, the block stays.

`P` may lie one block into a neighbouring chunk, which is inside the write radius. The test for "`P`
would be filled" uses the same function every chunk uses, so a neighbour that has not run yet makes
the same decision, and a neighbour generated before the mod was added still gets plugged at the
border. This is what keeps reservoirs seamless without a shared pass.

### Configuration
`Configuration` is a record decoded with `RecordCodecBuilder`:

| Reservoir | Codec |
|---|---|
| `fluidstate` | `FluidState.CODEC` |
| `kind` | `pool` or `cap`, a `StringRepresentable` enum |
| `reaches_surface` | optional, default `false` |
| `min_y`, `max_y` | `VerticalAnchor` |
| `cell_width` | int, 8 to 128 |
| `cell_height` | int, 4 to 64, optional, default 16 |
| `chance` | float, 0 to 1 |
| `biomes` | `RegistryCodecs.homogeneousList(Registries.BIOME)` |
| `salt` | int |
| `perimeter_targets` | optional, default `always_true` to Bituminous Gravel |
| `boundary` | optional `{ "noise": ResourceKey<NoiseParameters>, "wobble": float 0 to 16 }`; absent means flat boundaries |

The codec's `validate` step returns a `DataResult` error, "a cap reservoir cannot reach the surface",
for `kind: cap` with `reaches_surface: true`, so the configured feature fails to load with that
message. `cell_height` is unused when `reaches_surface` is true.

### Shipped reservoirs

| Configured feature | kind | surface | band | cell | chance | biomes | salt |
|---|---|---|---|---|---|---|---|
| `crude_oil_reservoir` | pool | no | -48 to 32 | 32×16 | 0.08 | `#minecraft:is_overworld` | 1 |
| `natural_gas_reservoir` | cap | no | -56 to 16 | 32×16 | 0.06 | `#minecraft:is_overworld` | 2 |

Both use the bituminous targets from `underground_oil_lake.json` and `boundary` with noise
`voluminousenergy:petroleum_reservoir_boundary` and wobble 4. The noise is shipped as
`worldgen/noise/petroleum_reservoir_boundary.json` (`firstOctave` -4, amplitudes `[1.0, 1.0]`) and read
through `RandomState.getOrCreateNoise`, so it is seeded per world. These are starting values for
the in-game check.

### Placement: every overworld chunk, biome decided per reservoir
Each configured feature has a `worldgen/placed_feature` with an empty `placement` list, so it runs
once per chunk with the origin at the chunk's minimum corner. `petroleum_reservoirs_overworld.json`
(`neoforge:add_features`, `#minecraft:is_overworld`, step `lakes`) adds both to every overworld
biome. There is no `minecraft:biome` placement filter.

Biome selection lives in the configuration and is tested at the reservoir centre. If a datapack
reservoir limited to, say, badlands were added only to badlands biomes, a reservoir reaching into a
plains chunk would stop at the border, unsealed, because the plains chunk would never run the
feature.

### Step `lakes`
This matches vanilla lakes and the oil deposit, and comes before monster rooms, geodes, ores, and
structure pieces. The alternative, `fluid_springs`, places reservoirs after those, so reservoirs would flood
dungeons, strongholds, mineshafts, and ancient cities with petroleum. That is more invasive than
letting later features occasionally break a seal.

### Generic type id
The feature type is `ve_fluid_reservoir_feature`, matching `ve_fluid_deposit_feature`, because it takes
any fluid. "Petroleum" names the capability, the biome modifier, and the shipped
configurations.

## Risks / Trade-offs

- [A ravine crossing a buried reservoir] → An oil pool shows at the ravine floor, open to the sky; a gas
  cap gets a bituminous lid across the ravine. Accepted, comparable to vanilla's floating aquifer
  water where carvers cut through.
- [Features and structures after `lakes` break a seal, and monster rooms treat gas as air] →
  Accepted, as for the cave carvers.
- [`getBaseHeight` cost] → Called only for reservoir cells (about 5 per cell per chunk) and cached per
  call. If profiling shows it is heavy, sample fewer columns.
- [Boundary noise cost] → Sampled only at positions within `wobble` blocks of a boundary between
  two cells, once per position, and only in chunks a reservoir touches.
- [Fill and seal iterate up to 16×16×(band height) positions] → Only in chunks touched by a reservoir
  cell. Vanilla's aquifer already does per-block work on every empty block of every chunk.
- [Other mods replacing `RandomState` or biome sources] → Results stay deterministic as long as they
  are pure functions of the seed, which both vanilla and NeoForge require.
- [Starting values are off] → Tuned in JSON during the in-game check.

## Migration Plan

None. Only new chunks get reservoirs, and borders with old chunks are plugged by the seal rule.
Removing or overriding the biome modifier turns reservoirs off for new chunks.

## Open Questions

- Final `chance`, cell sizes, and bands, settled during the in-game check.
