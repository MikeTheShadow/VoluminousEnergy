# Test plan: port to 26.3

This plan verifies the port without a human at the keyboard, in throwaway worlds only. Each test
names the spec requirement it covers. Tiers run in order, and a tier runs only after the previous
one passes. Only the checks in T6 need a person.

## Safety rules

- No test opens, copies into, or writes to the user's worlds: `runs/server/world`,
  `runs/server/world-26.1-dev`, everything in `runs/client/saves`, or `runs/gameTestServer/world`
  beyond what `runGameTestServer` already does.
- The working directory for every throwaway world is `$AUTOTEST`, a fresh directory under the
  session scratchpad.
- A Gradle init script passed with `-I` moves the `server` and `client` runs' working directory to
  `$AUTOTEST/server/ve-autotest-<name>` and `$AUTOTEST/client`, so `build.gradle` is not edited for
  test runs. Each test server gets its own `server.properties` (seed, RCON), config, and world
  there, so the harness never reads or writes anything in `runs/server`, including its
  `server.properties`. Only `eula.txt` is copied from it.
- Headless clients start under `xvfb-run` with their working directory in `$AUTOTEST/client`, so
  they use their own options, config, and saves.
- The 26.2 build used in T4 runs from a separate `git worktree` of `26.2-dev` in `$AUTOTEST`,
  with its own `runs/`. The worktree is removed at the end.
- RCON listens on `127.0.0.1` only, with a random password generated per run.
- `$AUTOTEST` is deleted when the plan finishes, unless a test failed, in which case its logs and
  worlds are kept for inspection and their path is reported.

## Harness

- **Gametests:** the `gametest` source set (design decision 9), registered under the
  `voluminousenergy` namespace. Structure-based tests use small empty structures generated once
  and committed under the source set's resources. `manual_only` tests are the worldgen tests that
  need real terrain.
- **`autotest.py`:** a Python 3 script (standard library only) committed under
  `src/gametest/autotest/`. It does the following:
  - writes the Gradle init script and each test server's own `server.properties`;
  - starts and stops servers and clients;
  - speaks RCON (a small socket client, since no RCON tool is installed);
  - collects logs and gametest reports;
  - prints a pass/fail table.
  Each tier is a subcommand (`autotest.py t3`), and `autotest.py all` runs them in order.
- **Log scan:** every server or client run fails on any `ERROR` or `Exception` line that mentions
  `voluminousenergy`, any missing-model or missing-texture warning for our namespace, any
  registry or datapack load failure, and any `Attempted to load class` client-on-server error.

## T0. Static checks

| ID | Check | Covers |
|---|---|---|
| T0.1 | `./gradlew compileJava` and `./gradlew build` succeed | all |
| T0.2 | `./gradlew runClientData` runs, and the `src/generated/resources` diff contains only expected changes: loot modifiers use `condition` with `minecraft:all_of` in the old order, and nothing else changes unless a task explains it | loot modifiers |
| T0.3 | Every JSON file under `src/main/resources` and `src/generated/resources` parses (`python3 -I -m json.tool`), and no file under `worldgen/feature/` has a `config` key | worldgen datapack requirements |
| T0.4 | `grep` finds no references to removed APIs (`neoforge.items`, `neoforge.energy`, `fluids.capability`, `FluidAction`, `ConfiguredFeature`, `FeaturePlaceContext`, `PerlinSimplexNoise`) and no `CapabilityAdapters` | design 1, 4, 6 |
| T0.5 | AGENTS.md conventions on changed files: no new inline `com.veteam.` qualified names, no `System.out`, no new `javax.annotation` imports, no spaced dashes in comments | AGENTS.md |
| T0.6 | `openspec validate port-to-26-3 --strict` passes | change artifacts |

## T1. Offline algorithm checks (vanilla jars, no game launch)

| ID | Check | Covers |
|---|---|---|
| T1.1 | Parse the committed `worldgen/noise/petroleum_reservoir_boundary.json` with 26.3 `NormalNoise.DIRECT_CODEC` and sample it for 3 seeds at about 550 000 points. Sample the 26.2 definition on the 26.2 jar the same way. Max abs difference ≤ 1e-5 | design 6, reservoir shape parity |
| T1.2 | Check that `RandomSource`, `XoroshiroRandomSource`, `LegacyRandomSource`, `WorldgenRandom`, and `PositionalRandomFactory` are still byte-identical between the 26.2 and 26.3 decompiled sources (guards a NeoForge or Minecraft hotfix) | design 6 |
| T1.3 | Tunnel shape parity: the 26.3 feature's `traceOutline`, `traceTunnel`, and `tunnelThickness` are the 26.2 carver's (from `git show 26.2-dev`) apart from configuration access, and the vanilla helpers they call (`Mth.sin`, `Mth.cos` and their table, `UniformFloat`, `UniformHeight`) are unchanged between the decompiled 26.2 and 26.3 jars. With T1.2 this fixes the outline for a given random seed | petroleum caves keep their shape |

