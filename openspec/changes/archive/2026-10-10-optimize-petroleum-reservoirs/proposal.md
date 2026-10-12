## Why

Petroleum reservoirs make new terrain load noticeably slower. A flight recording of the client while
flying through a new world showed the reservoir feature taking 47% of all chunk generation CPU, more
than vanilla's own terrain noise (17%). Nearly all of that, 45% of chunk generation, is vanilla's
`getBaseHeight`, which the feature calls five times for every reservoir cell it considers, again in
every chunk that looks at the cell. Ownership, filling, sealing, and the boundary noise together are
under 5% of the feature's time.

## What Changes

- **Faster by default, same reservoirs:** each reservoir cell, including its terrain samples, is
  worked out once per loaded dimension and reused by every chunk that needs it, instead of once
  per chunk. A world generates exactly the same reservoirs as before, with far fewer height samples.
  This is a new startup config option, `Cache Petroleum Reservoir Cells` under World Generation →
  Feature Generation, default `true`. Turning it off is a fallback that gives the same reservoirs,
  only slower.
- **Optional fast surface sampling:** a new startup config option, `Fast Petroleum Reservoir
  Surface Sampling` under World Generation → Feature Generation, default `false`. When enabled,
  the terrain height around a reservoir is estimated the way vanilla aquifers estimate it, from a
  coarse density search, taken at the corners of the reservoir's grid column so every cell and layer
  in that column shares the samples. This is much cheaper, but it changes some reservoirs' levels and
  tops, so the same seed generates slightly different reservoirs with fast sampling on.
- Switching fast sampling in an existing world is safe: chunks generated under different settings
  meet at bituminous plugs, never at an open spill.
- The `worldgen/petroleum-reservoirs` spec states the plug variant as following the ground beneath
  the plug, which is what the implementation already does. Plugs only fill open space, so the
  current wording ("the replaced block", which is air or fluid) would make every plug gravel.

## Capabilities

### New Capabilities
None.

### Modified Capabilities
- `worldgen/petroleum-reservoirs`: adds the cell caching and fast surface sampling options and
  the guarantee that caching never changes output; corrects the plug variant rule in the sealing requirement.

## Impact

- Code: `FluidReservoirCells` gains a per-level shared cell cache and the fast surface sampler;
  `VEFluidReservoirFeature` takes its cells from the shared cache. `VEGenericListener` clears the
  cache when a level unloads. `Config` gains two booleans.
- Data: none.
- Config: two new startup options in `voluminousenergy-startup.toml`, `Cache Petroleum Reservoir
  Cells` (default `true`) and `Fast Petroleum Reservoir Surface Sampling` (default `false`).
  Changing either needs a restart, like the other startup options. Only the side that generates
  chunks reads them (a dedicated server, or the integrated server in single player).
- Save compatibility: no registry, NBT, data component, or attachment changes. With the default
  config, reservoirs in new chunks match those generated before this change for the same seed.
- Memory: the cache holds a bounded number of cells per dimension, a few hundred kilobytes at most.
- Depends on the archived `add-petroleum-reservoirs` change, on `1.21.1-petroleum-worldgen`.
- Follow-up ports (not in scope): none until reservoirs are ported.
