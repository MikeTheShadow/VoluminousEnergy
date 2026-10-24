## Context

`VEGenericListener` registers `Capabilities.Fluid.BLOCK` for every machine with tanks by wrapping
the face's `MultiFluidSlotWrapper` (an `IFluidHandler`) in `CapabilityAdapters.FluidHandlerAdapter`.
The adapter implements the indexed `insert` and `extract` through the wrapper's per-tank
`fill(int, ...)` and `drain(int, ...)`, under a `SnapshotJournal` of every tank, and inherits
`ResourceHandler`'s default unindexed `insert` and `extract`, which loop over every index in order
and pass on the remainder.

The wrapper's own unindexed `fill(FluidStack, FluidAction)` already picks the first tank where
`canFill` holds and fills only that one, but no pipe calls it. `canFill` is true for a full tank
holding the same fluid, and `isFluidValid` falls back to `FluidTank.isFluidValid`, which accepts
any fluid because `VETileEntityFactory` builds its tanks without a validator. So the default loop
pours the first ingredient into the second input tank of a Fluid Mixer or Combustion Generator
whenever it reaches it.

A `runGameTestServer` run on 26.1-dev at `0f28c003` gave one required failure,
`storage_fluid_mixer_fill_routing` (5,000 mB accepted, 4,000 mB capacity), and five optional
failures: `storage_fluid_fill_refuses_disabled_side`, `storage_fluid_empty_machine_refuses_wrong_fluid`,
`storage_fluid_mixer_second_ingredient_first`, `storage_fluid_combustion_oxidizer_routing`, and
`storage_fluid_out_of_range_tank_index`. The optional `storage_fluid_drain_amount_refuses_disabled_side`,
which fails on 1.21.1, passed, because the amount-only drain here goes through the adapter's
indexed `extract`, which checks side status.

## Goals / Non-Goals

**Goals:**

- Unindexed inserts through the adapter behave as the wrapper's unindexed `fill`: one tank per
  insert, with a cancelled transaction restoring every tank.
- Unindexed extracts keep spanning tanks, and the spec says so.
- Gametests cover the fix's boundaries through the handler pipes use, and equality failures read
  correctly.

**Non-Goals:**

- The always-true `isFluidValid` fallback, fills through a disabled side, and `isFluidValid`
  throwing for an out-of-range tank. These stay optional gametests.
- Replacing `CapabilityAdapters` with a wrapper that implements the transfer handler directly, as
  26.3 does. That changes every machine's capability for a one-method defect.

## Decisions

### Override the adapter's unindexed insert and delegate to the wrapper's unindexed fill

