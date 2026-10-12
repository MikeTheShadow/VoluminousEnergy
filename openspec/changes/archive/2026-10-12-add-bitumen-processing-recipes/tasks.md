## 1. Centrifugal Separator recipe

- [x] 1.1 Add `centrifugal_separation/bitumen.json`: ingredients `#c:bitumen` ×4 and `minecraft:bucket` ×1, `process_time` 200, results in order Crude Oil bucket ×1, Sand Dust ×2 (`chance` 1), Carbon Dust ×1 (`chance` 0.25), shaped like `soulsand_dust.json`; verify the JSON parses

## 2. Crusher recipes

- [x] 2.1 Add `crushing/ores/bituminous_sand.json` and `crushing/ores/bituminous_red_sand.json`: one block each, results Bitumen ×5 then Sand Dust ×3 (`chance` 1), `process_time` 200, experience 0 to 0, shaped like `saltpeterore.json`; verify the JSON parses
- [x] 2.2 Add `crushing/ores/bituminous_gravel.json`: one Bituminous Gravel, results Bitumen ×5 then `minecraft:gravel` ×1 (`chance` 0.25), `process_time` 200, experience 0 to 0; verify the JSON parses

## 3. Verification

- [x] 3.1 Run `./gradlew compileJava` and confirm it succeeds (no Java changes are expected, so this only guards the branch)
- [x] 3.2 In-game check via `./gradlew runClient` (maintainer): the log shows no recipe parse errors; a Centrifugal Separator with 4 Bitumen and a bucket outputs a Crude Oil bucket and 2 Sand Dust, with Carbon Dust on some runs; it does not start with 3 Bitumen or without a bucket; a Crusher turns Bituminous Sand and Bituminous Red Sand into 5 Bitumen and 3 Sand Dust, and Bituminous Gravel into 5 Bitumen with Gravel on some runs; with JEI, the uses of Bitumen and of each bituminous block list these recipes with the chanced outputs marked
