## Why

Two recipe types disagree with what they claim. Encoding a `dimensional_lasing` recipe through its codec
throws a NullPointerException, so any tool or datagen provider that writes recipes back to JSON crashes on
it. The Blast Furnace's JEI page shows a heat-fluid list and a heat-fluid amount that the machine does not
use: the list is cached once for every recipe, and the machine drains a hard-coded 250 mB while JEI and
the config say 50 mB.

## What Changes

- **Dimensional lasing recipes encode.** Writing a `dimensional_lasing` recipe through its codec produces
  JSON with the same region fluid, amounts, and four climate ranges it was loaded from, instead of
  throwing. The climate codec also stops writing continentalness into the erosion, humidity, and
  temperature fields.
- **Each Blast Furnace recipe lists its own heat fluids.** The JEI page for a recipe shows every fluid hot
  enough for that recipe's minimum heat, not the list built for whichever recipe JEI laid out first.
  With the shipped recipes (all 900 K) nothing looks different; datapacks that add recipes with other
  thresholds now display correctly.
- **JEI and the machine agree on "hot enough".** A fluid whose temperature equals the recipe's minimum
  heat runs in the machine today but is missing from JEI. JEI now lists it, matching the machine.
- **The Blast Furnace consumes the configured heat fluid amount.** Each operation drains the "Heat
  Source Consumption" config value (default 50 mB) instead of a hard-coded 250 mB, and does not start
  unless the heat tank holds at least that much. A bucket of lava now lasts 20 operations instead of 4.
  Today an operation starts with any amount of hot fluid in the tank, even 1 mB.

Compatibility:

- **Config:** no new entries and no changed defaults. "Heat Source Consumption" (startup config, default
  50, range 0–4000) was already there but unused by the machine. Players who had edited it see their
  value take effect for the first time.
- **Saves and network:** no NBT, data component, attachment, registry id, or packet format changes.
- **Datapacks:** recipe JSON formats are unchanged.

Out of scope: Dimensional Laser energy use and climate matching, the Primitive Blast Furnace, and fluid
temperatures themselves.

## Capabilities

### New Capabilities

- `machines/dimensional-laser`: how a `dimensional_lasing` recipe round-trips through its JSON form.

### Modified Capabilities

- `machines/blast-furnace`: which heat fluids JEI lists for a recipe, which fluids the machine accepts as
  hot enough, and how much heat fluid an operation needs and consumes.

## Impact

- **Code:** `recipe/DimensionalLaserRecipe.java` (codec getters), `util/recipe/VERecipeCodecs.java`
  (`VE_CLIMATE_CODEC` getters), `recipe/IndustrialBlastingRecipe.java` (hot-enough list and threshold),
  `recipe/parser/IndustrialBlastFurnaceParser.java` (amount check and drain).
- **Gametests:** a new recipe test class in `src/gametest/`, registered in `VEGameTests`.
- **Balance:** Blast Furnace heat fluid cost drops 5× at default config. Other machines are unaffected.
- **Follow-up, not scope:** the same defects exist on `26.1-dev`, `26.2-dev`, and `26.3-dev`. Merge forward
  from `1.21.1-dev` once this lands.
