# machines/storage-and-transfer Specification

## Purpose

Defines how every machine's item slots, fluid tanks, and energy buffer are saved and how
neighbouring blocks such as pipes, cables, and hoppers move items, fluids, and energy in and out of
them.

## Requirements

### Requirement: Fluid automation follows side configuration
A neighbouring block SHALL be able to fill a machine's input tanks on the face each tank is assigned
to, and drain its tanks from that face when the tank's side is enabled. Input tanks SHALL be
drainable only when the `Allow Extraction from Input Tanks` startup config option is `true`. A tank
that holds a fluid SHALL refuse a different fluid, and no tank SHALL be filled past its capacity.

A fill that does not name a tank SHALL go into a single input tank: the first one on that face that
holds the same fluid or is empty and accepts it. Fluid that does not fit there SHALL be refused, not
moved into another input tank. A fill that names a tank SHALL affect only that tank, and a fill
naming an output tank SHALL accept nothing.

A drain that does not name a tank SHALL take the fluid from every drainable tank on that face
holding it, in tank order, until the requested amount is met, and SHALL take nothing from a tank
whose side is disabled. A drain that names only an amount SHALL take the first fluid on that face
it is allowed to drain, in the same way.

A fill or drain of no fluid or of 0 mB SHALL move nothing. No fill SHALL accept more than fits, and
no drain SHALL return more than the tanks hold, however large the requested amount. A fill or drain
naming a tank the face does not have SHALL move nothing.

#### Scenario: Pipe fills the input tank up to its capacity
- **WHEN** a pipe on the bottom face of an empty Distillation Unit offers 5,000 mB of Crude Oil
- **THEN** the input tank holds 4,000 mB of Crude Oil and the remaining 1,000 mB is refused

#### Scenario: A full input tank does not overflow into the next
- **WHEN** both of a Fluid Mixer's input tanks are on its bottom face, the first holds 4,000 mB of Hydrogen, the second is empty, and a pipe on the bottom face offers 1,000 mB of Hydrogen
- **THEN** none of the Hydrogen is accepted and the second input tank stays empty

#### Scenario: An offer larger than one tank does not overflow into the next
- **WHEN** both of a Fluid Mixer's input tanks are empty and on its bottom face, and a pipe on the bottom face offers 5,000 mB of Hydrogen without naming a tank
- **THEN** 4,000 mB is accepted into the first input tank, 1,000 mB is refused, and the second input tank stays empty

#### Scenario: The second ingredient reaches the second input tank
- **WHEN** a Fluid Mixer's first input tank holds Hydrogen, its second is empty, both are on the bottom face, and a pipe on the bottom face offers 1,000 mB of Nitrogen
- **THEN** the second input tank holds 1,000 mB of Nitrogen and the first is unchanged

#### Scenario: A tank one millibucket short of full takes one millibucket
- **WHEN** a Distillation Unit's input tank holds 3,999 mB of Crude Oil and a pipe on its bottom face offers 2 mB of Crude Oil
- **THEN** 1 mB is accepted, 1 mB is refused, and the tank holds 4,000 mB

#### Scenario: The largest possible offer fills only to capacity
- **WHEN** a pipe on the bottom face of an empty Distillation Unit offers 2,147,483,647 mB of Crude Oil
- **THEN** 4,000 mB is accepted and the input tank holds 4,000 mB

#### Scenario: A fill naming a tank affects only that tank
- **WHEN** both of a Fluid Mixer's input tanks are empty, a fill naming the second input tank offers 1,000 mB of Nitrogen, and a fill naming the output tank offers 1,000 mB of Ammonia
- **THEN** the second input tank holds 1,000 mB of Nitrogen, the first input tank and the output tank stay empty, and the Ammonia is refused

#### Scenario: A tank the face does not have moves nothing
- **WHEN** a Distillation Unit's input tank holds 1,000 mB of Crude Oil, and fills and drains of Crude Oil name the tank before the first and the tank after the last on its bottom face
- **THEN** every fill and drain moves nothing and the input tank still holds 1,000 mB

#### Scenario: A tank holding one fluid refuses another
- **WHEN** a Distillation Unit's input tank holds 1,000 mB of Crude Oil and a pipe on its bottom face offers Lava
- **THEN** none of the Lava is accepted and the input tank still holds 1,000 mB of Crude Oil

#### Scenario: Pipe drains an output tank
- **WHEN** a Distillation Unit's first output tank holds 1,000 mB of Naphtha, its side is enabled on the bottom face, and a pipe on the bottom face pulls 500 mB of Naphtha
- **THEN** the pipe receives 500 mB of Naphtha and the tank holds 500 mB

