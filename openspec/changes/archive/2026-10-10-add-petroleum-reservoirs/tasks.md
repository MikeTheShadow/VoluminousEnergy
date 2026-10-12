## 1. Configuration

- [x] 1.1 Add the `pool`/`cap` `StringRepresentable` enum and the `Configuration` record for the reservoir feature in `world/feature/`, with the codec from design.md (ranges on `cell_width`, `cell_height`, `chance`; optional `reaches_surface`, `cell_height`, `perimeter_targets`, `boundary`) and a `validate` step rejecting `cap` with `reaches_surface`; verify `./gradlew compileJava` succeeds

## 2. Cell grid

- [x] 2.1 Add `FluidReservoirCells` in `world/feature/`: grid indexing, jittered centres from `RandomState.getOrCreateRandomFactory`, nearest-centre ownership (2×3×2 search, or 2×2 columns for surface pools), the `chance` and centre-biome tests, and the `minSurface` sample from five `getBaseHeight` columns; verify it compiles
- [x] 2.2 Add level and floor resolution per reservoir cell (buried pool, cap, surface pool, all clamped to the band) and a per-call cache keyed by grid cell; verify it compiles
- [x] 2.3 Add the boundary rule to ownership (nearest and second-nearest centre, pair ordered by grid index, `wobble × noise` from `RandomState.getOrCreateNoise`, noise sampled only within `wobble` of a boundary, flat when `boundary` is absent); verify it compiles

## 3. Feature

- [x] 3.1 Add the reservoir feature's early out: list the grid cells that can own a position in the chunk and return when none is a reservoir; verify it compiles
- [x] 3.2 Add the fill pass over the chunk (cell is a reservoir, level or floor rule, block is `Blocks.AIR`, `Blocks.CAVE_AIR`, or water; flag 2; no ticks); verify it compiles
- [x] 3.3 Add the seal pass (face neighbours of this chunk's reservoir fluid, skipping the open face, open blocks that the reservoir would not fill, never `BlockTags.FEATURES_CANNOT_REPLACE`, plug state from `LakeMask.perimeterState`, writes up to one block into neighbouring chunks); verify it compiles
- [x] 3.4 Register `ve_fluid_reservoir_feature` at the bottom of the features group in `VEFeatures`; verify `./gradlew compileJava` succeeds

## 4. Data

- [x] 4.1 Add `worldgen/configured_feature/{crude_oil_reservoir,natural_gas_reservoir}.json` with the values in design.md and the bituminous targets from `underground_oil_lake.json`; verify the JSON parses
- [x] 4.2 Add `worldgen/placed_feature/` entries for both with an empty `placement` list; verify the JSON parses
- [x] 4.3 Add `neoforge/biome_modifier/petroleum_reservoirs_overworld.json` (`neoforge:add_features`, `#minecraft:is_overworld`, step `lakes`, both placed features); verify the JSON parses
- [x] 4.4 Add `worldgen/noise/petroleum_reservoir_boundary.json` (`firstOctave` -4, amplitudes `[1.0, 1.0]`) and set `boundary` (that noise, wobble 4) in both configured features; verify the JSON parses
- [x] 4.5 Rename field to reservoir across classes, the feature id, JSON files, the random factory id, the noise, and the biome modifier, and delete the desert surface configured and placed features; verify `./gradlew compileJava` succeeds and no `field` ids remain

## 5. Verification

- [x] 5.1 Run `./gradlew compileJava` and confirm it succeeds
- [x] 5.2 Run `./gradlew runData` and confirm `src/generated/resources` has no unexpected diff (this change adds no datagen output)
- [ ] 5.3 Load a world with a datapack that sets `natural_gas_reservoir` to `reaches_surface: true` and confirm the log reports that a cap cannot reach the surface
- [ ] 5.4 In-game check via `./gradlew runClient` on a new world (maintainer). Check that:
  - underground oil reservoirs flood caves to a flat level with air above;
  - gas reservoirs fill caves above a flat floor and are open underneath;
  - passages leaving a reservoir are plugged, and oil and gas reservoirs meeting are separated by bitumen;
  - reservoirs continue across chunk and biome borders with no seams;
  - reservoir walls and plugs inside open caves curve and bulge rather than forming flat planes;
  - breaking a gas plug releases rising gas;
  - the log has no "Detected setBlock in a far chunk" warnings;
  - chunk generation speed is not noticeably slower in a new world (watch for lag when flying through new terrain).

  Retune values in the JSON if needed.

  Outstanding at archive (2026-10-10): the maintainer confirmed flat oil levels, gas open underneath,
  plugged passages, curved walls, no seams, and a clean log (all far-chunk errors come from
  `GeyserFeature`). Not yet confirmed: breaking a gas plug releases rising gas. Failed: chunk
  generation is noticeably slower, handed to a follow-up change for optimizing reservoirs. Task 5.3
  was not run.
