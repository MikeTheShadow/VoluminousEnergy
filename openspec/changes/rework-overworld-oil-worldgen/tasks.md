## 1. Geyser reservoir fix

- [x] 1.1 Change the bottom-section loop bound in `GeyserFeature.place` from `y > 0` to `y >= 0`; verify `./gradlew compileJava` succeeds and the diff touches only that line

## 2. Bituminous blocks and the Bitumen item

- [x] 2.1 Add `Bitumen extends VEItem` in `items/`, following `SaltpeterChunk`, and register it as `bitumen` in `VEItems`; verify it compiles
- [x] 2.2 Add `BituminousBlock extends VEBlock` in `blocks/blocks/ores/` (gravel sound, strength 0.6F, snare instrument, black map colour, `requiresCorrectToolForDrops`, `setRequiresShovel`, `setRequiresWoodAndBlacklistLowerTiers`, registry name from the constructor, not a falling block); verify it compiles
- [x] 2.3 Register `bituminous_gravel`, `bituminous_sand`, and `bituminous_red_sand` with `registerWithBlockItemSupport` under `// Ores` in `VEBlocks`; verify `./gradlew compileJava` succeeds
- [x] 2.4 Add blockstates, `cube_all` block models, and block item models for the three blocks, plus an `item/handheld` model for `bitumen` (matching `saltpeterchunk`), in `src/main/resources/assets/voluminousenergy/`, pointing at `voluminousenergy:block/<id>` and `voluminousenergy:item/bitumen` textures; verify each JSON parses and the paths match the registry ids
- [x] 2.5 Add loot tables for the three blocks shaped like `saltpeterore.json` (Silk Touch drops the block, otherwise 3 to 7 `voluminousenergy:bitumen` with Fortune `ore_drops` and `explosion_decay`), and `Bituminous Gravel`, `Bituminous Sand`, `Bituminous Red Sand`, `Bitumen` entries in `en_us.json`; verify the JSON parses
- [x] 2.6 In `src/main/resources/data/c/tags/`, for both `block` and `item`: add `ores/bitumen.json` with the three blocks and `#c:ores/bitumen` to `ores.json`, with no `ores_in_ground` entries. Add `item/bitumen.json` with only `voluminousenergy:bitumen`. Verify each JSON parses and the block and item trees match

## 3. Shared lake mask

- [x] 3.1 Extract the ellipsoid mask and border test from `VELakesFeature` into a helper in `world/feature/` with descriptive names, and make `VELakesFeature` use it with no behaviour change; verify `./gradlew compileJava` succeeds and the diff of `VELakesFeature` only swaps the inline mask code for helper calls

## 4. Surface lake rim

- [x] 4.1 Change `VELakesFeature.Configuration` to a `RecordCodecBuilder` with `fluidstate` and an optional `perimeter_targets` list (`OreConfiguration.TargetBlockState.CODEC`) defaulting to `always_true → minecraft:stone`, and place the rim from the first matching target; verify it compiles and an unchanged JSON with only `fluidstate` still decodes, using the `ve_bsc_lake_feature` default path
- [x] 4.2 Add colorless sand, red sand, and `always_true` bituminous targets to `worldgen/configured_feature/surface_oil_lake.json`; verify the JSON parses

## 5. Underground oil deposits

- [x] 5.1 Add `VEFluidDepositFeature` using the mask helper: abort on any `FEATURES_CANNOT_REPLACE` block in the mask or border, or any non-solid or fluid border position; fill the whole mask with the fluid source and the border from `perimeter_targets`; register it as `ve_fluid_deposit_feature` in `VEFeatures`; verify `./gradlew compileJava` succeeds
- [x] 5.2 Switch `underground_oil_lake.json` to `voluminousenergy:ve_fluid_deposit_feature` with the three bituminous targets, and set `underground_oil_lakes_overworld.json` anchors to -32 and 47; verify the JSON parses
- [x] 5.3 Remove `isForSurface` and the underground branch from `SurfaceMattersLakesFeature`, and remove `VE_BSC_LAKE_UNDERGROUND_FEATURE` from `VEFeatures`; verify a search for `ve_bsc_underground_lakes_feature` in `src/` returns nothing and `./gradlew compileJava` succeeds

## 6. Verification

- [x] 6.1 Run `./gradlew compileJava` and confirm it succeeds
- [x] 6.2 Run `./gradlew runData`, review the `src/generated/resources` diff, and confirm that only `mineable/shovel` and `needs_wood_tool` gained the three bituminous blocks
- [ ] 6.3 In-game check via `./gradlew runClient` on a new world (maintainer): an underground deposit is fully oil with a complete Bituminous Gravel shell and no air; a plains surface lake has a bituminous rim and open air above; a desert surface lake rim shows Bituminous Sand; bituminous blocks do not fall and sound like gravel; a shovel drops 3 to 7 Bitumen, Silk Touch drops the block, and an empty hand drops nothing; `/tag` or a tag-aware recipe viewer shows the blocks under `#c:ores` and `#c:ores/bitumen` and the item under `#c:bitumen`; an oil geyser's reservoir has no stone ring at its widest layer
