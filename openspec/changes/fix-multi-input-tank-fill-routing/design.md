## Context

`MultiFluidSlotWrapper` is the per-face `ResourceHandler<FluidResource>` that `CapabilityMap` hands
to neighbours. Since `e2ce4833` it inherits NeoForge's default `insert(resource, amount, transaction)`
and `extract(resource, amount, transaction)`, which loop over every index and pass on the
remainder. Every tank starts on `Direction.DOWN`, so a two-input machine exposes both input tanks
on one face by default.

`isValid` falls back to `VEFluidTank.isValid`, which `StacksResourceHandler` defines as always
`true`. For a two-input machine with one fluid already in tank 0, the second tank therefore accepts
that same fluid whenever no partial recipe names it there, which is why the default loop overflows.

## Goals / Non-Goals

**Goals:**
- Unindexed fills stop at the first compatible input tank, as in the 1.21.1 `fill`.
- Unindexed drains keep spanning tanks.

**Non-Goals:**
- Fixing the always-true `isValid` fallback. It also lets an empty Combustion Generator take an
  oxidizer into its fuel tank, but tightening it changes which fluids every machine accepts and
  needs its own change.

## Decisions

- **Override `insert(resource, amount, transaction)` again; leave `extract` on the default.** The
  override finds the first index where `canFill` holds and returns `insert(index, ...)` for it.
  `canFill` is true for a full tank holding the same fluid, so a full first tank refuses the rest
  instead of falling through. This deliberately deviates from NeoForge's documented default, which
  spreads; the machine's input tanks are recipe slots, not interchangeable storage. Draining has no
  such hazard: tanks that hold the same fluid are both legitimate sources.
  Alternative considered: keep spreading but skip empty tanks once any tank holds the fluid. It
  fixes the stall, but a fluid valid at both recipe positions would still never reach the second
  tank, and the rule is harder to state to players than "one tank per fill".
- **Gametest on a real Fluid Mixer.** The removed test used standalone `TankType.BOTH`, `allowAny`,
  ignore-direction tanks, which no machine has. The new test places a Fluid Mixer, fills its input
  tanks through the bottom-face capability, and asserts the spec's two fill scenarios. The drain
  scenario stays on the standalone-tank wrapper, renamed to say it covers extraction only, since no
  machine exposes two tanks holding the same fluid without staging.

## Risks / Trade-offs

- [A machine with two input tanks that should both take one fluid] → None exists today (Fluid Mixer
  and Combustion Generator take different fluids per tank). A future one would need indexed fills
  or a validator.
- [Pipes that only use unindexed inserts fill one tank per call] → Matches 1.21.1 behaviour players
  already know.
