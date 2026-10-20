## MODIFIED Requirements

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
