# items/solid-fuels Specification

## Purpose

Defines Voluminous Energy's solid fuel items: the Cactchar and Cane Charcoal fuels and their tiny
variants (how long they burn, what generators make of them, what they craft into), and how many
torches every solid fuel crafts.

## Requirements

### Requirement: Cactchar and Cane Charcoal burn like charcoal

Cactchar and Cane Charcoal SHALL each burn for 1600 ticks as furnace fuel, the same as charcoal. Each
SHALL run a Primitive Stirling Generator or a Stirling Generator for 1600 ticks at 128 FE/t.

#### Scenario: Cactchar in a furnace

- **WHEN** a player puts 1 Cactchar into a vanilla furnace's fuel slot and raw iron in its input
- **THEN** the furnace burns for 1600 ticks and smelts 8 items

#### Scenario: Cane Charcoal in a Stirling Generator

- **WHEN** a Stirling Generator's input slot holds 1 Cane Charcoal
- **THEN** it generates 128 FE/t for 1600 ticks

### Requirement: Tiny Cactchar and Tiny Cane Charcoal behave like the other tiny fuels

One Cactchar SHALL craft into 8 Tiny Cactchar, and one Cane Charcoal SHALL craft into 8 Tiny Cane
Charcoal, alone in a crafting grid. Each tiny piece SHALL burn for 200 ticks as furnace fuel and SHALL
run a Primitive Stirling Generator or a Stirling Generator for 200 ticks at 128 FE/t.

#### Scenario: Crafting tiny pieces

- **WHEN** a player puts 1 Cactchar alone in a crafting grid
- **THEN** the result is 8 Tiny Cactchar

#### Scenario: Tiny piece in a furnace

- **WHEN** a player puts 1 Tiny Cane Charcoal into a vanilla furnace's fuel slot
- **THEN** the furnace burns for 200 ticks and smelts 1 item

#### Scenario: Tiny piece in a Primitive Stirling Generator

- **WHEN** a Primitive Stirling Generator's input slot holds 1 Tiny Cactchar
- **THEN** it generates 128 FE/t for 200 ticks

### Requirement: Solid fuels make torches in proportion to burn time

Each Voluminous Energy solid fuel, and any item in `voluminousenergy:charcoal_grade_fuels`, over a
stick SHALL craft into 1 torch per 400 ticks of its burn time, the ratio vanilla coal and charcoal give. Over a
stick over a block in `minecraft:soul_fire_base_blocks` it SHALL craft into the same number of soul
torches. A fuel that burns for less than 400 ticks SHALL still craft into 1. The yields are:

| Fuel | Burn time | Torches or soul torches |
|---|---|---|
| Cactchar, Cane Charcoal (`voluminousenergy:charcoal_grade_fuels`) | 1600 | 4 |
| Coal Coke | 3200 | 8 |
| Petcoke | 4000 | 10 |
| Rosin | 800 | 2 |
| Tallow, every tiny fuel | 200 | 1 |

#### Scenario: Torches from Cactchar

- **WHEN** a player places 1 Cactchar above 1 stick in a crafting grid
- **THEN** the result is 4 torches

#### Scenario: Soul torches from Cane Charcoal

- **WHEN** a player places 1 Cane Charcoal above 1 stick above 1 soul sand in a crafting grid
- **THEN** the result is 4 soul torches

#### Scenario: Torches from Coal Coke

- **WHEN** a player places 1 Coal Coke above 1 stick in a crafting grid
- **THEN** the result is 8 torches

#### Scenario: Soul torches from Petcoke

- **WHEN** a player places 1 Petcoke above 1 stick above 1 soul soil in a crafting grid
- **THEN** the result is 10 soul torches

#### Scenario: Rosin unchanged

- **WHEN** a player places 1 Rosin above 1 stick in a crafting grid
- **THEN** the result is 2 torches

#### Scenario: Torch from a tiny piece

- **WHEN** a player places 1 Tiny Cane Charcoal above 1 stick in a crafting grid
- **THEN** the result is 1 torch

#### Scenario: A datapack adds a charcoal-grade fuel

- **WHEN** a datapack adds another mod's 1600-tick fuel to `voluminousenergy:charcoal_grade_fuels`
- **THEN** that fuel over a stick crafts into 4 torches after the next reload

### Requirement: The new fuels carry common tags

Cactchar SHALL be in `c:cactchar` and Cane Charcoal in `c:cane_charcoal`, and both of those tags
SHALL be in `voluminousenergy:charcoal_grade_fuels`. Tiny Cactchar SHALL be in `c:tiny/cactchar` and
Tiny Cane Charcoal in `c:tiny/cane_charcoal`, and both of those tags SHALL be in `c:tiny`. None of the
four items SHALL be added to `minecraft:coals`.

#### Scenario: Tag lookup

- **WHEN** a datapack or another mod checks the `c:tiny` item tag
- **THEN** it contains Tiny Cactchar and Tiny Cane Charcoal alongside the existing tiny fuels

#### Scenario: Campfire recipe unchanged

- **WHEN** a player tries to craft a campfire with Cactchar in place of coal
- **THEN** there is no result, because `minecraft:coals` does not include Cactchar
