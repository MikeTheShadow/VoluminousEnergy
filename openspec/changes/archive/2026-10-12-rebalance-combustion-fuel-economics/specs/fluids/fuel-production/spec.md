## Purpose

Defines the yields of the recipes that make Combustion Generator fuels and oxidizers from raw resources,
and that each bucket of input leaves its consumers whole steps rather than stranded remainders.

## ADDED Requirements

### Requirement: Liquefying coal and coke keeps their energy

The Aqueoulizer SHALL turn 1 coal dust and 500 mB of water into 500 mB of liquefied coal, and 1 coke dust
and 500 mB of water into 500 mB of liquefied coke.

#### Scenario: Two coal dust and a bucket of water

- **WHEN** an Aqueoulizer holds 2 coal dust and 1000 mB water and runs until it stops
- **THEN** its output tank holds 1000 mB liquefied coal and its water tank is empty

#### Scenario: Coke dust

- **WHEN** an Aqueoulizer holds 1 coke dust and 500 mB water
- **THEN** one operation produces 500 mB liquefied coke

### Requirement: Crude distillation splits evenly between light fuel and naphtha

The Distillation Unit SHALL turn 250 mB of crude oil into 125 mB of light fuel and 125 mB of naphtha. The
chance of a raw sulfur byproduct SHALL stay at 5%. Naphtha distillation SHALL stay at 500 mB naphtha into
375 mB gasoline and 125 mB diesel.

#### Scenario: One bucket of crude

- **WHEN** a Distillation Unit distils 1000 mB of crude oil
- **THEN** it holds 500 mB light fuel and 500 mB naphtha, enough for exactly one naphtha distillation

### Requirement: Red fuming nitric acid yields a quarter N₂O₄

The Centrifugal Agitator SHALL turn 500 mB of red fuming nitric acid into 125 mB of dinitrogen tetroxide
and 375 mB of white fuming nitric acid.

#### Scenario: One bucket of red fuming nitric acid

- **WHEN** a Centrifugal Agitator processes 1000 mB of red fuming nitric acid
- **THEN** it holds 250 mB dinitrogen tetroxide (ten 25 mB burns) and 750 mB white fuming nitric acid
  (three 250 mB nitroglycerin operations)

### Requirement: A bucket of input leaves no stranded fluid

For each recipe in this capability, 1000 mB of fluid input SHALL produce output amounts that their next
consumer uses in whole steps: multiples of 250 mB for Combustion Generator fuels and nitroglycerin, of
500 mB for naphtha distillation, and of the oxidizer's per-burn amount for oxidizers.

#### Scenario: Crude to naphtha distillation

- **WHEN** a player pipes the naphtha from one bucket of distilled crude into a second Distillation Unit
- **THEN** that unit runs one naphtha operation and its input tank is empty afterwards