## T2. Gametests in `./gradlew runGameTestServer`

Each test sets up blocks in its own structure, runs for a bounded number of ticks, and asserts.
The run fails if any required test fails, and the JUnit-style report is kept.

### Machine storage and transfer (`machines/storage-and-transfer`)

| ID | Test | Asserts |
|---|---|---|
| T2.1 | Round-trip every machine: place each block in `VEBlocks.REGISTERED_BLOCKS` that has a tile, fill every slot with a valid item, every tank to a partial amount, energy to a partial amount, and set side configuration, then save the tile with `saveWithFullMetadata` and load into a fresh tile | contents, energy, side settings, and upgrade slot are equal |
| T2.2 | NBT shape: save the round-trip tiles from T2.1 | the compound has `inv.Items` (with `Slot`), `inv.Size`, `energy.energy`, `energy.energy_production`, `energy.energy_consumption`, `energy.upgrade_slot`, and one child per tank with `Fluid`, exactly as 26.2 wrote them |
| T2.3 | Load 26.2 fixtures: the NBT captured from the 26.2 build in T4.2, committed as test resources, is loaded into fresh tiles | items, fluids, and energy match the values T4.1 set |
| T2.4 | Hopper into an enabled input side of a Crusher holding Iron Ore | ore moves into the input slot |
| T2.5 | Same with the input side disabled | nothing moves |
| T2.6 | Item capability on the output face, `insert` Cobblestone in a committed transaction | 0 inserted |
| T2.7 | Item capability on the output face, `extract` from a full output slot | items leave, `onContentsChanged` fired once after commit |
| T2.8 | Fluid capability fill of Crude Oil into a Distillation Unit; fill of Lava into the same tank | Crude Oil accepted up to capacity; Lava refused |
| T2.9 | Drain an output tank on its enabled face; drain an input tank with `Allow Extraction from Input Tanks` false and then true | output drains; input refuses, then drains |
| T2.10 | Energy capability insert of 1 000 000 FE into an empty Electric Furnace in one call | inserted equals the configured max transfer, never above capacity across repeated ticks |
| T2.11 | Open a transaction, insert items, fluid, and energy through the capabilities, then close without commit | all contents unchanged and no `onContentsChanged` |
| T2.12 | Nested transaction: commit the inner one and abort the outer | contents unchanged |
| T2.13 | Crude Oil Bucket in a Distillation Unit's fluid input slot, tick | tank +1 000 mB and an empty bucket in the fluid output slot; an empty bucket with a full output tank fills and the tank drops 1 000 mB |
| T2.14 | Battery in a Battery Box with energy | battery energy rises and box energy falls by the same amount |
| T2.15 | Stalled recipe: a Crusher with a full output slot and input; extract the output through the capability | processing resumes within 2 ticks (recipe dirty flag after root commit) |
| T2.16 | Owner operations directly: take more than present, add a result that partly fits, fill past capacity, drain from empty, consume energy below zero, and add energy above capacity | each returns the clamped amount and leaves valid state |

### Recipe smoke tests (all processor types)

| ID | Test | Asserts |
|---|---|---|
| T2.17 | For each machine with a recipe processor, load the inputs of one real recipe from the recipe manager, give energy and fluids, and tick until the recipe time plus a margin. This covers `BasicProcessor`, the electric furnace, gas-fired furnace, combustion generator, generator, air compressor, pump, Battery Box, Tooling Station, Dimensional Laser, Hydroponic Incubator, Sawmill, Implosion Compressor, Industrial Blast Furnace, and the RNG parser machines | the outputs and fluid outputs of the recipe appear, inputs drop by the recipe amounts, and energy drops for consumers or rises for generators |
| T2.18 | Hydroponic Incubator with an empty fluid output tank and a recipe with a fluid result, if one exists. Otherwise assert there is none and record it | the fluid lands in the tank (the old live-stack bug cannot reappear) |

