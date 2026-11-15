# machines/primitive-blast-furnace Specification

## Purpose

Defines what the Primitive Blast Furnace processes without fuel or power, starting with charring logs,
cactus, and sugar cane into solid fuel, and how datapacks extend which items count.

## Requirements

### Requirement: Logs char into charcoal

The Primitive Blast Furnace SHALL turn 1 item tagged `minecraft:logs_that_burn` into 1 charcoal in 800
ticks, with no fuel and no power.

#### Scenario: One oak log

- **WHEN** a Primitive Blast Furnace has 1 oak log in its input slot and an empty output slot
- **THEN** after 800 ticks the input slot is empty and the output slot holds 1 charcoal

#### Scenario: Wood and stripped logs count

- **WHEN** the input slot holds 4 stripped birch wood
- **THEN** the furnace chars them one at a time and the output slot ends with 4 charcoal

#### Scenario: Nether stems do not char

- **WHEN** a crimson stem is in the input slot
- **THEN** the furnace does not light and produces nothing, because crimson stems are not in
  `minecraft:logs_that_burn`

### Requirement: Three cactus char into one Cactchar

The Primitive Blast Furnace SHALL turn 3 items tagged `voluminousenergy:charrable/cactus` into 1 Cactchar
in 200 ticks. It SHALL NOT start an operation while the input slot holds fewer than 3. The
`voluminousenergy:charrable/cactus` tag SHALL contain `#c:crops/cactus`.

#### Scenario: Exactly three cactus

- **WHEN** the input slot holds 3 cactus and the output slot is empty
- **THEN** after 200 ticks the input slot is empty and the output slot holds 1 Cactchar

#### Scenario: Too few cactus

- **WHEN** the input slot holds 2 cactus
- **THEN** the furnace does not light and both cactus stay in the slot
- **AND** when a third cactus is added, the furnace starts

#### Scenario: Leftover cactus

- **WHEN** the input slot holds 7 cactus
- **THEN** the furnace makes 2 Cactchar over two operations and 1 cactus stays in the input slot

#### Scenario: A modpack adds a modded cactus

- **WHEN** a datapack adds `regions_unexplored:saguaro_cactus` to `c:crops/cactus` or to
  `voluminousenergy:charrable/cactus`
- **THEN** 3 saguaro cactus char into 1 Cactchar after the next reload

### Requirement: Sixteen sugar cane char into one Cane Charcoal

The Primitive Blast Furnace SHALL turn 16 items tagged `voluminousenergy:charrable/sugar_cane` into 1
Cane Charcoal in 200 ticks. It SHALL NOT start an operation while the input slot holds fewer than 16.
The `voluminousenergy:charrable/sugar_cane` tag SHALL contain `#c:crops/sugar_cane`.

#### Scenario: A full operation

- **WHEN** the input slot holds 16 sugar cane and the output slot is empty
- **THEN** after 200 ticks the input slot is empty and the output slot holds 1 Cane Charcoal

#### Scenario: Fifteen sugar cane

- **WHEN** the input slot holds 15 sugar cane
- **THEN** the furnace does not light and all 15 stay in the slot

#### Scenario: A stack of sugar cane

- **WHEN** a hopper fills the input slot with 64 sugar cane
- **THEN** the furnace makes 4 Cane Charcoal over four operations and the input slot ends empty

### Requirement: JEI shows each charring recipe's input count

When JEI is installed, the Primitive Blast Furnace pages for the charring recipes SHALL show the input
count one operation consumes.

#### Scenario: Sugar cane page

- **WHEN** a player looks up the uses of sugar cane in JEI
- **THEN** the Primitive Blast Furnace page shows 16 sugar cane in the input slot and 1 Cane Charcoal
  in the output slot

#### Scenario: Cactus page

- **WHEN** a player looks up how to make Cactchar in JEI
- **THEN** the Primitive Blast Furnace page shows 3 cactus in the input slot and 1 Cactchar in the
  output slot
