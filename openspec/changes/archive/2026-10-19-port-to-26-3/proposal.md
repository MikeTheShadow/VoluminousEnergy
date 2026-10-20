## Why

NeoForge for Minecraft 26.3 is stable, and players and modpacks moving to 26.3 cannot use Voluminous
Energy until it is ported. The `26.3-dev` branch already points at NeoForge 26.3.0.92 and JEI
31.16.0.84, but the mod no longer compiles. NeoForge 26.3 deletes the legacy item, fluid, and
energy capability classes the machines are built on. Minecraft 26.3 rewrites worldgen features,
carvers, and density functions, and moves fuel values and tool block transforms into data. This
port is larger than the 26.2 one and changes how machines store resources and how petroleum caves
generate, which is why it goes through a proposal.

## What Changes

- The mod runs on Minecraft 26.3 with NeoForge 26.3.0.92 or later, Java 25, and optionally JEI
  31.16.0.84. The Minecraft range becomes `[26.3,26.4)` and dev builds are labelled `26.3-dev`.
  This is already committed.
- Gameplay stays as on 26.2. Machines keep their slots, tanks, energy, side configuration,
  upgrades, and recipes. Pipes and cables from other mods still reach them through NeoForge
  capabilities. Batteries, the multitool, the fluid scanner, buckets, crops, solid fuels, loot
  drops, ores, oil lakes, geysers, and petroleum reservoirs behave as they do on 26.2, except
  that reservoirs now also fill seagrass and kelp.
- Machine storage is rebased onto NeoForge's transfer API storage classes. Machine code moves to a
  small set of owner operations instead of the removed legacy calls, and dead code goes with it.
  Storage keeps the 26.2 save format, so machines in a world carried over from 26.2 keep their
  contents.
- The mod gets its first automated tests: gametests in a separate source set that does not ship,
  plus a harness that verifies the port in throwaway worlds (see `test-plan.md`).
- Petroleum cave tunnels become a feature instead of a carver. Minecraft 26.3 carvers can only mark
  blocks to hollow out, and the game fills them with air, water, or lava, so a carver can no longer
  fill a tunnel with oil or gas or build its bituminous shell. The tunnels keep their shape,
  heights, rarity, fill, and shell. They now generate after all vanilla caves in a chunk, so no
  vanilla cave ever opens one.
- **BREAKING (datapacks):** following vanilla 26.3, the mod's feature JSON moves from
  `worldgen/configured_feature/` to `worldgen/feature/`, with the fields that were nested under
  `config` now at the top level and block states written in vanilla 26.3's `id`/`properties` form.
  Ids such as `voluminousenergy:surface_oil_lake` stay the same. The
  petroleum caves `voluminousenergy:crude_oil_cave` and `voluminousenergy:natural_gas_cave` move from
  `worldgen/configured_carver/` to `worldgen/feature/`, and their biome modifier changes from
  `neoforge:add_carvers` to `neoforge:add_features`. The caves gain a `salt` field and replace the
  blocks in a new mod tag, `voluminousenergy:overworld_carver_replaceables`, which keeps 26.2's
  `#minecraft:overworld_carver_replaceables` entries now that vanilla dropped that tag. Datapacks
  that override any of these need to move and flatten their files.
- **BREAKING (datapacks):** NeoForge global loot modifiers now take a single optional `condition`
  instead of a `conditions` list. The mod's animal fat and mysterious multiplier modifiers are
  regenerated with their conditions combined under `minecraft:all_of`, and drop the same items at
  the same chances. Datapacks that override these modifiers need the new shape.
- Solid fuel burn times move to vanilla's new `cooking_fuel` item component instead of a code
  override, with the same durations. Like vanilla's own fuels, they read burn-time providers in
  data, so smokers and blast furnaces still burn them for half as long.
- Chunks scanned with the fluid scanner on 26.2 keep their saved fluid amounts. Minecraft 26.3
  replaced the noise and climate sampling the scanner uses, so chunks first scanned on 26.3 get
  amounts from the new noise, still within each Dimensional Laser recipe's range.

## Capabilities

### New Capabilities
- `machines/storage-and-transfer`: how machine inventories, tanks, and energy persist across saves
  and version upgrades, and how neighbouring blocks insert into and extract from them through
  side configuration.
- `items/solid-fuels`: burn times of the mod's solid fuels in vanilla furnaces.
- `fluids/climate-deposits`: which fluids, and how much, the fluid scanner finds in a chunk for the
  Dimensional Laser to draw, and that amounts already saved survive the upgrade.

