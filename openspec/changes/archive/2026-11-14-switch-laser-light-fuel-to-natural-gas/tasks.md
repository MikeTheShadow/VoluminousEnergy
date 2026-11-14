## 1. Recipes

- [x] 1.1 `git mv` `recipe/dimensional_lasing/light_fuel.json` to `natural_gas.json` and set
  `region_fluid.fluid` to `voluminousenergy:natural_gas`, leaving amounts and climate untouched; verify
  with `git diff -M --stat` that it shows as a rename with a one-line change
- [x] 1.2 Same for `light_fuel_hot_eroded_humid.json` to `natural_gas_hot_eroded_humid.json`; verify
  `grep -r light_fuel src/main/resources/data/voluminousenergy/recipe/dimensional_lasing` finds nothing

## 2. Gametest

- [x] 2.1 Add `recipe_lasing_region_fluids` to `RecipeTests`, registered at the bottom of `register`:
  the two `natural_gas*` ids load with region fluid `voluminousenergy:natural_gas`, minimum 125 and
  maximum 155, no loaded lasing recipe has light fuel as its region fluid, and the two old
  `light_fuel*` ids are absent; extend the class Javadoc's spec list if needed and verify
  `./gradlew compileGametestJava` (or `compileJava`) succeeds

## 3. Verification

- [x] 3.1 `./gradlew compileJava` succeeds
- [x] 3.2 `./gradlew runGameTestServer` passes, including `recipe_lasing_region_fluids` and
  `recipe_lasing_codec_round_trip`
- [x] 3.3 In-game check via `./gradlew runClient`: JEI's Dimensional Lasing category lists two Natural
  Gas recipes and no light fuel recipe, and a laser on a freshly scanned chunk matching one of them
  fills its tank with Natural Gas (JEI confirmed in game; the tank fill was not checked in game)
