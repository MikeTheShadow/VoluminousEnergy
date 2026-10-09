## 1. Remove the Raw Cinnabar Block

- [x] 1.1 Delete `RawCinnabarBlock.java`, the `RAW_CINNABAR_BLOCK` registration in `VEBlocks`, and the commented-out `raw_cinnabar_block` lines in `VEBlockItems` and `VEFeatures`. Verify `grep -rn "RAW_CINNABAR_BLOCK\|RawCinnabarBlock\|raw_cinnabar_block" src/main/java` returns nothing
- [x] 1.2 Delete the `raw_cinnabar_block` blockstate, block and item models, item definition, texture, and loot table, and remove its key from `en_us.json`, `ja_jp.json`, and `ko_kr.json`. Verify `grep -rln raw_cinnabar_block src/main/resources` lists only the files that tasks 2.1 and 2.3 replace

## 2. Vanilla Cinnabar as raw cinnabar storage

- [x] 2.1 Replace `vanilla_crafting/storage_blocks/raw_cinnabar_block.json` with `cinnabar.json` (3×3 `voluminousenergy:raw_cinnabar` to `minecraft:cinnabar`) and `raw_cinnabar_from_cinnabar.json` (shapeless 1 `minecraft:cinnabar` to 9 `voluminousenergy:raw_cinnabar`), and verify both are valid JSON
- [x] 2.2 Point `c/tags/block/storage_blocks/raw_cinnabar.json` and `c/tags/item/storage_blocks/raw_cinnabar.json` at `minecraft:cinnabar`, and verify both are valid JSON
- [x] 2.3 Set `raw_block_state` in `worldgen/configured_feature/cinnabar_deposit.json` to `minecraft:cinnabar`, and verify it is valid JSON
- [x] 2.4 Call `MaterialConstants.setCinnabarTier(Blocks.CINNABAR)` at the start of `VETagDataGenerator.addTags`, and verify `./gradlew compileJava` succeeds

## 3. Sulfur

- [x] 3.1 Add `vanilla_crafting/storage_blocks/sulfur.json` (3×3 `voluminousenergy:raw_sulfur` to `minecraft:sulfur`) and `raw_sulfur_from_sulfur.json` (shapeless 1 `minecraft:sulfur` to 9 `voluminousenergy:raw_sulfur`), and verify both are valid JSON
- [x] 3.2 Add `crushing/sulfur_spike.json`: `minecraft:sulfur_spike` in, 2 `voluminousenergy:sulfurdust` plus 1 at chance 0.25, 200 ticks, 0 experience, in the layout of `crushing/raw_ores/sulfur.json`. Verify it is valid JSON
- [x] 3.3 In `AnimalFatLootModifier.doApply`, read `ATTACKING_ENTITY` with `getOptionalParameter` so that a death with no killer counts as Looting 0. Verify `./gradlew compileJava` succeeds
- [x] 3.4 Add `sulfurCubeModifierProvider` to `VEGlobalLootModifierData` and call it from `start()`. It is an `AnimalFatLootModifier` for `VEItems.RAW_SULFUR`, counts 1 to 5 (exclusive upper bound), with a `LootTableIdCondition` for `minecraft:entities/sulfur_cube` and an entity-properties condition on `THIS` using `CubeMobPredicate.sized(MinMaxBounds.Ints.exactly(1))`. Verify `./gradlew compileJava` succeeds
- [x] 3.5 Add `crushing/sulfur.json` (`minecraft:sulfur` to 9 `voluminousenergy:sulfurdust`) and `crushing/potent_sulfur.json` (`minecraft:potent_sulfur` to 41 `voluminousenergy:sulfurdust` plus 40 at chance 1), 200 ticks, 0 experience, in the layout of `crushing/sulfur_spike.json`. Verify both are valid JSON and load without errors
- [x] 3.6 Add `VEGenericListener.onLivingDrops`, which rolls the Sulfur Cube loot table for dying baby Sulfur Cubes (vanilla skips loot for babies, and every size 1 cube is one), honouring `mobDrops`. Verify `./gradlew compileJava` succeeds

