## 1. Setup

- [x] 1.1 Cut work branch `1.21.1-biomass-charring` from `1.21.1-dev`; verify with `git branch --show-current`

## 2. Fuel items

- [x] 2.1 Add `Cactchar` and `CaneCharcoal` in `items/solid_fuels/` (burn time 1600) and register `CACTCHAR` (`cactchar`) and `CANE_CHARCOAL` (`cane_charcoal`) at the bottom of `// Misc` in `VEItems`; verify `./gradlew compileJava`
- [x] 2.2 Add `TinyCactchar` and `TinyCaneCharcoal` in `items/solid_fuels/tiny/` (burn time 200) and register `TINY_CACTCHAR` and `TINY_CANE_CHARCOAL` at the bottom of `//Tiny fuels` in `VEItems`; verify `./gradlew compileJava`
- [x] 2.3 Add `item/handheld` models for the four items, matching `coalcoke.json` and `tiny_charcoal.json`, and their names at the bottom of the `"Items"` group in `en_us.json` ("Cactchar", "Cane Charcoal", "Tiny Cactchar", "Tiny Cane Charcoal"); verify the JSON parses with `python3 -m json.tool`
- [x] 2.4 Maintainer: draw `cactchar.png`, `cane_charcoal.png`, `tiny_cactchar.png`, and `tiny_cane_charcoal.png` in GIMP from the vanilla charcoal palette; verify the four files exist under `textures/item/`

## 3. Tags

- [x] 3.1 Add `c:cactchar`, `c:cane_charcoal`, `c:tiny/cactchar`, and `c:tiny/cane_charcoal`, and append the two tiny tags to the bottom of `c:tiny`'s `values`; verify by inspecting the files
- [x] 3.2 Add `voluminousenergy:charrable/cactus` containing `#c:crops/cactus` and `voluminousenergy:charrable/sugar_cane` containing `#c:crops/sugar_cane`; verify neither file names a mod other than `c`
- [x] 3.3 Add `voluminousenergy:charcoal_grade_fuels` containing `#c:cactchar` and `#c:cane_charcoal`; verify by inspecting the file

## 4. Recipes

- [x] 4.1 Add `primitive_blast_furnacing/logs_to_charcoal.json` (`#minecraft:logs_that_burn` ×1, 800 ticks, 1 charcoal), `cactchar.json` (`#voluminousenergy:charrable/cactus` ×3, 200 ticks, 1 Cactchar), and `cane_charcoal.json` (`#voluminousenergy:charrable/sugar_cane` ×16, 200 ticks, 1 Cane Charcoal); verify in 6.2
- [x] 4.2 Add `vanilla_crafting/tiny_solid_fuel/tiny_cactchar.json` and `tiny_cane_charcoal.json` (1 full item to 8 tiny), following `tiny_charcoal.json` but keyed on the `c:` tags; verify in 6.4
- [x] 4.3 Add torch and soul torch recipes in `vanilla_crafting/torch/`, following the rosin files: `torch_from_charcoal_grade_fuels.json` and `soul_torch_from_charcoal_grade_fuels.json` (`#voluminousenergy:charcoal_grade_fuels`, 4), `torch_from_coal_coke.json` and `soul_torch_from_coal_coke.json` (`#c:coal_coke`, 8), and `torch_from_petcoke.json` and `soul_torch_from_petcoke.json` (`#c:petcoke`, 10); verify in 5.3
- [x] 4.4 Add `stirling/cactchar.json` and `stirling/cane_charcoal.json` (1600 ticks) and `stirling/tiny/cactchar.json` and `stirling/tiny/cane_charcoal.json` (200 ticks), all 128 FE/t; verify in 6.4

## 5. Gametests

- [x] 5.1 In `RecipeTests`, add `recipe_primitive_charring_counts`: place a Primitive Blast Furnace, put 15 sugar cane in slot 0 and assert the loaded `cane_charcoal` recipe's `isCompleteRecipe` fails, put 16 and assert it passes and `completeRecipe` leaves slot 0 empty and slot 1 holding 1 Cane Charcoal; repeat with 2 and 3 cactus for `cactchar`; verify on `./gradlew runGameTestServer`
- [x] 5.2 In `RecipeTests`, add `recipe_primitive_charring_logs`: assert the `logs_to_charcoal` ingredient accepts an oak log and stripped birch wood, rejects a crimson stem, and has process time 800; verify on `./gradlew runGameTestServer`
- [x] 5.3 In `RecipeTests`, add `recipe_torch_yield_matches_burn_time`: for every loaded recipe under `voluminousenergy:vanilla_crafting/torch/`, assert the result count equals `max(1, burnTime / 400)` for every item its fuel ingredient accepts, and assert the Coal Coke and Petcoke torch recipes exist with 8 and 10; verify on `./gradlew runGameTestServer`

## 6. Verification

- [x] 6.1 `./gradlew compileJava` succeeds
- [x] 6.2 `./gradlew runGameTestServer` passes with every required test green, including the three new ones
- [x] 6.3 `./gradlew runData` leaves no diff under `src/generated/resources` (every new file here is hand-written); confirm with `git status`
- [x] 6.4 In-game check via `./gradlew runClient` with JEI: a Primitive Blast Furnace chars an oak log into charcoal in 40 s, does not light with 15 sugar cane and starts on the 16th, turns 7 cactus into 2 Cactchar with 1 left over; JEI shows 16 sugar cane and 3 cactus on the furnace pages; Cactchar in a vanilla furnace smelts 8 items; Tiny Cane Charcoal runs a Primitive Stirling Generator for 10 s; 1 Cactchar plus a stick crafts 4 torches and with soul sand 4 soul torches; 1 Coal Coke plus a stick crafts 8 torches and 1 Petcoke 10, and Rosin still 2; 1 Tiny Cactchar plus a stick crafts 1 torch; all four textures render in hand and in the creative tab
