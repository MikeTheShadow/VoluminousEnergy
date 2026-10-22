## 1. Fill routing

- [x] 1.1 Restore the `insert(FluidResource, int, TransactionContext)` override in `MultiFluidSlotWrapper` that inserts into the first index where `canFill` holds and returns 0 when none does, keep `extract` on the NeoForge default, and change the class Javadoc to say an unindexed fill goes to the first tank that accepts it while an unindexed drain spans every tank holding the fluid; verify with `./gradlew compileJava`

## 2. Gametests

- [x] 2.1 Add `storage_fluid_mixer_fill_routing` to `StorageTests`: place a Fluid Mixer, insert 5 000 mB of Hydrogen through the bottom-face capability (expect 4 000 accepted, second input tank empty), insert 1 000 mB more Hydrogen (expect 0), then insert 1 000 mB of Nitrogen (expect 1 000 in the second input tank, first unchanged)
- [x] 2.2 Rename `storage_fluid_multi_tank_transfer` to `storage_fluid_multi_tank_extract`, drop its insert assertions, stage 400 mB and 1 000 mB of Water in the two tanks, and assert a 1 200 mB extract takes 1 200 and leaves 0 and 200

## 3. Verification

- [x] 3.1 Run `./gradlew compileJava` and verify it succeeds (no datagen run needed; no registry, tag, loot, or model change)
- [x] 3.2 Run `./gradlew runGameTestServer` and verify every test passes, including the two from group 2
- [ ] 3.3 In-game check via `./gradlew runClient` (needs a human): pipe Hydrogen into a Fluid Mixer's bottom face until the first input tank is full and confirm the second stays empty, then pipe Nitrogen and confirm it fills the second tank and the mixer makes Ammonia
