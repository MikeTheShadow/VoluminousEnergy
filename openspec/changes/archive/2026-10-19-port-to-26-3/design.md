## Context

See proposal.md for why. On `26.2-dev` the machines keep their contents in NeoForge's legacy
storage types: `VEItemStackHandler` extends `ItemStackHandler`, each `VERelationalTank` wraps a
`FluidTank`, and `VEEnergyStorage` extends `EnergyStorage`. Recipe processors, parsers, menus,
`VETileEntity`, and the side wrappers (`MultiSlotWrapper`, `MultiFluidSlotWrapper`) all call that
legacy surface directly. The transfer API already appears at one boundary only:
`CapabilityAdapters` wraps the legacy storage in `ResourceHandler`/`EnergyHandler` for the block
capabilities registered in `VEGenericListener`, and item capabilities go through
`VEItemCapabilities`, which converts transfer handlers back to `IEnergyStorage`/`IFluidHandler`
with NeoForge's `of(...)` bridges. NeoForge 26.3 deletes the legacy packages
(`neoforge.items`, `neoforge.energy`, `neoforge.fluids.capability`, `FluidUtil`, `IFluidTank`,
`FluidActionResult`) and the bridges, but keeps the transfer API unchanged apart from small
additions.

Minecraft 26.3 changes these areas the mod depends on:

- **Features:** `ConfiguredFeature`, `FeatureConfiguration`, `FeaturePlaceContext`, and the
  `configurations` package are gone. `Feature` is an interface whose instances carry their own
  settings and are registered in the `worldgen/feature` registry. Their codecs are registered in
  `worldgen/feature_type`, and `place(level, generator, random, origin)` replaces
  `place(context)`. `OreConfiguration.TargetBlockState` becomes `BlockReplacement`.
- **Carvers:** carvers follow the same pattern (`worldgen/carver`, `worldgen/carver_type`). A
  carver now only marks positions in a shared `CarverOutput` mask. `NoiseBasedChunkGenerator` then
  fills every marked position from the aquifer (air, water, or lava). A carver has no chunk or
  block access.
- **Density functions:** functions are compiled into samplers. `RandomState.router()` and
  `RandomState.sampler()` are gone, replaced by `getSampler(function)` and
  `createClimateSampler(SamplerContext)`, and samplers return floats. `PerlinSimplexNoise` is
  removed and `SimplexNoise` is rewritten with a different range.
- **Fuel and tools:** fuel is the `cooking_fuel` item component (`CookingFuel` with `ResolvableInt`
  burn time), so `FuelValues` and the `getBurnTime` override are gone. Strip, scrape, wax-off,
  flatten, and till moved to the `block_transformer` component, and NeoForge dropped those
  `ItemAbility` constants and the `DEFAULT_AXE/SHOVEL/HOE_ACTIONS` sets.
- **Smaller changes:** `PushReaction.DESTROY` became `POPPED`. `BonemealableBlock` methods take a
  `BonemealSource`. `BiomeModifier.modify` takes a `RegistryAccess`. Loot modifiers take one
  `Optional<Holder<LootItemCondition>>`. Advancement trigger instances use
  `Optional<Holder<LootItemCondition>>` for the player. `PoseStack.mulPose(Quaternionf)` became
  `rotate(...)`. `BlockState.blocksMotion()` is gone from fluid spreading. `GatherDataEvent`
  lookup accessors were renamed.

## Goals / Non-Goals

**Goals:**
- Compile and run on 26.3 with the smallest behaviour difference from 26.2 that the platform
  allows, and keep machine NBT readable across the upgrade.
- Keep the factory model: no per-machine tile or container classes. Changes land in
  `VETileEntityFactory`, `VEContainerFactory`, `VETileEntity`, `VEContainer`, and the shared
  storage classes.

**Non-Goals:**
- Rewriting recipe processors to run inside transfer transactions.
- Rebalancing anything or adding config options. Burn times become data only as far as vanilla's
  own fuels are.
- Porting anything to other branches.

## Decisions

### 1. Machine storage is rebased onto NeoForge's transfer storage

The existing storage classes stay, but extend NeoForge's transfer implementations instead of the
deleted legacy ones:

- `VEItemStackHandler` extends `ItemStacksResourceHandler`.
- A new `VEFluidTank` extends `FluidStacksResourceHandler` with one index, replacing `FluidTank`
  inside `VERelationalTank`.
- `VEEnergyStorage` extends `SimpleEnergyHandler`.

Every VE behaviour these classes carry maps onto a NeoForge hook:

