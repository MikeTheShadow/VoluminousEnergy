## Purpose

Defines which plant items the Crusher accepts as organic input and what Shredded Biomass and
by-products it yields for them.

## ADDED Requirements

### Requirement: Crusher shreds the post-1.21.1 grasses, bushes, and ground foliage

The Crusher SHALL accept Short Dry Grass, Tall Dry Grass, Bush, Firefly Bush, Leaf Litter, and Pale
Hanging Moss, each processed one item at a time in 200 ticks with no experience, and SHALL yield:
Short Dry Grass, 1 Shredded Biomass and a 25% chance of 1 Stick; Tall Dry Grass, 2 Shredded Biomass
and a 50% chance of 1 Stick; Bush, 2 Shredded Biomass and a 50% chance of 2 Stick; Firefly Bush, 2 Shredded Biomass
and a 50% chance of 1 Stick; Leaf Litter, 1 Shredded Biomass; Pale Hanging Moss, 2 Shredded Biomass.

#### Scenario: Crushing Tall Dry Grass

- **WHEN** a powered Crusher has 1 Tall Dry Grass in its input slot and empty output slots
- **THEN** after 200 ticks the main output holds 2 Shredded Biomass, the RNG slot holds 1 Stick
  about half the time, and the input is consumed

#### Scenario: Crushing Short Dry Grass

- **WHEN** a powered Crusher has 1 Short Dry Grass in its input slot and empty output slots
- **THEN** after 200 ticks the main output holds 1 Shredded Biomass and the RNG slot holds 1 Stick
  about one time in four

#### Scenario: Crushing Leaf Litter

- **WHEN** a powered Crusher has 4 Leaf Litter in its input slot
- **THEN** each completed cycle consumes 1 Leaf Litter and adds 1 Shredded Biomass to the main output

### Requirement: Crusher does not shred new flowers, saplings, leaves, or moss blocks

The Crusher SHALL NOT accept Open Eyeblossom, Closed Eyeblossom, Golden Dandelion, Wildflowers,
Cactus Flower, Pale Oak Sapling, Pale Oak Leaves, Pale Moss Block, or Pale Moss Carpet, matching
the older flowers, saplings, leaves, and moss, which have no Crusher recipe.

#### Scenario: Inserting a new flower

- **WHEN** a player places an Open Eyeblossom in a Crusher's input slot
- **THEN** the Crusher does not start processing
