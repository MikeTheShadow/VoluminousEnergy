# worldgen/ore-deposits Specification

## Purpose

Defines the blocks that make up the mod's generated ore deposits: an ore shell around a solid core
of the material's raw storage block.

## Requirements

### Requirement: The Cinnabar deposit has a vanilla Cinnabar core

The Cinnabar deposit SHALL generate a core of vanilla Cinnabar surrounded by Cinnabar Ore. Its size,
rarity, height range, and biomes SHALL stay as they are.

#### Scenario: Exploring a new world

- **WHEN** a player finds a Cinnabar deposit in a newly generated Overworld chunk
- **THEN** its core is Cinnabar blocks, its shell is Cinnabar Ore, and it has no Raw Cinnabar Blocks
