## Purpose

Defines the shape of overworld oil geysers: a crude oil column rising to the surface and an
underground reservoir around its base, both continuous.

## ADDED Requirements

### Requirement: Geyser reservoir has no uncut layers
An oil geyser's underground reservoir SHALL be continuous: every horizontal layer of the reservoir,
its widest layer included, SHALL be filled with crude oil out to the reservoir's radius at that
layer, wherever the existing block is one the geyser may replace (air, dirt, base stone, or the
geyser's allow list). No horizontal disc or ring of the original terrain SHALL remain inside the
reservoir.

#### Scenario: Cross-section through the widest layer
- **WHEN** a player drains or tunnels through an oil geyser's reservoir at its widest layer
- **THEN** that layer is crude oil across its full width, the same as the layers directly above and below it

#### Scenario: Vertical cut through the reservoir
- **WHEN** a player looks at a vertical cross-section of the reservoir away from the central column
- **THEN** oil is continuous from the bottom of the reservoir to its top with no one-block stone band

### Requirement: Geyser placement is otherwise unchanged
Apart from the reservoir fix, oil geysers SHALL keep their current height, column width, frequency,
and biome placement.

#### Scenario: Geyser on the surface
- **WHEN** an oil geyser generates in a new overworld chunk
- **THEN** its above-ground column looks the same as before this change
