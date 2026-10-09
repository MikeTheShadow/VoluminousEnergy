## 1. Crusher recipes

- [x] 1.1 Add `short_dry_grass.json`, `tall_dry_grass.json`, `bush.json`, `firefly_bush.json`, `leaf_litter.json`, and `pale_hanging_moss.json` to `src/main/resources/data/voluminousenergy/recipe/crushing/organics/` with the values in the `machines/crusher` spec, using the same field layout as `short_grass.json`, and verify each file is valid JSON

## 2. Hydroponic Incubator recipes

- [x] 2.1 Add `short_dry_grass.json`, `bush.json`, `firefly_bush.json`, and `pale_hanging_moss.json` to `hydroponic_incubating/shrubbery/` with the values in the `machines/hydroponic-incubator` spec, using the layout of `shrubbery/grass.json`, and verify each file is valid JSON
- [x] 2.2 Add `open_eyeblossom.json`, `closed_eyeblossom.json`, `golden_dandelion.json`, `wildflowers.json`, and `cactus_flower.json` to `hydroponic_incubating/flowers/` using the layout of `flowers/poppy.json`, and verify each file is valid JSON
- [x] 2.3 Add `pale_oak_sapling.json` to `hydroponic_incubating/trees/` using the layout of `trees/jungle_sapling.json`, and verify it is valid JSON

## 3. Verification

- [x] 3.1 Run `./gradlew compileJava` and verify it succeeds (no datagen run needed; the recipes are hand-written and `src/generated/resources` is untouched)
- [x] 3.2 In-game check via `./gradlew runClient`: confirm the world loads with no recipe parse errors for the sixteen files in `logs/latest.log`, that JEI lists each new Crusher and Hydroponic Incubator recipe, that a Crusher turns Tall Dry Grass into Shredded Biomass, that an Incubator duplicates Short Dry Grass and a Pale Oak Sapling without consuming them, and that Leaf Litter does not start the Incubator (needs a human)

The in-game check ran before Short Dry Grass was changed to 1 Shredded Biomass in the Crusher; that value was not re-checked in game.
