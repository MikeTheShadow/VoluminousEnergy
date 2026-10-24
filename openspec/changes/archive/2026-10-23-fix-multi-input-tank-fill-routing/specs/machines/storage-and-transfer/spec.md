## MODIFIED Requirements

### Requirement: Fluid automation follows side configuration
A neighbouring block SHALL be able to fill a machine's input tanks with fluids a recipe or the tank
allows, and drain its output tanks from the face each tank is assigned to when that side is
enabled. Input tanks SHALL be drainable only when the `Allow Extraction from Input Tanks` startup
config option is `true`.

A fill that does not name a tank SHALL go into a single input tank: the first one on that face that
holds the same fluid or is empty and accepts it. Fluid that does not fit there SHALL be refused, not
moved into another input tank. A drain that does not name a tank SHALL take the fluid from every
drainable tank on that face holding it until the requested amount is met.

#### Scenario: Pipe fills the input tank
- **WHEN** a fluid pipe connected to a Distillation Unit offers Crude Oil
- **THEN** the Distillation Unit's input tank fills with Crude Oil

#### Scenario: Pipe drains an output tank
- **WHEN** a Centrifugal Agitator's output tank holds Naphtha, its side is enabled on the east face, and a pipe on the east face pulls fluid
- **THEN** the pipe receives Naphtha and the tank amount drops by the same amount

#### Scenario: Wrong fluid is refused
- **WHEN** a pipe offers Lava to a machine whose recipes never take Lava in that tank
- **THEN** none of the Lava enters the tank

#### Scenario: A full input tank does not overflow into the next
- **WHEN** both of a Fluid Mixer's input tanks are on its bottom face, the first holds 4,000 mB of Hydrogen, the second is empty, and a pipe on the bottom face offers 1,000 mB of Hydrogen
- **THEN** none of the Hydrogen is accepted and the second input tank stays empty

#### Scenario: The second ingredient reaches the second input tank
- **WHEN** a Fluid Mixer's first input tank holds Hydrogen, its second is empty, both are on the bottom face, and a pipe on the bottom face offers 1,000 mB of Nitrogen
- **THEN** the second input tank holds 1,000 mB of Nitrogen and the first is unchanged

#### Scenario: A drain spans tanks holding the fluid
- **WHEN** two drainable tanks on one face hold 400 mB and 1,000 mB of Water and a pipe on that face pulls 1,200 mB of Water
- **THEN** the pipe receives 1,200 mB, taken from both tanks
