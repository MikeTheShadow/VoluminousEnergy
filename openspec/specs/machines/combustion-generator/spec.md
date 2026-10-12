# machines/combustion-generator Specification

## Purpose

Defines how the Combustion Generator turns a combustible fluid and an oxidizer into FE: how much of each
one burn consumes, how long the burn lasts, how much FE it makes per tick, and what JEI reports for it.

## Requirements

### Requirement: A burn consumes 250 mB of fuel and lasts the configured burn length

The Combustion Generator SHALL start a burn only when its fuel tank holds at least 250 mB of a combustible
fluid and its oxidizer tank holds at least that oxidizer's per-burn amount. Starting a burn SHALL drain
250 mB of fuel and the oxidizer's per-burn amount. The burn SHALL last the number of ticks set by the
"Fixed Process Time" config value (default 1600), and the generator SHALL produce the fuel's FE/t rating
multiplied by the oxidizer's multiplier on each tick of the burn. "Balanced Mode" SHALL also give a
1600-tick burn.

#### Scenario: Diesel on compressed air with default config

- **WHEN** a Combustion Generator with default config holds 1000 mB diesel and 1000 mB compressed air
- **THEN** a burn starts, the tanks drop to 750 mB diesel and 750 mB compressed air, and the generator
  makes 160 FE/t for 1600 ticks before the next burn starts

#### Scenario: Configured burn length is per 250 mB burn

- **WHEN** "Fixed Process Time" is set to 800 and the generator burns 250 mB of gasoline on compressed air
- **THEN** the burn lasts 800 ticks at 128 FE/t

### Requirement: Each oxidizer has its own amount per burn

Each oxidizer SHALL define how many mB one burn consumes, in a required `amount` field of its oxidizer
fluid data; an entry without it SHALL fail to load with a logged error. The built-in oxidizers SHALL use
compressed air 250 mB, oxygen 125 mB, and dinitrogen tetroxide 25 mB per burn, keeping their multipliers
of ×1, ×2, and ×4. A fuel recipe SHALL NOT stop a burn from starting when the oxidizer tank holds at least
the oxidizer's own amount but less than 250 mB.

#### Scenario: N₂O₄ below 250 mB starts a burn

- **WHEN** the generator holds 250 mB diesel and 100 mB dinitrogen tetroxide
- **THEN** a burn starts at 640 FE/t and leaves 75 mB dinitrogen tetroxide

#### Scenario: One bucket of oxygen lasts eight burns

- **WHEN** the generator holds 2000 mB light fuel and 1000 mB oxygen
- **THEN** it completes eight burns at 128 FE/t and the oxygen tank is empty after the eighth starts

#### Scenario: Oxygen below its amount does not start a burn

- **WHEN** the generator holds 1000 mB diesel and 100 mB oxygen
- **THEN** no burn starts and both tanks keep their contents

#### Scenario: Datapack oxidizer with its own amount

- **WHEN** a datapack adds an oxidizer fluid data entry with a multiplier of 3 and an amount of 50
- **THEN** each burn with that oxidizer consumes 50 mB of it and produces the fuel rating × 3 FE/t

### Requirement: JEI shows the energy and oxidizer amounts the generator uses

When JEI is installed, the Combustion category SHALL show, for the oxidizer currently displayed in its
oxidizer slot, the FE/t a generator makes with that fuel and oxidizer, and the volumetric energy as FE per
1000 mB of fuel burnt with that oxidizer under the current "Fixed Process Time". Both values SHALL match
what a Combustion Generator produces. Each oxidizer SHALL be shown at its own per-burn amount, and the mB
one burn consumes SHALL be printed under the fuel and oxidizer tanks, as other Voluminous Energy fluid
categories do. On a dedicated server, clients SHALL show the oxidizer amounts the server loaded.

#### Scenario: Consumption printed under the tanks

- **WHEN** a player opens the JEI Combustion page for diesel and the oxidizer slot shows oxygen
- **THEN** "mB:" is followed by 250 under the fuel tank and 125 under the oxidizer tank

#### Scenario: Diesel on compressed air

- **WHEN** a player opens the JEI Combustion page for diesel with default config and the oxidizer slot shows compressed air
- **THEN** it shows 1024000 FE/B and 160 FE/t

#### Scenario: Diesel on dinitrogen tetroxide

- **WHEN** the oxidizer slot on the diesel page cycles to dinitrogen tetroxide
- **THEN** it shows 4096000 FE/B and 640 FE/t, the same 640 FE/t a Combustion Generator shows burning diesel on N₂O₄

#### Scenario: Oxidizer amounts in the oxidizer slot

- **WHEN** the player watches the oxidizer slot cycle on any Combustion page
- **THEN** it shows 250 mB compressed air, 125 mB oxygen, and 25 mB dinitrogen tetroxide

#### Scenario: Server datapack amount reaches the client

- **WHEN** a dedicated server's datapack sets oxygen to 100 mB per burn and a client joins
- **THEN** the client's JEI Combustion page shows 100 mB oxygen
