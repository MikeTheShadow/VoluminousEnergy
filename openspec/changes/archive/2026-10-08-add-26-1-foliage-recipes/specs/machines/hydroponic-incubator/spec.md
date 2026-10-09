## Purpose

Defines which plant items the Hydroponic Incubator duplicates and the main and chance outputs it
produces for each, using Ammonium Nitrate Solution without consuming the input plant.

## ADDED Requirements

### Requirement: Incubator duplicates the post-1.21.1 grasses, bushes, and hanging moss

The Hydroponic Incubator SHALL accept Short Dry Grass, Bush, Firefly Bush, and Pale Hanging Moss.
Each cycle SHALL take 200 ticks, consume 250 mB of Ammonium Nitrate Solution, leave the input in
place, and yield:
Short Dry Grass, 9 Short Dry Grass with chances of 4 Tall Dry Grass (33%), 3 Dead Bush (15%), and 2
Cactus Flower (5%);
Bush, 9 Bush with chances of 4 Bush (66%) and 1 Firefly Bush (5%);
Firefly Bush, 6 Firefly Bush with chances of 4 Firefly Bush (66%) and 2 Bush (20%);
Pale Hanging Moss, 9 Pale Hanging Moss with a 33% chance of 9 more.

#### Scenario: Incubating Short Dry Grass

- **WHEN** a powered Hydroponic Incubator has 1 Short Dry Grass in its input slot and at least
  250 mB of Ammonium Nitrate Solution in its tank
- **THEN** after 200 ticks the primary output holds 9 Short Dry Grass, the RNG slots may hold Tall
  Dry Grass, Dead Bush, or Cactus Flower at their chances, the tank has 250 mB less, and the Short
  Dry Grass is still in the input slot

#### Scenario: Incubating Firefly Bush

- **WHEN** a powered Hydroponic Incubator has 1 Firefly Bush in its input slot and enough Ammonium
  Nitrate Solution
- **THEN** after 200 ticks the primary output holds 6 Firefly Bush and the input is not consumed

### Requirement: Incubator duplicates the post-1.21.1 flowers

The Hydroponic Incubator SHALL accept Open Eyeblossom, Closed Eyeblossom, Golden Dandelion,
Wildflowers, and Cactus Flower. Each cycle SHALL take 200 ticks, consume 250 mB of Ammonium
Nitrate Solution, leave the input in place, and yield 9 of the same flower with a 33% chance of 9
more, like the existing flower recipes.

#### Scenario: Incubating a Closed Eyeblossom

- **WHEN** a powered Hydroponic Incubator has 1 Closed Eyeblossom in its input slot and enough
  Ammonium Nitrate Solution
- **THEN** after 200 ticks the primary output holds 9 Closed Eyeblossom, not Open Eyeblossom

### Requirement: Incubator grows the Pale Oak Sapling

The Hydroponic Incubator SHALL accept a Pale Oak Sapling. Each cycle SHALL take 200 ticks, consume
250 mB of Ammonium Nitrate Solution, leave the input in place, and yield 9 Pale Oak Log with chances
of 1 Pale Oak Sapling (75%), 4 Stick (25%), and 1 Pale Hanging Moss (5%).

#### Scenario: Incubating a Pale Oak Sapling

- **WHEN** a powered Hydroponic Incubator has 1 Pale Oak Sapling in its input slot and enough
  Ammonium Nitrate Solution
- **THEN** after 200 ticks the primary output holds 9 Pale Oak Log and the sapling is still in the
  input slot

### Requirement: Incubator does not duplicate Leaf Litter, Tall Dry Grass, or pale moss blocks

The Hydroponic Incubator SHALL NOT accept Leaf Litter, Tall Dry Grass, Pale Oak Leaves, Pale Moss
Block, or Pale Moss Carpet.

#### Scenario: Inserting Leaf Litter

- **WHEN** a player places Leaf Litter in a Hydroponic Incubator's input slot with Ammonium Nitrate
  Solution in the tank
- **THEN** the incubator does not start processing
