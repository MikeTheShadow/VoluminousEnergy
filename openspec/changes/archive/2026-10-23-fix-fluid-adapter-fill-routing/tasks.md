## 1. Fill routing

- [x] 1.1 Override `insert(FluidResource, int, TransactionContext)` in `CapabilityAdapters.FluidHandlerAdapter`: check the preconditions, simulate the wrapper's unindexed `fill`, and if anything fits, update snapshots and execute the fill for the simulated amount; leave `extract` on the default; verify with `./gradlew compileJava`

## 2. Gametests

- [x] 2.1 Add `GameTestLog.assertEqual(helper, actual, expected, name)` that fails with "Expected <name> to be <expected>, but was <actual>", move every `assertValueEqual` call in `StorageTests` to it, and verify with `./gradlew compileGametestJava`
- [x] 2.2 Add a `MachineKit` helper that returns the `ResourceHandler<FluidResource>` from `Capabilities.Fluid.BLOCK` for a face, and verify with `./gradlew compileGametestJava`
- [x] 2.3 Add required fill tests through that handler: 3,999 mB plus 2 mB, an offer of `Integer.MAX_VALUE` mB, a fill naming the second input tank and one naming the output tank, and fills and drains naming tank `-1` and tank `size()`; verify with `./gradlew compileGametestJava`
- [x] 2.4 Add required tests for a drain spanning two Fluid Electrolyzer output tanks, a cancelled unindexed fill, and a confirmed inner fill inside a cancelled outer transaction; verify with `./gradlew compileGametestJava`
- [x] 2.5 Make `storage_fluid_drain_amount_refuses_disabled_side` required on this branch, only if it passes in the same run as the fix

## 3. Verification

- [x] 3.1 Run `./gradlew compileJava compileGametestJava` and confirm both succeed (no datagen run needed: no registry, tag, loot, or model change)
- [x] 3.2 Run `./gradlew runGameTestServer` and confirm every required test passes, including `storage_fluid_mixer_fill_routing`, and that the optional failures are exactly `storage_fluid_fill_refuses_disabled_side`, `storage_fluid_empty_machine_refuses_wrong_fluid`, `storage_fluid_mixer_second_ingredient_first`, `storage_fluid_combustion_oxidizer_routing`, and `storage_fluid_out_of_range_tank_index`, each listed with its logged value
- [x] 3.3 Run `openspec validate fix-fluid-adapter-fill-routing` and confirm the change is valid
- [x] 3.4 In-game check via `./gradlew runClient` (needs a human): with a fluid pipe on a Fluid Mixer's bottom face, pipe Hydrogen past 4,000 mB and confirm the second input tank stays empty, then pipe Nitrogen and confirm it fills the second input tank and the mixer makes Ammonia (confirmed by a human on 2026-10-23 with Pipez on stock settings)