### Menus (server side, no client)

| ID | Test | Asserts |
|---|---|---|
| T2.19 | For every machine, open its menu for a mock server player | slot count and slot positions match `VEContainerFactory`, and every slot is backed by the right handler index |
| T2.20 | Quick-move (shift-click) from the player inventory into each machine, and from each output slot to the player | items move to the slots vanilla rules allow, and the machine contents match the menu |
| T2.21 | Set a stack into a slot through the menu, then read the machine | the machine holds it (`StackCopySlot` write-back) |
| T2.22 | Tooling Station and experience-listener slots: insert and remove a multitool and its parts through the menu | the listener side effects happen as on 26.2 (multitool assembled, experience granted) |

### Items

| ID | Test | Asserts | Covers |
|---|---|---|---|
| T2.23 | Each solid fuel: light a furnace, then a blast furnace, with it and Iron Ore | furnace burn ticks are 4000, 3200, 800, and 200 as specified and blast furnace ticks are half; Petcoke smelts 20 ore in each | `items/solid-fuels` |
| T2.24 | Hopper beside a furnace holding Coal Coke | Coal Coke enters the fuel slot | `items/solid-fuels` |
| T2.25 | Battery item energy through `VEItemCapabilities` and the battery charge/discharge path | charge and discharge stay within max transfer and capacity |  |
| T2.26 | Multitool: bits report abilities (chain: none, scooper: `SHOVEL_DOUSE`, trimmer: shears set). `useOn` with a chain bit on an Oak Log, and a scooper bit on Grass | the log is not stripped and no path is made (parity with 26.2) | `items/multitool` |
| T2.27 | Multitool fluid tank through `Capabilities.Fluid.ITEM` | fill and drain work and persist in the item's component |  |
| T2.28 | Ammonium Nitrate Bucket used on a crop; record whether it has a fluid capability | the crop grows; no fluid capability, as on 26.2, where NeoForge gave it only to plain `BucketItem`s |  |

### Blocks, fluids, loot, advancements

| ID | Test | Asserts | Covers |
|---|---|---|---|
| T2.29 | Bone meal on each VE crop at age 0 | the age increases | crops |
| T2.30 | Natural Gas source on an open stone floor | gas climbs to at least 2 blocks above the source and never below the floor (a gas source spreads sideways first and climbs through its flowing neighbours, so a one-block sealed column never rises, on 26.2 as on 26.3) | `fluids/gases` |
| T2.31 | Gas next to a `#minecraft:washed_away_by_fluids` block and a solid block | the washable block is replaced, the solid one is not | design 7 |
| T2.32 | Piston pushes into each VE fluid block and the Pressure Ladder | the block pops (`POPPED`) | design 7 |
| T2.33 | Loot: for each of our global loot modifiers, read it from the loaded registry | its condition is `all_of` with the terms in the old order and the same ids and chances |  |
| T2.34 | Loot: roll the mooshroom loot table 2 000 times through the full loot pipeline with a player killer and looting 0 | Animal Fat appears in 50% ± 5% of rolls; with looting 3, at least 90% |  |
| T2.35 | Build a Dimensional Laser until its build tick reaches 1 000, then fire its construction trigger for a mock player (a mock player cannot join the level without a connection) | the construct-laser advancement completes |  |

### Fluid deposits (`fluids/climate-deposits`)

| ID | Test | Asserts |
|---|---|---|
| T2.36 | `queryForFluids` twice at 50 positions | identical results |
| T2.37 | Every reported amount for a recipe fluid | within its Dimensional Laser recipe's minimum and maximum |
| T2.38 | Positions matching no recipe | exactly one of water or lava |
| T2.39 | Saved-data round trip: write a `ChunkFluid` with 50 000 mB through `ChunkFluids`' codec, then read it back; a laser reading a chunk saved with 50 000 mB of Crude Oil | same fluids and amounts; the laser fills its tank with Crude Oil (the saved amount caps each cycle and is not reduced, as on 26.2) |

## T3. Worldgen in a throwaway dedicated-server world

`autotest.py t3` creates `ve-autotest-wg` with fixed seed `-4172144997902289642` (the seed is
recorded in the report, so any seed works) and default config. It starts `runServer` with RCON,
pre-generates the survey area with the `voluminousenergy:wg_pregen` `manual_only` test (forces
chunk generation in rings, so it needs no command mod), then runs the `manual_only` worldgen
tests over RCON with `/test run voluminousenergy:<name>` and reads their results from the log.

