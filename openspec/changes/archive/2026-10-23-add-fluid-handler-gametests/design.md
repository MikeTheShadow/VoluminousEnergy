## Context

Every machine with tanks exposes one `MultiFluidSlotWrapper` per face through `CapabilityMap`,
registered for `Capabilities.FluidHandler.BLOCK` in `VEGenericListener`. A wrapper holds the
`VERelationalTank`s assigned to its face. Tanks are built by `VETileEntityFactory.FluidInputTank`
and `FluidOutputTank` as `new FluidTank(capacity)`, start on `Direction.DOWN`, and start with their
side disabled.

What the 1.21.1 wrapper does today, from reading it:

- `fill` walks the face's non-output tanks and fills the first that passes `isFluidValid` and is
  empty or holds the same fluid. It never checks `getSideStatus()`.
- `isFluidValid` returns true for `allowAny` tanks, defers to a validator if there is one, then
  checks the processor's potential recipes, and finally falls back to `FluidTank.isFluidValid`,
  which is always true for these tanks. The recipe check therefore never refuses anything; only
  tanks with a validator (the Gas Fired Furnace's) are restricted.
- `drain(FluidStack)` checks the side status and the `Allow Extraction from Input Tanks` option and
  drains the first tank holding the fluid. `drain(int)` checks only the option.
- `isFluidValid` throws for an index outside `0..getTanks()-1`.

NeoForge 21.1 still uses vanilla 1.21.1's `GameTestRegistry`: tests come from `@GameTest` and
`@GameTestGenerator` methods, found through `RegisterGameTestsEvent` or classes annotated
`@GameTestHolder`, and `neoforge.enabledGameTestNamespaces` filters generated tests by the namespace
of their structure name. 26.3-dev registers through the test function registry instead.

## Goals / Non-Goals

**Goals:**
- A gametest harness on 1.21.1 that runs in `./gradlew runGameTestServer` and never ships in the jar.
- Required tests for every scenario in `specs/machines/storage-and-transfer/spec.md`.
- Optional tests that state the intended behaviour for each gap above, so fixing a gap is visible as
  an optional failure turning into a pass.

**Non-Goals:**
- Fixing any gap. `src/main` is untouched.
- Item, energy, save, menu, or recipe tests from 26.3's plan. The harness makes them easy to add later.
- 26.3's autotest harness and fixtures.

## Decisions

- **Source set and runs as on 26.3.** `build.gradle` gains a `gametest` source set compiled against
  `main`, with `gametestImplementation` and `gametestRuntimeOnly` extending the main configurations,
  and `modSource project.sourceSets.gametest` on the `gameTestServer` and `server` runs. Copying 26.3's
  block keeps the forward-merge diff small. Alternative: a `gametest` package in `main`, which is
  26.3's documented fallback; it ships test code in the jar, so it is used only if NeoGradle 7.1.21
  cannot load the second source set as part of the mod.

- **Register through one generator.** `VEGameTests` is `@GameTestHolder(VoluminousEnergy.MODID)` and has
  a single `@GameTestGenerator` method that turns `all()` into `TestFunction`s. The holder annotation
  is found by NeoForge's scan, so no event subscriber is needed. `all()` and the per-area
  `register(List<GameTestDefinition>)` methods match 26.3, so only the generator differs between
  branches. Alternative: one `@GameTest` method per test, which repeats annotation arguments and
  diverges from 26.3's list.

- **Test names are `voluminousenergy.<name>`.** `/test run` reads names as unquoted strings, which
  cannot contain `:`, so 26.3's `voluminousenergy:<name>` ids become a dotted prefix. The `<name>`
  part matches 26.3 where the test exists there (`storage_fluid_drain_rules`,
  `storage_fluid_mixer_fill_routing`).

- **`GameTestDefinition` carries `required`.** The record is
  `(name, function, structure, maxTicks, required)` with `of(...)` for required tests and
  `optional(...)` for gap tests. It omits 26.3's `LARGE` structure and `manual` factory, which no
  1.21.1 test needs yet. The generator passes `required` straight to `TestFunction`; vanilla prints
  failed optional tests in the summary without failing the run.

- **Own structure file.** `empty_5x5x5.nbt` is written for data version 3955 (1.21.1). 26.3's copy
  has data version 5023, newer than this game, which the structure loader would try to upgrade from
  a version it does not know. The file is gzipped NBT with only `size`, an empty `blocks` and
  `entities`, a one-entry `minecraft:air` palette, and `DataVersion`; it is generated once with a
  throwaway script outside the repo.

- **Tests go through the capability.** Each test looks up `Capabilities.FluidHandler.BLOCK` on the
  machine's bottom face with `level.getCapability`, so the face mapping in `CapabilityMap` is covered
  along with the wrapper. Contents are staged with `getRelationalTank(id).getTank().setFluid(...)` and
  side settings with `updateTankPacketFromGui(boolean, id)`, the same calls the GUI packets make.

- **Required fill tests enable the tank side first.** Today fills ignore the side, but the follow-up
  makes a disabled side refuse them. Enabling the side keeps the required tests valid across that
  fix, and the optional `storage_fluid_fill_refuses_disabled_side` test covers the disabled case.

- **Fills wait two ticks after placing.** Potential recipes are computed on the machine's tick, so the
  fill tests run in `runAfterDelay(2, ...)` as 26.3's do. Today's fallback makes this irrelevant, but
  it will matter once empty tanks are restricted by recipe.

- **The extraction option is set and restored inside one test function.** Tests read
  `Config.ALLOW_EXTRACTION_FROM_INPUT_TANKS`, set what they need, and restore it in `finally`, all in
  one call on the server thread, so tests sharing a batch never see the changed value.

- **Test catalogue.** Required tests, one per spec scenario group:

  | Test | Machine | Spec scenarios |
  |---|---|---|
  | `smoke` | none | harness loads |
  | `storage_fluid_fill_capacity` | Distillation Unit | fills up to capacity |
  | `storage_fluid_mixer_fill_routing` | Fluid Mixer | no overflow; second ingredient |
  | `storage_fluid_held_fluid_refuses_other` | Distillation Unit | holding one fluid refuses another |
  | `storage_fluid_drain_rules` | Distillation Unit | output drains; config gates input |
  | `storage_fluid_drain_disabled_side` | Distillation Unit | disabled side refuses named drain |
  | `storage_fluid_electrolyzer_drains` | Fluid Electrolyzer | named fluid; larger than tank; amount only |
  | `storage_fluid_empty_requests` | Distillation Unit | empty requests |
  | `storage_fluid_simulate` | Fluid Mixer, Distillation Unit | simulated fill and drain |

  Optional tests, each asserting the intended behaviour of one gap:

  | Test | Setup | Intended result |
  |---|---|---|
  | `storage_fluid_fill_refuses_disabled_side` | Distillation Unit, input side disabled, pipe offers Crude Oil | 0 accepted |
  | `storage_fluid_drain_amount_refuses_disabled_side` | Fluid Electrolyzer, Oxygen in a disabled output, pull 500 mB of any fluid | nothing received |
  | `storage_fluid_empty_machine_refuses_wrong_fluid` | empty Distillation Unit, pipe offers Lava | 0 accepted |
  | `storage_fluid_mixer_second_ingredient_first` | empty Fluid Mixer, pipe offers Nitrogen | Nitrogen in the second input tank, first empty |
  | `storage_fluid_combustion_oxidizer_routing` | empty Combustion Generator, pipe offers Oxygen | Oxygen in the oxidizer tank, fuel tank empty |
  | `storage_fluid_out_of_range_tank_index` | any machine's bottom-face handler | `isFluidValid(-1, ...)` and `isFluidValid(getTanks(), ...)` return false without throwing |

  Each optional test also logs the observed amounts with `GameTestLog.value`, so the run log records
  what the gap does today.

- **Not tested:** `fill` and `drain(int)` mark the recipe dirty on simulated and empty transfers. It
  costs a recipe re-check, not visible behaviour, and belongs with whichever follow-up touches those
  methods.

## Risks / Trade-offs

- [NeoGradle 7.1.21 does not load the `gametest` source set as part of the mod, so the holder is never
  scanned] → `runGameTestServer` then fails with no tests found, which the `smoke` test makes obvious.
  Fall back to a `gametest` package in `main` and say so in the change before continuing.
- [An optional test passes today, meaning the gap read from the code is not real] → Record it in
  tasks.md, make that test required, and drop the matching follow-up from the proposal.
- [A required test fails, meaning current behaviour differs from what the code suggests] → Stop and
  report it; do not edit `src/main` in this change. Either the scenario moves to the optional set or
  the spec is corrected, with the maintainer's approval.
- [Forward merges into 26.1-dev and 26.2-dev bring a generator those branches cannot compile] → Their
  gametest source set only compiles for the gametest runs, so `compileJava` and the jar are
  unaffected. Porting `VEGameTests` to 26.3's registration is listed as a follow-up in the proposal.
- [The 26.3 merge conflicts on every harness file] → Keep 26.3's files, add `required` to its
  `GameTestDefinition`, and port the new tests to the transfer API. Requirement names already match.

## Migration Plan

Nothing to migrate: no save, config, or registry change, and nothing ships. Rolling back is
reverting the commit.
