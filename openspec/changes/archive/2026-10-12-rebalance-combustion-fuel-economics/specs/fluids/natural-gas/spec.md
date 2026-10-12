## MODIFIED Requirements

### Requirement: Natural Gas is a combustion fuel
Natural Gas SHALL be listed in the `voluminousenergy:combustible` fluid tag and SHALL burn in the
Combustion Generator with any oxidizer at a base output of 24 FE per tick, scaled by the oxidizer's
multiplier like every other fuel. The fuel recipe SHALL be keyed on `c:natural_gas` with 250 mB of fuel
and the same 25 mB oxidizer ingredient as the other built-in fuel recipes, so each burn consumes 250 mB of
Natural Gas and the oxidizer's own per-burn amount.

#### Scenario: Burning with oxygen
- **WHEN** a Combustion Generator has 1000 mB Natural Gas in its fuel tank and 1000 mB Oxygen in its oxidizer tank
- **THEN** it generates 24 FE per tick multiplied by Oxygen's oxidizer multiplier, consuming 250 mB of Natural Gas and 125 mB of Oxygen per burn

#### Scenario: N₂O₄ below 250 mB starts a burn
- **WHEN** a Combustion Generator holds 250 mB Natural Gas and 100 mB dinitrogen tetroxide
- **THEN** a burn starts at 96 FE per tick and leaves 75 mB dinitrogen tetroxide

#### Scenario: Shown in JEI
- **WHEN** JEI is installed and a player looks up uses of the Natural Gas Bucket
- **THEN** the combustion recipe lists Natural Gas as a fuel

#### Scenario: Datapack override
- **WHEN** a datapack replaces `voluminousenergy:fluid_data/combustion/natural_gas.json` with a different `energy_per_tick`
- **THEN** after `/reload` the Combustion Generator uses the datapack's value for Natural Gas
