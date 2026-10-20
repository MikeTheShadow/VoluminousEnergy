# petroleum-reservoirs Specification

## Purpose

Defines aquifer-style petroleum reservoirs in the overworld: crude oil pools that flood cave space below a
flat level, inverted natural gas caps that fill cave space above a flat floor, how each is sealed, and how
datapacks configure them, including pools that reach the surface.

## Requirements

### Requirement: Underground oil reservoirs flood caves up to a flat level
In every overworld biome, the world SHALL contain underground oil reservoirs between y -48 and y 32. A
reservoir is a region of the world tens of blocks across. Within a reservoir, every cave position below the
reservoir's level SHALL be a crude oil source block, and positions at or above the level SHALL keep
their air, so the oil has a flat, open surface. Cave positions are air, cave air, water, and the water
plants seagrass, tall seagrass, kelp, and kelp plant. A reservoir's level SHALL be at least 8 blocks
below the lowest terrain sampled around the reservoir, so underground oil reservoirs do not reach the
surface except where a ravine cuts down to them. Reservoirs SHALL be rarer than vanilla aquifers.

#### Scenario: Finding an oil reservoir
- **WHEN** a player explores caves between y -48 and y 32 in a newly generated world
- **THEN** they occasionally reach part of the cave network that is flooded with crude oil up to one flat level, with air above it

#### Scenario: Aquifer water inside a reservoir
- **WHEN** a vanilla aquifer's water sits in a cave inside an oil reservoir, below the reservoir's level
- **THEN** that water is replaced by crude oil

#### Scenario: Seagrass inside a reservoir
- **WHEN** seagrass or kelp grows in water inside an oil reservoir, below the reservoir's level, including seagrass that a neighbouring chunk places there before the reservoir's own chunk generates
- **THEN** it is replaced by crude oil like the water around it, and the reservoir stays sealed

### Requirement: Natural gas reservoirs fill caves above a flat floor
In every overworld biome, the world SHALL contain natural gas reservoirs between y -56 and y 16. Within
a gas reservoir, every cave position at or above the reservoir's floor, up to the top of the reservoir, SHALL be
a natural gas source block, and positions below the floor SHALL keep their air, so the gas hangs
from the cave ceiling with an open underside. A gas reservoir's top SHALL be at least 8 blocks below
the lowest terrain sampled around it. Gas reservoirs SHALL NOT reach the surface.

#### Scenario: Walking under a gas reservoir
- **WHEN** a player walks through a cave whose upper part lies inside a gas reservoir
- **THEN** the cave's upper part is filled with natural gas above one flat floor, and the player can walk through the air beneath it

#### Scenario: No surface gas
- **WHEN** chunks generate anywhere in the overworld
- **THEN** no natural gas reservoir fills space that is open to the sky, except where a ravine cuts into it

### Requirement: Pools reach the surface only when configured to
A pool configuration MAY be set to reach the surface. Within such a reservoir, every cave position
from the bottom of its band up to the reservoir's level SHALL be its fluid, where the level is the
lowest terrain height sampled around the reservoir. This leaves pools open to the sky in low ground,
connected to flooded caves below. No shipped reservoir SHALL reach the surface.

#### Scenario: No shipped surface pools
- **WHEN** a player walks across the surface of a newly generated overworld with no datapacks
- **THEN** no reservoir has placed fluid open to the sky, except where a ravine cuts down into one

#### Scenario: Caves under a surface pool
- **WHEN** a datapack surface pool's level lies above a cave within the reservoir and its band
- **THEN** that cave is filled with the pool's fluid as well

