## Why

A pipe feeding Hydrogen into a Fluid Mixer fills the first input tank and then spills into the
second, so Nitrogen can no longer enter and Ammonia production stalls until a player empties the
tank by hand. The Combustion Generator's oxidizer tank fills with fuel the same way. 26.3 fixed this
in `fix-multi-input-tank-fill-routing`, whose proposal states that 26.1 and 26.2 need no follow-up
port because their `IFluidHandler.fill` routes to one tank. That is wrong: pipes on 26.1 never reach
that method. They reach a machine through `CapabilityAdapters`, whose fluid adapter inherits
NeoForge's default unindexed insert, and that default spreads a fill over every tank.

The 1.21.1-dev merge (`0f28c003`) brought the fluid gametests to 26.1-dev, and its required
`storage_fluid_mixer_fill_routing` test fails there: an offer of 5,000 mB is fully accepted although
one input tank holds 4,000 mB.

## What Changes

- A pipe or other neighbour filling a machine without naming a tank puts the fluid only into the
  first input tank on that face that holds the same fluid or is empty and accepts it, as on 1.21.1
  and 26.3. Once that tank is full, the rest is refused rather than spilling into another input tank.
- A pipe draining a machine without naming a tank keeps taking the fluid from every drainable tank
  on that face that holds it, as it already does on 26.1 and as 26.3 specifies. The storage spec on
  this branch, inherited from 1.21.1, says drains take from one tank; it is corrected to match.
- Indexed fills and drains, side rules, fluid validity, and the `Allow Extraction from Input Tanks`
  option behave as before.
- Gametests gain required cases for capacity boundaries, oversized offers, indexed fills,
  out-of-range tanks, spanning drains, and cancelled transfers, and their equality failures report
  the expected and actual values the right way round.
- No save, data component, attachment, registry id, or config change.

## Capabilities

### New Capabilities

None.

### Modified Capabilities

- `machines/storage-and-transfer`: the fluid automation requirement states single-tank fills,
  spanning drains, and the limits on oversized and out-of-range requests; the simulated transfer
  requirement covers transfers that are cancelled after an inner step was confirmed.

## Impact

- `src/main/java/com/veteam/voluminousenergy/util/CapabilityAdapters.java`: the fluid adapter
  overrides `insert(FluidResource, int, TransactionContext)`. `VEGenericListener` exposes this
  adapter as `Capabilities.Fluid.BLOCK`, so every pipe and neighbouring block is affected.
- `src/gametest/java/com/veteam/voluminousenergy/gametest/`: `StorageTests`, `MachineKit`, and
  `GameTestLog`.
- Player-visible effect: multi-input machines (Fluid Mixer and Combustion Generator, whose input
  tanks both start on the bottom face) keep their second input tank free for the second ingredient.
  Single-input machines are unaffected.
- Until this lands, 26.1-dev has a failing required gametest.

Follow-up ports, not scope of this change:

- Merge 26.1-dev into 26.2-dev. 26.2's adapter is the same, so the fix applies unchanged; run
  `runGameTestServer` there.
- Merge 26.2-dev into 26.3-dev. `CapabilityAdapters.java` was deleted on 26.3, so the merge hits a
  modify/delete conflict; keep the deletion, since 26.3's `MultiFluidSlotWrapper` already routes
  fills this way. The storage spec and gametests conflict too. `design.md` ("Merging forward to
  26.3") records each resolution.
- 1.21.1 is unaffected, since it exposes its fluid handler directly. Adding the same assertion
  helper there would keep `StorageTests` identical across branches.
