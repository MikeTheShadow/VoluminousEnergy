# items/solid-fuels Specification

## Purpose

Defines how long each of the mod's solid fuels burns in vanilla furnaces, smokers, and blast
furnaces, and in anything else that reads vanilla's cooking fuel value.

## Requirements

### Requirement: Solid fuels burn for fixed durations
Each solid fuel SHALL burn for the same number of ticks as on 26.2. In a furnace these are Petcoke
4000, Coal Coke 3200, Rosin 800, and Animal Fat, Tiny Coal, Tiny Charcoal, Tiny Coal Coke, Tiny
Petcoke, and Tiny Rosin 200 each. A smoker or blast furnace burns each of them for half as long,
as it does vanilla fuels.

#### Scenario: Petcoke in a furnace
- **WHEN** a player puts one Petcoke in an empty furnace's fuel slot and Iron Ore in its input slot
- **THEN** the furnace burns for 4000 ticks, smelting 20 Iron Ore

#### Scenario: Petcoke in a blast furnace
- **WHEN** a player puts one Petcoke in an empty blast furnace's fuel slot and Iron Ore in its input
  slot
- **THEN** the blast furnace burns for 2000 ticks, smelting 20 Iron Ore

#### Scenario: Tiny fuel smelts one item
- **WHEN** a player puts one Tiny Coal in a furnace with Raw Copper in its input slot
- **THEN** the furnace burns for 200 ticks and smelts one Raw Copper

### Requirement: Solid fuels are accepted where vanilla fuels are
Every solid fuel SHALL be placeable in a furnace's fuel slot by hand and by hopper, and SHALL appear
as a fuel wherever vanilla lists fuels, such as the recipe book.

#### Scenario: Hopper fuels a furnace
- **WHEN** a hopper feeding the side of a furnace holds Coal Coke
- **THEN** the hopper moves the Coal Coke into the furnace's fuel slot
