## Why

Players exploring the overworld find caves flooded with water and lava but never with the mod's own
raw resources, so crude oil only shows up as small lakes and buried deposits, and natural gas does
not generate in the world at all. Small petroleum cave tunnels (crude oil or natural gas), sealed
in a bituminous shell, make both fluids something to stumble on underground, and fit the
bituminous shell the oil worldgen rework introduces.

## What Changes

- Overworld caves can now include short winding tunnels, sometimes with a small chamber, that are
  completely filled with crude oil, and separate ones filled with natural gas. They are smaller and
  rarer than vanilla caves.
- Every tunnel is sealed: each block next to the tunnel becomes a bituminous block (Bituminous Sand
  in colorless sand, Bituminous Red Sand in red sand, Bituminous Gravel elsewhere), including where
  the tunnel crosses an existing cave, so the fluid does not spill. Breaking through the shell
  releases the fluid: oil flows out, gas rises out.
- Vanilla caves and canyons carved after a sealed tunnel stop at its shell instead of cutting
  through it, because bituminous blocks and the fluids are not in vanilla's carver-replaceable tag.
- A new data-driven carver type, `voluminousenergy:ve_fluid_cave_carver`, with two configured
  carvers, `voluminousenergy:crude_oil_cave` and `voluminousenergy:natural_gas_cave`, added to every
  overworld biome by a NeoForge `add_carvers` biome modifier. Datapacks can retune or remove them by
  id.
- Known, accepted gaps: structures and features placed after carving (mineshafts, dungeons, trial
  chambers, ancient cities) can break into a tunnel and expose its fluid. Because natural gas blocks
  count as air, a dungeon can generate inside a gas tunnel.

## Capabilities

### New Capabilities
- `worldgen/petroleum-caves`: sealed petroleum (crude oil and natural gas) cave tunnels: where and how often they
  generate, what fills them, the bituminous seal, and how they interact with vanilla caves.

### Modified Capabilities
None.

## Impact

- Code: a new `world/carver/` package with a `VECarvers` registry holder (`VE_CARVER_REGISTRY`) and
  the carver class; one registration line in the `VoluminousEnergy` constructor. It reuses the
  perimeter target selection from `LakeMask` in `world/feature/`.
- Data: `worldgen/configured_carver/{crude_oil_cave,natural_gas_cave}.json` and
  `neoforge/biome_modifier/petroleum_caves_overworld.json` in `src/main/resources`.
- Depends on the unarchived `rework-overworld-oil-worldgen` change (bituminous blocks, `LakeMask`)
  and on the Natural Gas fluid, both present on `1.21.1-petroleum-worldgen`. That branch must merge
  after `1.21.1-oil-feature-rework`.
- Save compatibility: new registry id `voluminousenergy:ve_fluid_cave_carver` (carver type) and new
  data ids. No NBT, data component, or attachment changes. Only newly generated chunks get the
  tunnels.
- Config: no new or changed options. Like the existing oil lakes and geysers, frequency and size
  are set in datapack JSON rather than `tools/Config`.
- Follow-up ports (not in scope): `26.1-dev` and `26.2-dev` would need the bituminous rework and
  Natural Gas first, and then their own proposal.
