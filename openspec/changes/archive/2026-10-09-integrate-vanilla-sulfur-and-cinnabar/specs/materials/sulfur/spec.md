## Purpose

Defines how the mod's sulfur materials (Raw Sulfur, Sulfur Dust) relate to the vanilla Sulfur block
and the Sulfur Cube mob added in Minecraft 26.2.

## ADDED Requirements

### Requirement: Raw Sulfur and Sulfur craft into each other at 9 to 1

A crafting table SHALL turn 9 Raw Sulfur in a full 3×3 grid into 1 Sulfur (`minecraft:sulfur`). It
SHALL turn 1 Sulfur on its own into 9 Raw Sulfur. Vanilla's own Sulfur recipes, Potent Sulfur from
9 Sulfur and Sulfur from 4 Sulfur Spikes, SHALL keep working.

#### Scenario: Compacting Raw Sulfur

- **WHEN** a player fills all nine slots of a crafting table with Raw Sulfur
- **THEN** the result is 1 Sulfur

#### Scenario: Uncrafting Sulfur

- **WHEN** a player places 1 Sulfur alone in any crafting grid slot
- **THEN** the result is 9 Raw Sulfur

#### Scenario: Potent Sulfur is unaffected

- **WHEN** a player fills all nine slots of a crafting table with Sulfur
- **THEN** the result is 1 Potent Sulfur

### Requirement: The smallest Sulfur Cube drops Raw Sulfur

When a size 1 Sulfur Cube dies, it SHALL drop a random 1 to 4 Raw Sulfur. The count SHALL be
multiplied by the Looting level of the killer's weapon, and a killer without Looting counts as level
1. A size 2 Sulfur Cube SHALL NOT drop Raw Sulfur. Vanilla marks every size 1 Sulfur Cube as a baby, and babies
normally drop no loot; the Raw Sulfur drop SHALL apply to them anyway, and SHALL respect the
`mobDrops` game rule.

#### Scenario: Killing a small cube without Looting

- **WHEN** a player kills a size 1 Sulfur Cube with an unenchanted sword
- **THEN** it drops between 1 and 4 Raw Sulfur

#### Scenario: Killing a small cube with Looting III

- **WHEN** a player kills a size 1 Sulfur Cube with a Looting III sword
- **THEN** it drops between 3 and 12 Raw Sulfur, in multiples of 3

#### Scenario: Killing a small cube with mob drops off

- **WHEN** the `mobDrops` game rule is false and a player kills a size 1 Sulfur Cube
- **THEN** it drops no Raw Sulfur

#### Scenario: Killing a large cube

- **WHEN** a player kills a size 2 Sulfur Cube
- **THEN** it drops no Raw Sulfur and splits into smaller cubes as in vanilla

#### Scenario: A small cube dies in an explosion

- **WHEN** a size 1 Sulfur Cube is killed by TNT with no player involved
- **THEN** it drops between 1 and 4 Raw Sulfur, and no error is logged

#### Scenario: A small cube dies in lava

- **WHEN** a size 1 Sulfur Cube dies in lava
- **THEN** no Raw Sulfur can be collected, as with any item that falls into lava, and no error is
  logged

### Requirement: Sulfur items use vanilla Sulfur's palette

The Raw Sulfur and Sulfur Dust textures SHALL use the muted yellow palette of vanilla Sulfur instead
of the current saturated golden yellow.

#### Scenario: Raw Sulfur beside the block

- **WHEN** a player holds Raw Sulfur next to a placed Sulfur block
- **THEN** both use the same yellow palette
