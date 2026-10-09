# items/multitool Specification

## Purpose

Defines which bit a multitool selects for the block it targets or breaks, which in turn sets the
model the player sees in hand and the mining speed applied.

## Requirements

### Requirement: Trimmer bit is selected for shears-harvested plants

A multitool that holds a trimmer bit SHALL select the trimmer bit when the player targets or breaks
any block in the `voluminousenergy:mineable/trimmer` block tag. That tag SHALL include every vanilla
plant whose loot table yields the block itself only when broken with shears: leaves, cobweb, vines,
glow lichen, hanging roots, seagrass, tall seagrass, short grass, tall grass, fern, large fern, dead
bush, bush, short dry grass, tall dry grass, pale hanging moss, small dripleaf, nether sprouts,
twisting vines, and weeping vines.

#### Scenario: Targeting Tall Dry Grass shows the trimmer

- **WHEN** a player holds a multitool containing an iron trimmer bit and looks at Tall Dry Grass
  placed on sand
- **THEN** the held multitool renders with the iron trimmer bit model

#### Scenario: Other shears-harvested plants behave like leaves

- **WHEN** a player holding a multitool with a trimmer bit targets Short Dry Grass, Bush, Large
  Fern, Pale Hanging Moss, or Small Dripleaf
- **THEN** the held multitool renders with the trimmer bit model, as it does for oak leaves

#### Scenario: Multitool without a trimmer bit is unchanged

- **WHEN** a player holds a multitool containing only a drill bit and looks at Tall Dry Grass
- **THEN** the held multitool renders as it did before this change, with no trimmer model

### Requirement: Trimmer bit covers bush and ground foliage

The `voluminousenergy:mineable/trimmer` tag SHALL also include firefly bush and leaf litter, so a
multitool with a trimmer bit selects the trimmer for them. The tag SHALL NOT add flowers, saplings,
or moss blocks and carpets.

#### Scenario: Targeting Firefly Bush or Leaf Litter shows the trimmer

- **WHEN** a player holding a multitool with a trimmer bit looks at a Firefly Bush or at Leaf Litter
  placed on grass
- **THEN** the held multitool renders with the trimmer bit model

#### Scenario: Flowers and pale moss do not select the trimmer

- **WHEN** a player holding a multitool with only a trimmer bit looks at an Open Eyeblossom or at a
  Pale Moss Carpet
- **THEN** the held multitool does not render the trimmer bit model
