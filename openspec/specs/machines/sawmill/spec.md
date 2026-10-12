# machines/sawmill Specification

## Purpose

Defines what the Sawmill shows players about its recipes, starting with the fluid each sawing operation
produces.

## Requirements

### Requirement: JEI shows the fluid produced per operation

When JEI is installed, the Sawmill category SHALL print the mB of fluid one operation produces under the
fluid output tank: the recipe's own amount, or the "Fluid Amount" config value for logs sawn without a
recipe.

#### Scenario: Log without a recipe

- **WHEN** a player opens the generic log page of the Sawmill in JEI with default config
- **THEN** "mB:" is followed by 250 under the fluid output tank

#### Scenario: Bamboo

- **WHEN** a player opens the Sawmill bamboo page in JEI
- **THEN** "mB:" is followed by 25 under the fluid output tank
