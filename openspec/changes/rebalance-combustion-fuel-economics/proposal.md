## Why

Burning fluids in the Combustion Generator rarely pays for the FE spent making them. With default config,
liquefied coal returns 1.58× its processing cost while the same coal gives 204,672 FE free in a Stirling
Generator, fully refined Dimensional Laser crude returns 0.96×, and N₂O₄ costs about 1,616 FE/mB to make,
so it loses FE on every fuel in the game (its break-even fuel rating is about 337 FE/t; the best fuel is
234). Two recipes also leave amounts in tanks that no consumer can use in whole 250 mB steps. The 0.5
release is the point to rebalance this, before modpacks settle on the current numbers.

## What Changes

Player-visible effects first:

- **Liquid fuels hold 4× the energy.** A Combustion Generator burn lasts 1600 ticks instead of 400. FE/t
  ratings are unchanged, so a diesel generator still makes 160 FE/t on compressed air but runs 4× longer
  per 250 mB, and JEI's "Volumetric Energy" per bucket rises 4×.
- **Better oxidizers are consumed more sparingly.** Each burn uses 250 mB of compressed air, 125 mB of
  oxygen, or 25 mB of dinitrogen tetroxide, instead of 250 mB of any oxidizer. JEI shows each oxidizer's
  amount. A generator with less than 250 mB of oxygen or N₂O₄ in its tank can now start a burn.
- **Liquefying coal and coke keeps their energy.** The Aqueoulizer turns 1 coal or coke dust and 500 mB of
  water into 500 mB of liquefied coal or coke (was 250 mB from 250 mB of water).
- **Crude oil yields more naphtha.** Distilling 250 mB of crude gives 125 mB light fuel and 125 mB naphtha
  (was 200 and 50), so more of each bucket reaches gasoline and diesel.
- **JEI shows fluid amounts where it did not.** The Combustion page prints the mB a burn consumes under
  its fuel and oxidizer tanks, and the Blast Furnace and Sawmill pages print their heat fluid and fluid
  output amounts, matching the other Voluminous Energy fluid pages.
- **N₂O₄ is a realistic share of fuming acid.** The Centrifugal Agitator turns 500 mB of red fuming nitric
  acid into 125 mB N₂O₄ and 375 mB white fuming nitric acid (was 250 mB into 25 and 225).

Data and code:

- **BREAKING (data):** oxidizer fluid data (`fluid_data/oxidizers/*.json`) gains a required `amount`
  field, in mB per burn. Datapack oxidizers without it fail to load and must add it.
- Every built-in `fuel_combustion` recipe lowers its oxidizer ingredient from 250 to 25 mB, the smallest
  oxidizer amount, so recipe matching no longer blocks oxidizers that need less than 250 mB. The generator
  enforces each oxidizer's own amount.
- **BREAKING (config meaning):** "Fixed Process Time" (default 1600) changes from ticks per bucket of fuel
  to ticks per 250 mB burn. The divide-by-4 is removed from both the fixed and "Balanced Mode" paths.
- **BREAKING (network):** the client-bound fluid data packet also carries each oxidizer's amount, so
  clients and servers must run the same mod version (true of any 0.5 build).

Out of scope: fuel FE/t ratings (bio fuels stay below petroleum fuels on purpose), the Dimensional Laser,
Stirling Generator, Pump, a no-oxidizer mode for nitroglycerin, reworking "Balanced Mode", the naphtha
distilling split, and any new tanks or machine layout changes.

## Capabilities

### New Capabilities

- `machines/combustion-generator`: how long one burn lasts, how much fuel and oxidizer it consumes, the FE
  it produces, and what JEI reports for it.
- `machines/blast-furnace`: the heat-source fluid amount JEI shows per blasting operation.
- `machines/sawmill`: the fluid amount JEI shows per sawing operation.
- `fluids/fuel-production`: the yields of the recipes that make combustion fuels and oxidizers changed
  here (liquefied coal and coke, crude distillation, N₂O₄ from red fuming nitric acid), including that
  each produces whole 250 mB steps per bucket of input.

### Modified Capabilities

None.

## Impact

- **Code:** `recipe/processor/CombustionGeneratorProcessor.java`, `items/data/OxidizerFluidsData.java`,
  `util/recipe/VERecipeCodecs.java` (oxidizer codec), `tools/networking/packets/ClientBoundFluidDataPacket.java`,
  `events/VEServerSideListener.java` (packet construction), `compat/jei/category/{Combustion,IndustrialBlasting,Sawmill}Category.java`,
  and the "Fixed Process Time" comment in `tools/Config.java`.
- **Data:** `fluid_data/oxidizers/{compressed_air,oxygen,dinitrogen_tetroxide}.json`, all ten
  `recipe/fuel_combustion/*.json`, `recipe/aqueoulizing/{liquefied_coal,liquefied_coke}.json`,
  `recipe/distilling/crude_oil.json`, `recipe/centrifugal_agitating/rfna.json`. All are hand-written under
  `src/main/resources`; no datagen output changes.
- **Save compatibility:** no NBT, data component, attachment, or registry id changes. No migration or
  transition code is provided, by decision.
- **Config:** existing config files that kept the default 1600 get the new 4× energy with no edit. Packs
  that changed "Fixed Process Time" see their value reinterpreted per burn (4× its old effect).
- **Modpacks and datapacks:** custom oxidizer data must add `amount`. Custom `fuel_combustion` recipes that
  still require 250 mB of oxidizer block oxygen and N₂O₄ from starting below 250 mB.
- **Follow-up, not scope:** port the same rebalance to other version branches if they ship the Combustion
  Generator; consider a separate change for Dimensional Laser energy use.
