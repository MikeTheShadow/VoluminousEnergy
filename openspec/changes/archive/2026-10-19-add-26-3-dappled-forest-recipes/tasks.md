## 1. Crusher recipes

- [x] 1.1 Add `red_shrub.json` (item ingredient, layout of `bush.json`) and `mushrooms.json` (`c:mushrooms` tag ingredient, layout of `seeds.json`) to `src/main/resources/data/voluminousenergy/recipe/crushing/organics/` with the values in the `machines/crusher` spec, and verify each file is valid JSON

## 2. Hydroponic Incubator recipes

- [x] 2.1 Add `poplar_sapling.json` to `hydroponic_incubating/trees/` using the layout of `trees/jungle_sapling.json`, and verify it is valid JSON
- [x] 2.2 Add `red_shrub.json` to `hydroponic_incubating/shrubbery/` using the layout of `shrubbery/bush.json`, and verify it is valid JSON
- [x] 2.3 Add `brown_mushroom.json`, `red_mushroom.json`, and `shelf_mushroom.json` to a new `hydroponic_incubating/mushrooms/` folder using the layout of `flowers/poppy.json`, and verify each file is valid JSON

## 3. Verification

- [x] 3.1 Run `./gradlew compileJava` and verify it succeeds (no datagen run needed; the recipes are hand-written and `src/generated/resources` is untouched)
- [x] 3.2 Run `./gradlew runGameTestServer` and verify it passes, including `recipe_crusher` and `recipe_hydroponic_incubator`, with no recipe parse errors for the seven new files in the log
- [x] 3.3 In-game check via `./gradlew runClient` (needs a human): JEI lists each new Crusher and Hydroponic Incubator recipe and the Sawmill accepts a Poplar Log and yields Poplar Planks; a Crusher turns a Red Shrub and a Shelf Mushroom into Shredded Biomass; an Incubator grows a Poplar Sapling, a Red Shrub, and a Brown Mushroom without consuming them; Orange Poplar Leaves start neither machine
