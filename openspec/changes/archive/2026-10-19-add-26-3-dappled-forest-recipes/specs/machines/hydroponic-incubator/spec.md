## ADDED Requirements

### Requirement: Incubator grows the Poplar Sapling

The Hydroponic Incubator SHALL accept a Poplar Sapling. Each cycle SHALL take 200 ticks, consume
250 mB of Ammonium Nitrate Solution, leave the input in place, and yield 9 Poplar Log with chances
of 1 Poplar Sapling (75%), 4 Stick (25%), and 1 Shelf Mushroom (5%).

#### Scenario: Incubating a Poplar Sapling

- **WHEN** a powered Hydroponic Incubator has 1 Poplar Sapling in its input slot and at least
  250 mB of Ammonium Nitrate Solution in its tank
- **THEN** after 200 ticks the primary output holds 9 Poplar Log, the RNG slots may hold a Poplar
  Sapling, Sticks, or a Shelf Mushroom at their chances, the tank has 250 mB less, and the sapling
  is still in the input slot

### Requirement: Incubator duplicates the Red Shrub

The Hydroponic Incubator SHALL accept a Red Shrub. Each cycle SHALL take 200 ticks, consume 250 mB
of Ammonium Nitrate Solution, leave the input in place, and yield 9 Red Shrub with chances of 4 Red
Shrub (66%) and 1 Brown Mushroom (5%).

#### Scenario: Incubating a Red Shrub

- **WHEN** a powered Hydroponic Incubator has 1 Red Shrub in its input slot and enough Ammonium
  Nitrate Solution
- **THEN** after 200 ticks the primary output holds 9 Red Shrub, the RNG slots may hold 4 Red Shrub
  or 1 Brown Mushroom at their chances, and the Red Shrub is still in the input slot

### Requirement: Incubator duplicates mushrooms

The Hydroponic Incubator SHALL accept Brown Mushroom, Red Mushroom, and Shelf Mushroom. Each cycle
SHALL take 200 ticks, consume 250 mB of Ammonium Nitrate Solution, leave the input in place, and
yield 9 of the same mushroom with a 33% chance of 9 more, like the existing flower recipes.

#### Scenario: Incubating a Shelf Mushroom

- **WHEN** a powered Hydroponic Incubator has 1 Shelf Mushroom in its input slot and enough
  Ammonium Nitrate Solution
- **THEN** after 200 ticks the primary output holds 9 Shelf Mushroom and the input is not consumed

#### Scenario: Incubating a Red Mushroom

- **WHEN** a powered Hydroponic Incubator has 1 Red Mushroom in its input slot and enough Ammonium
  Nitrate Solution
- **THEN** after 200 ticks the primary output holds 9 Red Mushroom, not Brown Mushroom

## MODIFIED Requirements

### Requirement: Incubator does not duplicate Leaf Litter, Tall Dry Grass, or pale moss blocks

The Hydroponic Incubator SHALL NOT accept Leaf Litter, Tall Dry Grass, Pale Oak Leaves, Pale Moss
Block, Pale Moss Carpet, Orange Poplar Leaves, Red Poplar Leaves, or Yellow Poplar Leaves.

#### Scenario: Inserting Leaf Litter

- **WHEN** a player places Leaf Litter in a Hydroponic Incubator's input slot with Ammonium Nitrate
  Solution in the tank
- **THEN** the incubator does not start processing

#### Scenario: Inserting poplar leaves

- **WHEN** a player places Yellow Poplar Leaves in a Hydroponic Incubator's input slot with
  Ammonium Nitrate Solution in the tank
- **THEN** the incubator does not start processing
