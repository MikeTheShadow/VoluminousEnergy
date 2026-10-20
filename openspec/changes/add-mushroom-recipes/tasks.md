## 1. Recipes

- [x] 1.1 Add `crushing/organics/mushrooms.json` and `hydroponic_incubating/mushrooms/brown_mushroom.json` and `red_mushroom.json` under `src/main/resources/data/voluminousenergy/recipe/`, byte-identical to the 26.3-dev files, and verify each is valid JSON

## 2. Verification

- [x] 2.1 Run `./gradlew compileJava` and verify it succeeds (no datagen run needed; the recipes are hand-written)
- [ ] 2.2 In-game check via `./gradlew runClient` (needs a human): no recipe parse errors in `logs/latest.log`; JEI lists the three recipes; a Crusher turns a Brown Mushroom into Shredded Biomass; an Incubator grows a Red Mushroom without consuming it
