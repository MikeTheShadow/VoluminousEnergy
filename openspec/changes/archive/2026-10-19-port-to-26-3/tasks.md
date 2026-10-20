Until group 9, `./gradlew compileJava` still fails elsewhere. A task is verified when
`./gradlew compileJava 2>&1 | grep 'src/main/java.*error:'` lists no errors in the files that task
names. Test IDs (T0.1 and so on) refer to `test-plan.md`.

## 1. Machine storage core (design decision 1)

- [x] 1.1 Rebase `VEItemStackHandler` on `ItemStacksResourceHandler`: `isValid` override with the
  existing validator, upgrade-slot, and recipe rules; `onContentsChanged` override with the
  existing dirty flags; copy-returning reads; owner operations for taking ingredients, adding a
  result (returning the remainder), and checking fit; 26.2 `Items`/`Size` serialization. Verify no
  compile errors in `VEItemStackHandler`
- [x] 1.2 Rebase `VEEnergyStorage` on `SimpleEnergyHandler`, keeping production, consumption,
  upgrade slot, add/consume/set clamping, `isFullyCharged`, and the 26.2 keys; verify no compile
  errors in `VEEnergyStorage`
- [x] 1.3 Add `VEFluidTank` on a one-index `FluidStacksResourceHandler` with capacity and validator
  overrides, copy-returning reads, fill/drain owner operations, and 26.2 `Fluid` serialization;
  switch `VERelationalTank` to it; verify no compile errors in both
- [x] 1.4 Remove the extraction hook and the unused `simulate` parameter from
  `AbstractItemStackValidator` and its six implementations; verify no compile errors in
  `blocks/tiles/inventory/`
- [x] 1.5 Rewrite `MultiSlotWrapper` and `MultiFluidSlotWrapper` as filtering `ResourceHandler`s
  over the real handlers with the existing side, slot-type, and `Allow Extraction from Input
  Tanks` rules; verify no compile errors in both
- [x] 1.6 Port `CapabilityMap`, delete `CapabilityAdapters`, and return the side views and
  `VEEnergyStorage` from `VEGenericListener`'s block capabilities; verify no compile errors in those
  files
- [x] 1.7 Port `VETileEntity` (bucket handling, save and load with the `inv`, `energy`, and
  per-tank child keys, side moves, `getFluidStackFromTank` returning a copy) and
  `VETileEntityFactory`; verify no compile errors in both
- [x] 1.8 Port `VESlotManager`, `TextUtil`, `VERender.renderGuiTank`, and `BucketInputOutputUtil`;
  verify no compile errors in those files

## 2. Recipe processors and parsers (owner operations)

- [x] 2.1 Move `BasicParser`, `RNGBasicParser`, `SawmillParser`, `ImplosionCompressorParser`, and
  `IndustrialBlastFurnaceParser` to owner operations; verify no compile errors in those files
- [x] 2.2 Move `HydroponicParser` to tank owner operations instead of editing live `FluidStack`s;
  verify no compile errors in it
- [x] 2.3 Move `BasicProcessor`, `ElectricFurnaceProcessor`, `GasFiredFurnaceProcessor`,
  `CombustionGeneratorProcessor`, `GeneratorProcessor`, `AirCompressorProcessor`, and
  `PumpTileProcessor` to owner operations, turning simulated calls into fit checks; verify no
  compile errors in those files
- [x] 2.4 Move `BatteryBoxProcessor`, `ToolingStationProcessor`, and
  `DimensionalLaserRecipeProcessor` to owner operations plus item capabilities (group 3); verify no
  compile errors in those files

## 3. Item capabilities and items (design decisions 2 and 7)

- [x] 3.1 Change `VEItemCapabilities` to return `EnergyHandler` and
  `ResourceHandler<FluidResource>`, and add the machine-to-item transaction helper beside
  `BucketInputOutputUtil`; verify no compile errors in both
- [x] 3.2 Port `VEEnergyItem`, `Multitool`, `FluidScanner` (`PlayerInventoryWrapper`),
  `AmmoniumNitrateBucket`, and `VEItemStackWithFluidHandler`; verify no compile errors in those
  files
- [x] 3.3 Move every solid fuel to `cookingFuel` with a mod burn-time provider in vanilla's form
  (furnace ticks unchanged, halved in smokers and blast furnaces as on 26.2) and remove
  `getBurnTime`; verify no compile errors in `items/solid_fuels/`
- [x] 3.4 Set chain bits to no abilities and scooper bits to `SHOVEL_DOUSE` in
  `VEMultitoolBitData`; verify no compile errors in `items/tools/multitool/`

## 4. Menus and screens (design decision 3)