| Behaviour | Hook |
|---|---|
| Recipe-based slot validity, upgrade-slot tag, per-machine `AbstractItemStackValidator` | `isValid(index, resource)` override |
| `setChanged`, `markRecipeDirty`, `markFluidInputDirty` | `onContentsChanged(index, previous)`, which fires on `set(...)` and on root commit of a transfer |
| Tank capacity and validators | `getCapacity(index, resource)` and `isValid` overrides |
| Energy production, consumption, upgrade slot, clamped add/consume/set | `SimpleEnergyHandler`'s protected `energy`/`capacity`/`maxInsert`/`maxExtract` and `onEnergyChanged` |
| 26.2 save format (`Items`/`Size`, `Fluid`, `energy`, plus `energy_production`, `energy_consumption`, `upgrade_slot`) | `serialize`/`deserialize` overrides that write and read the 26.2 keys |

Machine code is the owner of this storage and changes it outside any transfer, so it does not
use transactions. The classes expose reads that return copies (stack, fluid, energy) and a small
set of owner operations named for what machines do. These are taking ingredients out of a slot,
adding a result to a slot (returning what did not fit), checking whether a result fits, filling
and draining a tank, and adding, consuming, or setting energy. They are built on `set(...)`, so
they never open a transaction and cannot clash with one that is already open. The 40 call sites
in processors, parsers, `VETileEntity`, menus, and utilities move to these operations. The legacy
signatures (`insertItem(slot, stack, simulate)`, `FluidAction`, `receiveEnergy(amount, simulate)`)
are not recreated. The 8 simulated calls become explicit fit checks.

The side views become filtering `ResourceHandler`s that delegate to the real handler.
`MultiSlotWrapper` exposes a side's enabled slots and enforces input-only and output-only.
`MultiFluidSlotWrapper` exposes a side's tanks and enforces fill and drain rules, including
`Allow Extraction from Input Tanks`. Undoing cancelled transfers comes from the underlying
handlers' journals, so `CapabilityAdapters` and its snapshot code are deleted.
`VEGenericListener` returns the side views, and `VEEnergyStorage` itself, as the block
capabilities.

Dead code removed on the way:
- The extraction-validator hook. Every `allowItemExtraction` returns `true`, so it leaves
  `AbstractItemStackValidator`.
- The unused `simulate` parameter of `allowItemInsertion`.
- `HydroponicParser` editing the live `FluidStack` from `getFluidStackFromTank`, which skipped
  change notifications and did nothing on an empty output tank. It uses the tank owner operations
  as `BasicParser` does. `getFluidStackFromTank` returns a copy.

Alternatives:
- VE-owned standalone classes that reimplement the removed legacy behaviour and keep their method
  signatures, with `CapabilityAdapters` as the transfer boundary. Rejected: the mod would own
  storage semantics and snapshot code that NeoForge now provides, and recreating the legacy
  signatures carries the old API forward as debt.
- Machines call NeoForge's handlers directly with transactions for every internal move. Rejected:
  internal machine work has nothing to roll back, and NeoForge's own idiom for owner code is
  direct reads and `set(...)`.

### 2. Item-side capabilities use the transfer API directly

Batteries, the multitool's tank, buckets in machine slots, the fluid scanner, and the Ammonium
Nitrate Bucket deal with item stacks, where the transfer API's `ItemAccess` is the only supported
route. `VEItemCapabilities` returns `EnergyHandler` and `ResourceHandler<FluidResource>`. Callers
move resources in a transaction, through NeoForge's `transfer.fluid.FluidUtil`,
`EnergyHandlerUtil`, and `ResourceHandlerUtil` where they fit. Moves between a machine and an item
(bucket slots, battery slots) use the machine's own handler as the other side of the same
transaction, in one helper beside `BucketInputOutputUtil`. `PlayerInvWrapper` becomes
`PlayerInventoryWrapper`.

### 3. Menu slots extend `ResourceHandlerSlot`

`VEContainerFactory.VESlot` currently extends `SlotItemHandler`. It will extend NeoForge's
`ResourceHandlerSlot`, built directly on the machine's `VEItemStackHandler`, with
`VEItemStackHandler::set` as the index modifier. `ResourceHandlerSlot` is a `StackCopySlot`: it
hands vanilla menu code a copy of the stack and writes changes back. Any VE menu code that edits
`slot.getItem()` in place (`VEContainer.quickMoveStack` and the IO-listener slots
`SlotWithIOListener`, `ToolingStationSlotWithIOListening`, and `ExperienceListener`) is checked
and changed to write back through the slot. Slot index lookups that used `getSlotIndex()` use the
handler index the slot stores.

