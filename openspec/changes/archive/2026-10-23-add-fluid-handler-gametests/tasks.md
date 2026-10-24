## 1. Harness

- [x] 1.1 Add the `gametest` source set, its `gametestImplementation`/`gametestRuntimeOnly` configurations, and `modSource project.sourceSets.gametest` on the `server` and `gameTestServer` runs to `build.gradle`, copying 26.3-dev's block; verify `./gradlew compileJava` still succeeds
- [x] 1.2 Generate `src/gametest/resources/data/voluminousenergy/structure/gametest/empty_5x5x5.nbt` (gzipped NBT: `size` 5,5,5, empty `blocks` and `entities`, a `minecraft:air` palette, `DataVersion` 3955) with a throwaway script in the scratch directory; verify by decompressing it and checking the fields
- [x] 1.3 Add `GameTestDefinition` (`name`, `function`, `structure`, `maxTicks`, `required`, with `of` and `optional`), `GameTestLog` (as on 26.3), and `VEGameTests` (`@GameTestHolder(VoluminousEnergy.MODID)`, `all()` with a `smoke` test, and one `@GameTestGenerator` turning it into `TestFunction`s named `voluminousenergy.<name>` on structure `voluminousenergy:gametest/empty_5x5x5`); verify `./gradlew compileGametestJava` succeeds
- [x] 1.4 Run `./gradlew runGameTestServer` and verify the log lists `voluminousenergy.smoke` and the run passes; if no tests are found, apply the design's fallback and record it here before continuing

## 2. Required fluid tests

- [x] 2.1 Add `MachineKit` with `place`, `fluids` (the face's fluid capability), `setTank`, and `setTankSide`, and an empty `StorageTests.register` wired into `VEGameTests.all()`; verify `./gradlew compileGametestJava` succeeds
- [x] 2.2 Add `storage_fluid_fill_capacity`, `storage_fluid_mixer_fill_routing`, and `storage_fluid_held_fluid_refuses_other` (fill scenarios in the spec, sides enabled, run after a 2-tick delay); verify they pass in `./gradlew runGameTestServer`
- [x] 2.3 Add `storage_fluid_drain_rules` and `storage_fluid_drain_disabled_side`, setting and restoring `Allow Extraction from Input Tanks` in `finally`; verify they pass in `./gradlew runGameTestServer`
- [x] 2.4 Add `storage_fluid_electrolyzer_drains` covering the named-fluid, larger-than-tank, and amount-only scenarios on a Fluid Electrolyzer; verify it passes in `./gradlew runGameTestServer`
- [x] 2.5 Add `storage_fluid_empty_requests` and `storage_fluid_simulate`; verify they pass in `./gradlew runGameTestServer`

## 3. Optional gap tests

- [x] 3.1 Add the six optional tests from the design's catalogue with `GameTestDefinition.optional`, each asserting the intended result and logging observed amounts with `GameTestLog.value`; verify `./gradlew runGameTestServer` still passes and record here which optional tests fail (the gap is real) and which pass (the gap is not, so promote that test to required and update the proposal's follow-ups)
  - Run passes with "6 optional tests failed"; every gap is real. Observed: a disabled input side accepted 1 000 mB of Crude Oil; a drain by amount took 500 mB of Oxygen through a disabled side; an empty Distillation Unit accepted 1 000 mB of Lava; Nitrogen into an empty Fluid Mixer went to the first input tank; Oxygen into an empty Combustion Generator went to the fuel tank; `isFluidValid(-1, ...)` threw `IndexOutOfBoundsException`

## 4. Docs

- [x] 4.1 Update `AGENTS.md` (verification step 3), `openspec/config.yaml` (context), and `.serena/memories/suggested_commands.md` to say gametests live in `src/gametest/` and run with `./gradlew runGameTestServer`, keeping the wording in each file's style; verify with `grep -n "gametest" AGENTS.md openspec/config.yaml .serena/memories/suggested_commands.md`

## 5. Verification

- [x] 5.1 Run `./gradlew compileJava` and verify it succeeds (no datagen run needed: no registry, tag, loot, or model change)
- [x] 5.2 Run `./gradlew runGameTestServer` and verify every required test passes, with the optional failures matching task 3.1's record
- [x] 5.3 Run `./gradlew build` and verify the jar in `build/libs/` contains no `com/veteam/voluminousenergy/gametest/` classes and no `structure/gametest/` resource
- [x] 5.4 No in-game check needed: nothing under `src/main` changes, so players see no difference
