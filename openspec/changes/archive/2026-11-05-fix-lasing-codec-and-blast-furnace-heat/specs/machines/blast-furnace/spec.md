## ADDED Requirements

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

### Requirement: Each operation needs and consumes the configured heat-source amount

A blasting operation SHALL start only when the heat tank holds at least the "Heat Source Consumption"
config value of a hot-enough fluid, and SHALL drain exactly that amount when the operation completes.

#### Scenario: Default config

- **WHEN** "Heat Source Consumption" is 50, the heat tank holds 1000 mB of lava, and a steel operation
  completes
- **THEN** the heat tank holds 950 mB of lava

#### Scenario: Not enough heat fluid

- **WHEN** "Heat Source Consumption" is 50 and the heat tank holds 40 mB of lava
- **THEN** the Blast Furnace does not start a steel operation

#### Scenario: Edited config

- **WHEN** "Heat Source Consumption" is set to 250 and a steel operation completes with 1000 mB of lava
  in the tank
- **THEN** the heat tank holds 750 mB of lava
- **AND** the JEI page prints "mB:" followed by 250 under the heat fluid tank
