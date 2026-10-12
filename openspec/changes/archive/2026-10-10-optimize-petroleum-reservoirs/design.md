## Context

See proposal.md (Why) for the profile. The cost has a simple shape:

- `VEFluidReservoirFeature.place` builds a new `FluidReservoirCells` for every chunk, so its cell
  cache lives for one chunk only.
- `FluidReservoirCells.createCell` calls `ChunkGenerator.getBaseHeight` five times for each cell that
  rolls as a reservoir and passes the biome test. For `NoiseBasedChunkGenerator` each call builds a
  one-column `NoiseChunk`, including its aquifer, and walks the final density down from the top of
  the world until it finds an opaque block.
- A 32-wide cell is a candidate for roughly 6 to 9 chunks, and the crude oil and natural gas
  features keep separate grids, so the same five columns are sampled many times over.

Everything a cell holds depends only on the seed, the configuration, and its grid index: the centre
jitter comes from a positional random factory, and `getBaseHeight` and `getNoiseBiome` are pure
functions of `RandomState`. The sealing rule already relies on this.

## Goals / Non-Goals

**Goals:**
- Cut the reservoir feature's share of chunk generation sharply with the default config, with
  identical output.
- Offer a further, placement-changing speedup behind a startup option that defaults to off.
- Confirm both with a second flight recording.

**Non-Goals:**
- Optimizing ownership, fill, seal, or boundary noise: together they are under 5% of the feature.
- Changing reservoir sizes, frequencies, or bands.
- Persisting cells to disk.

## Decisions

### Share cells per level, as pure memoization
With `Config.CACHE_PETROLEUM_RESERVOIR_CELLS` on, the default, `FluidReservoirCells.forPlacement`
gives each placement an instance backed by the level's shared caches, held in a
`ConcurrentHashMap<ServerLevel, SharedLevel>`: one cell cache per configuration, and one
preliminary-surface cache for the level. Two configurations that are equal records share a cell
cache, which is correct because they produce the same cells. Each placement keeps a plain map in
front of the shared caches, because ownership reads a dozen cells for every open position and a
Guava cache is several times slower to read than a fastutil map.

- The instance uses the `ServerLevel` as its `LevelHeightAccessor` instead of the per-chunk
  `WorldGenRegion`, in both modes. Only the build height range is read from it, and that is the same for
  the level.
- Each shared cell cache is a Guava `Cache<Long, Cell>` with a maximum size of 16,384 entries,
  filled through `asMap().computeIfAbsent`. Guava computes each key at most once at a time and is safe across
  the worldgen worker threads. Guava ships with Minecraft, so this adds no dependency.
- Eviction and level reloads only cost recomputation, never different output, because each cell is
  a pure function of its inputs. That is what makes the default path identical to the archived
  behaviour.
- `VEGenericListener` handles `LevelEvent.Unload` and drops the level's entry, so a closed world's
  cells are not kept.
- With the option off, each placement has only its plain map, as the archived code does. The option exists as a fallback if the shared cache ever misbehaves, since both paths give
  the same output.

Alternatives: a `ThreadLocal` cache (no sharing between workers, so it would cut far less); a
cache keyed by `RandomState` with weak keys (the values reference the level, which references the
`RandomState`, so it would never be collected).

### Fast surface sampling replaces exact heights with vanilla's preliminary surface
With `Config.FAST_PETROLEUM_RESERVOIR_SURFACE_SAMPLING` on, `minSurface` for a cell comes from a
different sampler:

- **Where:** the four corners of the cell's grid column, `(gridX × W, gridZ × W)` to
  `((gridX + 1) × W, (gridZ + 1) × W)`, instead of the jittered centre and four offsets. Every
  vertical layer in a grid column, and the four neighbouring columns, share each corner. The
  per-level column cache is keyed by block column, so the oil and gas features share corners too
  when their cell widths match, as the shipped ones do.
- **How:** the method vanilla's `NoiseChunk.computePreliminarySurfaceLevel` uses for aquifers.
  Step down from the top of the noise range by the noise cell height (8 blocks in the overworld),
  evaluate `randomState.router().initialDensityWithoutJaggedness()` at a single point, and return
  the first `y` where it exceeds `0.390625`. No `NoiseChunk` or aquifer is built, and there is no
  per-block interpolation. Vanilla keeps this method private and per `NoiseChunk`, so the feature
  reimplements its loop. That is the one deliberate copy of vanilla here.
- **Fallback:** a generator that is not a `NoiseBasedChunkGenerator` has no noise settings to step
  by, so it keeps the exact `getBaseHeight` sampling even with fast sampling on.

Both options are read for each placement. They are startup options, so they cannot change
while a level is loaded. Fast sampling works with caching on or off; with caching off its corner
samples are still far cheaper each, but they are recomputed in every chunk.

Alternatives: sampling one exact column per cell (still builds a `NoiseChunk` per sample, and saves
less); making the fast path the default (rejected by the maintainer, since it changes placement).

### Config placement
Both options are startup options, which is where the mod's other worldgen settings live, and only
the side that generates chunks reads them. They go at the bottom of the `Feature Generation`
subcategory in `Config.setupWorldGen`, after `Enable Logging`: `Cache Petroleum Reservoir Cells`
(`define(..., true)`), then `Fast Petroleum Reservoir Surface Sampling` (`define(..., false)`).
A per-world server config was considered and deferred; the mod has none yet.

### Plug variant wording
Spec only. The seal already passes the block beneath the plug to `LakeMask.perimeterState`.

## Risks / Trade-offs

- [Cache memory] → 16,384 small records per configuration per level, a few hundred kilobytes; the
  bound keeps long flights from growing it.
- [Contention on the shared maps across worker threads] → `ConcurrentHashMap` and Guava's segmented
  cache; lookups happen once per cell per chunk, far below the cost they replace.
- [The preliminary surface misses jagged peaks and fine detail] → It can sit a few blocks off the
  true surface, so an opted-in buried pool can occasionally rise into a deep valley floor or a river
  bed. The 8-block clearance absorbs most of it, the seal still holds, and fast sampling is off by
  default.
- [The estimated speedup does not materialize] → Task 4.2 repeats the flight recording with the
  option off and on, and the change is not done until the numbers are reported.

## Results

Measured on 2026-10-10 with a flight recording attached to the client, one flight per setting in a
new world with seed `petroleum`, flying north for about 100 seconds at the same render distance.
Throughput compares terrain-noise samples per second, a proxy for chunks generated, against run A.

| Run | Caching | Fast sampling | Reservoir share of chunk generation CPU | Reservoir cost per unit of terrain noise | Throughput |
|---|---|---|---|---|---|
| Before (archived code) | n/a | n/a | 47% | n/a | n/a |
| A | off | off | 44.1% | 2.63 | 1× |
| B | on | off | 10.1% | 0.25 | about 3.6× |
| C | on | on | 5.1% | 0.11 | about 4.6× |

Run B excludes the samples of the temporary check from task 4.2, which rebuilt 7,168 cached cells
from scratch with no differences. In run C the exact height lookups no longer appear, and the
preliminary surface search accounts for 14 stack frames; most of the remaining reservoir time is the
fill pass.

## Migration Plan

None. With the default config new chunks match the archived behaviour. Turning fast sampling on or off
in an existing world only affects new chunks, and the sealing rule plugs any border with chunks
generated under the other setting.