### 4. Features become records registered as feature types

Each VE feature becomes a record implementing `Feature`, with the fields of its old
`Configuration` and a `MapCodec`. `VEFeatures.VE_FEATURE_REGISTRY` becomes a
`DeferredRegister<MapCodec<? extends Feature>>` on `Registries.FEATURE_TYPE`, under the same names,
so type ids such as `voluminousenergy:ve_ore_deposit_feature` are unchanged. `RiceFeature`'s
`BlockStateConfiguration` becomes a `state` field. Ore blobs use vanilla `OreFeature` with
`BlockReplacement` lists, so `VEOreGeneration`'s target lists change type only. Each
`configured_feature` JSON moves to `worldgen/feature/` and has its `config` object hoisted into the
top level, keeping the same ids. Block and fluid states in that JSON follow vanilla 26.3's
`{"id": ..., "properties": ...}` form instead of `{"Name": ..., "Properties": ...}`, which no longer
parses. The `minecraft:simple_state_provider` type became `minecraft:simple`, so the ore deposits'
providers are written as the plain state, the form vanilla writes.

### 5. Petroleum caves become a feature that replays tunnels per chunk

A carver can no longer place oil, gas, or a shell. `VEFluidCaveCarver` becomes a feature,
`VEFluidCaveFeature`, registered as feature type `ve_fluid_cave_feature`, keeping its fields plus
a `biomes` holder set and a `salt`. The carver-only `lava_level` and `debug_settings` are dropped. It runs in the `raw_generation` step, the first decoration step, so it
follows all carving and precedes every other feature, as the carver did. For the chunk being
decorated, it visits every source chunk within 4 chunks. The carver's `getRange()` of 2 sets the
tunnel length (48 steps), and a tunnel plus its radius reaches up to about 4 chunks from where it
starts, so the feature looks twice the range away, as vanilla's carver loop does (8 chunks for
range 4). For each source chunk it seeds a `WorldgenRandom` the way vanilla seeds carvers, from the
world seed plus the feature's `salt` and the source chunk position, and reads the source chunk's
biome where vanilla does (quart y 0 at the chunk's corner). The salts (1001 for oil, 1002 for gas)
keep the two kinds of tunnel apart and stay clear of the small indices vanilla adds to the seed for
its own carvers. It starts a tunnel when the start
probability passes and the source chunk's biome is in `biomes`, then replays the existing
`traceOutline` geometry. It writes fluid and shell blocks only inside the decorated chunk. Because
every chunk computes the same tunnels from the seed alone, tunnels are continuous and sealed
across chunk borders whatever order chunks generate in. This is the same technique
`FluidReservoirCells` already uses. The biome modifier becomes `neoforge:add_features` over
`#minecraft:is_overworld` with two new placed features that have no placement modifiers. The
`biomes` field, not the biome modifier, limits where tunnels start, so a restricted datapack
cannot produce half-filled tunnels at biome borders. The shell rule (`LakeMask` targets) is
unchanged.

Minecraft 26.3 carvers no longer test blocks, and `#minecraft:overworld_carver_replaceables` is
gone. The blocks a tunnel may fill, and the blocks a geyser carves, keep 26.2's list through a mod
tag, `voluminousenergy:overworld_carver_replaceables`, with the same entries.

Alternative: a mixin into `NoiseBasedChunkGenerator.applyCarvingMask` to let VE carvers place
their own blocks. Rejected: the mod uses no mixins, and this would patch a hot worldgen path that
changes every version.

Deviation from 26.2: vanilla caves can no longer run after a tunnel, so the shell plugs every
cave that meets a tunnel instead of a later cave stopping at it. The petroleum-caves delta
records this.

### 6. Climate, surface, and deposit noise use the 26.3 samplers

Worldgen algorithms are diffed against 26.2 before deciding whether to keep our own copy or adopt
vanilla's new one. The results:

- `RandomSource` and its implementations (`XoroshiroRandomSource`, `LegacyRandomSource`,
  `WorldgenRandom`, `PositionalRandomFactory`, `RandomSupport`) are byte-identical. Oil lakes
  (`LakeMask`), geysers, ore deposits, cave tracing, and reservoir cell placement keep their exact
  algorithms with no copy needed.
- `NormalNoise` was rewritten as an API, and `ImprovedNoise` was folded into `PerlinNoise`, but the
  algorithm is the same: same permutation setup, gradient table, smoothstep lerp, and per-octave
  seed strings. The noise JSON format changed (`firstOctave`/`amplitudes` became
  `base_octave`/`octave_count`/`base_amplitude`/`normalize`), and the default normalization is
  new. Vanilla converted its own noises with a parity `base_amplitude` that keeps the old scale.
  Sampling `voluminousenergy:petroleum_reservoir_boundary` on both versions for two seeds at about
  550 000 points each gave these results. With
  `{"base_octave": -4, "octave_count": 2, "base_amplitude": 0.9494731054427981}` (the value
  `NormalNoise.createParity(-4, 1.0, 1.0)` produces), 26.3 matches 26.2 within 1.5e-6, at most
  6e-6 blocks of boundary bend at wobble 4, a float-precision difference. With the default
  normalization, values differ by up to 0.07, the amplitude rises by about 5%, and boundaries shift
  by up to 0.2 blocks. The noise file is rewritten in the parity form, so reservoir shapes are kept
  without carrying our own noise generator.
- Reservoir levels and allowed biomes still read vanilla's preliminary surface and biomes, so they
  follow whatever terrain vanilla 26.3 generates, as intended.

`WorldUtil.sampleClimate` builds a `Climate.Sampler` with
`randomState.createClimateSampler(SamplerContext.EMPTY_UNCACHED)` and reads each axis's bound
sampler at the block position, the raw value 26.2 read from the noise router, now as a float.
`FluidReservoirCells` creates one uncached biome resolver per instance for `biomeAt`. It reads the
preliminary surface by sampling `NoiseGeneratorSettings.aquifers()`'s `surfaceLevel` function
through `randomState.getSampler(...)`, the same value vanilla uses for aquifers. If a generator
has no aquifer settings, it falls back to the generator's own surface height. The two
`PerlinSimplexNoise` uses (`FluidClimateSpawn` and `WorldUtil.fallbackFluidAmount`) share one
helper, `util/climate/DepositNoise`. 26.3 `SimplexNoise` initialises from the random source exactly
as 26.2's did and adds its offsets inside `get`, so summing eight octaves as `PerlinSimplexNoise` did
reproduces 26.2's deposit noise for a seed, apart from float rounding, in the same [-1, 1] range.
New scans can still differ from 26.2 where the climate inputs differ, since those come from
vanilla's rewritten density functions. Chunks already scanned keep their saved amounts.

### 7. Fuels, tool abilities, and block properties follow vanilla's new data

- Solid fuels use `Item.Properties.cookingFuel(key)`, as vanilla fuels do, and drop the
  `getBurnTime` override. On 26.2 the smoker and blast furnace halved every fuel's burn time in
  code; 26.3 moved that halving into vanilla's `context_int_provider/cooking/*` data, which
  divides the ticks by `minecraft:cooking/fast_burn_time_reduction_factor` in fast-cooking blocks.
  A constant burn time would double the mod's fuels in those blocks, so the mod adds five
  providers in vanilla's form under `data/voluminousenergy/context_int_provider/cooking/`
  (`time_petcoke`, `time_coal_coke`, `time_rosin`, `time_animal_fat`, `time_tiny_fuel`), keyed in
  `VECookingFuels`. Ticks are unchanged in every block (spec `items/solid-fuels`).
- Multitool bits: chain bits get an empty ability set and scooper bits get `SHOVEL_DOUSE` only.
  Trimmer bits keep `DEFAULT_SHEARS_ACTIONS`. On 26.2 the multitool has no `useOn`, so it never
  stripped, flattened, or tilled, and the removed abilities were never exercised. Behaviour is
  unchanged.
- `PushReaction.DESTROY` becomes `POPPED` in fluid block properties and `PressureLadder`.
- `VEFlowingGasFluid` mirrors vanilla 26.3 `FlowingFluid`, where the `blocksMotion()` check became
  `canHoldAnyFluid` (a `LiquidBlockContainer` or `#minecraft:washed_away_by_fluids`). Gases follow
  vanilla's new rule, so they stay consistent with water and lava.

### 8. Loot, advancements, crops, rendering, datagen

- `AnimalFatLootModifier` and `MysteriousMultiplierModifier` use `LootModifier.codecStart` with
  the single optional condition. `VEGlobalLootModifierData` wraps the previous condition lists in
  `AllOfCondition` and passes `provider.lookupOrThrow(Registries.ENCHANTMENT)` to
  `randomChanceAndLootingBoost`. Drops do not change. NeoForge 26.2's `LootModifier` already
  combined its `conditions` list with `AllOfCondition.allOf`, so an explicit `all_of` in the same
  order tests the same conditions in the same order and consumes the same random rolls.
  `random_chance` still accepts an inline number, and `neoforge:loot_table_id`, `entity_properties`,
  and modifier priority are unchanged.
- `ConstructDimensionalLaserTrigger.TriggerInstance` takes `Optional<Holder<LootItemCondition>>`
  for `player`.
- `VELandCrop` and `VEWaterCrop` add the `BonemealSource` parameter, with the same growth logic.
- `LaserBlockEntityRenderer` uses `PoseStack.rotate(...)`.
- `VEOreBiomeModifier` and `VEAndedMultiBiomeModifier` gain the `RegistryAccess` parameter.
- `VoluminousEnergy`'s data gathering uses the renamed lookup provider accessor.
- 26.3 rewrote the loot table and loot condition formats with no fallback for the old keys:
  `conditions` lists became one `condition` (several are combined with `minecraft:all_of`),
  `functions` became `modifier`, the `condition` and `function` discriminators became `type`, and
  `block_state_property` became `match_block` with `blocks` and `state`. Unknown keys are ignored,
  so old files would load but silently drop their conditions and functions. The hand-written block
  loot tables and the `stuck_in_oil` advancement are converted with the same conditions and
  functions. Their silk-touch tests keep 26.2's `"enchantment"` key, which the enchantment predicate
  does not read; fixing that is a separate change.
- 26.3 decodes recipes in parallel. `VERecipe`'s constructor added each recipe to a static
  `HashMap` of `ArrayList`s that nothing reads, which crashed recipe loading under contention, so
  that write is removed. The recipe cache the machines read is rebuilt on the main thread as
  before.

### 9. Verification is automated with gametests and throwaway worlds

The mod gets its first gametests, registered through `RegisterGameTestsEvent` under the
`voluminousenergy` namespace. They live in a separate `gametest` source set (Java plus
resources: empty test structures and test datapacks), added only to the `gameTestServer` and
`server` runs, so nothing test-related ships in the jar. If NeoGradle cannot run a second source
set as part of the same mod, the fallback is a `gametest` package in `main`. Its registration
already only runs when gametests are enabled, which a player's game never does.
Structure-based tests run in `./gradlew runGameTestServer`. Worldgen tests need real terrain, so
they are `manual_only` and run with `/test run` on a dedicated server in a throwaway world, driven
over RCON. The full plan, including how the user's own worlds stay untouched, is in
`test-plan.md`.

## Risks / Trade-offs

- [Owner operations differ subtly from the legacy calls they replace, such as partial inserts,
  stack-size limits, or capacity on an empty tank] → Each operation is covered by a gametest, and
  recipe smoke tests run real recipes through every processor type (test plan T2).
- [`onContentsChanged` now fires on root commit for pipe transfers instead of mid-transfer] → Only
  dirty flags hang off it. A gametest checks that a pipe insert still restarts a stalled recipe.
- [`StackCopySlot` write-back misses a VE menu path that edited stacks in place] → Server-side menu
  gametests run quick-move and slot edits on every machine menu (test plan T2).
- [The save format slips, for example an omitted `Size` or a renamed key] → The serialization
  overrides keep the 26.2 keys verbatim. The cross-version test (T4) builds a world on the 26.2
  build and loads it on 26.3.
- [Petroleum caves look different because the feature replays geometry with a different random
  sequence than the carver] → Seed the same way vanilla seeds carvers and keep `traceOutline`
  unchanged. Exact tunnel positions for a seed may still differ from 26.2, which only affects
  newly generated chunks.
- [Replaying tunnels per chunk costs time] → A cave feature visits at most 81 source chunks, each
  rejecting on one random float before any tracing. If profiling shows a cost, cache traced
  outlines per source chunk the way `FluidReservoirCells` caches cells.
- [More 26.3 API breaks surface once the first errors are fixed, since javac stops reporting
  some errors after attribution failures] → Tasks are grouped so each area compiles before the
  next. Anything unexpected that changes behaviour is raised before it is worked around.
- [JEI 31.16 API changes] → The JEI plugin compiles last against the new API. Category and
  recipe wiring stays the same.

## Migration Plan

Players update the mod jar alongside Minecraft 26.3. Machine contents, scanned chunk fluids, and
item components carry over. Worldgen changes apply only to newly generated chunks. Datapack
authors move `configured_feature`/`configured_carver` overrides to `worldgen/feature` and flatten
them, and switch loot modifier overrides to a single `condition`. Rollback is not supported, as
for any Minecraft version upgrade.
