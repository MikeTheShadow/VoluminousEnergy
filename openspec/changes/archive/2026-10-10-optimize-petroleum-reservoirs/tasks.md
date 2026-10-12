## 1. Config

- [x] 1.1 Add `CACHE_PETROLEUM_RESERVOIR_CELLS` (`Cache Petroleum Reservoir Cells`, default `true`) and then `FAST_PETROLEUM_RESERVOIR_SURFACE_SAMPLING` (`Fast Petroleum Reservoir Surface Sampling`, default `false`) at the bottom of the `Feature Generation` subcategory in `Config.setupWorldGen`; verify `./gradlew compileJava` succeeds

## 2. Shared cells

- [x] 2.1 Add `FluidReservoirCells.forPlacement(Configuration, ServerLevel, ChunkGenerator)` backed by a per-level `ConcurrentHashMap` of shared caches (a Guava `Cache<Long, Cell>` per configuration, maximum 16,384, filled through `asMap().computeIfAbsent`) with the per-placement plain map in front, use the `ServerLevel` as the height accessor, and have `VEFluidReservoirFeature.place` use it, with only the plain map when caching is off; verify it compiles
- [x] 2.2 Handle `LevelEvent.Unload` in `VEGenericListener` to drop the level's shared caches; verify it compiles

## 3. Fast surface sampling

- [x] 3.1 Add the preliminary surface sampler (step down by the noise cell height, `initialDensityWithoutJaggedness` above `0.390625`), a per-level column-height cache keyed by block column, and `minSurface` from the four grid-column corners when fast sampling is on, with exact `getBaseHeight` sampling kept for generators that are not `NoiseBasedChunkGenerator`; verify it compiles

## 4. Verification

- [x] 4.1 Run `./gradlew compileJava` and `./gradlew runData`, and confirm `src/generated/resources` has no unexpected diff (this change adds no datagen output)
- [x] 4.2 During the caching-on, fast-sampling-off flight in 4.3, run a temporary check that rebuilds every 64th cached cell from scratch and logs any difference; confirm zero differences are logged, then remove the check and recompile
- [x] 4.3 Record flights through new terrain with a flight recording attached for three settings (caching off and fast sampling off, which matches the 47% baseline; caching on and fast sampling off; caching on and fast sampling on) and report the reservoir feature's share of chunk generation CPU for each

  Results (2026-10-10, seed `petroleum`, flying north about 100 s each): caching off and fast
  sampling off 44.1% of chunk generation CPU; caching on 10.1% (excluding the 4.2 check); caching
  and fast sampling on 5.1%. Reservoir cost per unit of terrain noise fell from 2.63 to 0.25 to
  0.11, and terrain noise throughput rose about 3.6 times with caching and 4.6 times with both.
- [x] 4.4 In-game check via `./gradlew runClient` (maintainer). Check that:
  - a fresh startup config contains `Cache Petroleum Reservoir Cells = true` and `Fast Petroleum Reservoir Surface Sampling = false`;
  - with the defaults, new terrain loads noticeably faster than before and reservoirs look as they did;
  - with fast sampling on, reservoirs still generate sealed and without seams;
  - turning fast sampling on in a world generated with it off leaves no spills where old and new chunks meet;
  - the log has no errors from the reservoir feature.

  Outcome (2026-10-10, maintainer): fresh config defaults confirmed; default reservoirs look as
  before; fast sampling reservoirs sealed without seams; the log of run C has no reservoir errors.
  Turning fast sampling on in a world generated with it off was not tested, and the maintainer
  closed it as untested.
