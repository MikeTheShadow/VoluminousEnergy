## 1. Recipe field

- [x] 1.1 Add a `heatSourceConsumption` field (0 when unset), a getter, and a setter to `IndustrialBlastingRecipe`, and extend the full constructor with the amount; verify `./gradlew compileJava` succeeds
- [x] 1.2 Add `Codec.intRange(0, 4000).optionalFieldOf("heat_source_consumption", 0)` to the recipe's `MapCodec` after `minimum_heat_kelvin`; verify `./gradlew compileJava` succeeds
- [x] 1.3 Write the amount in the `StreamCodec` after `minimumHeat` and read it back in the same order in `decode`; verify by reading the encode and decode bodies side by side
- [x] 1.4 Make `getFluidIngredients` build the hot-enough stacks at `Math.max(getHeatSourceConsumption(), 1)` through a private helper the JEI category can share; verify no `Config` reference remains in `IndustrialBlastingRecipe`

## 2. Readers

- [x] 2.1 Make `IndustrialBlastFurnaceParser` check against `blastingRecipe.getHeatSourceConsumption()` and drain that amount only when it is above 0; verify no `Config` import remains in the parser
- [x] 2.2 Make `IndustrialBlastingCategory` print `recipe.getHeatSourceConsumption()` and list the same stacks as the recipe's heat fluid ingredient; verify no `Config.BLAST_FURNACE_HEAT_SOURCE_CONSUMPTION` reference remains under `compat/`

## 3. Config and data

- [x] 3.1 Delete `BLAST_FURNACE_HEAT_SOURCE_CONSUMPTION` and its `define` call from `Config`; verify `grep -rn BLAST_FURNACE_HEAT_SOURCE_CONSUMPTION src` returns nothing once task 4.1 lands
- [x] 3.2 Add `"heat_source_consumption": 50` after `minimum_heat_kelvin` in all five files under `src/main/resources/data/voluminousenergy/recipe/industrial_blasting/` (steel, tungsten steel, netherite scrap, nighalite, solarium ingot); verify each still parses as JSON

## 4. Gametests

- [x] 4.1 Change `recipe_blasting_heat_source_drain` in `RecipeTests` to read the amount from the loaded steel recipe and assert it equals 50; verify it passes in `./gradlew runGameTestServer`
- [x] 4.2 Add `recipe_blasting_heat_source_codec` to `RecipeTests`: parse blasting JSON built in the test with the field absent and set to 0 (both: amount 0, 10 mB of lava runs it and stays 10 mB, an empty tank does not start, the heat ingredient accepts lava), set to 250 (200 mB does not start, 1000 mB keeps 750 mB), and set to -1 (decode error); verify it passes in `./gradlew runGameTestServer`

## 5. Verification

- [x] 5.1 Run `./gradlew compileJava` and confirm it succeeds
- [x] 5.2 Run `./gradlew runGameTestServer` and confirm every required test passes, including both blasting heat-source tests
- [x] 5.3 In-game check with `./gradlew runClient` and JEI: each shipped Blast Furnace page shows "mB: 50" and lava at 50 mB, and a built Blast Furnace drains 50 mB of lava per shipped operation (confirmed by the maintainer with Nighalite)
- [ ] 5.5 In-game datapack check: after `/reload` with a datapack setting tungsten steel to 250 and adding a recipe without the field, the tungsten steel page shows "mB: 250", the new page shows "mB: 0" with lava listed, a built Blast Furnace drains 250 mB per tungsten steel operation, and runs the new recipe without draining the lava (machine side already covered by `recipe_blasting_heat_source_codec`)
- [x] 5.4 Start a world with an existing `voluminousenergy-startup.toml` that still has "Heat Source Consumption" and confirm it loads without errors and the entry is gone or ignored
- [x] 5.6 Dedicated server check: start `./gradlew runServer`, connect from `./gradlew runClient`, and confirm the shipped Blast Furnace JEI pages show "mB: 50" and a Blast Furnace on the server drains 50 mB per operation (confirmed by the maintainer)
