## Why

A pipe feeding one fluid into a Fluid Mixer or Combustion Generator fills the first input tank and
then overflows into the second, so the second ingredient (Nitrogen, an oxidizer) can no longer
enter and the machine stalls until a player empties the tank by hand. Commit `e2ce4833` introduced
this on 26.3-dev by letting unindexed inserts spread over every accepting tank; 1.21.1 and the
26.3 port before it sent each fill to one tank only.

## What Changes

- A pipe or other neighbour filling a machine without naming a tank puts the fluid only into the
  first input tank on that face that already holds the fluid or is empty and accepts it. Once that
  tank is full, the rest is refused rather than spilling into another input tank.
- A pipe draining a machine without naming a tank still takes the fluid from every drainable tank
  on that face that holds it, as `e2ce4833` made it, until the requested amount is met.
- The `storage_fluid_multi_tank_transfer` gametest is replaced: a Fluid Mixer test checks that
  Hydrogen fills only its first input tank and Nitrogen still reaches the second, and a drain test
  keeps the spread extraction covered.
- No save, data component, attachment, registry id, or config change.

## Capabilities

### New Capabilities

None.

### Modified Capabilities

- `machines/storage-and-transfer`: the fluid automation requirement states how an unindexed fill
  picks one input tank and how an unindexed drain spans tanks.

## Impact

- `src/main/java/com/veteam/voluminousenergy/util/MultiFluidSlotWrapper.java`: restores the
  `insert(resource, amount, transaction)` override and its class Javadoc wording for fills.
- `src/gametest/java/com/veteam/voluminousenergy/gametest/StorageTests.java`: replaces the
  multi-tank test.
- Affects every machine whose face exposes two or more input tanks: the Fluid Mixer and Combustion
  Generator (both input tanks start on the bottom face). Single-input machines behave the same
  either way.
- 26.3-dev only. The transfer API wrapper exists only on this branch; 1.21.1, 26.1, and 26.2 keep
  their `IFluidHandler.fill`, which already routes to one tank. No follow-up port.
