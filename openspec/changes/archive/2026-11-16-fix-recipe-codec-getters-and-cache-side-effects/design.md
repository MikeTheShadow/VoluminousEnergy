## Context

`VERecipeCodecs.VE_FLUID_INGREDIENT_CODEC` declares `Codec.INT.fieldOf("amount").forGetter((ingredient) -> 1)`
and `REGISTRY_COMBUSTION_FLUID_CODEC` declares `Codec.FLOAT.fieldOf("energy_per_tick").forGetter((ingredient) -> 1f)`.
`apply` passes decoded fields to the record constructors by position, so decoding is correct; only encoding
writes the constant. The records already expose the right accessors (`RegistryFluidIngredient::amount`,
`RegistryFluidValue::value`). Seven recipe types embed the fluid ingredient codec through
`listOf().fieldOf("fluid_ingredients")`. The combustion codec is used only by `CombustibleFluidsData.loadData`,
which parses. No code path encodes either today: recipes are hand-written JSON, there is no recipe datagen,
and client sync goes through `FluidSerializerHelper`/`IngredientSerializerHelper` stream codecs.

`VERecipe` keeps two static maps. `recipeCache` is rebuilt by `updateCache(RecipeManager)` from
`VEServerSideListener` (server start, reload) and `VEClientSideListener` (`RecipesUpdatedEvent`), and is
what `getCachedRecipes`, `getPotentialRecipes`, and `getCompleteRecipe` read. `newCache` is appended to by
the full constructor and by `addRecipeToCacheClient` (called at the end of both serializer helpers'
`fromNetwork`), cleared by `updateCache`, and read by nothing. `isServerSide` is set by the constructor,
never reset, and only gates `addRecipeToCacheClient`. Both are left over from an earlier cache design.

## Goals / Non-Goals

**Goals:**

- Encoding and decoding are inverse operations for both codecs.
- Constructing or network-decoding a `VERecipe` touches no static state.
- Gametests that pin both codecs against every shipped recipe and fuel entry.

**Non-Goals:**

- Adding recipe datagen.
- Changing `recipeCache` or when `updateCache` runs.
- Reworking `CombustibleFluidsData`'s own static cache.

## Decisions

### Point the getters at the record accessors

`forGetter(RegistryFluidIngredient::amount)` and `forGetter(RegistryFluidValue::value)`, matching how the
oxidizer codec beside them is written. No other codec in the file has a constant getter.

### Delete `newCache`, `isServerSide`, and `addRecipeToCacheClient`

The constructor keeps only its field assignments. The two serializer helpers stop calling
`addRecipeToCacheClient`, and the method is deleted rather than left as a no-op so no caller can come to
depend on it. `updateCache` drops its `newCache.clear()`. Alternative considered: keep `newCache` and read
it on the client instead of rebuilding from the recipe manager. Rejected; `RecipesUpdatedEvent` already
supplies the authoritative manager, and a second cache fed from packet decoding is what made the flag
necessary.

### Gametests

All go in `RecipeTests`, registered from its `register` method after the existing entries, and use only
public API.

- `recipe_fluid_ingredient_codec_round_trip`: for every loaded recipe that is a `VERecipe` with a non-empty
  `registryFluidIngredients`, encode with `Recipe.CODEC` through `RegistryOps<JsonElement>`, decode, and
  compare the `RegistryFluidIngredient` lists (records, so `equals` covers tag, fluid, and amount). Also
  assert `distilling/crude_oil` encodes amount 250. Fails today on the first recipe whose amount is not 1.
- `recipe_combustion_fuel_codec_round_trip`: list `fluid_data/combustion/*.json` through the server's
  `ResourceManager`, decode each with `REGISTRY_COMBUSTION_FLUID_CODEC`, encode, decode again, and compare
  records; assert diesel encodes 160. Fails today on every entry.
- `recipe_decoding_leaves_cache_unchanged`: snapshot `VERecipe.getCachedRecipes` for distilling, decode a
  distilling recipe from JSON and pass it through `sendToClient`, then assert the cached list has the same
  size and elements. This is a guard: the dead `newCache` is never read, so no public lookup observes it
  and the test passes before the fix too. Checking `newCache` itself would need reflection on a private
  field that this change deletes, so it is not done.

## Risks / Trade-offs

- [A third-party mod calls `VERecipe.addRecipeToCacheClient`] → It is public, so this is a removal of
  public API. No caller exists in this repository and the method had no effect on lookups; accept the
  break inside an alpha release train.
- [Encoding every loaded recipe surfaces another codec bug] → The round-trip test fails on it with the
  recipe id in the message. If that happens, record it as a separate change rather than widening scope.