## 4. Textures

- [x] 4.1 Maintainer picks one cinnabar, one sulfur, and one acid option from the design.md Texture palettes tables; record the choice in design.md and delete the rejected columns
- [x] 4.2 In GIMP, apply the chosen cinnabar table to `raw_cinnabar.png`, `cinnabar_dust.png`, `cinnabarore.png`, and `deepslate_cinnabar_ore.png` by exact select-by-colour and fill with alpha locked. Verify with Pillow that each file's colour set is exactly the table's targets plus the untouched stone or deepslate greys
- [x] 4.3 In GIMP, apply the chosen sulfur table to `raw_sulfur.png` and `sulfurdust.png` the same way, and verify each colour set is exactly the table's targets
- [x] 4.4 In GIMP, apply the chosen acid grey table to `sulfuric_acid_still.png` and `sulfuric_acid_flowing.png` with alpha locked, keeping the `.mcmeta` files. Verify the sizes are unchanged, every pixel keeps alpha `b4`, and the colour sets are exactly the table's targets
- [x] 4.5 In GIMP, recolour the five liquid slots of `sulfuric_acid_bucket.png` by pixel position (the pixels that differ from vanilla `bucket.png`) from the chosen bucket column. Verify the metal pixels still match vanilla `bucket.png`

## 5. Verification

- [x] 5.1 Run `./gradlew compileJava` and verify it succeeds
- [x] 5.2 Run `./gradlew runClientData` and review the `src/generated/resources` diff. `raw_cinnabar_block` is gone and `minecraft:cinnabar` is present in `needs_iron_tool` and `incorrect_for_{wood,gold,stone}_tool`. `raw_cinnabar_block` is gone from `mineable/pickaxe`. A new `loot_modifiers/raw_sulfur/entities/sulfur_cube.json` exists (NeoForge 26.2 discovers modifiers from the folder, so there is no global list to update). No other changes
5.3 In-game checks via `./gradlew runClient` in a new world (need a human):

- [x] 5.3.1 Confirm `logs/latest.log` shows no recipe, tag, loot, or worldgen errors
  - No recipe, tag, or loot errors. The log does show worldgen errors from `GeyserFeature` reading and writing chunks outside its write radius; they also appear in a run from before this change, so they are not caused by it.
- [x] 5.3.2 Confirm both crafting directions for Cinnabar and Sulfur, and that 9 Sulfur still makes Potent Sulfur
- [x] 5.3.3 Confirm a stone pickaxe gets no drop from Cinnabar, an iron one does, and Polished Cinnabar still drops with stone
- [x] 5.3.4 Confirm a Crusher turns Cinnabar into 45 Cinnabar Dust, a Sulfur Spike into 2 Sulfur Dust (plus 1 about a quarter of the time), Sulfur into 9 Sulfur Dust, and Potent Sulfur into 41 plus 40 Sulfur Dust
- [x] 5.3.5 Confirm a size 1 Sulfur Cube drops 1 to 4 Raw Sulfur, more with Looting, a size 2 cube drops none, and with `/gamerule mobDrops false` a size 1 cube drops none
- [x] 5.3.6 Confirm a size 1 Sulfur Cube killed by TNT drops Raw Sulfur, and one that dies in lava leaves no Raw Sulfur, both without an error
- [x] 5.3.7 Confirm a located Cinnabar deposit has a Cinnabar core
- [x] 5.3.8 Confirm every recoloured texture looks right in the inventory and the world, including Sulfuric Acid placed and in an Aqueoulizer tank, and Cinnabar Dust next to Netherrack Dust
- [x] 5.4 At archive, update the Purpose of `openspec/specs/machines/crusher/spec.md` so it covers mineral inputs as well as plants