This deliberately deviates from NeoForge's default `ResourceHandler.insert(resource, amount,
transaction)`, which spreads over every index. The override simulates the wrapper's
`fill(FluidStack, SIMULATE)`, and if anything fits, updates the snapshots and executes the fill for
the simulated amount, the same pattern as the adapter's indexed `insert`. Executing only the
simulated amount means no offer, however large, can push a tank past capacity, and the tank choice,
including validators and recipe ingredients, stays in one place, the wrapper, as on 1.21.1.

Alternative considered: find the index in the adapter with a copy of `canFill`, as 26.3 does inside
its wrapper. That duplicates the rule in a class whose job is translation, and `canFill` is private.

The override is already written, restored from the paused work on this branch, and compiles.

### Leave extract on the default and specify spanning drains

Tanks on one face holding the same fluid are both legitimate sources, and 26.3 specifies spanning
drains. The inherited 1.21.1 wording ("a drain SHALL take from a single tank per request") is true
on 1.21.1, whose wrapper drains one tank, but not on 26.1, where every pipe drain goes through the
default loop; the existing drain tests pass only because each holds its fluid in one tank. The
fill and drain sentences of the requirement follow 26.3's text so the 26.3 merge resolves by
keeping one side.

### Test through the transfer handler where the bridge cannot reach

Existing tests use `MachineKit.fluids`, which bridges the capability with `IFluidHandler.of`. The
bridge's `fill` calls the unindexed `insert` directly, so `storage_fluid_mixer_fill_routing`
exercises the fix as it stands. The bridge cannot name an index or hold a transaction open, so a new
`MachineKit` helper returns the `ResourceHandler<FluidResource>` from `Capabilities.Fluid.BLOCK` for
the indexed, out-of-range, and transaction cases.

An empty resource is not tested through the transfer handler: NeoForge's contract makes `insert`
and `extract` throw for one, which moves nothing. The required `storage_fluid_empty_requests` test
already covers empty and 0 mB requests through the bridge, which returns nothing for them.

`storage_fluid_drain_amount_refuses_disabled_side` becomes required on this branch, since it passes
here and states behaviour the requirement already demands. It stays optional on 1.21.1.

### Assert equality through a helper that reports in the right order

Vanilla's `GameTestHelper.assertValueEqual(value, expected, name)` formats
`test.error.value_not_equal`, "Expected %s to be %s: was %s", with the name, the value, and the
expected value in that order, so a failure prints the received value as the expected one. Swapping
arguments at the call sites would fix the message but make every call read against the parameter
names. Instead `GameTestLog` gains `assertEqual(helper, actual, expected, name)`, which fails with
"Expected <name> to be <expected>, but was <actual>", and `StorageTests` calls it in place of
`assertValueEqual`.

## Risks / Trade-offs

- [Automation that relied on overflow spreading now backs up] → Intended: the second input tank is
  reserved for the second ingredient, matching 1.21.1 and 26.3.
- [A cancelled transaction leaves fluid behind] → The override updates snapshots before executing,
  as the indexed insert does, and the cancelled and nested transaction gametests cover it.
- [Simulate and execute disagree if a tank changes between them] → They run back to back on the
  server thread inside one call, as the indexed insert already relies on.
- [Moving 32 assertion call sites conflicts with later edits to `StorageTests` on 1.21.1] → A
  one-time conflict; adding the helper on 1.21.1 as well removes it.

## Migration Plan

No save, registry, config, or data migration. Rollback is reverting the fix commit; tanks already
holding overflowed fluid keep it either way.

## Merging forward to 26.3

This change is archived on 26.1 and reaches 26.3 through the 26.1-dev into 26.2-dev into 26.3-dev
merges. The 26.2-dev merge applies cleanly, since 26.2's adapter is the same. The 26.3-dev merge
needs these resolutions:

- `src/main/java/com/veteam/voluminousenergy/util/CapabilityAdapters.java` hits a modify/delete
  conflict, because the 26.3 port (`309551e7`) deleted it when machine storage moved to the
  transfer API. Keep the deletion (`git rm` the file). 26.3 needs no replacement:
  `MultiFluidSlotWrapper` implements `ResourceHandler<FluidResource>` and overrides the unindexed
  `insert` to fill one tank (`8007867d`), its unindexed extract stays on the spanning default, and
  `VEGenericListener` registers the wrapper as `Capabilities.Fluid.BLOCK` directly.
- `openspec/specs/machines/storage-and-transfer/spec.md` exists on both branches with the same
  requirement names. Keep 26.3's text, and add the 26.1 sentences and scenarios 26.3 lacks: fills
  naming an output tank, oversized and out-of-range requests, and cancelled and nested transfers.
  The single-tank fill and spanning drain sentences already agree.
- 26.3's `StorageTests` and `MachineKit` are a separate, larger suite (`storage_fluid_fill_rules`,
  `storage_fluid_multi_tank_extract`, `MachineKit.capability`), so both files conflict throughout.
  Keep 26.3's files and port only the cases 26.3 lacks onto its helpers. 26.3's own
  `assertValueEqual` calls have the same reversed messages; moving them to
  `GameTestLog.assertEqual` is a separate change.
- After resolving, confirm `CapabilityAdapters` is referenced nowhere, then run
  `./gradlew compileJava compileGametestJava runGameTestServer` on 26.3 and confirm
  `storage_fluid_mixer_fill_routing` and `storage_fluid_multi_tank_extract` still pass.
