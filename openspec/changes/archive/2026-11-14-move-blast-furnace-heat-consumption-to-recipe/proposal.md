## Why

The Blast Furnace's heat fluid cost per operation is one number for every recipe, set by the "Heat
Source Consumption" startup config entry. Recipe authors (us, and modpack datapacks) cannot make one
operation cost more heat than another, or make a recipe that only needs the heat without burning
through the fluid. Startup config is also read separately on each side and never synced, so on a
dedicated server a client with a different local value sees a JEI amount that the server does not use.

## What Changes

- **Each `industrial_blasting` recipe states its own heat fluid cost.** A new optional
  `heat_source_consumption` field (mB, 0–4000) sets how much hot fluid that recipe needs to start and
  drains when it completes.
- **Without the field, a recipe consumes no heat fluid.** The tank still has to hold a fluid at or
  above the recipe's `minimum_heat_kelvin`, but nothing is drained; the minimum heat is the only
  condition. Writing `0` means the same.
- **The shipped recipes cost 50 mB.** All five shipped blasting recipes (steel, tungsten steel,
  netherite scrap, nighalite, solarium) set `heat_source_consumption` to 50, so players see no change in
  cost. Rebalancing individual recipes is a later decision.
- **JEI shows each recipe's own amount.** The "mB:" figure under the heat tank comes from the recipe
  being shown, and matches the server because recipes are synced to clients. A recipe without
  consumption prints 0.
- **BREAKING (config):** the "Heat Source Consumption" entry is removed from the Blast Furnace section
  of `voluminousenergy-startup.toml`. A value a player set there no longer has any effect; the cost is
  now set per recipe through a datapack.
- **BREAKING (datapacks):** an existing third-party `industrial_blasting` recipe without the new field
  stops consuming heat fluid. Add the field to keep a cost.

Compatibility:

- **Datapacks:** see above. A value outside 0–4000 makes that recipe fail to load with a codec error
  in the log.
- **Saves:** no NBT, data component, attachment, or registry id changes.
- **Network:** the `industrial_blasting` recipe packet gains one int. Client and server must run the
  same mod version, which is already required.
- **Config:** one startup entry removed, nothing added. A leftover entry in an existing toml is
  ignored.

Follow-up, not scope: merge forward from `1.21.1-dev` into `26.1-dev`, `26.2-dev`, and `26.3-dev`
once this lands; the same config read exists on each.

## Capabilities

### New Capabilities

None.

### Modified Capabilities

- `machines/blast-furnace`: the heat-source amount an operation needs, consumes, and shows in JEI
  comes from the recipe instead of the startup config, blasting recipes gain a JSON field for it, and a
  recipe without the field only needs a hot-enough fluid present.

## Impact

- **Code:** `recipe/IndustrialBlastingRecipe.java` (codec field, stream codec, getter, fluid
  ingredients), `recipe/parser/IndustrialBlastFurnaceParser.java` (amount check and drain),
  `compat/jei/category/IndustrialBlastingCategory.java` (displayed amount), `tools/Config.java`
  (entry removed).
- **Data:** the five JSON files under
  `src/main/resources/data/voluminousenergy/recipe/industrial_blasting/`.
- **Gametests:** `RecipeTests` reads the amount from the recipe and gains cases for a non-default
  amount and for a recipe without consumption.
