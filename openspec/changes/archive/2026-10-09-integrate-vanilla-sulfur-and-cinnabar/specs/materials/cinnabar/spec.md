## Purpose

Defines how the mod's cinnabar materials (Raw Cinnabar, Cinnabar Dust, the Cinnabar ores) relate to
vanilla's Cinnabar block, which acts as the raw cinnabar storage block from Minecraft 26.2.

## ADDED Requirements

### Requirement: Vanilla Cinnabar is the raw cinnabar storage block

The mod SHALL NOT register a Raw Cinnabar Block. The id `voluminousenergy:raw_cinnabar_block` SHALL
NOT exist as a block or an item. Vanilla Cinnabar (`minecraft:cinnabar`) SHALL be the only entry the
mod adds to the `c:storage_blocks/raw_cinnabar` block and item tags.

#### Scenario: Searching the creative inventory

- **WHEN** a player searches the creative inventory for "Raw Cinnabar Block"
- **THEN** no item is found

#### Scenario: Datapack reads the storage tag

- **WHEN** a datapack resolves the item tag `c:storage_blocks/raw_cinnabar` with only this mod
  installed
- **THEN** the tag contains `minecraft:cinnabar` and nothing else

### Requirement: Raw Cinnabar and Cinnabar craft into each other at 9 to 1

A crafting table SHALL turn 9 Raw Cinnabar in a full 3×3 grid into 1 Cinnabar. It SHALL turn 1
Cinnabar on its own into 9 Raw Cinnabar.

#### Scenario: Compacting Raw Cinnabar

- **WHEN** a player fills all nine slots of a crafting table with Raw Cinnabar
- **THEN** the result is 1 Cinnabar

#### Scenario: Uncrafting Cinnabar

- **WHEN** a player places 1 Cinnabar alone in any crafting grid slot
- **THEN** the result is 9 Raw Cinnabar

### Requirement: Cinnabar requires an iron-tier pickaxe

Vanilla Cinnabar SHALL drop only when mined with a pickaxe of iron tier or better, the tier of the
mod's Cinnabar ores. A wooden, golden, or stone pickaxe SHALL break it without a drop. The polished,
brick, chiseled, slab, stair, and wall variants SHALL keep vanilla's tool rules.

#### Scenario: Mining with a stone pickaxe

- **WHEN** a survival player breaks a Cinnabar block in a Sulfur Cave with a stone pickaxe
- **THEN** the block breaks and drops nothing

#### Scenario: Mining with an iron pickaxe

- **WHEN** a survival player breaks a Cinnabar block with an iron pickaxe
- **THEN** it drops 1 Cinnabar

#### Scenario: Mining a variant with a stone pickaxe

- **WHEN** a survival player breaks Polished Cinnabar with a stone pickaxe
- **THEN** it drops 1 Polished Cinnabar, as in vanilla

### Requirement: Cinnabar items use vanilla Cinnabar's palette

The textures of Cinnabar Ore, Deepslate Cinnabar Ore, Raw Cinnabar, and Cinnabar Dust SHALL use the
muted brick-red palette of vanilla Cinnabar instead of the current saturated orange-red. Cinnabar
Dust SHALL be clearly lighter and pinker than Netherrack Dust.

#### Scenario: Dusts side by side

- **WHEN** a player puts Cinnabar Dust and Netherrack Dust in adjacent inventory slots
- **THEN** the two read as different items at a glance: Cinnabar Dust is lighter and pinker, and
  Netherrack Dust is dark maroon

#### Scenario: Raw Cinnabar beside the block

- **WHEN** a player holds Raw Cinnabar next to a placed Cinnabar block
- **THEN** both use the same red palette
