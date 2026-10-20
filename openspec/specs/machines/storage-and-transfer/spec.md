# machines/storage-and-transfer Specification

## Purpose

Defines how every machine's item slots, fluid tanks, and energy buffer are saved and how
neighbouring blocks such as pipes, cables, and hoppers move items, fluids, and energy in and out of
them.

## Requirements

### Requirement: Machine contents survive saving and upgrading
A machine's item slots, tank contents, stored energy, side configuration, and upgrade slot SHALL be
restored exactly when its chunk is saved and loaded again. A machine placed and filled on the 26.2
build of the mod SHALL keep its slot contents, tank contents, and stored energy when the world is
opened with the 26.3 build.

#### Scenario: Upgrading a world from 26.2
- **WHEN** a Crusher holding 32 Iron Ore in its input slot and 10 000 FE is saved on the 26.2 build, and the world is opened with the 26.3 build
- **THEN** the Crusher still holds 32 Iron Ore in its input slot and 10 000 FE

#### Scenario: Upgrading a machine with tanks
- **WHEN** a Centrifugal Agitator whose input tank holds 4 000 mB of Crude Oil is saved on the 26.2 build, and the world is opened with the 26.3 build
- **THEN** its input tank still holds 4 000 mB of Crude Oil

#### Scenario: Reloading on 26.3
- **WHEN** a machine with items, fluid, and energy is saved and reloaded on the 26.3 build
- **THEN** its items, fluid, energy, and side settings are the same as before saving

### Requirement: Item automation follows side configuration
A neighbouring block SHALL be able to insert items only into a machine's enabled input slots on the
face those slots are assigned to, and extract items only from enabled output slots on their face.
Slot rules still apply: an item a slot would not accept by hand SHALL NOT be inserted.

#### Scenario: Hopper feeds the input side
- **WHEN** a Crusher's input slot is enabled on its top face and a hopper above it holds Iron Ore
- **THEN** the hopper moves Iron Ore into the Crusher's input slot

#### Scenario: Disabled side refuses items
- **WHEN** a Crusher's input slot is disabled and a hopper above it holds Iron Ore
- **THEN** the hopper moves nothing into the Crusher

#### Scenario: Output side cannot be filled
- **WHEN** a pipe on a Crusher's output face tries to insert Cobblestone
- **THEN** nothing is inserted

### Requirement: Fluid automation follows side configuration
A neighbouring block SHALL be able to fill a machine's input tanks with fluids a recipe or the tank
allows, and drain its output tanks from the face each tank is assigned to when that side is
enabled. Input tanks SHALL be drainable only when the `Allow Extraction from Input Tanks` startup
config option is `true`.

#### Scenario: Pipe fills the input tank
- **WHEN** a fluid pipe connected to a Distillation Unit offers Crude Oil
- **THEN** the Distillation Unit's input tank fills with Crude Oil

#### Scenario: Pipe drains an output tank
- **WHEN** a Centrifugal Agitator's output tank holds Naphtha, its side is enabled on the east face, and a pipe on the east face pulls fluid
- **THEN** the pipe receives Naphtha and the tank amount drops by the same amount

#### Scenario: Wrong fluid is refused
- **WHEN** a pipe offers Lava to a machine whose recipes never take Lava in that tank
- **THEN** none of the Lava enters the tank

### Requirement: Energy automation reaches every machine with an energy buffer
Cables from other mods SHALL be able to insert energy into and extract energy from a machine's
energy buffer, no faster per tick than the machine's configured transfer rate and never beyond its
capacity or below zero.

#### Scenario: Cable charges a machine
- **WHEN** a cable delivering 1 000 FE per tick connects to an empty Electric Furnace
- **THEN** the Electric Furnace's stored energy rises each tick until it is full

### Requirement: Simulated transfers do not change machines
A transfer that a neighbouring block only simulates, or that it cancels before completion, SHALL
leave the machine's slots, tanks, and energy exactly as they were.

#### Scenario: Pipe probes without moving
- **WHEN** a pipe checks how much Crude Oil a machine's input tank would accept but cancels the transfer
- **THEN** the tank's contents are unchanged

### Requirement: Batteries and buckets keep working with machines
A battery in a machine's battery or upgrade slot SHALL charge or discharge as on 26.2. Buckets and
fluid containers placed in a machine's fluid input or output slot SHALL fill or empty the matching
tank as on 26.2.

#### Scenario: Emptying a bucket into a tank
- **WHEN** a Crude Oil Bucket is placed in a Distillation Unit's fluid input slot and the tank has room
- **THEN** the tank gains 1 000 mB of Crude Oil and an empty bucket appears in the fluid output slot
