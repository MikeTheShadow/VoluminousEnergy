## Why

Light fuel is a refined product of crude oil distillation, so finding it as a raw region fluid under the
Dimensional Laser skips the refining chain. Natural Gas is a raw petroleum resource like crude oil and
fits the laser's role of extracting what lies in the ground.

## What Changes

- Players scanning a chunk whose climate matches either of the two shipped light fuel lasing recipes
  get Natural Gas from the Dimensional Laser instead of light fuel. Climate ranges and the 125–155 mB
  deposit amount stay the same.
- The recipe ids change: `voluminousenergy:dimensional_lasing/light_fuel` becomes
  `voluminousenergy:dimensional_lasing/natural_gas`, and
  `voluminousenergy:dimensional_lasing/light_fuel_hot_eroded_humid` becomes
  `voluminousenergy:dimensional_lasing/natural_gas_hot_eroded_humid`. **BREAKING** for datapacks that
  override or remove those recipes by id.
- JEI's Dimensional Lasing category shows the two recipes with Natural Gas as their output.
- No shipped lasing recipe yields light fuel any more. Light fuel stays obtainable from crude oil in the
  Distillation Unit.

Save compatibility: chunks scanned before the change keep the fluid list stored for them in the
`chunk_fluids` saved data, so a laser already pumping light fuel from such a chunk keeps pumping light
fuel. Only chunks scanned after the update get Natural Gas. No registry id, NBT key, data component,
or attachment changes.

Config: none.

Other branches: the 26.x branches carry the same lasing recipes and need the same switch once this
lands, by merging forward from `1.21.1-dev`.

## Capabilities

### New Capabilities

None.

### Modified Capabilities

- `machines/dimensional-laser`: adds a requirement on which region fluids the shipped lasing recipes
  yield (crude oil and Natural Gas, never light fuel).

## Impact

- `src/main/resources/data/voluminousenergy/recipe/dimensional_lasing/`: `light_fuel.json` and
  `light_fuel_hot_eroded_humid.json` are renamed to `natural_gas.json` and
  `natural_gas_hot_eroded_humid.json` with `region_fluid.fluid` set to `voluminousenergy:natural_gas`.
- `src/gametest/.../RecipeTests.java`: a gametest checks the shipped lasing recipes' region fluids.
- No Java changes in `src/main`; `WorldUtil.queryForFluids` and `DimensionalLaserRecipeProcessor`
  already handle any fluid, and the laser's `FluidOutputTank` accepts gases.