### Modified Capabilities
- `worldgen/oil-lakes`: the stable-id requirement now names features in `worldgen/feature`
  instead of configured features.
- `worldgen/petroleum-caves`: tunnels are features in `worldgen/feature`, added by an
  `add_features` biome modifier, and generate after all vanilla caves in a chunk instead of
  stopping later carvers. The shell variant rule lives in the feature JSON.
- `worldgen/petroleum-reservoirs`: the datapack requirement now names features in
  `worldgen/feature` instead of configured features. Reservoirs also fill seagrass and kelp as
  they fill water, which closes a rare unsealed position that 26.2 also generates where a
  neighbouring chunk grows seagrass into water the reservoir was about to fill.

## Impact

- Code: about 110 source files fail to compile. The largest areas are machine storage and
  capabilities (`VETileEntity`, `VETileEntityFactory`, `VEContainer`, `VEContainerFactory`,
  `VEItemStackHandler`, `VEEnergyStorage`, `VERelationalTank`, `CapabilityMap`,
  `CapabilityAdapters`, `MultiSlotWrapper`, `MultiFluidSlotWrapper`, `VEItemCapabilities`, and every
  recipe processor and parser), worldgen (`world/feature`, `world/carver`, `world/ores`,
  `world/modifiers`, `FluidReservoirCells`, `WorldUtil`, `FluidClimateSpawn`), and items (batteries,
  multitool, fluid scanner, Ammonium Nitrate Bucket, solid fuels). Smaller changes are needed in
  crops (bonemeal API), gas fluids, the laser renderer, the advancement trigger, loot modifiers,
  `PressureLadder` and fluid block properties (`PushReaction.DESTROY` is now `POPPED`), and
  datagen.
- Data: the 20 files in `src/main/resources/data/voluminousenergy/worldgen/configured_feature/` and
  the 2 in `configured_carver/` move to `worldgen/feature/` and are flattened.
  `neoforge/biome_modifier/petroleum_caves_overworld.json` becomes an `add_features` modifier.
  `worldgen/noise/petroleum_reservoir_boundary.json` moves to vanilla's new noise format, with a
  parity amplitude so reservoir boundaries keep their 26.2 shape. New data files: five burn-time
  providers in `context_int_provider/cooking/` and the block tag
  `voluminousenergy:overworld_carver_replaceables`. The 57 hand-written block loot tables and the
  `stuck_in_oil` advancement move to 26.3's loot formats with the same conditions and functions. The 2
  `placed_feature` files and the other 26 biome modifiers keep their paths. Petroleum caves gain 2
  placed features. Datagen output is regenerated for loot modifiers and anything else whose format
  changed.
- Save compatibility: machine NBT stays in the 26.2 format (`inv` with `Items`/`Size`, `energy`
  with `energy`, and one compound per tank with `Fluid`). Chunk fluid saved data is unchanged.
  Registry ids for blocks, items, fluids, tile types, menus, recipe types, feature types, and biome
  modifier types are unchanged. The carver type `voluminousenergy:ve_fluid_cave_carver` is removed
  and replaced by a feature type. Nothing in saved chunks refers to carver types, so this does not
  affect existing worlds. Data components and attachments are unchanged. Vanilla 26.3 handles
  world-level migration.
- Config: no options are added or removed. The NeoForge `COMMON` to `LOCAL` config rename does not
  affect the mod, which only registers `STARTUP` and `CLIENT` configs.
- Dependencies: NeoForge 26.3.0.92, JEI 31.16.0.84, and NeoGradle 7.1.39 (already the latest).
  The Gradle wrapper stays at 9.2.1, matching the NeoForge 26.3 MDK.
- Docs: `AGENTS.md`, `openspec/config.yaml`, and the Serena memories on this branch are updated to
  describe 26.3.
- Build and tests: `build.gradle` gains a `gametest` source set wired into the `gameTestServer` and
  `server` runs only. `src/gametest/` holds the gametests, test structures, test datapacks, and
  `autotest.py`. `CapabilityAdapters` is deleted.
- Follow-ups for other branches: none required. `26.2-dev` and `1.21.1-dev` keep their own storage
  and carver code. Future merges from `26.2-dev` into `26.3-dev` must resolve conflicts in the
  storage and worldgen classes this change rewrites, as is already done for gas block flags.
