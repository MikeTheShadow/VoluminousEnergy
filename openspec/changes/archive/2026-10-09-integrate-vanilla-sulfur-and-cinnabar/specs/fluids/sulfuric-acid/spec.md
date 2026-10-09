## Purpose

Defines how Sulfuric Acid looks as a placed fluid, inside machine and tank GUIs, and as a bucket.

## ADDED Requirements

### Requirement: Sulfuric Acid is pale yellow-beige

Sulfuric Acid's still and flowing textures, and the fluid in the Sulfuric Acid Bucket texture,
SHALL use the pale yellow-beige palette of vanilla's Sulfur Cube bucket and Sulfur Cube spawn egg
instead of grey. The bucket's metal SHALL stay identical to the vanilla bucket.

#### Scenario: Placed fluid

- **WHEN** a player empties a Sulfuric Acid Bucket into the world
- **THEN** the source and flowing blocks are pale yellow-beige

#### Scenario: Fluid in a machine tank

- **WHEN** an Aqueoulizer's output tank holds Sulfuric Acid
- **THEN** the tank in the GUI shows the same pale yellow-beige

#### Scenario: Bucket next to the Sulfur Cube bucket

- **WHEN** a player holds a Sulfuric Acid Bucket next to a Bucket of Sulfur Cube
- **THEN** the liquid in both buckets has matching colours
