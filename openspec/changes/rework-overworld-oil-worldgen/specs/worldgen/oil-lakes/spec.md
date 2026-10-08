## Purpose

Defines how crude oil lakes generate in the overworld: open surface lakes rimmed with bituminous
blocks, and sealed underground oil deposits wrapped in a bituminous shell.

## ADDED Requirements

### Requirement: Bituminous variant follows the replaced block
Wherever oil lake generation places a bituminous block, it SHALL choose the variant from the block
being replaced at that position: Bituminous Sand for blocks in
`c:ore_bearing_ground/sand/colorless_sand`, Bituminous Red Sand for blocks in
`c:ore_bearing_ground/sand/red_sand`, and Bituminous Gravel for every other block, stone and
deepslate included. The choice SHALL be data-driven in the configured feature JSON so a datapack
can change it.

#### Scenario: Shell through sand and stone
- **WHEN** an oil lake's bituminous positions cross a layer of sand lying on stone
- **THEN** the positions that were sand become Bituminous Sand and the positions that were stone become Bituminous Gravel

#### Scenario: Red sand host
- **WHEN** a bituminous position was red sand in a badlands biome
- **THEN** it becomes Bituminous Red Sand

### Requirement: Surface oil lakes keep their shape with a bituminous rim
Surface oil lakes SHALL generate as they do today, with crude oil in the lower half of the basin, air
above it, and dirt under exposed sky converted to grass or mycelium, except that every rim block that
was previously placed as stone SHALL instead be the bituminous variant chosen by the replaced block.

#### Scenario: Plains surface lake
- **WHEN** a surface oil lake generates in a plains biome
- **THEN** the basin holds crude oil with open air above it, and its rim, where solid ground was, is Bituminous Gravel rather than stone

#### Scenario: Desert surface lake
- **WHEN** a surface oil lake generates into desert sand
- **THEN** the rim blocks that replaced sand are Bituminous Sand

### Requirement: Underground oil deposits are sealed and fully oil-filled
Underground oil deposits SHALL generate at a uniformly distributed height between y -32 and y 47 in
overworld biomes, at the same frequency as underground oil lakes before this change. Every position
inside the deposit SHALL be crude oil source blocks, with no air. Every position adjacent to the
deposit (the shell) SHALL be the bituminous variant chosen by the replaced block.

#### Scenario: Digging into a deposit
- **WHEN** a player mines into an underground oil deposit from the side
- **THEN** they break through a continuous bituminous layer and reach crude oil source blocks with no air pocket above the oil

#### Scenario: Deposit in deepslate
- **WHEN** a deposit generates at y -20 surrounded by deepslate
- **THEN** its entire shell is Bituminous Gravel

### Requirement: Underground oil deposits do not leak or break protected blocks
An underground oil deposit SHALL NOT generate if any shell position is air or a fluid, so the oil is
never exposed to a cave. It SHALL NOT generate if any of its positions holds a block tagged
`minecraft:features_cannot_replace`.

#### Scenario: Deposit beside a cave
- **WHEN** a deposit's shell would intersect the air of a cave
- **THEN** no deposit is placed at that position and the cave is left unchanged

#### Scenario: Deposit beside a spawner
- **WHEN** a deposit's footprint would include a dungeon's mob spawner or chest
- **THEN** no deposit is placed at that position

### Requirement: Configured feature ids are stable
The configured features `voluminousenergy:surface_oil_lake` and
`voluminousenergy:underground_oil_lake` SHALL keep their ids, so existing biome modifiers and
datapacks referring to them still resolve.

#### Scenario: Datapack disables underground oil
- **WHEN** a datapack removes `voluminousenergy:underground_oil_lake` from overworld biomes by id
- **THEN** no underground oil deposits generate
