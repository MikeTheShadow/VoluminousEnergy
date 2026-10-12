## 1. Setup

- [x] 1.1 Cut work branch `1.21.1-combustion-fuel-rebalance` from `1.21.1-dev`; verify with `git branch --show-current`

## 2. Recipe yields

- [x] 2.1 Set `recipe/aqueoulizing/liquefied_coal.json` and `liquefied_coke.json` to 500 mB water in and 500 mB out; verify the JSON diff shows only those amounts
- [x] 2.2 Set `recipe/distilling/crude_oil.json` results to 125 mB light fuel and 125 mB naphtha, keeping the 5% raw sulfur; verify the JSON diff shows only those amounts
- [x] 2.3 Set `recipe/centrifugal_agitating/rfna.json` to 500 mB red fuming nitric acid in, 125 mB dinitrogen tetroxide and 375 mB white fuming nitric acid out; verify the JSON diff shows only those amounts
- [x] 2.4 Lower the `voluminousenergy:oxidizer` ingredient to 25 mB in all ten `recipe/fuel_combustion/*.json`, leaving the fuel at 250 mB; verify `grep -c '"amount":25'` finds one per file
- [x] 2.5 Lower the `voluminousenergy:oxidizer` ingredient in `recipe/fuel_combustion/natural_gas.json` from 250 to 25 mB, leaving the fuel at 250 mB; verify all eleven fuel recipes now ask for 25 mB of oxidizer

## 3. Oxidizer data with a per-burn amount

- [x] 3.1 Add `"amount"` to `fluid_data/oxidizers/compressed_air.json` (250), `oxygen.json` (125), and `dinitrogen_tetroxide.json` (25); verify the three files
- [x] 3.2 In `VERecipeCodecs`, replace the oxidizer codec with a record carrying tag or fluid, a required `multiplier`, and a required `amount`, sharing the tag and fluid lookup with `RegistryFluidValue` instead of copying it; verify `./gradlew compileJava`
- [x] 3.3 Make `OxidizerFluidsData` cache multiplier and amount per fluid, keep `getOxidizerMultiplier`, and add an amount lookup; verify `./gradlew compileJava`
- [x] 3.4 Send multiplier and amount per fluid in `ClientBoundFluidDataPacket` and update `VEServerSideListener` and `updateFromPacket` to match; verify `./gradlew compileJava`

## 4. Combustion Generator

- [x] 4.1 In `CombustionGeneratorProcessor`, check and drain the oxidizer tank by the oxidizer's amount, use the 250 mB fuel constant in place of the literal `drain(250, ...)`, and remove the divide-by-4 from both burn-length paths; verify `./gradlew compileJava`
- [x] 4.2 Reword the "Fixed Process Time" comment in `tools/Config.java` to say ticks per 250 mB burn; verify the generated config comment after `./gradlew runClient` starts

## 5. JEI

- [x] 5.1 In `CombustionCategory`, compute FE/t as rating × the displayed oxidizer's multiplier and volumetric energy as that × "Fixed Process Time" × 4, and list each oxidizer at its own amount; verify `./gradlew compileJava`
- [x] 5.2 Print the mB one burn consumes under the Combustion page's fuel and oxidizer tanks, and the heat fluid and fluid output amounts under the Blast Furnace and Sawmill tanks, in the existing "mB:" style; verify `./gradlew compileJava`

## 6. Verification

- [x] 6.1 Run `./gradlew compileJava` and confirm it succeeds
- [x] 6.2 Confirm no datagen output changes are expected (all edits are hand-written resources); run `./gradlew runData` only if a generated file turns out to cover these recipes
- [x] 6.3 In-game via `./gradlew runClient`: a Combustion Generator with 1000 mB diesel and 1000 mB compressed air makes 160 FE/t and each burn lasts 1600 ticks (80 seconds)
- [x] 6.4 In-game: with 250 mB diesel and 100 mB dinitrogen tetroxide a burn starts at 640 FE/t and leaves 75 mB; with 100 mB oxygen no burn starts
- [x] 6.5 In-game: one bucket of oxygen lasts eight burns of light fuel
- [x] 6.6 In-game: Aqueoulizer turns 2 coal dust and 1000 mB water into 1000 mB liquefied coal; Distillation Unit turns 1000 mB crude into 500 mB light fuel and 500 mB naphtha; Centrifugal Agitator turns 1000 mB red fuming nitric acid into 250 mB dinitrogen tetroxide and 750 mB white fuming nitric acid
- [x] 6.7 In-game with JEI: on the diesel Combustion page the oxidizer slot cycles 250 mB compressed air, 125 mB oxygen, 25 mB dinitrogen tetroxide, and the numbers follow it: 160 / 320 / 640 FE/t and 1024000 / 2048000 / 4096000 FE/B
- [x] 6.8 Dedicated server check (`./gradlew runServer` plus a client): the client's JEI oxidizer amounts match the server's data and the server log shows no oxidizer data errors
- [x] 6.9 In-game with JEI: Combustion shows mB 250 under the fuel tank and the cycling oxidizer amount under the oxidizer tank; Blast Furnace shows 50 under the heat fluid tank; Sawmill shows 250 on the log page and 25 on bamboo; no text overlaps a slot
- [x] 6.10 In-game: a Combustion Generator with 250 mB Natural Gas and 100 mB dinitrogen tetroxide starts a burn at 96 FE/t and leaves 75 mB; with JEI, the Natural Gas Combustion page cycles 250 / 125 / 25 mB oxidizer at 24 / 48 / 96 FE/t
