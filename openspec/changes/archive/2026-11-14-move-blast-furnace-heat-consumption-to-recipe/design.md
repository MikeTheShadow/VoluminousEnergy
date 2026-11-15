## Context

`Config.BLAST_FURNACE_HEAT_SOURCE_CONSUMPTION` (startup config, default 50, range 0–4000) is read in
four places: `IndustrialBlastingRecipe.getFluidIngredients`, `IndustrialBlastFurnaceParser`
(`isCompleteRecipe` amount check and `completeRecipe` drain), `IndustrialBlastingCategory` (the "mB:"
text and the amount on the listed heat fluids), and the `recipe_blasting_heat_source_drain` gametest.
Startup config is loaded per side and not synced, so the JEI figure comes from the client's own toml.

`IndustrialBlastingRecipe` already carries one recipe-specific number, `minimum_heat_kelvin`, through
its `MapCodec` and its hand-written `StreamCodec` (written before `IngredientSerializerHelper.toNetwork`,
read back before `fromNetwork`). The heat-source amount follows the same path.

The heat fluid ingredient from `getFluidIngredients` also gates insertion: `VEItemStackHandler` tests a
bucket's fluid against it before accepting the bucket into the heat slot. `FluidIngredient.of(Stream)`
discards empty stacks, and a 0 mB `FluidStack` is empty, so building that ingredient at 0 mB would
yield an empty ingredient that accepts no heat fluid at all.

## Goals / Non-Goals

**Goals:**

- One source for the heat-source amount, the recipe, used by the machine, JEI, and the gametests.
- A recipe can require heat without consuming the fluid that provides it.

**Non-Goals:**

- Changing any shipped recipe's cost. All five stay at 50 mB.
- Keeping third-party recipes at their old cost; omitting the field means no consumption by design.
- Per-fluid costs (for example hotter fluids costing less). The amount is per recipe, whatever
  hot-enough fluid is in the tank.
- The Primitive Blast Furnace, whose recipes have no heat fluid.

## Decisions

**Field name `heat_source_consumption`, in mB.** It matches the config entry players already know and
reads alongside `minimum_heat_kelvin`. Considered `heat_fluid_amount`; rejected because "heat source" is
the term the JEI page, the config, and the spec already use.

**Optional, absent means 0, declared as `Codec.intRange(0, 4000).optionalFieldOf(..., 0)`.** A missing
field and an explicit 0 behave identically: the minimum heat is the only condition and nothing is
drained. The no-argument constructor (used by network decode and by gametest helpers) leaves the field
at 0, so no default constant is needed. 4000 is the heat tank's capacity (`DEFAULT_TANK_CAPACITY`); a
larger value could never start. An out-of-range value fails the codec, and vanilla's recipe loader logs
it and skips that recipe only.

**Matching ingredient uses at least 1 mB.** `getFluidIngredients` builds the hot-enough fluid stacks at
`Math.max(heatSourceConsumption, 1)` through one private helper, so the ingredient never collapses to
empty and hot-enough buckets stay insertable for a 0 mB recipe. The amount on that ingredient is used
only for matching and display; the parser reads `getHeatSourceConsumption()` directly for the check and
the drain. Alternative considered: a separate fluid-only predicate for insertion. Rejected because it
would change `VEItemStackHandler`, which every machine shares, for one recipe type.

**Parser behaviour at 0.** `isCompleteRecipe` keeps its hot-enough check (an empty tank holds
`Fluids.EMPTY`, which is never hot enough, so "any amount" means at least 1 mB) and its
`amount < consumption` check, which passes trivially at 0. `completeRecipe` skips the drain when the
amount is 0.

**JEI at 0.** The "mB:" text prints the recipe's real amount, 0 included. The heat slot lists the same
stacks as the matching ingredient, so a 0 mB recipe shows its fluids at 1 mB rather than an empty slot.

**Shipped JSON states the field.** The five files under `recipe/industrial_blasting/` get
`"heat_source_consumption": 50` after `minimum_heat_kelvin`. They are hand-written in
`src/main/resources`, not datagen, so no `runData` pass is involved.

**Network order.** `StreamCodec.encode` writes `minimumHeat` then `heatSourceConsumption`, both before
`helper.toNetwork`; `decode` reads them in the same order onto a fresh recipe before
`helper.fromNetwork`.

**Readers switch to a getter.** `getHeatSourceConsumption()` replaces every
`Config.BLAST_FURNACE_HEAT_SOURCE_CONSUMPTION.get()`. The parser already holds its
`IndustrialBlastingRecipe`, and the JEI category receives the recipe in `draw` and `ingredientHandler`,
so no signatures change.

**Remove the config entry outright.** Keeping it as a fallback or multiplier would leave two knobs that
interact and would reintroduce the unsynced client value in JEI. `BLAST_FURNACE_HEAT_SOURCE_CONSUMPTION`
and its `define` call are deleted from `Config`.

**Gametests extend `RecipeTests`.** `recipe_blasting_heat_source_drain` reads the amount from the loaded
steel recipe and asserts it is 50. A new `recipe_blasting_heat_source_codec` test parses blasting JSON
built in the test (steel's ingredients, 900 K) four ways: without the field and with 0 (both: 0 mB, a
placed Blast Furnace with 10 mB of lava runs it and keeps 10 mB, an empty tank does not start, and the
heat fluid ingredient accepts lava), with 250 (200 mB does not start, 1000 mB keeps 750 mB), and with
-1 (expects a decode error). Parsing hand-built JSON uses only the decode path that recipe loading
already exercises.

## Risks / Trade-offs

- [A player who edited "Heat Source Consumption" loses that tuning silently] → Called out as
  **BREAKING** in the proposal and in the release notes under `# Changes`, with the datapack field as
  the replacement.
- [A third-party blasting recipe without the field becomes free to run] → Accepted by the maintainer;
  called out as **BREAKING** for datapacks.
- [A 0 mB recipe's JEI slot hovers as 1 mB while the text says 0] → The text is the stated cost; the
  slot shows which fluids qualify. Acceptable for a datapack-only case.
- [A datapack recipe at 4000 mB fills the whole tank per operation, so automation that tops up the tank
  in small amounts stalls until it is full] → Acceptable; that is the cost the recipe author chose.
- [Mixed-version client and server disagree on the packet layout] → Already unsupported; NeoForge
  refuses the connection on a mod version mismatch.

## Migration Plan

No world or save migration. A leftover "Heat Source Consumption" line in an existing
`voluminousenergy-startup.toml` is not part of the spec any more, so NeoForge's config correction drops
it on the next load. Rollback is reverting the commit; the old codec ignores the extra field, so
recipes that set it still load and cost the config value again.
