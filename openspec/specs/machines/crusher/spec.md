# machines/crusher Specification

## Purpose

Defines which items the Crusher accepts and what it yields for them: plants it shreds into Shredded
Biomass and by-products, and mineral blocks it grinds into dust.

## Requirements

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

### Requirement: Crusher turns Sulfur Spikes into Sulfur Dust

The Crusher SHALL accept a Sulfur Spike, processed one at a time in 200 ticks with no experience. It
SHALL yield 2 Sulfur Dust in the main output and a 25% chance of 1 Sulfur Dust in the RNG slot, an
average of 2.25 per spike, matching 4 Sulfur Spikes crafted into 1 Sulfur that crushes into 9.

#### Scenario: Crushing a Sulfur Spike

- **WHEN** a powered Crusher has 1 Sulfur Spike in its input slot and empty output slots
- **THEN** after 200 ticks the main output holds 2 Sulfur Dust, the RNG slot holds 1 Sulfur Dust
  about one time in four, and the input is consumed

### Requirement: Crusher turns Sulfur and Potent Sulfur into Sulfur Dust

The Crusher SHALL accept Sulfur and Potent Sulfur, each processed one block at a time in 200 ticks
with no experience. Sulfur SHALL yield 9 Sulfur Dust in the main output, the same as the 9 Raw Sulfur
it uncrafts into. Potent Sulfur SHALL yield 81 Sulfur Dust, the same as the 9 Sulfur it is crafted
from: 41 in the main output and 40 in the RNG slot every time.

#### Scenario: Crushing a Sulfur block

- **WHEN** a powered Crusher has 1 Sulfur in its input slot and empty output slots
- **THEN** after 200 ticks the main output holds 9 Sulfur Dust, the RNG slot is empty, and the input
  is consumed

#### Scenario: Crushing Potent Sulfur

- **WHEN** a powered Crusher has 1 Potent Sulfur in its input slot and empty output slots
- **THEN** after 200 ticks the main output holds 41 Sulfur Dust and the RNG slot holds 40 Sulfur Dust

### Requirement: Crusher crushes vanilla Cinnabar as raw cinnabar storage

The Crusher SHALL accept vanilla Cinnabar through its raw cinnabar storage block recipe. It SHALL
yield 45 Cinnabar Dust in the main output and a 25% chance of 18 Redstone in the RNG slot, with 1 to
3 experience, the same as 9 Raw Cinnabar crushed one at a time.

#### Scenario: Crushing a Cinnabar block

- **WHEN** a powered Crusher has 1 Cinnabar in its input slot and empty output slots
- **THEN** after 200 ticks the main output holds 45 Cinnabar Dust, the RNG slot holds 18 Redstone
  about one time in four, and the input is consumed

#### Scenario: Polished Cinnabar is not raw storage

- **WHEN** a player places Polished Cinnabar in a Crusher's input slot
- **THEN** the Crusher does not start processing
