## ADDED Requirements

### Requirement: Crusher shreds Red Shrub and mushrooms

The Crusher SHALL accept Red Shrub and every item in the `c:mushrooms` tag, which includes Brown
Mushroom, Red Mushroom, and Shelf Mushroom, each processed one item at a time in 200 ticks with no
experience. It SHALL yield: Red Shrub, 2 Shredded Biomass and a 50% chance of 2 Stick; a mushroom,
1 Shredded Biomass.

#### Scenario: Crushing a Red Shrub

- **WHEN** a powered Crusher has 1 Red Shrub in its input slot and empty output slots
- **THEN** after 200 ticks the main output holds 2 Shredded Biomass, the RNG slot holds 2 Stick
  about half the time, and the input is consumed

#### Scenario: Crushing a Shelf Mushroom

- **WHEN** a powered Crusher has 1 Shelf Mushroom in its input slot and empty output slots
- **THEN** after 200 ticks the main output holds 1 Shredded Biomass, the RNG slot is empty, and the
  input is consumed

#### Scenario: Crushing a Brown Mushroom

- **WHEN** a powered Crusher has 1 Brown Mushroom in its input slot and empty output slots
- **THEN** after 200 ticks the main output holds 1 Shredded Biomass and the input is consumed

## MODIFIED Requirements

### Requirement: Crusher does not shred new flowers, saplings, leaves, or moss blocks

The Crusher SHALL NOT accept Open Eyeblossom, Closed Eyeblossom, Golden Dandelion, Wildflowers,
Cactus Flower, Pale Oak Sapling, Pale Oak Leaves, Pale Moss Block, Pale Moss Carpet, Poplar
Sapling, Orange Poplar Leaves, Red Poplar Leaves, or Yellow Poplar Leaves, matching the older
flowers, saplings, leaves, and moss, which have no Crusher recipe.

#### Scenario: Inserting a new flower

- **WHEN** a player places an Open Eyeblossom in a Crusher's input slot
- **THEN** the Crusher does not start processing

#### Scenario: Inserting poplar leaves

- **WHEN** a player places Orange Poplar Leaves in a Crusher's input slot
- **THEN** the Crusher does not start processing