### Requirement: Reservoirs are sealed except at their open face
Every position that shares a face with a reservoir's fluid, is not part of the reservoir, and is not solid
or holds any fluid, SHALL become a bituminous block. The exceptions are the position directly above
an oil pool's fluid and the position directly below a gas cap's fluid. The variant SHALL follow the
block beneath the plug: Bituminous Sand above blocks in `c:ore_bearing_ground/sand/colorless_sand`,
Bituminous Red Sand above blocks in `c:ore_bearing_ground/sand/red_sand`, and Bituminous Gravel above
every other block. Solid blocks next to a reservoir SHALL be left unchanged. Blocks tagged
`minecraft:features_cannot_replace` SHALL never be replaced by a reservoir or its plugs.

#### Scenario: Cave leading out of an oil reservoir
- **WHEN** a cave passage leaves an oil reservoir sideways below the reservoir's level
- **THEN** the passage is plugged with bituminous blocks where it leaves the reservoir, and the oil does not run into the rest of the passage

#### Scenario: Gas reservoir above a passage
- **WHEN** a cave rises out of the top of a gas reservoir
- **THEN** it is plugged with bituminous blocks at the reservoir's top

#### Scenario: Surface pool rim
- **WHEN** the ground at the edge of a datapack surface pool dips below the reservoir's level
- **THEN** the gap is filled with bituminous blocks, and the fluid does not spill out

#### Scenario: Plug on a sand floor
- **WHEN** a cave passage with a sand floor leaves an oil reservoir
- **THEN** the plug blocks resting on the sand are Bituminous Sand, and plug blocks resting on stone are Bituminous Gravel

### Requirement: Reservoirs are still and do not overwrite other fluids
Generation SHALL NOT make reservoir fluids flow; they stay as placed until something next to them
changes. A reservoir SHALL NOT replace lava, crude oil, natural gas, or any other non-water fluid. Where
fluid from one reservoir meets a different fluid, the meeting point SHALL be plugged by the sealing rule.

#### Scenario: Oil reservoir meeting a gas reservoir
- **WHEN** an oil reservoir and a gas reservoir overlap in the same cave
- **THEN** the oil and gas are separated by bituminous blocks and neither replaces the other

#### Scenario: Breaching a reservoir
- **WHEN** a player breaks a bituminous plug of a gas reservoir
- **THEN** natural gas flows out through the opening and rises

### Requirement: Reservoirs are continuous across chunk and biome borders
Whether a region is a reservoir, and its level or floor, SHALL depend only on the world seed, the reservoir
configuration, and the biome at the reservoir's centre. A reservoir SHALL generate the same in every chunk it
covers, with no seams at chunk borders and no cut-off at biome borders. In chunks generated before
the mod was added, nothing SHALL change.

#### Scenario: Reservoir across a biome border
- **WHEN** a reservoir configured for some biomes only has its centre in one of them, and part of the reservoir extends into a neighbouring biome that is not in the list
- **THEN** the fluid continues into the neighbouring biome with the same level

#### Scenario: Same seed, same reservoirs
- **WHEN** two worlds are created with the same seed and the same mods
- **THEN** their reservoirs are in the same places with the same levels

### Requirement: Reservoir boundaries are irregular
Where a reservoir ends inside open cave, the boundary between reservoir fluid and the rest of the cave, and
the bituminous plug along it, SHALL follow an irregular, curving surface rather than a flat plane,
in the way vanilla aquifer barriers curve. The boundary SHALL still depend only on the seed, the
configuration, and position, so it stays seamless across chunks and the reservoir stays sealed.

#### Scenario: Reservoir ending in a large cave
- **WHEN** an oil reservoir's boundary passes through a large open cave below the reservoir's level
- **THEN** the bituminous plug holding the oil back bulges and curves across the cave instead of forming a flat wall

#### Scenario: Flat boundaries by datapack
- **WHEN** a datapack sets a reservoir configuration's boundary wobble to 0
- **THEN** that reservoir's boundaries are flat planes between cell centres