- [x] 4.1 Rebase `VEContainerFactory.VESlot` on `ResourceHandlerSlot` over `VEItemStackHandler`
  with `set` as the index modifier, and port `VEContainerFactory` and `VEContainer`, changing any
  in-place edit of `slot.getItem()` to write back; verify no compile errors in both
- [x] 4.2 Port `SlotWithIOListener`, `ToolingStationSlotWithIOListening`, and `ExperienceListener`
  the same way; verify no compile errors in `blocks/containers/iolisteners/`
- [x] 4.3 Port `DimensionalLaserScreen` and `PrimitiveStirlingGeneratorScreen`; verify no compile
  errors in `blocks/screens/`

## 5. Worldgen features and ores (design decisions 4 and 6)

- [x] 5.1 Change `VEFeatures.VE_FEATURE_REGISTRY` to a `Registries.FEATURE_TYPE` register of
  `MapCodec<? extends Feature>` under the same names; verify no compile errors in `VEFeatures`
- [x] 5.2 Convert `VELakesFeature`, `SurfaceMattersLakesFeature`, `VEFluidDepositFeature`, and
  `LakeMask` to records implementing `Feature`; verify no compile errors in those files
- [x] 5.3 Convert `GeyserFeature`, `RiceFeature` (with a `state` field), and
  `VEOreDepositFeature`; verify no compile errors in those files
- [x] 5.4 Convert `VEFluidReservoirFeature`, and port `FluidReservoirCells` to a per-instance
  climate sampler and the aquifer `surfaceLevel` sampler, falling back to the generator's surface
  height when there are no aquifer settings; verify no compile errors in both
- [x] 5.5 Switch `VEOreGeneration` targets to `BlockReplacement`, and add the `RegistryAccess`
  parameter to `VEOreBiomeModifier`, `VEAndedMultiBiomeModifier`, and `VEModifiers`; verify no
  compile errors in `world/ores/` and `world/modifiers/`
- [x] 5.6 `git mv` the 20 `worldgen/configured_feature/*.json` files to `worldgen/feature/` and
  hoist each `config` object to the top level; verify with T0.3
- [x] 5.7 Rewrite `worldgen/noise/petroleum_reservoir_boundary.json` as
  `{"base_octave": -4, "octave_count": 2, "base_amplitude": 0.9494731054427981}`; verify with T1.1
- [x] 5.8 Let `VEFluidReservoirFeature` fill seagrass, tall seagrass, kelp, and kelp plants as it
  fills water, so a neighbour's seagrass cannot leave a predicted fill unplugged; verify with T3.5

## 6. Petroleum caves as a feature (design decision 5)

- [x] 6.1 Replace `VEFluidCaveCarver` with `VEFluidCaveFeature` (feature type
  `ve_fluid_cave_feature`, existing fields plus `biomes`). It replays tunnels from every source
  chunk within 2 chunks with carver-style seeding and the unchanged `traceOutline`, and writes fluid
  and shell only inside the decorated chunk. Remove `VECarvers` and its registration; verify no
  compile errors in `world/`
- [x] 6.2 Move `configured_carver/crude_oil_cave.json` and `natural_gas_cave.json` to
  `worldgen/feature/` in the new shape with `"biomes": "#minecraft:is_overworld"`, add matching
  `placed_feature` files with empty placement, and change
  `biome_modifier/petroleum_caves_overworld.json` to `neoforge:add_features` in step
  `raw_generation`; verify with T0.3

## 7. Climate and deposit noise (design decision 6)

- [x] 7.1 Add the shared octave noise helper in `util/climate/` on 26.3 `SimplexNoise`, returning
  [-1, 1], and use it in `FluidClimateSpawn` and `WorldUtil.fallbackFluidAmount`; verify no compile
  errors in both
- [x] 7.2 Rewrite `WorldUtil.sampleClimate` with `createClimateSampler(SamplerContext.EMPTY_UNCACHED)`
  and unquantized axes; verify no compile errors in `WorldUtil`

## 8. Blocks, fluids, loot, advancements, rendering, datagen

- [x] 8.1 Replace `PushReaction.DESTROY` with `POPPED` in every fluid class and `PressureLadder`;
  verify no compile errors in `fluids/` and `PressureLadder`
- [x] 8.2 Mirror vanilla 26.3 `FlowingFluid`'s `canHoldAnyFluid` in `VEFlowingGasFluid`; verify no
  compile errors in it
- [x] 8.3 Add `BonemealSource` to `VELandCrop` and `VEWaterCrop` and fix their codecs; verify no
  compile errors in `blocks/blocks/crops/`
