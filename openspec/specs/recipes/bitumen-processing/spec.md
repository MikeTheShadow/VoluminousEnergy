# recipes/bitumen-processing Specification

## Purpose

Defines the machine recipes that turn Bitumen back into crude oil and break the bituminous blocks
down into Bitumen and their host material.

## Requirements

### Requirement: The Centrifugal Separator turns Bitumen into crude oil

The Centrifugal Separator SHALL accept 4 items tagged `c:bitumen` in its input slot and one empty
bucket in its bucket slot. Each completed operation SHALL consume both and produce one Crude Oil
bucket and 2 Sand Dust every time, and 1 Carbon Dust with a 25% chance. The recipe SHALL take the
same base processing time as the other Centrifugal Separator recipes.

#### Scenario: Separating Bitumen

- **WHEN** a powered Centrifugal Separator holds 4 Bitumen in its input slot and an empty bucket in
  its bucket slot
- **THEN** it consumes the 4 Bitumen and the bucket, and outputs a Crude Oil bucket and 2 Sand Dust

#### Scenario: Carbon Dust is chanced

- **WHEN** a powered Centrifugal Separator completes many Bitumen operations
- **THEN** roughly one operation in four also outputs 1 Carbon Dust

#### Scenario: No bucket

- **WHEN** a Centrifugal Separator holds 4 Bitumen and its bucket slot is empty
- **THEN** it does not start the Bitumen recipe

#### Scenario: Too little Bitumen

- **WHEN** a Centrifugal Separator holds 3 Bitumen and an empty bucket
- **THEN** it does not start the Bitumen recipe

### Requirement: The Crusher breaks bituminous sands into Bitumen and Sand Dust

The Crusher SHALL accept one Bituminous Sand or one Bituminous Red Sand and produce 5 Bitumen and 3
Sand Dust every time.

#### Scenario: Crushing Bituminous Sand

- **WHEN** a powered Crusher holds one Bituminous Sand
- **THEN** it consumes the block and outputs 5 Bitumen and 3 Sand Dust

#### Scenario: Crushing Bituminous Red Sand

- **WHEN** a powered Crusher holds one Bituminous Red Sand
- **THEN** it consumes the block and outputs 5 Bitumen and 3 Sand Dust

### Requirement: The Crusher breaks Bituminous Gravel into Bitumen and sometimes Gravel

The Crusher SHALL accept one Bituminous Gravel and produce 5 Bitumen every time and one Gravel with a
25% chance.

#### Scenario: Crushing Bituminous Gravel

- **WHEN** a powered Crusher holds one Bituminous Gravel
- **THEN** it consumes the block and outputs 5 Bitumen

#### Scenario: Gravel is chanced

- **WHEN** a powered Crusher crushes many Bituminous Gravel blocks
- **THEN** roughly one block in four also yields one Gravel

### Requirement: Bitumen recipes appear in JEI

When JEI is installed, the Bitumen separation recipe SHALL appear in the Centrifugal Separator
category and the three bituminous crushing recipes in the Crusher category, with their chanced
outputs marked as chanced.

#### Scenario: Looking up Bitumen uses

- **WHEN** a player with JEI installed looks up the uses of Bitumen
- **THEN** JEI lists the Centrifugal Separator recipe that produces a Crude Oil bucket
