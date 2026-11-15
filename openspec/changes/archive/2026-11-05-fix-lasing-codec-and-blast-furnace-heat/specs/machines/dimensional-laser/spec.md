## Purpose

Defines the Dimensional Laser's `dimensional_lasing` recipes as datapacks and tools see them, starting
with how a loaded recipe is written back to its JSON form.

## ADDED Requirements

### Requirement: Dimensional lasing recipes encode to the JSON they were loaded from

Encoding a loaded `dimensional_lasing` recipe through its recipe codec SHALL succeed and SHALL produce a
`region_fluid` object (fluid, minimum, maximum) and a `climate` object whose `continentalness`,
`erosion`, `humidity`, and `temperature` ranges each carry that recipe's own minimum and maximum.
Decoding that output SHALL give a recipe with the same region fluid, amounts, and climate ranges.

#### Scenario: Shipped plains crude oil recipe

- **WHEN** the shipped `voluminousenergy:dimensional_lasing/crude_oil_plains` recipe is encoded to JSON
- **THEN** encoding does not throw
- **AND** `region_fluid` is `voluminousenergy:crude_oil` with minimum 48 and maximum 128
- **AND** `erosion` is -0.5 to 0.25, `humidity` is -0.3 to -0.15, and `temperature` is -0.15 to 0.05,
  not the continentalness range 0.25 to 0.4

#### Scenario: Every shipped recipe round-trips

- **WHEN** each shipped `dimensional_lasing` recipe is encoded and the result decoded again
- **THEN** the decoded recipe has the same region fluid, minimum and maximum amount, and all eight
  climate bounds as the loaded one
