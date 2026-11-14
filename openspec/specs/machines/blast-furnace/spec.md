# machines/blast-furnace Specification

## Purpose

Defines the Blast Furnace's heat-source rules (which fluids are hot enough and how much one operation
needs and consumes) and what JEI shows players about them.

## Requirements

### Requirement: JEI shows the heat-source fluid consumed per operation

When JEI is installed, each Blast Furnace page SHALL print the mB of heat-source fluid one operation of
that page's recipe consumes under the heat fluid tank, and SHALL list its hot-enough fluids in the heat
slot. The value SHALL come from the recipe the client received from the server, not from any local
config. A recipe that consumes 0 mB SHALL print 0 and still list its hot-enough fluids.

#### Scenario: Default heat source consumption

- **WHEN** a player opens the steel Blast Furnace page in JEI with no datapack overrides
- **THEN** "mB:" is followed by 50 under the heat fluid tank
- **AND** hovering lava in the heat slot shows 50 mB

#### Scenario: Recipes with different consumption

- **WHEN** a datapack sets the tungsten steel recipe's `heat_source_consumption` to 250 and leaves steel
  at 50
- **THEN** the tungsten steel page prints "mB:" followed by 250
- **AND** the steel page prints "mB:" followed by 50

#### Scenario: Recipe without consumption

- **WHEN** a datapack adds a 900 K blasting recipe with no `heat_source_consumption`
- **THEN** its page prints "mB:" followed by 0
- **AND** its heat slot lists lava

### Requirement: A fluid is hot enough when it reaches the recipe's minimum heat

A heat-source fluid SHALL be hot enough for an `industrial_blasting` recipe when its fluid temperature is
at or above that recipe's `minimum_heat_kelvin`. The Blast Furnace and the JEI page SHALL apply the same
rule.

#### Scenario: Fluid exactly at the threshold

- **WHEN** a datapack recipe has `minimum_heat_kelvin` 1300 and the heat tank holds lava (1300 K)
- **THEN** the Blast Furnace processes the recipe
- **AND** the recipe's JEI page lists lava as a heat-source fluid

#### Scenario: Fluid below the threshold

- **WHEN** the heat tank holds water (300 K) and the recipe needs 900 K
- **THEN** the Blast Furnace does not process the recipe

### Requirement: JEI lists each recipe's own heat-source fluids

When JEI is installed, each Blast Furnace page SHALL list exactly the fluids that are hot enough for that
page's recipe, independent of which other blasting recipes exist or the order JEI shows them in.

#### Scenario: Recipes with different minimum heat

- **WHEN** a datapack adds an `industrial_blasting` recipe with `minimum_heat_kelvin` 1500 beside the
  shipped 900 K steel recipe
- **THEN** the steel page lists lava
- **AND** the 1500 K page does not list lava
- **AND** both pages show the same lists whichever one the player opens first

### Requirement: Blasting recipes declare their heat-source consumption

An `industrial_blasting` recipe SHALL accept an optional integer `heat_source_consumption` field giving
the mB of heat-source fluid one operation of that recipe needs and consumes. When the field is absent
the recipe SHALL consume 0 mB. A value below 0 or above 4000 SHALL make that recipe fail to load, with
the error logged, while other recipes keep loading. Every shipped `industrial_blasting` recipe SHALL set
the field to 50.

#### Scenario: Field omitted

- **WHEN** a datapack adds an `industrial_blasting` recipe with no `heat_source_consumption` field
- **THEN** the recipe loads and one operation of it consumes no heat-source fluid

#### Scenario: Field set to zero

- **WHEN** a datapack adds an `industrial_blasting` recipe with `heat_source_consumption` 0
- **THEN** the recipe loads and behaves exactly as if the field were omitted

#### Scenario: Field out of range

- **WHEN** a datapack adds an `industrial_blasting` recipe with `heat_source_consumption` -1
- **THEN** that recipe does not load and the server log names it with a decoding error
- **AND** the shipped blasting recipes still load

#### Scenario: Shipped recipes keep their cost

- **WHEN** a player runs any shipped Blast Furnace recipe with no datapack overrides
- **THEN** one operation consumes 50 mB of heat-source fluid

### Requirement: Each operation needs and consumes the recipe's heat-source amount

A blasting operation SHALL start only when the heat tank holds a hot-enough fluid, at least the running
recipe's `heat_source_consumption` of it, and SHALL drain exactly that amount when the operation
completes. No config value SHALL change this amount.

#### Scenario: Shipped steel recipe

- **WHEN** the heat tank holds 1000 mB of lava and a steel operation completes
- **THEN** the heat tank holds 950 mB of lava

#### Scenario: Not enough heat fluid

- **WHEN** the heat tank holds 40 mB of lava
- **THEN** the Blast Furnace does not start a steel operation

#### Scenario: Recipe with a higher consumption

- **WHEN** a datapack sets a blasting recipe's `heat_source_consumption` to 250 and an operation of it
  completes with 1000 mB of lava in the tank
- **THEN** the heat tank holds 750 mB of lava

#### Scenario: Higher consumption not met

- **WHEN** a recipe's `heat_source_consumption` is 250 and the heat tank holds 200 mB of lava
- **THEN** the Blast Furnace does not start that recipe

### Requirement: A recipe without consumption needs only the minimum heat

When a blasting recipe's `heat_source_consumption` is 0, the Blast Furnace SHALL run it whenever the
heat tank holds any amount of a hot-enough fluid, and SHALL leave the tank unchanged when the operation
completes. Hot-enough fluids SHALL still be accepted into the heat tank and its bucket slot for such a
recipe.

#### Scenario: Heat present, nothing drained

- **WHEN** a recipe has no `heat_source_consumption`, needs 900 K, and the heat tank holds 10 mB of lava
- **THEN** the Blast Furnace runs the recipe
- **AND** the heat tank still holds 10 mB of lava after the operation completes

#### Scenario: No heat fluid

- **WHEN** a recipe has no `heat_source_consumption` and the heat tank is empty
- **THEN** the Blast Furnace does not start the recipe

#### Scenario: Filling the tank for a recipe without consumption

- **WHEN** a recipe has no `heat_source_consumption` and a player puts a lava bucket in the heat
  fluid's bucket slot
- **THEN** the bucket is accepted
