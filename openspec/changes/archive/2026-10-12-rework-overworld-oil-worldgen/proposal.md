## Why

Underground oil lakes generate as half-air vanilla-style lakes with a stone rim, which reads as an
odd flooded cave rather than a buried oil reservoir, and they are indistinguishable from the rock
around them until broken into. Oil geysers also leave a one-block-thick horizontal ring of uncut
stone inside their underground reservoir, a bug present since 1.15.2.

## What Changes

- Underground oil lakes become sealed oil deposits: the whole blob is crude oil (no air), wrapped in
  a complete bituminous shell, mirroring how ore deposits wrap raw ore blocks in ore.
- Surface oil lakes keep their current shape (oil below, air above) and only their stone rim becomes
  bituminous.
- Three new bituminous blocks, chosen per shell position from the block being replaced: Bituminous
  Sand where the host is colorless sand, Bituminous Red Sand where it is red sand, and Bituminous
  Gravel everywhere else, stone and deepslate included. They do not fall and use gravel sounds. Like
  saltpeter ore, they need a shovel and drop a new Bitumen item (3 to 7, plus Fortune), or
  themselves with Silk Touch. They are tagged `c:ores/bitumen` and `c:ores` like saltpeter ore, but
  not `c:ores_in_ground`, since they are not ore blobs, and the Bitumen item is tagged
  `c:bitumen`. Textures are supplied by the maintainer; this change ships blockstate, model, item
  model, loot table, lang, and tag data referencing them.
- Oil geysers generate their underground reservoir with no uncut layer.
- **BREAKING (datapacks)**: the feature type `voluminousenergy:ve_bsc_underground_lakes_feature` is
  removed and replaced by a new data-driven `voluminousenergy:ve_fluid_deposit_feature`. The
  configured feature id `voluminousenergy:underground_oil_lake` is kept, so biome modifiers and
  datapacks that reference the configured feature are unaffected.

## Capabilities

### New Capabilities
- `worldgen/oil-lakes`: generation of surface oil lakes and underground oil deposits, including the
  bituminous rim and shell and how the variant is chosen.
- `worldgen/oil-geysers`: shape continuity of the oil geyser column and its underground reservoir.
- `blocks/bitumen`: the three bituminous blocks, the Bitumen item they drop, and their tags.

### Modified Capabilities
None. `openspec/specs/` is empty; this change writes the baseline for these areas.

## Impact

- Code: `world/feature/VELakesFeature`, `SurfaceMattersLakesFeature`, `GeyserFeature`, `VEFeatures`,
  a new fluid deposit feature in `world/feature/`, three blocks registered in `VEBlocks`, one item
  registered in `VEItems`.
- Data: `worldgen/configured_feature/{surface_oil_lake,underground_oil_lake}.json`,
  `neoforge/biome_modifier/underground_oil_lakes_overworld.json`, new block and item assets, loot
  tables, lang entries, and `c:` block and item tags in `src/main/resources`; regenerated
  `mineable/shovel` and `needs_wood_tool` tags in `src/generated`.
- Save compatibility: new registry ids `bituminous_gravel`, `bituminous_sand`, and
  `bituminous_red_sand` (blocks and block items) and `bitumen` (item); no NBT, data component, or
  attachment changes. Already generated chunks keep their old lakes and
  geysers; only new chunks use the new generation.
- Config: no new or changed options.
- Follow-up ports (not in scope): the geyser reservoir fix applies unchanged to `26.1-dev` and
  `26.2-dev`, which carry the same `GeyserFeature` loop. The bituminous rework would need its own
  proposal on those branches.
