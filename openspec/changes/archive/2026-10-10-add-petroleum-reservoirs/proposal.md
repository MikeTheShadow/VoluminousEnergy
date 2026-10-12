## Why

Vanilla aquifers flood whole stretches of the cave network with water, held in by stone barriers and
open at the top, and occasionally reach the surface. Petroleum, meaning crude oil and natural gas, has no equivalent,
so the mod's raw fuels never appear at that scale. Reproducing aquifers would mean a mixin into
vanilla's noise aquifer or a custom world type that copies about 1,700 lines of vanilla generator
code; both were ruled out. The aquifer is a function of seed and position, though, so a feature can
evaluate an equivalent function in each chunk and get aquifer-sized bodies with neither.

## What Changes

- **Oil reservoirs:** in any overworld biome, sections of the underground cave network between y -48 and
  y 32 are flooded with crude oil up to a flat level, with open air above the oil like an aquifer.
  Where the oil would spill into the rest of the cave network, the opening is plugged with
  bituminous blocks.
- **Natural gas reservoirs:** the inverse of an oil reservoir. Cave space above a flat floor, between y -56
  and y 16, is filled with natural gas, sealed above and at the sides, and open underneath, where
  the gas hangs from the cave ceiling. Gas reservoirs never reach the surface.
- **Surface pools by datapack only:** a pool can be configured to reach the surface, filling low
  ground and the caves below it with oil up to the lowest terrain height around the reservoir. No
  shipped reservoir does this; desert surface pools were tried in game and dropped.
- A reservoir is decided by the biome at its centre and is continuous across chunk and biome borders;
  it is never cut off at a border.
- Plugs use the same host-dependent bituminous variants as the oil worldgen rework (sand, red sand,
  gravel).
- Reservoir walls and plugs follow irregular curves, like the stone barriers around vanilla aquifers,
  rather than flat planes. Reservoirs stay fully sealed; nothing spills at generation.
- A new data-driven feature type, `voluminousenergy:ve_fluid_reservoir_feature`, with two configured
  features: `voluminousenergy:crude_oil_reservoir` and `voluminousenergy:natural_gas_reservoir`. Each JSON entry chooses a fluid, `pool` (fills below a level)
  or `cap` (fills above a floor), whether a pool reaches the surface, its height band, reservoir size,
  frequency, and biomes. A `cap` that reaches the surface is rejected when the data loads.
- Known, accepted gaps: structures and features placed after reservoirs can break into one; a dungeon
  can generate inside a gas reservoir because natural gas blocks count as air.

## Capabilities

### New Capabilities
- `worldgen/petroleum-reservoirs`: aquifer-style crude oil pools, inverted natural gas caps, how they
  are sealed, and how datapacks configure them, including surface pools.

### Modified Capabilities
None.

## Impact

- Code: a new feature class in `world/feature/` registered as `ve_fluid_reservoir_feature` in
  `VEFeatures`, with its configuration record and a small helper for the cell grid. It reuses
  `LakeMask.perimeterState` for plug variants.
- Data: two `worldgen/configured_feature/*.json`, two matching `worldgen/placed_feature/*.json` with
  no placement modifiers, the boundary noise `worldgen/noise/petroleum_reservoir_boundary.json`, and `neoforge/biome_modifier/petroleum_reservoirs_overworld.json`
  (`neoforge:add_features`, step `lakes`) in `src/main/resources`.
- Depends on the unarchived `rework-overworld-oil-worldgen` change and on Natural Gas, both on
  `1.21.1-petroleum-worldgen`. Independent of `add-sealed-petroleum-cave-carvers`, although tunnels and
  reservoirs meeting each other are plugged by the same rules.
- Save compatibility: new registry id `voluminousenergy:ve_fluid_reservoir_feature` and new data ids.
  No NBT, data component, or attachment changes. Only newly generated chunks get reservoirs. A reservoir
  that crosses into chunks generated before the mod was added stops at that border.
- Naming: a reservoir here is the flooded stretch of cave, not the oil geyser's underground pool,
  which the oil worldgen rework also calls a reservoir.
- World type: none. Reservoirs work in existing worlds' new chunks and any overworld that uses the
  vanilla biome feature lists.
- Config: no new or changed options. Sizes and frequency are set in datapack JSON, like the other
  oil worldgen.
- Follow-up ports (not in scope): `26.1-dev` and `26.2-dev` would need the bituminous rework and
  Natural Gas first, and then their own proposal.
