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
holds the same fluid or is empty. Fluid that does not fit there SHALL be refused, not moved into
another input tank.

From an enabled side, a drain SHALL take from a single tank per request, up to what that tank
holds. A drain that names a fluid SHALL take it from the first tank on that face holding it, and
SHALL take nothing from a tank whose side is disabled. A drain that names only an amount SHALL take
from the first tank on that face that holds any fluid it is allowed to drain. A fill or drain of no
fluid or of 0 mB SHALL move nothing.

#### Scenario: Pipe fills the input tank up to its capacity
- **WHEN** a pipe on the bottom face of an empty Distillation Unit offers 5 000 mB of Crude Oil
- **THEN** the input tank holds 4 000 mB of Crude Oil and the remaining 1 000 mB is refused

#### Scenario: A full input tank does not overflow into the next
- **WHEN** both of a Fluid Mixer's input tanks are on its bottom face, the first holds 4 000 mB of Hydrogen, the second is empty, and a pipe on the bottom face offers 1 000 mB of Hydrogen
- **THEN** none of the Hydrogen is accepted and the second input tank stays empty

#### Scenario: The second ingredient reaches the second input tank
- **WHEN** a Fluid Mixer's first input tank holds Hydrogen, its second is empty, both are on the bottom face, and a pipe on the bottom face offers 1 000 mB of Nitrogen
- **THEN** the second input tank holds 1 000 mB of Nitrogen and the first is unchanged

#### Scenario: A tank holding one fluid refuses another
- **WHEN** a Distillation Unit's input tank holds 1 000 mB of Crude Oil and a pipe on its bottom face offers Lava
- **THEN** none of the Lava is accepted and the input tank still holds 1 000 mB of Crude Oil

#### Scenario: Pipe drains an output tank
- **WHEN** a Distillation Unit's first output tank holds 1 000 mB of Naphtha, its side is enabled on the bottom face, and a pipe on the bottom face pulls 500 mB of Naphtha
- **THEN** the pipe receives 500 mB of Naphtha and the tank holds 500 mB

#### Scenario: A disabled side refuses a drain naming a fluid
- **WHEN** a Distillation Unit's first output tank holds 1 000 mB of Naphtha, its side is disabled, and a pipe on the bottom face pulls 500 mB of Naphtha
- **THEN** the pipe receives nothing and the tank still holds 1 000 mB

#### Scenario: Input tanks drain only when the config option allows it
- **WHEN** a Distillation Unit's input tank holds 1 000 mB of Crude Oil with its side enabled, and a pipe on the bottom face pulls 500 mB of Crude Oil, first with `Allow Extraction from Input Tanks` set to `false` and then to `true`
- **THEN** the first pull receives nothing and the second receives 500 mB

#### Scenario: A drain naming a fluid finds the tank that holds it
- **WHEN** a Fluid Electrolyzer's first output tank holds 1 000 mB of Oxygen and its second holds 1 000 mB of Hydrogen, both sides are enabled, and a pipe on the bottom face pulls 600 mB of Hydrogen
- **THEN** the pipe receives 600 mB of Hydrogen, the second output tank holds 400 mB, and the Oxygen is untouched

#### Scenario: A drain larger than the tank returns what the tank holds
- **WHEN** a Fluid Electrolyzer's first output tank holds 1 000 mB of Oxygen with its side enabled, and a pipe on the bottom face pulls 1 500 mB of Oxygen
- **THEN** the pipe receives 1 000 mB of Oxygen and the tank is empty

#### Scenario: A drain naming only an amount skips input tanks the option protects
- **WHEN** a Fluid Electrolyzer's input tank holds 1 000 mB of Water and its output tanks hold Oxygen and Hydrogen, every side is enabled, `Allow Extraction from Input Tanks` is `false`, and a pipe on the bottom face pulls 500 mB of any fluid
- **THEN** the pipe receives 500 mB of Oxygen and the Water and Hydrogen are untouched

#### Scenario: Empty requests move nothing
- **WHEN** a pipe on the bottom face of a Distillation Unit whose tanks hold Crude Oil and Naphtha offers no fluid, offers 0 mB of Crude Oil, and pulls 0 mB
- **THEN** nothing is accepted or received and both tanks are unchanged

### Requirement: Simulated transfers do not change machines
A transfer that a neighbouring block only simulates SHALL leave the machine's tanks exactly as they
were, and SHALL report the amount the same transfer would move if carried out.

#### Scenario: Pipe probes a fill without moving
- **WHEN** a pipe on the bottom face of an empty Fluid Mixer simulates offering 1 000 mB of Hydrogen
- **THEN** the simulation reports 1 000 mB and both input tanks stay empty

#### Scenario: Pipe probes a drain without moving
- **WHEN** a Distillation Unit's first output tank holds 1 000 mB of Naphtha with its side enabled, and a pipe on the bottom face simulates pulling 500 mB of Naphtha
- **THEN** the simulation reports 500 mB of Naphtha and the tank still holds 1 000 mB
