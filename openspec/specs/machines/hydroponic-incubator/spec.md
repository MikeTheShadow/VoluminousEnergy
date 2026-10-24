# machines/hydroponic-incubator Specification

## Purpose

Defines which plant items the Hydroponic Incubator duplicates and the main and chance outputs it
produces for each, using Ammonium Nitrate Solution without consuming the input plant.

## Requirements

### Requirement: Incubator duplicates mushrooms

The Hydroponic Incubator SHALL accept Brown Mushroom and Red Mushroom. Each cycle SHALL take 200
ticks, consume 250 mB of Ammonium Nitrate Solution, leave the input in place, and yield 9 of the
same mushroom with a 33% chance of 9 more, like the existing flower recipes.

#### Scenario: Incubating a Red Mushroom

- **WHEN** a powered Hydroponic Incubator has 1 Red Mushroom in its input slot and at least 250 mB
  of Ammonium Nitrate Solution in its tank
- **THEN** after 200 ticks the primary output holds 9 Red Mushroom, not Brown Mushroom, the tank has
  250 mB less, and the Red Mushroom is still in the input slot