The survey area is a square of 41 × 41 chunks around the origin in the Overworld, and
11 × 11 chunks in the Nether and the End. If a feature the survey requires is not found, the area
grows by 20 chunks up to twice, before the test fails as "not found" (rare features).

| ID | Test | Asserts | Covers |
|---|---|---|---|
| T3.1 | `wg_petroleum_caves_sealed`: find every connected body of crude oil and natural gas whose outline matches a tunnel (not a reservoir or lake) | at least one oil and one gas tunnel; every fluid block is a source; every face neighbour of the body is the same fluid, a bituminous block of the variant its replaced block calls for, or `#minecraft:features_cannot_replace` | petroleum-caves: filled, sealed |
| T3.2 | `wg_petroleum_caves_heights`: the same bodies | height range and farthest reach recorded: tunnels start within y -40..30 (oil) and -48..16 (gas) and then pitch as vanilla cave tunnels do, so outlines reach about 12 to 20 blocks past the band; the reach is measured from the start chunk's middle, so it can pass 48 by up to the start's offset in that chunk. The outline code is 26.2's carver verbatim (T1.3) | petroleum-caves: generate |
| T3.3 | `wg_caves_never_cut_shell`: for each tunnel, check every shell position | none is air, cave air, water, or lava, so no carver cut it; openings inside a structure or beside a geode or dungeon, which vanilla places after petroleum features as it did on 26.2, are counted separately | petroleum-caves: vanilla caves |
| T3.4 | `wg_chunk_border_continuity`: tunnels and reservoirs crossing chunk borders | the sealing invariant holds across the border (covered by T3.1 and T3.5, reported separately) | caves, reservoirs |
| T3.5 | `wg_reservoirs`: every reservoir body | pools have a flat top level and caps a flat floor; sealed except the open face, with openings beside structures, geodes, and dungeons counted separately; no flowing fluid; no other fluid overwritten; each column's fluid at least 8 blocks under the highest solid block (not leaves or logs), unless ground at least that high stands 12 blocks away (a ravine or pit, counted separately); columns whose fluid continues above the reservoir belong to a geyser or lake and are counted separately | petroleum-reservoirs |
| T3.6 | `wg_lakes_geysers`: surface oil lakes, underground oil deposits, and geysers in the area | each found at least once; lakes have bituminous rims; underground deposits are sealed and fully filled; geysers have no uncut layers | oil-lakes, oil-geysers |
| T3.7 | `wg_ores`: ore blobs and deposits | each VE ore found in its allowed dimension and height band; deposits have a raw-block core inside an ore shell; the Cinnabar deposit core is vanilla Cinnabar | ore-deposits |
| T3.8 | `wg_rice`: rice crops in the area | rice found in water in an allowed biome | rice feature |
| T3.9 | `wg_other_dimensions`: Nether and End surveys | no crude oil, natural gas, or bituminous blocks; Eighzo ore present in the End | petroleum-caves: other dimensions |
| T3.10 | `wg_fingerprint`: hash of the sorted positions and states of every VE fluid, bituminous, ore, and raw block in the area | recorded for T3.12 | |
| T3.11 | `wg_reservoir_cache` in the reference world: every reservoir cell and position owner read through the shared caches, over the whole area, against cells computed from scratch; then a second world with `Cache Petroleum Reservoir Cells = false` runs `wg_reservoirs` | no difference; reservoirs sealed with caching off. Whole-world fingerprints are not compared: chunks generate on several threads, so two fresh worlds of the same seed and order already differ at chunk borders | reservoir caching never changes output |
| T3.12 | Third world with the same seed, pregen in a random chunk order with the server restarted halfway | T3.1 to T3.5 hold in it; the fingerprint is recorded, not compared, because vanilla ore placement, oil lakes, geysers, and reservoir plugs at chunk borders depend on generation order on 26.2 as well | sealed and seamless whatever the order, reload mid-exploration |
| T3.13 | Datapack override world: a test datapack in the world sets `crude_oil_cave` to `minecraft:lava` with biomes `#minecraft:is_deep_ocean` (the survey area has no desert, and its tunnel starts split between deep ocean and other biomes), sets `surface_oil_lake` to Lava, and removes `natural_gas_cave` from the biome modifier | lava tunnels start only under deep ocean and stay sealed under neighbouring biomes; replayed starts in other biomes hold lava at under 1% of their outline; no gas tunnels; surface lakes are lava | caves, lakes datapack requirements |
| T3.14 | Invalid datapack world: a natural gas reservoir cap with `reaches_surface` set | the feature fails to load and the log names the cap-reaches-surface problem | petroleum-reservoirs: invalid configuration |
| T3.15 | Fast surface sampling world: `Fast Petroleum Reservoir Surface Sampling = true` | reservoirs present, sealed, and as deep under the ground as T3.5 requires | petroleum-reservoirs: fast sampling |
| T3.16 | Fluid scanner on real terrain: `queryForFluids` at 2 000 positions across ±50 000 blocks, the first 200 run twice | identical results, and recipe fluids found with their amounts recorded against the recipe ranges (climate sampler on real noise) | climate-deposits |
| T3.17 | Server log scan for the whole tier, plus a check that the server reached `Done` without client-class errors | clean, apart from the geyser's far-chunk worldgen lines, which 1.21.1 and 26.2 also log and which are counted separately | dedicated server |