### Requirement: Reservoirs are configurable by datapack
Each reservoir type SHALL be a feature in data, in the `worldgen/feature` registry with its
settings as top-level fields, setting its fluid, whether it is a pool (filled below a level) or a
cap (filled above a floor), whether a pool reaches the surface, its height band, its size, its
frequency, the biomes allowed at its centre, its plug variants, and its boundary noise and wobble,
where a wobble of 0 gives flat boundaries. The shipped entries SHALL be
`voluminousenergy:crude_oil_reservoir` and `voluminousenergy:natural_gas_reservoir`. A
configuration that makes a cap reach the surface SHALL fail to load with an error naming the
problem, rather than generating.

#### Scenario: Datapack adds a badlands oil reservoir
- **WHEN** a datapack adds a crude oil pool feature with `reaches_surface` set, a band from y 40 to y 120, and its biomes set to `#minecraft:is_badlands`, and adds it to the overworld through a biome modifier
- **THEN** badlands generate surface oil pools with Bituminous Red Sand rims in red sand

#### Scenario: Invalid gas configuration
- **WHEN** a datapack sets a natural gas cap feature to reach the surface
- **THEN** the world fails to load that feature and the log reports that a cap cannot reach the surface

### Requirement: Reservoir cell caching never changes output
The startup config SHALL have a boolean option `Cache Petroleum Reservoir Cells` under World
Generation → Feature Generation, default `true`. When it is `true`, results worked out for a
reservoir SHALL be shared between the chunks that need them. With `Fast Petroleum Reservoir Surface
Sampling` set to `false`, every reservoir SHALL generate in the same place, with the same level or
floor and the same plugs, whether caching is on or off and as it did before this option existed.
The result SHALL NOT depend on the order in which chunks generate, on how many threads generate
them, or on whether the world was saved and reloaded in between.

#### Scenario: Caching on by default
- **WHEN** the mod starts with no existing startup config file
- **THEN** the generated `voluminousenergy-startup.toml` contains `Cache Petroleum Reservoir Cells = true`

#### Scenario: Caching on and off give the same reservoirs
- **WHEN** a world is created with a given seed, once with caching on and once with it off, both with fast surface sampling off, and the same area is explored in both
- **THEN** both worlds contain the same reservoirs with the same levels, floors, and plugs

#### Scenario: Same seed before and after the optimization
- **WHEN** a world is created with a given seed and default config, once with the mod before this change and once after it, and the same area is explored in both
- **THEN** both worlds contain the same reservoirs with the same levels, floors, and plugs

#### Scenario: Reloading mid-exploration
- **WHEN** a player explores half of a reservoir, quits to the title screen, reloads the world, and explores the other half
- **THEN** the reservoir continues with the same level and no seam where the two sessions meet

### Requirement: Fast surface sampling is an opt-in config option
The startup config SHALL have a boolean option `Fast Petroleum Reservoir Surface Sampling` under
World Generation → Feature Generation, default `false`. When it is `true`, the terrain height that
limits a reservoir's level or top SHALL come from a cheaper, coarser estimate of the terrain shared
by all reservoirs in the same area, rather than exact terrain heights. Reservoirs SHALL still be
sealed, still stay at least 8 blocks below the estimated terrain, and still be continuous across
chunk borders. Reservoir placement MAY differ from the default for the same seed.

#### Scenario: Option off by default
- **WHEN** the mod starts with no existing startup config file
- **THEN** the generated `voluminousenergy-startup.toml` contains `Fast Petroleum Reservoir Surface Sampling = false`

#### Scenario: Faster generation with fast sampling on
- **WHEN** fast surface sampling is set to `true`, the game is restarted, and a player flies through newly generated terrain
- **THEN** reservoirs still generate, sealed and without seams, and new terrain loads faster than with fast sampling off

#### Scenario: Switching fast sampling in an existing world
- **WHEN** a world generated with fast sampling off is loaded with it on, and a reservoir spans already generated chunks and new chunks
- **THEN** wherever the new chunks' reservoir differs from the old chunks', the meeting point is plugged with bituminous blocks and no fluid spills