- [x] 8.4 Port `AnimalFatLootModifier` and `MysteriousMultiplierModifier` to the single optional
  condition, and `VEGlobalLootModifierData` to `AllOfCondition` and the enchantment lookup; verify no
  compile errors in `loot/` and `datagen/`
- [x] 8.5 Port `ConstructDimensionalLaserTrigger` to an `Optional<Holder<LootItemCondition>>`
  player; verify no compile errors in it
- [x] 8.6 Switch `LaserBlockEntityRenderer` to `PoseStack.rotate`, and fix
  `MultiBlockStateMatchRuleTest` and the data-gathering lookup in `VoluminousEnergy`; verify no
  compile errors in those files
- [x] 8.7 Fix anything `compileJava` reports in `compat/jei/` against JEI 31.16.0.84 without changing
  categories or recipe wiring; verify no compile errors in `compat/`
- [x] 8.8 Convert the 57 hand-written block loot tables and the `stuck_in_oil` advancement to 26.3's
  loot formats (`condition`/`modifier`, `type` keys, `match_block`), keeping their conditions and
  functions; verify `./gradlew runGameTestServer` loads the mod's data with no registry errors
- [x] 8.9 Remove `VERecipe`'s constructor write to its unread static cache, which 26.3's parallel
  recipe decoding turned into a crash; verify the same data load

## 9. Build, datagen, and docs

- [x] 9.1 Run `./gradlew compileJava` and fix remaining errors until it succeeds (T0.1)
- [x] 9.2 Run `./gradlew runClientData` and review the `src/generated/resources` diff (T0.2)
- [x] 9.3 Update `AGENTS.md`, `openspec/config.yaml`, and `.serena/memories/` from 26.2 to 26.3,
  including the transfer API storage model, feature records, and the gametest source set and
  `autotest.py`; verify
  `grep -rn "26\.2" AGENTS.md openspec/config.yaml .serena/memories` shows only intentional history
- [x] 9.4 Run `./gradlew build` and confirm it succeeds (T0.1)

## 10. Test infrastructure (design decision 9)

- [x] 10.1 Add the `gametest` source set with Java and resources, wired only into the
  `gameTestServer` and `server` runs as part of the `voluminousenergy` mod, and confirm the built
  jar contains no gametest classes. If NeoGradle cannot merge the source set into the mod, use the
  `gametest` package fallback from design decision 9 and say so
- [x] 10.2 Register gametests through `RegisterGameTestsEvent` with a shared environment, and
  generate the small empty test structures once; verify `./gradlew runGameTestServer` runs an
  always-pass smoke test
- [x] 10.3 Write `src/gametest/autotest/autotest.py` (standard library only): Gradle init script
  that moves the server and client runs into the throwaway directory, per-world
  `server.properties`, RCON client, server and `xvfb-run` client launch, log scan, and
  `report.md`. Verify `autotest.py t0` runs, and that `runs/server/server.properties` stays
  byte-identical (`sha256sum` before and after)
- [x] 10.4 Write the T1 offline checks (noise parity, random-source identity); verify they pass
- [x] 10.5 Write the T2 gametests: storage and transfer (T2.1–T2.16), recipe smoke (T2.17–T2.18),
  menus (T2.19–T2.22), items (T2.23–T2.28), blocks, fluids, loot, and advancements (T2.29–T2.35),
  and fluid deposits (T2.36–T2.39); verify each group compiles and runs
- [x] 10.6 Write the T3 `manual_only` worldgen tests, the `wg_pregen` helper, and the test
  datapacks for T3.13 and T3.14; verify they run on a throwaway server
- [x] 10.7 Write the T4 steps: the `26.2-dev` worktree, the commands that fill every machine on the
  26.2 build, the fixture capture into T2.3, and the T1.3 source parity check against `26.2-dev`
- [x] 10.8 Write the T5 client smoke: build area setup, `xvfb-run` launch with `--gameDir` and
  quick play, screenshots, and log scan

## 11. Run the test plan

- [x] 11.1 `autotest.py t0` and `t1` pass
- [x] 11.2 `autotest.py t2` (`./gradlew runGameTestServer`) passes with every required test green
- [x] 11.3 `autotest.py t3` passes, with the seed and survey size recorded
- [x] 11.4 `autotest.py t4` passes, and the 26.2 worktree is removed afterwards
- [x] 11.5 `autotest.py t5` passes, with screenshots reviewed by the agent
- [x] 11.6 Confirm the user's worlds are untouched: the modification times and `sha256sum` of
  `runs/server/server.properties`, `runs/server/world*/level.dat`, and
  `runs/client/saves/*/level.dat` match a snapshot taken before 11.1
- [x] 11.7 Report `report.md` to the user, and hand over the T6 human checks (machine GUIs, gas and
  laser visuals, JEI categories)
