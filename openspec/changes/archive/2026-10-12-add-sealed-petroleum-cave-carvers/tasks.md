## 1. Carver registry

- [x] 1.1 Add `world/carver/VECarvers` with `VE_CARVER_REGISTRY` (`Registries.CARVER`) and register it in the `VoluminousEnergy` constructor under a new `// Register carvers` banner after `// Register features`; verify `./gradlew compileJava` succeeds

## 2. Carver configuration

- [x] 2.1 Add `VEFluidCaveCarver.Configuration extends CarverConfiguration` with the three cave floats, `fluidstate` and an optional `perimeter_targets` list (default `always_true` to Bituminous Gravel), and a codec grouping `CarverConfiguration.CODEC`, the three cave floats, `fluidstate`, and `perimeter_targets`; verify it compiles and the shipped JSON from 4.1 decodes when the game loads a world

## 3. Carver

- [x] 3.1 Implement the tunnel walk in `VEFluidCaveCarver` (vanilla room and tunnel math rewritten with descriptive names, `getRange()` 2, one system per start: a room 1 in 4 times, then one to three tunnels) that records ellipsoids without writing blocks; verify it compiles
- [x] 3.2 Implement the fill pass for the current chunk (replaceable set, `Blocks.AIR`, `Blocks.CAVE_AIR`, or water becomes the fluid source, `CarvingMask` set, no post-processing marks); verify it compiles
- [x] 3.3 Implement the geometric shell pass for the current chunk using `LakeMask.perimeterState`, skipping `BlockTags.FEATURES_CANNOT_REPLACE` and positions where no target matches; verify it compiles
- [x] 3.4 Register `ve_fluid_cave_carver` in `VECarvers`; verify `./gradlew compileJava` succeeds

## 4. Data

- [x] 4.1 Add `worldgen/configured_carver/crude_oil_cave.json` and `natural_gas_cave.json` with the values in design.md and the three bituminous targets from `underground_oil_lake.json`; verify the JSON parses
- [x] 4.2 Add `neoforge/biome_modifier/petroleum_caves_overworld.json` (`neoforge:add_carvers`, `#minecraft:is_overworld`, step `air`, both carvers); verify the JSON parses

## 5. Verification

- [x] 5.1 Run `./gradlew compileJava` and confirm it succeeds
- [x] 5.2 Run `./gradlew runData` and confirm `src/generated/resources` has no unexpected diff (this change adds no datagen output)
- [x] 5.3 In-game check via `./gradlew runClient` on a new world (maintainer). `/locate` cannot find carvers, so search in spectator mode below y 30. Check that:
  - petroleum tunnels (oil and gas) exist, are rarer than vanilla caves, and are fully filled with no air pockets;
  - every face is sealed by Bituminous Gravel, or Bituminous Sand under desert sand;
  - a vanilla cave crossing a tunnel is plugged on both sides, and a vanilla cave reaching a shell stops there;
  - breaking a gas tunnel's shell releases rising gas, and breaking an oil tunnel's shell lets oil flow out;
  - the log shows no "Detected setBlock in a far chunk" warnings;
  - the gas tunnels' purple gas reads clearly underground.

  Retune `probability` and the radius values in the JSON if needed.
