## Purpose

Defines what the Blast Furnace shows players about its recipes, starting with the heat-source fluid each
blasting operation consumes.

## ADDED Requirements

### Requirement: JEI shows the heat-source fluid consumed per operation

When JEI is installed, the Blast Furnace category SHALL print the mB of heat-source fluid one blasting
operation consumes under the heat fluid tank, taken from the "Heat Source Consumption" config value.

#### Scenario: Default heat source consumption

- **WHEN** a player opens any Blast Furnace page in JEI with default config
- **THEN** "mB:" is followed by 50 under the heat fluid tank
