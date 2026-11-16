## Purpose

Defines how a recipe's fluid ingredients, the `fluid_ingredients` list shared by every recipe type that
consumes fluids, appear to datapacks and tools, starting with how a loaded recipe is written back to JSON.

## ADDED Requirements

### Requirement: Fluid ingredients encode to the amount they were loaded with

Encoding a loaded recipe that has `fluid_ingredients` through its recipe codec SHALL write each fluid
ingredient's own `amount` alongside its `tag` or `fluid`. Decoding that output SHALL give a recipe whose
fluid ingredients have the same tag or fluid and the same amount, in the same order, as the loaded one.
This SHALL hold for every recipe type with a `fluid_ingredients` list: aqueoulizing, centrifugal
agitating, distilling, fluid electrolyzing, fluid mixing, fuel combustion, and hydroponic incubating.

#### Scenario: Shipped crude oil distilling recipe

- **WHEN** the shipped `voluminousenergy:distilling/crude_oil` recipe is encoded to JSON
- **THEN** its single fluid ingredient is written with tag `c:crude_oil` and amount 250, not 1

#### Scenario: Every shipped recipe with fluid ingredients round-trips

- **WHEN** each shipped recipe that has fluid ingredients is encoded and the result decoded again
- **THEN** the decoded recipe lists the same fluid ingredients, each with the same tag or fluid and amount

### Requirement: Building a recipe outside a reload does not change the recipes machines use

The recipes a machine or JEI looks up SHALL come only from the recipe manager's loaded recipes, refreshed
on server start, on reload, and when a client receives recipes. Decoding a recipe from JSON or from its
network form outside those events, such as by a tool, a datapack validator, or a test, SHALL NOT add it
to, or remove anything from, the recipes machines look up.

#### Scenario: Decoding a distilling recipe on a running server

- **WHEN** a distilling recipe is decoded from JSON and sent through its network form on a running server
- **THEN** the distilling recipes the Distillation Unit can match are the same before and after
