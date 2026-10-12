# gases Specification

## Purpose

Covers how the mod's gases (Natural Gas, Hydrogen, Oxygen, Nitrogen, Ammonia, and Light Fuel) appear in
the world once placed or generated, and how they interact with blocks, fluids, and growth around them.

## Requirements

### Requirement: Gases render in the world
Every gas SHALL be drawn in the world with its own still and flowing textures and colour, whether a
player placed it or world generation did. A gas SHALL render the same way in existing worlds as in
new ones.

#### Scenario: Emptying a gas bucket
- **WHEN** a player empties a Hydrogen, Oxygen, Nitrogen, Ammonia, Light Fuel, or Natural Gas Bucket onto a stone floor in open air
- **THEN** the rising gas column is visible in that gas's colour

#### Scenario: Natural gas cave
- **WHEN** a player breaks into a natural gas petroleum cave or a natural gas reservoir
- **THEN** the cave's gas is visible as purple Natural Gas, not as an empty cave

#### Scenario: Gas saved before the fix
- **WHEN** a world containing generated or placed gas is opened with this version
- **THEN** that gas is visible without regenerating chunks or replacing blocks

### Requirement: Gases are replaceable
Placing a block or emptying a bucket at a position holding gas SHALL put that block or fluid there,
and falling blocks SHALL drop through gas as they do through air. Gas SHALL make no sound and leave no
break particles when it is replaced this way.

#### Scenario: Placing a block into gas
- **WHEN** a player places stone at a position filled with Natural Gas
- **THEN** the stone is placed there, taking the gas's place

#### Scenario: Emptying water into gas
- **WHEN** a player empties a water bucket at a position filled with Hydrogen
- **THEN** a water source takes the Hydrogen's place, with only the bucket's own sound

#### Scenario: Emptying a gas bucket into the same gas
- **WHEN** a player empties a Nitrogen Bucket into the side of a rising Nitrogen column
- **THEN** no block break sound or particles play

#### Scenario: Sand falling through gas
- **WHEN** sand is placed above a column of Oxygen
- **THEN** the sand falls through the Oxygen and lands on the first solid block below it

### Requirement: Gas columns rise to the build height cap
A gas column in open air SHALL keep rising until something blocks it or it reaches y 320, wherever the
16-block chunk sections above it are empty.

#### Scenario: Column in open sky
- **WHEN** a player empties an Oxygen Bucket on a stone floor at y 70 with open sky above
- **THEN** the Oxygen column rises past y 80 and keeps rising until y 320

#### Scenario: Column under a ceiling
- **WHEN** a player empties a Hydrogen Bucket in a cave with a stone ceiling five blocks up
- **THEN** the Hydrogen rises to the ceiling and spreads beneath it

### Requirement: Gases are not empty space
A gas SHALL NOT be treated as open air by vanilla growth or world decoration. Plants, vines, lichen,
dripstone, and fire that only grow or place into air SHALL NOT grow or place into a gas, and world
generation SHALL NOT decorate the inside of a gas-filled cave as if it were open air.

#### Scenario: Cave decoration in a natural gas cave
- **WHEN** a new chunk generates a natural gas petroleum cave through a lush or dripstone cave biome
- **THEN** no glow lichen, cave vines, or pointed dripstone generate inside the gas, and the gas fills the cave volume

#### Scenario: Bone meal next to gas
- **WHEN** a player uses bone meal on a grass block whose surrounding air has been filled with Nitrogen
- **THEN** no grass or flowers grow into the positions holding Nitrogen
