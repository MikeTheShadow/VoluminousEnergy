## 1. Setup

- [x] 1.1 Cut work branch `1.21.1-recipe-codec-and-heat-cache-fixes` from `1.21.1-dev`; verify with `git branch --show-current`

## 2. Failing gametests first

- [x] 2.1 Add `RecipeTests` in `src/gametest/` with a `register(List<GameTestDefinition>)` entry point and register it from `VEGameTests.all()` after `StorageTests`; verify `./gradlew compileJava` and that the new class shows up in the gametest source set build
- [x] 2.2 Add the lasing round-trip test: encode every loaded `dimensional_lasing` recipe with its serializer codec to `JsonOps`, decode it, and compare region fluid, both amounts, and all eight climate bounds; also assert `crude_oil_plains` encodes erosion -0.5 to 0.25, humidity -0.3 to -0.15, temperature -0.15 to 0.05; verify it fails on `./gradlew runGameTestServer` with the current code
- [x] 2.3 Add the per-recipe heat list test: build `IndustrialBlastingRecipe`s at 900 K, 1500 K, and 1300 K, query them in that order, and assert lava is listed for 900 and 1300 and not for 1500; verify it fails on `./gradlew runGameTestServer` with the current code
- [x] 2.4 Add the heat drain test: place a Blast Furnace, put the loaded steel recipe's two ingredients in slots 2 and 3, and call that recipe's parser; with 1000 mB lava assert `isCompleteRecipe` passes and `completeRecipe` leaves `1000 - Heat Source Consumption`; with `Heat Source Consumption - 10` mB assert `isCompleteRecipe` fails; verify it fails on `./gradlew runGameTestServer` with the current code

## 3. Dimensional lasing codec

- [x] 3.1 In `VERecipeCodecs.VE_CLIMATE_CODEC`, use `ClimateData::erosion`, `::humidity`, and `::temperature` for those fields; verify `./gradlew compileJava`
- [x] 3.2 In `DimensionalLaserRecipe`, add private helpers that build `FluidMinMax` and `ClimateData` from the flat fields, point the codec `forGetter`s at them, and delete the unassigned `fluidMinMax` and `climateData` fields; verify the 2.2 test passes on `./gradlew runGameTestServer`

## 4. Blast Furnace heat source

- [x] 4.1 In `IndustrialBlastingRecipe`, make `hotEnoughFluids` a lazily filled instance field built with `isFluidHotEnough`, change `isFluidHotEnough` to `>=`, and map the list directly in `getFluidIngredients()` without the second filter; verify the 2.3 test passes on `./gradlew runGameTestServer`
- [x] 4.2 In `IndustrialBlastFurnaceParser`, require `blastingRecipe.isFluidHotEnough` and at least `Config.BLAST_FURNACE_HEAT_SOURCE_CONSUMPTION` mB in `isCompleteRecipe`, and drain that config value in `completeRecipe` instead of 250; verify the 2.4 test passes on `./gradlew runGameTestServer`

## 5. Verification

- [x] 5.1 `./gradlew compileJava` succeeds
- [x] 5.2 `./gradlew runGameTestServer` passes with every required test green, including the three new ones
- [x] 5.3 `./gradlew runData` is not needed: no registries, tags, loot, or models change; confirm `git status` shows nothing under `src/generated/resources`
- [x] 5.4 In-game check via `./gradlew runClient` with JEI: every Blast Furnace page lists lava and prints "mB: 50"; a formed Blast Furnace with 1000 mB lava, iron and carbon ingots, and power makes steel and leaves 950 mB lava; with 40 mB lava it does not start
