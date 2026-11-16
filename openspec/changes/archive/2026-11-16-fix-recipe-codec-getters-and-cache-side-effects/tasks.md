## 1. Setup

- [x] 1.1 Cut work branch `1.21.1-recipe-codec-getters-and-cache-side-effects` from `1.21.1-dev`; verify with `git branch --show-current`

## 2. Failing gametests first

- [x] 2.1 Add `recipe_fluid_ingredient_codec_round_trip` to `RecipeTests`: encode every loaded `VERecipe` with non-empty fluid ingredients through `Recipe.CODEC`, decode, compare the fluid ingredient lists, and assert `distilling/crude_oil` encodes amount 250; verify it fails on `./gradlew runGameTestServer` with the current code
- [x] 2.2 Add `recipe_combustion_fuel_codec_round_trip` to `RecipeTests`: decode each `fluid_data/combustion/*.json` from the server resource manager, encode, decode again, compare, and assert diesel encodes 160; verify it fails on `./gradlew runGameTestServer` with the current code
- [x] 2.3 Add `recipe_decoding_leaves_cache_unchanged` to `RecipeTests`: decode a distilling recipe from JSON, send it through its stream codec, and assert the cached distilling recipes are unchanged; verify it passes on `./gradlew runGameTestServer` (guard test, passes before and after)

## 3. Codec getters

- [x] 3.1 In `VERecipeCodecs`, use `RegistryFluidIngredient::amount` for `VE_FLUID_INGREDIENT_CODEC`'s `amount` getter; verify the 2.1 test passes on `./gradlew runGameTestServer`
- [x] 3.2 In `VERecipeCodecs`, use `RegistryFluidValue::value` for `REGISTRY_COMBUSTION_FLUID_CODEC`'s `energy_per_tick` getter; verify the 2.2 test passes on `./gradlew runGameTestServer`

## 4. Recipe construction side effects

- [x] 4.1 Remove the `addRecipeToCacheClient` calls from `FluidSerializerHelper.fromNetwork` and `IngredientSerializerHelper.fromNetwork`
- [x] 4.2 In `VERecipe`, delete `newCache`, `isServerSide` with its comment, `addRecipeToCacheClient`, the constructor's cache and flag code, and `newCache.clear()` in `updateCache`, plus any imports left unused; verify `./gradlew compileJava` and that the 2.3 test still passes

## 5. Verification

- [x] 5.1 `./gradlew compileJava` succeeds
- [x] 5.2 `./gradlew runGameTestServer` passes with every required test green, including the three new ones
- [x] 5.3 `./gradlew runData` is not needed: no registries, tags, loot, or models change; confirm `git status` shows nothing under `src/generated/resources`
- [x] 5.4 In-game check via `./gradlew runClient`: on a singleplayer world, and on a dedicated server via `./gradlew runServer` joined from the client, a Distillation Unit with 1000 mB crude oil and power distills, and JEI shows the distilling recipe with 250 mB crude oil
