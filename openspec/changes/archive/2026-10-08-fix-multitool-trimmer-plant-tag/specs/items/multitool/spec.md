## Purpose

Defines which bit a multitool selects for the block it targets or breaks, which in turn sets the
model the player sees in hand and the mining speed applied.

## ADDED Requirements

### Requirement: Trimmer bit is selected for shears-harvested plants

A multitool that holds a trimmer bit SHALL select the trimmer bit when the player targets or breaks
any block in the `voluminousenergy:mineable/trimmer` block tag. That tag SHALL include every vanilla
plant whose loot table yields the block itself only when broken with shears: leaves, cobweb, vines,
glow lichen, hanging roots, seagrass, tall seagrass, short grass, tall grass, fern, large fern, dead
bush, small dripleaf, nether sprouts, twisting vines, and weeping vines.

#### Scenario: Targeting Large Fern shows the trimmer

- **WHEN** a player holds a multitool containing an iron trimmer bit and looks at a Large Fern
- **THEN** the held multitool renders with the iron trimmer bit model, as it does for oak leaves

#### Scenario: Targeting Small Dripleaf shows the trimmer

- **WHEN** a player holds a multitool containing an iron trimmer bit and looks at a Small Dripleaf
- **THEN** the held multitool renders with the iron trimmer bit model

#### Scenario: Multitool without a trimmer bit is unchanged

- **WHEN** a player holds a multitool containing only a drill bit and looks at a Large Fern
- **THEN** the held multitool renders as it did before this change, with no trimmer model