#### Scenario: A disabled side refuses a drain naming a fluid
- **WHEN** a Distillation Unit's first output tank holds 1,000 mB of Naphtha, its side is disabled, and a pipe on the bottom face pulls 500 mB of Naphtha
- **THEN** the pipe receives nothing and the tank still holds 1,000 mB

#### Scenario: A disabled side refuses a drain naming only an amount
- **WHEN** a Fluid Electrolyzer's first output tank holds 1,000 mB of Oxygen, its side is disabled, `Allow Extraction from Input Tanks` is `false`, and a pipe on the bottom face pulls 500 mB of any fluid
- **THEN** the pipe receives nothing and the tank still holds 1,000 mB

#### Scenario: Input tanks drain only when the config option allows it
- **WHEN** a Distillation Unit's input tank holds 1,000 mB of Crude Oil with its side enabled, and a pipe on the bottom face pulls 500 mB of Crude Oil, first with `Allow Extraction from Input Tanks` set to `false` and then to `true`
- **THEN** the first pull receives nothing and the second receives 500 mB

#### Scenario: A drain naming a fluid finds the tank that holds it
- **WHEN** a Fluid Electrolyzer's first output tank holds 1,000 mB of Oxygen and its second holds 1,000 mB of Hydrogen, both sides are enabled, and a pipe on the bottom face pulls 600 mB of Hydrogen
- **THEN** the pipe receives 600 mB of Hydrogen, the second output tank holds 400 mB, and the Oxygen is untouched

#### Scenario: A drain larger than the tank returns what the tank holds
- **WHEN** a Fluid Electrolyzer's first output tank holds 1,000 mB of Oxygen with its side enabled, and a pipe on the bottom face pulls 1,500 mB of Oxygen
- **THEN** the pipe receives 1,000 mB of Oxygen and the tank is empty

#### Scenario: A drain spans tanks holding the fluid
- **WHEN** a Fluid Electrolyzer's two output tanks hold 400 mB and 1,000 mB of Water, both sides are enabled on the bottom face, and a pipe on the bottom face pulls 1,200 mB of Water
- **THEN** the pipe receives 1,200 mB of Water, the first output tank is empty, and the second holds 200 mB

#### Scenario: A drain naming only an amount skips input tanks the option protects
- **WHEN** a Fluid Electrolyzer's input tank holds 1,000 mB of Water and its output tanks hold Oxygen and Hydrogen, every side is enabled, `Allow Extraction from Input Tanks` is `false`, and a pipe on the bottom face pulls 500 mB of any fluid
- **THEN** the pipe receives 500 mB of Oxygen and the Water and Hydrogen are untouched

#### Scenario: Empty requests move nothing
- **WHEN** a pipe on the bottom face of a Distillation Unit whose tanks hold Crude Oil and Naphtha offers no fluid, offers 0 mB of Crude Oil, and pulls 0 mB
- **THEN** nothing is accepted or received and both tanks are unchanged

### Requirement: Simulated transfers do not change machines
A transfer that a neighbouring block only simulates or cancels SHALL leave the machine's tanks
exactly as they were, and a simulation SHALL report the amount the same transfer would move if
carried out. This SHALL hold when a step inside the transfer was confirmed before the transfer as a
whole was cancelled.

#### Scenario: Pipe probes a fill without moving
- **WHEN** a pipe on the bottom face of an empty Fluid Mixer simulates offering 1,000 mB of Hydrogen
- **THEN** the simulation reports 1,000 mB and both input tanks stay empty

#### Scenario: Pipe probes a drain without moving
- **WHEN** a Distillation Unit's first output tank holds 1,000 mB of Naphtha with its side enabled, and a pipe on the bottom face simulates pulling 500 mB of Naphtha
- **THEN** the simulation reports 500 mB of Naphtha and the tank still holds 1,000 mB

#### Scenario: A cancelled fill leaves the tanks unchanged
- **WHEN** a pipe on the bottom face of an empty Fluid Mixer offers 5,000 mB of Hydrogen and then cancels the transfer
- **THEN** both input tanks stay empty

#### Scenario: A confirmed step inside a cancelled transfer leaves the tanks unchanged
- **WHEN** a pipe on the bottom face of an empty Fluid Mixer offers 1,000 mB of Hydrogen in an inner step it confirms, and then cancels the transfer around it
- **THEN** both input tanks stay empty
