## Why

Minecraft 26.2 adds Cinnabar and Sulfur as decorative stones in the new Sulfur Caves biome, plus
the Sulfur Spike and the Sulfur Cube mob. The mod already has Cinnabar and Sulfur materials, so
players now see two unrelated things with the same name in different colours, and the vanilla blocks
do nothing in the mod's processing chain. This change makes the vanilla blocks the mod's storage
blocks and connects the new sulfur sources to the mod. It also matches the mod's textures to the
vanilla palette.

## What Changes

- **BREAKING** The Raw Cinnabar Block (`voluminousenergy:raw_cinnabar_block`) is removed entirely,
  including its item, models, texture, loot table, lang entries, and tags. Vanilla Cinnabar takes its
  place. There is no migration: blocks and items with that id disappear from existing worlds. 26.2
  expects fresh worlds.
- Vanilla Cinnabar needs an iron or better pickaxe to drop, the tier the Raw Cinnabar Block had.
  Wood, gold, and stone pickaxes break it without a drop. This applies to every Cinnabar block,
  including those in Sulfur Caves. The polished, brick, and chiseled variants keep vanilla's rules.
- Crafting: 9 Raw Cinnabar make 1 Cinnabar, and 1 Cinnabar makes 9 Raw Cinnabar. The second recipe
  is new; the old block could not be uncrafted.
- Crafting: 9 Raw Sulfur make 1 Sulfur, and 1 Sulfur makes 9 Raw Sulfur. Both recipes are new.
- Vanilla Cinnabar is now `c:storage_blocks/raw_cinnabar`. The Crusher's existing raw-block recipe
  therefore crushes Cinnabar into 45 Cinnabar Dust, with a 25% chance of 18 Redstone.
- The Cinnabar deposit's core is vanilla Cinnabar instead of the Raw Cinnabar Block. Its Cinnabar Ore
  shell, size, and placement are unchanged.
- The smallest Sulfur Cube (size 1) drops 1 to 4 Raw Sulfur, multiplied by the killer's Looting
  level. Larger cubes drop nothing extra, because they split into smaller cubes.
- The Crusher turns 1 Sulfur Spike into 2 Sulfur Dust, with a 25% chance of 1 more, averaging the
  9 dust that 4 spikes give as a crafted Sulfur block.
- New textures for Cinnabar Ore, Deepslate Cinnabar Ore, Raw Cinnabar, and Cinnabar Dust, using
  palette C4: colours from vanilla Cinnabar and Polished Cinnabar. Cinnabar Dust stays clearly
  lighter and pinker than Netherrack Dust and apart from Bauxite Dust.
- New textures for Raw Sulfur and Sulfur Dust, using palette S4: colours from vanilla Sulfur and
  Polished Sulfur.
- Sulfuric Acid's still and flowing textures and its bucket change from grey to palette A2, the pale
  yellow goo and highlights of the Sulfur Cube bucket and spawn egg.
- Every palette option that was considered, with its hex values and the reason it was or was not
  chosen, is recorded in design.md under Texture palettes.
- Save and config: one registry id is removed (block and item). No data component, attachment, NBT,
  or config change.

## Capabilities

### New Capabilities

- `materials/cinnabar`: The vanilla Cinnabar block as the mod's raw cinnabar storage block: crafting
  in both directions, tool tier, tags, and the colours of the cinnabar items and ores.
- `materials/sulfur`: The vanilla Sulfur block as the mod's raw sulfur storage block: crafting in
  both directions, the Sulfur Cube's Raw Sulfur drop, and the colours of the sulfur items.
- `worldgen/ore-deposits`: Which blocks the Cinnabar deposit is made of.
- `fluids/sulfuric-acid`: How Sulfuric Acid looks in the world, in tanks, and in a bucket.

### Modified Capabilities

- `machines/crusher`: Adds Sulfur Spike crushing and Cinnabar crushing through the raw-cinnabar
  storage tag.

## Impact

- Java: `VEBlocks` loses `RAW_CINNABAR_BLOCK`, and `RawCinnabarBlock` is deleted. Commented-out
  references to the block in `VEBlockItems` and `VEFeatures` are removed. Vanilla Cinnabar gets the
  cinnabar tool tier in `VETagDataGenerator`. A Sulfur Cube entry goes into
  `VEGlobalLootModifierData`, reusing `AnimalFatLootModifier`.
- Data: recipes in `vanilla_crafting/storage_blocks/`, a new crushing recipe, the
  `cinnabar_deposit` configured feature, and `c:storage_blocks/raw_cinnabar` tags.
- Generated: the tool-tier tags and a new loot modifier, regenerated with `runClientData`.
- Assets: a deleted blockstate, models, item definition, and texture; recoloured PNGs; lang entries
  removed from `en_us`, `ja_jp`, and `ko_kr`.
- JEI shows the recipes through the existing recipe types, so `compat/` is untouched.
- No port to `1.21.1-dev` or `26.1-dev`: vanilla Cinnabar, Sulfur, the Sulfur Spike, and the Sulfur
  Cube exist only from 26.2.
