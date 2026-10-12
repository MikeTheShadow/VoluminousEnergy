## Context

See proposal.md for the bug. The six gases share one shape: each fluid class (`Ammonia`, `Hydrogen`,
`LightFuel`, `NaturalGas`, `Nitrogen`, `Oxygen`) builds its `VEFlowingGasFluid` source and flowing
fluids and a `VEFlowingFluidBlock` from a static `stdProp`:

```java
Block.Properties.of().setId(...).noCollision().strength(100.0F).noLootTable().replaceable()
        .pushReaction(PushReaction.DESTROY).air()
```

Liquids use the same chain ending in `.liquid()`. Both flags date from one 2023 commit that made
blocks placeable into fluids, but placement comes from `replaceable()`. Vanilla 26.1's
`canBeReplaced(BlockState, BlockPlaceContext)` and `canBeReplaced(BlockState, Fluid)` both return true
for a replaceable state, and `LiquidBlock` overrides neither.

26.1's `SectionCompiler` wraps all per-block work, including the fluid tessellation and NeoForge's
`CustomFluidRenderer` hook, in `if (!blockState.isAir())`. 26.2 does the same.

## Goals / Non-Goals

**Goals:**
- Placed and generated gases reach the section compiler's fluid path, so `GaseousFluidRenderer` draws
  them.
- Placement, bucket, falling block, flow, and breathing behaviour stay as they are.

**Non-Goals:**
- Restoring the "gas counts as air" behaviour of vanilla growth, decoration, and heightmaps on 26.x.
- Changing gas or worldgen behaviour on 1.21.1.
- Reworking `GaseousFluidRenderer` beyond what its first in-world run on 26.1 shows to be broken.

## Decisions

**Replace `.air()` with `.liquid().sound(SoundType.EMPTY)` on the six gas block properties.** Removing
`.air()` is the maintainer's choice over drawing gases from a client `AddSectionGeometryEvent` handler,
which would have kept gases as air but added a second, per-section render path for one fluid family.
The replacement flags match vanilla water and lava rather than the mod's liquids, which lack the sound.
- Without `.liquid()`, emptying a bucket at a position already holding gas makes `BucketItem` call
  `destroyBlock` on the gas first, playing a break sound and particles. `.air()` used to skip that.
- `SoundType.EMPTY` silences every other path that plays a non-air block's sounds, such as an entity
  falling or stepping where gas is.
- Gas is deliberately treated as a liquid although it is not one. The accepted effects:
  - mineshaft pieces avoid it as they do water;
  - structures may replace it;
  - oil lakes refuse a spot where gas touches their upper border, as they do for any liquid;
  - cactus does not grow with gas above it;
  - players cannot respawn inside it.

**Accept that gas columns now rise to the gas code's own limit.** `VEFlowingGasFluid.spread` stops at
y 320. While gases counted as air, vanilla's `LevelChunk` dropped any gas placed into a 16-block section
holding only air, and reported such a section's gas as plain air, so open-air columns stopped at the top
of their starting section. A test server shows 35 gas blocks from one source on 1.21.1 and 150 without
`.air()` on 26.1.

**Leave `VEFlowingGasFluid` as is.** Its two `isAir()` checks still behave the same.
- The source search already rules out gas blocks with `instanceof LiquidBlock` before reaching
  `isAir()`.
- `spreadTo` now calls `beforeDestroyingBlock` when one gas spreads into another gas's block. That
  drops nothing, because gas blocks have no loot table.

**Keep the worldgen fill rules and drop their stale reason.** `VEFluidReservoirFeature.canFill` and
`VEFluidCaveCarver.canFill` test `Blocks.AIR`, `Blocks.CAVE_AIR`, and water by block, which keeps them
from overwriting another gas. They keep doing so. The reservoir's Javadoc gives "natural gas counts as
air" as the reason, which stops being true on 26.x. Delete it; with gas no longer air, the explicit
block test needs no explanation.

**Diverge from 1.21.1 on these six lines.** 1.21.1 renders gas correctly, and its worldgen and growth
behaviour rely on gas counting as air, so it keeps `.air()`. Merges from 1.21.1-dev resolve any
conflict on a gas `stdProp` line by keeping the 26.x version without `.air()`.

## Risks / Trade-offs

- [`GaseousFluidRenderer` has never drawn in-world gas on 26.1, so lighting, face culling, or texture
  problems may show up for the first time] → The in-game check covers every gas in daylight and
  underground. Small fixes go in this change; anything larger becomes a follow-up change.
- [Sections that hold only gas now count as non-empty, so tall gas columns build and draw geometry
  that 26.1 skipped] → Accepted. This matches 1.21.1, where gas sections were always drawn.
- [Gas tunnels and reservoirs look different from 1.21.1: vanilla cave decoration no longer
  overwrites their gas, and dungeons no longer generate inside them] → Intended for decoration, which
  the `fluids/gases` spec requires; accepted for dungeons.
- [Taller gas columns in open air mean more gas blocks to tick and draw] → Accepted; the column ends at
  y 320 and dissipates once its source is gone, as before.
- [Grass turns to dirt under the gas beside each source] → Not caused by this change, and kept. The
  rising gas there is stored as a full falling state, and vanilla kills grass under any full fluid, as
  with water. 1.21.1 does the same; a test that seemed to show otherwise ran in force-loaded chunks that
  1.21.1 does not random-tick.
- [The `WORLD_SURFACE` heightmap rises over a gas column that reaches the sky] → Accepted. Rain and
  snow already stop at gas through `MOTION_BLOCKING`, which counts any non-empty fluid.

## Migration Plan

No data migration. Existing gas blocks keep their ids and states and render after the update.
Reverting the change makes gases invisible again without affecting saves.
