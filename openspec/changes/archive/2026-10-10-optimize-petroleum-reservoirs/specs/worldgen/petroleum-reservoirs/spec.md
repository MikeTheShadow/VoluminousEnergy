## ADDED Requirements

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

## MODIFIED Requirements

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
