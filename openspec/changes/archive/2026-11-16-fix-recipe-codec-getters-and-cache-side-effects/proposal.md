## Why

Two shared codecs write a constant instead of the value they decoded. Encoding any recipe with fluid
ingredients writes `"amount": 1` for every fluid, and encoding combustion fuel data writes
`"energy_per_tick": 1.0`, so a datagen provider or an external tool that writes these back to JSON
produces recipes that consume 1 mB and fuels that make 1 FE/t. Nothing in the mod encodes them today
(recipes are hand-written JSON and sync to clients through the stream codec), which is why the bug is
latent; it becomes live the moment recipe datagen is added. Separately, every `VERecipe` constructor
call appends the recipe to a static map and flips a static flag that nothing reads. Fixing the codecs
adds gametests that build recipes from JSON, and each of those constructions lands in that map, so the
dead state is removed in the same change.

## What Changes

- **Fluid ingredient amounts encode.** Encoding a recipe with `fluid_ingredients` (aqueoulizing,
  centrifugal agitating, distilling, fluid electrolyzing, fluid mixing, fuel combustion, hydroponic
  incubating) writes each ingredient's own `amount`, so encoding and decoding round-trip.
- **Combustion fuel energy encodes.** Encoding a combustion fuel data entry writes its own
  `energy_per_tick`.
- **Recipe construction has no static side effects.** The `VERecipe` constructor no longer adds to a
  static cache or sets a static server-side flag. The write-only cache, the flag, and the client hook
  that fed the cache from network decoding are removed. Which recipes machines and JEI see is still
  decided only by the cache rebuilt from the recipe manager on load, reload, and client recipe sync.

Compatibility:

- **Config:** no changes.
- **Saves and network:** no NBT, data component, attachment, registry id, or packet format changes.
  Recipe sync to clients uses the stream codecs, which this change does not touch.
- **Datapacks:** recipe and fluid data JSON formats are unchanged; decoding behaves as before.

Out of scope: adding recipe datagen, the item ingredient codec (`VE_LAZY_INGREDIENT_CODEC`, whose
getters already round-trip), and the combustion fluid data cache in `CombustibleFluidsData`.

## Capabilities

### New Capabilities

- `recipes/fluid-ingredients`: how a recipe's fluid ingredients round-trip through its JSON form.

### Modified Capabilities

- `machines/combustion-generator`: combustion fuel data entries encode to the FE/t they were loaded with.

## Impact

- **Code:** `util/recipe/VERecipeCodecs.java` (two `forGetter`s), `recipe/VERecipe.java` (constructor,
  `newCache`, `isServerSide`, `addRecipeToCacheClient`), `recipe/serializer/FluidSerializerHelper.java`
  and `recipe/serializer/IngredientSerializerHelper.java` (calls to `addRecipeToCacheClient`).
- **Gametests:** new tests in `RecipeTests` in `src/gametest/`.
- **Balance:** none. Loaded recipes and fuel ratings are unchanged.
- **Follow-up, not scope:** merge forward to `26.1-dev`, `26.2-dev`, and `26.3-dev` once this lands, and
  check whether the same getters and constructor side effects exist there.
