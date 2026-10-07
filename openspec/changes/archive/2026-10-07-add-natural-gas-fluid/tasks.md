## 1. Fluid registration

- [x] 1.1 Add `fluids/NaturalGas.java` mirroring `Hydrogen.java` (gas source/flowing with width 4, `VEFlowingFluidBlock`, bucket stacking to 1 with an empty-bucket remainder, `VEFluidType` with Hydrogen's properties and `natural_gas_still`/`natural_gas_flowing` texture ids); verify with `./gradlew compileJava`
- [x] 1.2 Add a `// Natural Gas` block to `VEFluids` after Hydrogen registering `natural_gas`, `flowing_natural_gas`, `natural_gas_block`, `natural_gas_bucket`, and fluid type `natural_gas`; verify with `./gradlew compileJava`
- [x] 1.3 Register the Natural Gas client fluid type extension in `VEClientSideListener` after Hydrogen; verify with `./gradlew compileJava`

## 2. Textures and models

- [x] 2.1 Generate `textures/block/fluids/natural_gas_still.png` and `natural_gas_flowing.png` by remapping Hydrogen's textures through the design.md palette tables, and copy Hydrogen's two `.mcmeta` files; verify sizes are 16x512 and 32x1024 and the colour sets equal the tables exactly
- [x] 2.2 Generate `textures/item/natural_gas_bucket.png` by remapping `hydrogen_bucket.png` through the design.md bucket table; verify it is 16x16 and its only non-grey colours are the five listed
- [x] 2.3 Add `models/item/natural_gas_bucket.json` pointing at `voluminousenergy:item/natural_gas_bucket`; verify the path resolves to the file from 2.2

## 3. Localisation

- [x] 3.1 Add `fluid.`, `fluid_type.`, `block.` (`natural_gas_block`), and `item.` (`natural_gas_bucket`) entries to `en_us.json` ("Natural Gas", "Natural Gas Bucket") and `ja_jp.json` ("天然ガス", "天然ガスバケツ") next to Hydrogen's; verify both files still parse as JSON

## 4. Tags and combustion data

- [x] 4.1 Add `data/c/tags/fluid/natural_gas.json` (`replace: false`, `voluminousenergy:natural_gas`) and append the fluid to `data/c/tags/fluid/gaseous.json`; verify both parse as JSON
- [x] 4.2 Append `voluminousenergy:natural_gas` to `data/voluminousenergy/tags/fluid/combustible.json`, and add `fluid_data/combustion/natural_gas.json` (`c:natural_gas`, 24) and `recipe/fuel_combustion/natural_gas.json` (copy of crude oil's with `c:natural_gas`); verify all parse as JSON

## 5. Verification

- [x] 5.1 `./gradlew compileJava` succeeds
- [x] 5.2 `./gradlew runData` runs; confirm `src/generated/resources` has no diff (all new assets and data are hand-written), or review and include any diff it does produce
- [x] 5.3 `./gradlew runServer` starts a dedicated server on a fresh world without errors (dedicated-server safety of the client registration); Gradle does not forward console input to the run, so a temporary load-function datapack confirmed the block places as `voluminousenergy:natural_gas`, the fluid is in `c:natural_gas`, `c:gaseous`, and `voluminousenergy:combustible`, and a `voluminousenergy:natural_gas_bucket` item spawns
- [x] 5.4 In-game check via `./gradlew runClient` (needs a human): `/give @p voluminousenergy:natural_gas_bucket` gives one bucket; bucket in the creative tab with the purple fill and the right name; emptied Natural Gas rises and spreads at most 4 blocks; still and flowing textures animate; Natural Gas next to Nitrogen reads purple versus blue-lavender; a Combustion Generator burns Natural Gas with Oxygen at 24 FE/tick times Oxygen's multiplier; with JEI installed, the bucket shows the combustion recipe. Maintainer confirmed in game that Natural Gas places and looks right; the Combustion Generator output and the JEI lookup were not reported separately and remain to be spot-checked
