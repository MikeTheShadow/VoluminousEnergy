## Context

`DimensionalLaserRecipe` has two ways to build a recipe. The JSON codec calls the
`(FluidMinMax, ClimateData)` constructor, which copies the records into flat fields
(`regionFluid`, `minimumAmount`, `continentalnessMin`, ...). The stream codec uses the no-arg constructor
and setters. The flat fields are the only state either path fills. The codec's `forGetter`s read the
`fluidMinMax` and `climateData` fields, which nothing assigns, so encoding dereferences `null`.
Separately, `VERecipeCodecs.VE_CLIMATE_CODEC` uses `ClimateData::continentalness` as the getter for all
four fields. Decoding is unaffected because `apply` passes fields to the record constructor by position.
Encoding writes continentalness four times.

`IndustrialBlastingRecipe.getHotEnoughFluids()` stores its result in a `static` field, so the first
caller's `minimumHeat` decides the list for every recipe. `IndustrialBlastingCategory` uses that list
directly. `getFluidIngredients()` filters it again by the recipe's own heat, which hides the bug only
when the first caller had the lowest threshold. The machine path goes through
`IndustrialBlastFurnaceParser`, which registers no fluid ingredient position. Its `isCompleteRecipe`
rejects a tank fluid only when its temperature is `< minimumHeat`, never checks the amount, and
`completeRecipe` drains a literal 250 mB. The recipe's own `isFluidHotEnough` uses `>`, the opposite
boundary.

## Goals / Non-Goals

**Goals:**

- One "hot enough" predicate, owned by `IndustrialBlastingRecipe`, used by the parser and by JEI.
- Encoding and decoding a `dimensional_lasing` recipe are inverse operations.
- Gametests that fail on the current code and pass after the fix.

**Non-Goals:**

- Changing how `dimensional_lasing` recipes sync to clients (the stream codec works).
- Moving the Blast Furnace onto `BasicParser`'s generic fluid ingredient handling.

## Decisions

### Laser codec getters derive the records from the flat fields

The `forGetter`s call two private helpers on `DimensionalLaserRecipe` that build `FluidMinMax` and
`ClimateData` (from `FloatPair`s) out of the flat fields. The never-assigned `fluidMinMax` and
`climateData` fields are deleted so nothing can read them again. The unused `minMaxAmounts` field is
not part of this bug and stays.

Alternative: keep the records as fields and assign them in the record constructor. Rejected because
recipes built by the stream codec on the client would still have `null` records, so the trap stays for
any client-side encode.

### Fix the climate codec getters in place

`erosion`, `humidity`, and `temperature` use `ClimateData::erosion`, `::humidity`, and `::temperature`.
`VE_CLIMATE_CODEC` has no other users, so nothing else changes.

### Per-recipe hot-enough list, threshold `>=`

`hotEnoughFluids` becomes a lazily filled instance field. It is filled on first use, which is always
after registries freeze (recipes load after registration, and JEI runs later still). The list is built
with `isFluidHotEnough`, which changes from `>` to `>=`. `getFluidIngredients()` drops its second filter
and maps the list straight to `FluidStack`s of the configured amount.

`>=` was chosen over `>` because the machine already accepts the equal case, and "minimum heat" reads as
inclusive. No shipped fluid sits at 900 K, so the shipped recipes behave the same either way.

Alternative: compute the list on every call with no cache. It scans the fluid registry per call, and
JEI calls it once per layout, so it would work too. The lazy instance field keeps the method cheap for
any future per-tick caller at no cost in clarity.

### Parser uses the recipe's predicate and the config amount

`IndustrialBlastFurnaceParser.isCompleteRecipe` rejects the operation unless
`blastingRecipe.isFluidHotEnough(tankFluid.getFluid())` holds and the tank amount is at least
`Config.BLAST_FURNACE_HEAT_SOURCE_CONSUMPTION.get()`. `completeRecipe` drains that same config value.
Both read the config at call time, as `IndustrialBlastingCategory` already does, so JEI and the machine
cannot drift.

Alternative: register the heat tank with `addFluidIngredient(0, 0)` and let `BasicParser` check and
drain through `getFluidIngredients()`. Rejected for this change. It would also route the tank through
`isPartialRecipe`, which changes which items the machine accepts while the tank holds a cold fluid. That
is a separate behaviour change.

### Gametests at the recipe and parser level

A new `RecipeTests` class in `src/gametest/` is registered from `VEGameTests.all()`, like `StorageTests`:

- **Lasing round trip:** for every loaded `dimensional_lasing` recipe, encode with the serializer's codec
  to `JsonOps`, decode, and compare all getters. Also assert the plains recipe's erosion, humidity, and
  temperature bounds against the shipped JSON values.
- **Per-recipe heat lists:** construct two `IndustrialBlastingRecipe`s at 900 K and 1500 K, query the
  900 K one first, and assert lava is in the first list and not the second. A third at 1300 K asserts
  lava is listed at the boundary.
- **Heat drain:** place a Blast Furnace, stage the loaded steel recipe's items in slots 2 and 3, and call
  that recipe's parser directly. With 1000 mB of lava, `isCompleteRecipe` passes and `completeRecipe`
  leaves 1000 minus the config value. With less lava than the config value, `isCompleteRecipe` fails.
  Calling the parser skips the multiblock casing and power checks in `MultiBlockRecipeProcessor`, which
  this change does not touch.

All three are required tests.

## Risks / Trade-offs

- [Heat fluid lasts 5× longer at default config] → Chosen by the maintainer. The config entry lets packs
  restore 250.
- [Players who set "Heat Source Consumption" expecting no effect see it apply] → Stated in the proposal
  and release notes.
- [A blasting operation now needs the full amount in the tank before starting] → Matches what JEI
  shows. A tank of 40 mB lava used to run one operation and now waits for more.
