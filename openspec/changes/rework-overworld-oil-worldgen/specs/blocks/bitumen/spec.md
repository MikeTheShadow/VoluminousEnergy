## Purpose

Defines the bituminous blocks that rim oil lakes and seal underground oil deposits, the Bitumen item
they drop, and how both are tagged.

## ADDED Requirements

### Requirement: Three bituminous blocks and a Bitumen item exist
The mod SHALL register three blocks with block items, `voluminousenergy:bituminous_gravel`,
`voluminousenergy:bituminous_sand`, and `voluminousenergy:bituminous_red_sand`, displayed as
Bituminous Gravel, Bituminous Sand, and Bituminous Red Sand. It SHALL register an item
`voluminousenergy:bitumen`, displayed as Bitumen. All four SHALL appear in the Voluminous Energy
creative tab.

#### Scenario: Creative tab
- **WHEN** a player opens the Voluminous Energy creative tab
- **THEN** Bituminous Gravel, Bituminous Sand, Bituminous Red Sand, and Bitumen are listed

### Requirement: Bituminous blocks do not fall
Bituminous blocks SHALL NOT be affected by gravity, regardless of variant.

#### Scenario: Unsupported bituminous sand
- **WHEN** a player mines the block below a Bituminous Sand block
- **THEN** the Bituminous Sand stays in place

### Requirement: Bituminous blocks sound like gravel
All three variants SHALL use gravel sounds for breaking, placing, stepping, and hitting.

#### Scenario: Walking on Bituminous Red Sand
- **WHEN** a player walks across Bituminous Red Sand
- **THEN** they hear gravel footsteps

### Requirement: Bituminous blocks drop Bitumen when mined with a shovel
Bituminous blocks SHALL require a shovel (wooden or better) to drop anything. Mined with a shovel
without Silk Touch, a block SHALL drop 3 to 7 Bitumen, increased by Fortune with the ore drop
formula and reduced by explosion decay. Mined with a Silk Touch shovel, it SHALL drop itself. Broken
without a shovel, it SHALL drop nothing.

#### Scenario: Breaking with a shovel
- **WHEN** a player breaks Bituminous Gravel with an iron shovel
- **THEN** it drops between 3 and 7 Bitumen and no block

#### Scenario: Breaking with Fortune
- **WHEN** a player breaks Bituminous Sand with a Fortune III shovel
- **THEN** it drops Bitumen, on average more than with an unenchanted shovel

#### Scenario: Breaking with Silk Touch
- **WHEN** a player breaks Bituminous Red Sand with a Silk Touch shovel
- **THEN** it drops one Bituminous Red Sand

#### Scenario: Breaking by hand
- **WHEN** a player breaks Bituminous Gravel with an empty hand
- **THEN** nothing drops

### Requirement: Bituminous blocks carry ore tags like saltpeter ore
Each bituminous block, as a block and as an item, SHALL be in `c:ores/bitumen`, which SHALL be part
of `c:ores`. The bituminous blocks SHALL NOT be in any `c:ores_in_ground` tag, because they are not
generated as ore blobs inside a host block.

#### Scenario: Another mod looks up ores
- **WHEN** another mod or a datapack queries the block tag `#c:ores`
- **THEN** all three bituminous blocks match, as saltpeter ore does

#### Scenario: Ore-in-ground lookup
- **WHEN** a recipe asks for the item tag `#c:ores_in_ground/red_sand`
- **THEN** Red Saltpeter Ore matches and Bituminous Red Sand does not

### Requirement: Bitumen item is tagged as bitumen
The Bitumen item SHALL be in the item tag `c:bitumen`. The bituminous blocks SHALL NOT be in it.

#### Scenario: Recipe using the tag
- **WHEN** a recipe asks for `#c:bitumen`
- **THEN** Bitumen matches and Bituminous Gravel does not