## T4. Save compatibility across versions

| ID | Step | Asserts |
|---|---|---|
| T4.1 | In a `26.2-dev` worktree, start the 26.2 server in `ve-autotest-compat` and use commands to place one of every VE machine. Set contents with `/data merge block`: items in every slot, partial tanks, partial energy, non-default side settings, and an upgrade item. Stop the server | the world saves |
| T4.2 | Still on 26.2, restart and dump every machine with `/data get block` | captured NBT, saved as T2.3 fixtures and as the expected values |
| T4.3 | Copy the world to `$AUTOTEST/server` and start the 26.3 server on it | the world loads with no missing-registry, unknown-block-entity, or datapack errors, and with no data fixer errors for our namespace |
| T4.4 | `/data get block` every machine on 26.3 | items, tanks, energy, and side settings equal the T4.2 values |
| T4.5 | `manual_only` test `compat_machines_work`: for each machine, run the T2.17 recipe smoke at its position | the carried-over machines still process |
| T4.6 | Scanned chunks: a 26.2 scan needs a player, so instead the harness asserts that `persistence/` (the `ChunkFluids` saved-data codec) has no diff against `26.2-dev`. T2.39 covers the round trip on 26.3 | codec unchanged |

## T5. Headless client smoke (`xvfb-run`)

`autotest.py t5` copies the T4.3 world into `$AUTOTEST/client/saves/ve-autotest-client` and adds a
build area in front of spawn using the 26.3 server before copying. The build area holds one of
every machine, an enclosed natural gas column behind glass, a working Dimensional Laser, and a
surface oil lake. It then starts `runClient` under `xvfb-run -s "-screen 0 1280x720x24"` with
`--gameDir $AUTOTEST/client --quickPlaySingleplayer ve-autotest-client --width 1280 --height 720`.

| ID | Check | Asserts |
|---|---|---|
| T5.1 | The client reaches the world (log line for the integrated server `Done`, and player join) | within 5 minutes |
| T5.2 | JEI loaded with the VE plugin (log) and no JEI errors | JEI optional dependency works |
| T5.3 | Screenshots with `import -display` every 10 s for 60 s, viewed by the agent | the world renders; machine blocks show models, not missing-texture checkerboards; the gas column and the laser beam are visible |
| T5.4 | Client log scan | no render, model, texture, or screen errors in our namespace |
| T5.5 | Quit through the window close event (`kill -TERM` of the client JVM) and check that the world saved | the level file updates |

Opening GUIs needs mouse input, which is not available here (no `xdotool`), so GUI rendering
moves to T6.

## T6. Human checks (only what cannot be automated here)

1. Open each machine GUI: tank bars, energy bars, progress arrows, and side-configuration buttons
   render and respond.
2. Look at gases in the world and the Dimensional Laser beam for visual regressions the
   screenshots cannot judge.
3. JEI: open the VE recipe categories and check that a few recipes show correctly.

All three are confirmed by the maintainer. The Stirling Generator and Fluid Electrolyzer screens
were checked for item 1, nitrogen for item 2, and JEI in single player and on a dedicated server
for item 3; the dedicated server also covers the recipe network codecs, which single player does
not serialize.

## Reporting

`autotest.py` writes `$AUTOTEST/report.md`: a table of every test ID with pass, fail, or skip, the
seeds used, gametest JUnit reports, log scan results, and screenshot paths. A failing ID links to
its log excerpt. The agent summarizes it to the user and keeps `$AUTOTEST` only on failure.
