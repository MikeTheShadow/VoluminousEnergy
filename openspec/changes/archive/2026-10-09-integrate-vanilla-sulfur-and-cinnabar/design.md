## Context

From the 26.2 client jar:

- `minecraft:cinnabar` and `minecraft:sulfur` are plain stones: `Properties.of()`,
  `requiresCorrectToolForDrops()`, `strength(1.5F, 6.0F)`, in `minecraft:mineable/pickaxe`, and not
  in any `needs_*_tool` tag. Each drops itself. Vanilla uses Cinnabar and Sulfur only in their own
  stone families and in Potent Sulfur (9 Sulfur, shapeless). Sulfur also comes from 4 Sulfur Spikes in
  a 2×2. No vanilla recipe uses a single Cinnabar or Sulfur, or a full 3×3 of either raw item.
- `SulfurCube` extends `AbstractCubeMob` with `MIN_SIZE = 1` and `MAX_SIZE = 2`.
  `minecraft:entities/sulfur_cube` is an empty loot table. The `minecraft:type_specific/cube_mob`
  sub-predicate (`CubeMobPredicate.sized`) matches on `AbstractCubeMob.getSize()`, the same predicate
  vanilla's slime loot uses.

In the mod:

- `RawCinnabarBlock` sets the cinnabar tier through `MaterialConstants.setCinnabarTier`, which goes
  to `VETagDataGenerator.setRequiresIronAndBlacklistLowerTiers`. That puts the block in
  `needs_iron_tool` and in `incorrect_for_{stone,gold,wood}_tool`. The block registers these from its
  constructor, and `VETagDataGenerator.addTags` writes the static lists.
- The Cinnabar deposit is data: `worldgen/configured_feature/cinnabar_deposit.json` runs
  `voluminousenergy:ve_ore_deposit_feature`, and `raw_block_state` is the core it fills.
- Storage block recipes, `c` tags, crushing recipes, deposit features, and lang are hand-written in
  `src/main/resources`. Tool-tier tags and loot modifiers are datagen output in
  `src/generated/resources`.
- `AnimalFatLootModifier` adds `nextInt(min, max)` items (the upper bound is exclusive), multiplied by
  luck and by the Looting level. It reads the killer with
  `context.getParameter(LootContextParams.ATTACKING_ENTITY)`. In 26.2 that is `ContextMap.getOrThrow`,
  so it throws `NoSuchElementException` when nothing killed the mob.
- Sulfuric Acid's still and flowing textures are greyscale, and its bucket has grey liquid. No VE
  fluid calls `VEFluidType.setColourTint`, and `VEFluidClientExtension.getTintColor` uses the tint
  only when `colourTint > 0`, which rules out any opaque ARGB value because its sign bit is set.

## Goals / Non-Goals

**Goals:**
- Delete the Raw Cinnabar Block outright, with no alias and no leftover commented-out references.
- Keep hand-written data hand-written and datagen data in datagen, as now.

**Non-Goals:**
- A `c:storage_blocks/raw_sulfur` tag or a crushing recipe for the Sulfur block. Raw sulfur has no
  storage block tag today, and the spec does not ask for one.
- Renaming `AnimalFatLootModifier` to a generic name. That would change its codec id and every
  generated animal fat JSON, so it is left for a separate change.
- Fixing the `colourTint > 0` check in `VEFluidClientExtension`.
- Tool rules for Sulfur or for the Cinnabar stone family variants.

## Decisions

- **Cinnabar's tool tier through the existing helper.** `VETagDataGenerator.addTags` calls
  `MaterialConstants.setCinnabarTier(Blocks.CINNABAR)` before it writes the tier lists, so the
  cinnabar tier stays defined in one place and vanilla Cinnabar matches the ores. Vanilla Cinnabar
  already requires the correct tool for drops, so tags are enough and no block properties change.
  This deliberately changes vanilla: Cinnabar needs an iron pickaxe instead of any pickaxe. Any
  datapack can undo it. Writing the tag entries for `Blocks.CINNABAR` by hand was the alternative,
  but it would repeat the iron tier.
- **The deposit core is changed in its JSON.** `raw_block_state` in `cinnabar_deposit.json` becomes
  `minecraft:cinnabar`. No Java changes, because the feature already accepts any block state.
- **`c:storage_blocks/raw_cinnabar` points at vanilla Cinnabar.** This keeps
  `crushing/raw_blocks/raw_cinnabar.json` working without editing it, and lets other mods' raw
  cinnabar block recipes see the block that is now the 9:1 storage form. Deleting the tag would
  orphan that crushing recipe.
- **Recipes:** `vanilla_crafting/storage_blocks/raw_cinnabar_block.json` is replaced by
  `cinnabar.json` (3×3 Raw Cinnabar to Cinnabar) and `raw_cinnabar_from_cinnabar.json` (shapeless,
  1 Cinnabar to 9 Raw Cinnabar). `sulfur.json` and `raw_sulfur_from_sulfur.json` follow the same
  pattern. Ingredients are item ids, not tags, so the recipes do not pick up other mods' sulfur or
  cinnabar items.
- **Sulfur Cube drop through `AnimalFatLootModifier`, as the maintainer chose.**
  `VEGlobalLootModifierData` gets a `sulfurCubeModifierProvider` that adds one modifier,
  `raw_sulfur/entities/sulfur_cube`, with two conditions: a `LootTableIdCondition` for
  `minecraft:entities/sulfur_cube`, and a `LootItemEntityPropertyCondition` on `THIS` built from an
  `EntityPredicate.Builder` carrying `CubeMobPredicate.sized(MinMaxBounds.Ints.exactly(1))`. Item
  `VEItems.RAW_SULFUR`, `minimum_count` 1, `maximum_count` 5, because the bound is exclusive and the
  spec wants 1 to 4. There is no random-chance condition. The alternative was NeoForge's
  `add_table` with vanilla's `enchanted_count_increase` (+0 to 1 per level). The maintainer
  preferred the multiplicative Looting scaling the mod already uses.
- **Baby Sulfur Cubes roll their loot table from a `LivingDropsEvent` handler.** `SulfurCube.setSize`
  makes every size 1 cube a baby, and `LivingEntity.shouldDropLoot` returns false for babies, so
  vanilla never rolls `entities/sulfur_cube` for them and the loot modifier never runs. NeoForge has no
  hook on `shouldDropLoot`, but `CommonHooks.onLivingDrops` fires after it on every death.
  `VEGenericListener.onLivingDrops` therefore rolls the cube's own loot table for a dying baby Sulfur
  Cube with the same entity loot context vanilla builds in `dropFromLootTable` (`THIS_ENTITY`,
  `ORIGIN`, `DAMAGE_SOURCE`, the attacking entities, and the last damaging player with their luck when
  recently hit) and adds the results to the drops, honouring `mobDrops`. The modifier stays the single
  data-driven definition of the drop. This deliberately breaks vanilla's "babies drop nothing" rule
  for this one mob; adult size 1 cubes, which grow out of the baby state, still go through vanilla's
  path, so nothing is rolled twice. Alternatives: hard-coding the drop in the handler (not
  data-driven), or moving the drop to size 2 cubes (not what the maintainer asked for).
- **`AnimalFatLootModifier` reads the killer with `getOptionalParameter`.** Sulfur Cubes often die
  with no attacking entity, for example in an explosion or in lava. With `getParameter`, those
  drops throw. A cube that dies in lava drops nothing collectable, because its drops burn like any
  other item. With the optional read, a killerless death counts as Looting 0
  and gets the ×1 multiplier. This also fixes the same throw for animal fat mobs.
- **Sulfur Spike crushing:** new hand-written `crushing/sulfur_spike.json` next to `lapis.json` and
  `quartz.json`, with the same shape as `crushing/raw_ores/sulfur.json`: 200 ticks, 0 experience,
  2 `voluminousenergy:sulfurdust`, then 1 more at `chance` 0.25. That averages 2.25 dust per spike,
  the same as crafting 4 spikes into a Sulfur block and crushing it for 9. A recipe taking 4 spikes
  for exactly 9 dust was considered; the maintainer preferred one spike at a time with the remainder
  as a chance output.
- **Sulfur and Potent Sulfur crushing:** `crushing/sulfur.json` gives 9 Sulfur Dust, matching the 9
  Raw Sulfur a block uncrafts into. `crushing/potent_sulfur.json` gives 81, matching its 9 Sulfur:
  81 is more than a stack, so it is split 41 in the main output and 40 in the RNG slot at `chance` 1,
  which `RNGBasicParser` always inserts. The odd item goes to the main output, as the maintainer asked.
- **Textures are palette swaps, applied pixel by pixel in GIMP.** Every texture this change recolours
  belongs to a template family whose members differ only in palette, so each texture is defined by a
  table of source colour to target colour (see Texture palettes). Each target is chosen from a key
  colour plus the per-index offsets of the closest hue analogue in the same family. The tables are
  applied in GIMP with exact select-by-colour and fill, alpha locked. The bucket is edited by pixel
  position because its liquid shares `#ffffff` with the metal. Sulfuric Acid is recoloured in its
  textures, not tinted: every VE fluid takes its colour from its textures, and the tint path cannot
  represent an opaque colour (see Context).

## Texture palettes

The maintainer chose C4 for cinnabar, S4 for sulfur, and A2 for Sulfuric Acid after comparing every
option applied to the shipped textures.

### Survey

- Dusts: 28 of 30 `item/*dust*.png` are palette swaps of one 7-index template (19, 18, 17, 12, 9, 8, 5
  pixels per index), including Cinnabar Dust and Sulfur Dust.
- Raw Cinnabar and Raw Sulfur have their own shapes but use exactly their dust's 7 colours, so one
  palette covers the raw item and the dust.
- Cinnabar Ore and Deepslate Cinnabar Ore use four of those colours plus two ore-only speck colours
  (`#d54125`, `#eb3b1a`). The stone and deepslate pixels are not touched.
- Sulfuric Acid still (13 greys) shares its template with Compressed Air, Mercury, Oxygen, and WFNA;
  flowing (26 greys) shares with Oxygen. Oxygen is the only tinted member: it tints the body greys
  (up to `#c4c4c4`) and leaves the foam greys neutral. Every pixel has alpha `b4`.
- The Sulfuric Acid bucket shares its layout with Diesel, Biofuel, Naphtha, and Tree Sap: five liquid
  slots (`#bfbfbf`, `#c6c6c6`, `#d4d4d4`, `#e0e0e0`, `#ffffff`) form a dark to light ramp. Naphtha is the
  yellow analogue.
- Analogues: Bauxite Dust for cinnabar (nearest hue and saturation), Sand Dust for sulfur, Oxygen for
  the acid body rule, Naphtha for the bucket ramp.
- Acid reference, the Sulfur Cube bucket and spawn egg: skull bone `#d1af8a`, skull sockets `#644e3e`,
  goo `#d4d490` `#e8e292` `#ecf390`, highlights `#edfabf` `#edffea` `#fffffa`, rim `#a1795f` `#8f654f`.

### Alternatives considered

Each was rendered on the shipped textures beside the current ones and the neighbouring palettes.

- Cinnabar: C1 (key `#964b42`, vanilla Cinnabar's most common colour, + Bauxite Dust offsets) read
  the same as Bauxite Dust, whose key `#a55743` is only 6° of hue away. C2 (key `#aa5553` + Bauxite
  offsets, rose) and C3 (key `#a63c39` + Bauxite offsets, deep red) were viable; C4 was preferred
  because every colour but the highlight is a vanilla Cinnabar or Polished Cinnabar colour.
- Sulfur: S1 (key `#c2b36b`) and S2 (key `#c4b45f`), each + Sand Dust offsets, drifted into Sand
  Dust's khaki. S3 (key `#cfc86a` + Sand offsets) was viable; S4 was preferred for its vanilla colours
  and Polished Sulfur's brown darks, which give a stronger outline.
- Sulfuric Acid: A1 followed Oxygen's rule (tint the body only, foam stays grey) and barely read as
  yellow over stone. A3 (body key `#d2c18d`, between the skull's bone and the goo) and A4 (body key
  `#c9b98a`, the bone yellowed) were more beige; A2 matches the cube's goo.

### All options compared

The full candidate tables as reviewed, kept so the choice can be revisited. Options with a key colour
are that key plus the analogue's per-index offsets (Bauxite Dust for cinnabar, Sand Dust for sulfur);
C4 and S4 take each index from the vanilla textures. Ore speck colours sit 25% and 40% of the way from
index 0 to index 4. Acid bucket slots are the key plus Naphtha's slot offsets.

#### Cinnabar options

| Index | Role | Pixels (raw / dust / ores) | Current | Bauxite Dust (analogue) | C1 | C2 | C3 | C4 |
|---|---|---|---|---|---|---|---|---|
| 0 | base | 32 / 19 / 0 / 0 | `#cc3d24` | `#a55743` | `#964b42` | `#aa5553` | `#a63c39` | `#ab4341` |
| 1 | light | 12 / 18 / 0 / 0 | `#fc7d6a` | `#d48264` | `#c57663` | `#d98074` | `#d5675a` | `#c66f6f` |
| 2 | darkest | 24 / 17 / 14 / 14 | `#681f12` | `#542a1f` | `#451e1e` | `#59282f` | `#550f15` | `#641d21` |
| 3 | dark | 31 / 12 / 2 / 2 | `#912c1a` | `#7f3f2f` | `#70332e` | `#843d3f` | `#802425` | `#852b2a` |
| 4 | mid-light | 18 / 9 / 1 / 0 | `#f24d30` | `#d4664d` | `#c55a4c` | `#d9645d` | `#d54b43` | `#b84f50` |
| 5 | highlight | 9 / 8 / 11 / 11 | `#ecbcb4` | `#d69e81` | `#c79280` | `#db9c91` | `#d78377` | `#dca3a0` |
| 6 | mid-dark | 34 / 5 / 0 / 0 | `#a3311d` | `#874534` | `#783933` | `#8c4344` | `#882a2a` | `#9c3630` |
| ore 1 | speck between 0 and 4 | 0 / 0 / 12 / 13 | `#d54125` | | `#a24f44` | `#b65956` | `#b2403c` | `#ae4645` |
| ore 2 | speck between 0 and 4 | 0 / 0 / 3 / 3 | `#eb3b1a` | | `#a95146` | `#bd5b57` | `#b9423d` | `#b04847` |

| Neighbour | base | light | darkest | dark | mid-light | highlight | mid-dark |
|---|---|---|---|---|---|---|---|
| Netherrack Dust | `#572121` | `#723232` | `#411616` | `#501b1b` | `#652828` | `#854242` | `#511515` |
| Copper Dust | `#c95e35` | `#fc9982` | `#6d3421` | `#883f26` | `#e97a52` | `#fec3b6` | `#a54926` |

Vanilla Cinnabar, by pixel count: `#964b42` `#aa5553` `#85413a` `#ad6764` `#925d59` `#714c4e` `#6e3e3c` `#7f5454` `#c66f6f` `#825858` `#8f686b`

Vanilla Polished Cinnabar, by pixel count: `#9c3630` `#a63c39` `#8f302c` `#ab4341` `#8f3e38` `#9a413e` `#641d21` `#b84f50` `#c55c5d` `#852b2a` `#853834` `#702427` `#a04743`

#### Sulfur options

| Index | Role | Pixels (raw / dust) | Current | Sand Dust (analogue) | S1 | S2 | S3 | S4 |
|---|---|---|---|---|---|---|---|---|
| 0 | base | 32 / 19 | `#f0ca00` | `#d5c496` | `#c2b36b` | `#c4b45f` | `#cfc86a` | `#c4b45f` |
| 1 | light | 18 / 18 | `#ffe78e` | `#e3dbb0` | `#d0ca85` | `#d2cb79` | `#dddf84` | `#ddde72` |
| 2 | darkest | 41 / 17 | `#ad9326` | `#a5936f` | `#928244` | `#948338` | `#9f9743` | `#9f7f4f` |
| 3 | dark | 3 / 12 | `#c8a316` | `#d1ba8a` | `#bea95f` | `#c0aa53` | `#cbbe5e` | `#ad9451` |
| 4 | mid-light | 17 / 9 | `#f2d84e` | `#dacfa3` | `#c7be78` | `#c9bf6c` | `#d4d377` | `#cfc86a` |
| 5 | highlight | 5 / 8 | `#ffefb5` | `#edebcb` | `#dadaa0` | `#dcdb94` | `#e7ef9f` | `#ece9a6` |
| 6 | mid-dark | 9 / 5 | `#beab1a` | `#bfaa80` | `#ac9955` | `#ae9a49` | `#b9ae54` | `#bba059` |

| Neighbour | base | light | darkest | dark | mid-light | highlight | mid-dark |
|---|---|---|---|---|---|---|---|
| End Stone Dust | `#dee6a4` | `#f6fabd` | `#c5be8b` | `#cdc68b` | `#eef6b4` | `#f6fad8` | `#d5da94` |
| Gold Dust | `#e9b115` | `#fdf55f` | `#752802` | `#b26411` | `#fad64a` | `#fffde0` | `#dc9613` |

Vanilla Sulfur, by pixel count: `#c2b36b` `#b5a065` `#c2bf5f` `#cfc86a` `#a39455` `#e0d675`

Vanilla Polished Sulfur, by pixel count: `#bba059` `#c4b45f` `#baab5b` `#c6c15c` `#ad9451` `#936f4c` `#ddde72` `#cfce68` `#9f7f4f`

#### Sulfuric Acid options

| Grey | Pixels (still / flowing) | Role | Oxygen | A1 | A2 | A3 | A4 |
|---|---|---|---|---|---|---|---|
| `#9d9d9d` | 0 / 2 | body | `#8bafaf` | `#d5d584` | `#cccc88` | `#cab985` | `#c1b182` |
| `#9e9e9e` | 0 / 1058 | body | `#8cb0b0` | `#d6d685` | `#cdcd89` | `#cbba86` | `#c2b283` |
| `#a1a1a1` | 0 / 9122 | body | `#90b2b2` | `#d6d689` | `#d0d08c` | `#cebd89` | `#c5b586` |
| `#a2a2a2` | 0 / 6 | body | `#92b2b2` | `#d4d48c` | `#d1d18d` | `#cfbe8a` | `#c6b687` |
| `#a3a3a3` | 0 / 17 | body | `#93b3b3` | `#d5d58d` | `#d2d28e` | `#d0bf8b` | `#c7b788` |
| `#a4a4a4` | 0 / 6791 | body | `#95b3b3` | `#d3d38f` | `#d3d38f` | `#d1c08c` | `#c8b889` |
| `#a5a5a5` | 3299 / 8 | body | `#96b4b4` | `#d4d490` | `#d4d490` | `#d2c18d` | `#c9b98a` |
| `#a6a6a6` | 0 / 4340 | body | `#97b5b5` | `#d5d591` | `#d5d591` | `#d3c28e` | `#caba8b` |
| `#a9a9a9` | 0 / 3309 | body | `#9bb7b7` | `#d5d595` | `#d8d894` | `#d6c591` | `#cdbd8e` |
| `#aaaaaa` | 0 / 3 | body | `#9db7b7` | `#d3d398` | `#d9d995` | `#d7c692` | `#cebe8f` |
| `#ababab` | 0 / 6 | body | `#9eb8b8` | `#d4d499` | `#dada96` | `#d8c793` | `#cfbf90` |
| `#acacac` | 0 / 2298 | body | `#a0b8b8` | `#d2d29b` | `#dbdb97` | `#d9c894` | `#d0c091` |
| `#aeaeae` | 3119 / 1286 | body | `#a2baba` | `#d4d49d` | `#dddd99` | `#dbca96` | `#d2c293` |
| `#b0b0b0` | 0 / 2 | body | `#a5bbbb` | `#d2d2a1` | `#dfdf9b` | `#ddcc98` | `#d4c495` |
| `#b1b1b1` | 0 / 1248 | body | `#a6bcbc` | `#d3d3a2` | `#e0e09c` | `#decd99` | `#d5c596` |
| `#b5b5b5` | 0 / 1 | body | `#acbebe` | `#d1d1a8` | `#e4e4a0` | `#e2d19d` | `#d9c99a` |
| `#b8b8b8` | 26 / 1037 | body | `#b0c0c0` | `#d1d1ad` | `#e7e7a3` | `#e5d4a0` | `#dccc9d` |
| `#bebebe` | 0 / 1 | body | `#b9c3c3` | `#ceceb7` | `#ededa9` | `#ebdaa6` | `#e2d2a3` |
| `#c2c2c2` | 1106 / 736 | body | `#bec6c6` | `#cfcfbc` | `#f1f1ad` | `#efdeaa` | `#e6d6a7` |
| `#c4c4c4` | 0 / 1 | body | `#c1c7c7` | `#cdcdc0` | `#f3f3af` | `#f1e0ac` | `#e8d8a9` |
| `#cecece` | 42 / 348 | foam | `#cecece` | `#cecece` | `#e8e292` | `#e8e292` | `#e8e292` |
| `#cfcfcf` | 22 / 0 | foam | `#cfcfcf` | `#cfcfcf` | `#e8e292` | `#e8e292` | `#e8e292` |
| `#d2d2d2` | 96 / 0 | foam | `#d2d2d2` | `#d2d2d2` | `#ecf390` | `#ecf390` | `#ecf390` |
| `#d3d3d3` | 257 / 0 | foam | `#d3d3d3` | `#d3d3d3` | `#ecf390` | `#ecf390` | `#ecf390` |
| `#d5d5d5` | 7 / 674 | foam | `#d5d5d5` | `#d5d5d5` | `#edfabf` | `#edfabf` | `#edfabf` |
| `#d6d6d6` | 4 / 307 | foam | `#d6d6d6` | `#d6d6d6` | `#edfabf` | `#edfabf` | `#edfabf` |
| `#d8d8d8` | 5 / 103 | foam | `#d8d8d8` | `#d8d8d8` | `#edfabf` | `#edfabf` | `#edfabf` |
| `#f9f9f9` | 2 / 48 | foam | `#f9f9f9` | `#f9f9f9` | `#edffea` | `#edffea` | `#edffea` |
| `#ffffff` | 207 / 16 | foam | `#ffffff` | `#ffffff` | `#fffffa` | `#fffffa` | `#fffffa` |

All still and flowing pixels keep alpha `b4`.

| Bucket slot | Naphtha (analogue) | A1 | A2 | A3 | A4 |
|---|---|---|---|---|---|
| `#bfbfbf` | `#a5a564` | `#d4d490` | `#d4d490` | `#d2c18d` | `#c9b98a` |
| `#c6c6c6` | `#aeae6d` | `#dddd99` | `#dddd99` | `#dbca96` | `#d2c293` |
| `#d4d4d4` | `#c1c17c` | `#f0f0a8` | `#f0f0a8` | `#eedda5` | `#e5d5a2` |
| `#e0e0e0` | `#c2c283` | `#f1f1af` | `#f1f1af` | `#efdeac` | `#e6d6a9` |
| `#ffffff` | `#d6d689` | `#ffffb5` | `#ffffb5` | `#fff2b2` | `#faeaaf` |

### Chosen palettes

#### Cinnabar: C4

Applied to Raw Cinnabar, Cinnabar Dust, Cinnabar Ore, and Deepslate Cinnabar Ore.

| Index | Role | Pixels (raw / dust / ore / deepslate ore) | Current | C4 | Taken from |
|---|---|---|---|---|---|
| 0 | base | 32 / 19 / 0 / 0 | `#cc3d24` | `#ab4341` | Polished Cinnabar |
| 1 | light | 12 / 18 / 0 / 0 | `#fc7d6a` | `#c66f6f` | Cinnabar |
| 2 | darkest | 24 / 17 / 14 / 14 | `#681f12` | `#641d21` | Polished Cinnabar |
| 3 | dark | 31 / 12 / 2 / 2 | `#912c1a` | `#852b2a` | Polished Cinnabar |
| 4 | mid-light | 18 / 9 / 1 / 0 | `#f24d30` | `#b84f50` | Polished Cinnabar |
| 5 | highlight | 9 / 8 / 11 / 11 | `#ecbcb4` | `#dca3a0` | new, lighter than Cinnabar's `#c66f6f` |
| 6 | mid-dark | 34 / 5 / 0 / 0 | `#a3311d` | `#9c3630` | Polished Cinnabar |
| ore 1 | ore speck | 0 / 0 / 12 / 13 | `#d54125` | `#ae4645` | 25% of the way from index 0 to index 4 |
| ore 2 | ore speck | 0 / 0 / 3 / 3 | `#eb3b1a` | `#b04847` | 40% of the way from index 0 to index 4 |

#### Sulfur: S4

Applied to Raw Sulfur and Sulfur Dust.

| Index | Role | Pixels (raw / dust) | Current | S4 | Taken from |
|---|---|---|---|---|---|
| 0 | base | 32 / 19 | `#f0ca00` | `#c4b45f` | Polished Sulfur |
| 1 | light | 18 / 18 | `#ffe78e` | `#ddde72` | Polished Sulfur |
| 2 | darkest | 41 / 17 | `#ad9326` | `#9f7f4f` | Polished Sulfur |
| 3 | dark | 3 / 12 | `#c8a316` | `#ad9451` | Polished Sulfur |
| 4 | mid-light | 17 / 9 | `#f2d84e` | `#cfc86a` | Sulfur |
| 5 | highlight | 5 / 8 | `#ffefb5` | `#ece9a6` | new, lighter than Polished Sulfur's `#ddde72` |
| 6 | mid-dark | 9 / 5 | `#beab1a` | `#bba059` | Polished Sulfur |

#### Sulfuric Acid: A2

Body greys keep their step above key `#d4d490` (the Sulfur Cube's goo); foam greys take the cube's
highlights. Applied to the still and flowing textures; every pixel keeps alpha `b4`.

| Current | Pixels (still / flowing) | Role | A2 |
|---|---|---|---|
| `#9d9d9d` | 0 / 2 | body | `#cccc88` |
| `#9e9e9e` | 0 / 1058 | body | `#cdcd89` |
| `#a1a1a1` | 0 / 9122 | body | `#d0d08c` |
| `#a2a2a2` | 0 / 6 | body | `#d1d18d` |
| `#a3a3a3` | 0 / 17 | body | `#d2d28e` |
| `#a4a4a4` | 0 / 6791 | body | `#d3d38f` |
| `#a5a5a5` | 3299 / 8 | body | `#d4d490` |
| `#a6a6a6` | 0 / 4340 | body | `#d5d591` |
| `#a9a9a9` | 0 / 3309 | body | `#d8d894` |
| `#aaaaaa` | 0 / 3 | body | `#d9d995` |
| `#ababab` | 0 / 6 | body | `#dada96` |
| `#acacac` | 0 / 2298 | body | `#dbdb97` |
| `#aeaeae` | 3119 / 1286 | body | `#dddd99` |
| `#b0b0b0` | 0 / 2 | body | `#dfdf9b` |
| `#b1b1b1` | 0 / 1248 | body | `#e0e09c` |
| `#b5b5b5` | 0 / 1 | body | `#e4e4a0` |
| `#b8b8b8` | 26 / 1037 | body | `#e7e7a3` |
| `#bebebe` | 0 / 1 | body | `#ededa9` |
| `#c2c2c2` | 1106 / 736 | body | `#f1f1ad` |
| `#c4c4c4` | 0 / 1 | body | `#f3f3af` |
| `#cecece` | 42 / 348 | foam | `#e8e292` |
| `#cfcfcf` | 22 / 0 | foam | `#e8e292` |
| `#d2d2d2` | 96 / 0 | foam | `#ecf390` |
| `#d3d3d3` | 257 / 0 | foam | `#ecf390` |
| `#d5d5d5` | 7 / 674 | foam | `#edfabf` |
| `#d6d6d6` | 4 / 307 | foam | `#edfabf` |
| `#d8d8d8` | 5 / 103 | foam | `#edfabf` |
| `#f9f9f9` | 2 / 48 | foam | `#edffea` |
| `#ffffff` | 207 / 16 | foam | `#fffffa` |

Bucket liquid: the five slot colours, edited by pixel position because the metal also uses `#ffffff`.
Each slot is the key plus Naphtha's offset for that slot.

| Current slot | Naphtha offset | A2 |
|---|---|---|
| `#bfbfbf` | +0, +0, +0 | `#d4d490` |
| `#c6c6c6` | +9, +9, +9 | `#dddd99` |
| `#d4d4d4` | +28, +28, +24 | `#f0f0a8` |
| `#e0e0e0` | +29, +29, +31 | `#f1f1af` |
| `#ffffff` | +49, +49, +37 | `#ffffb5` |

## Risks / Trade-offs

- [Sulfur Caves generate a lot of Cinnabar, and each block is 9 Raw Cinnabar or 45 Cinnabar Dust
  when crushed] → The maintainer accepted this by asking for two-way crafting. The iron-pickaxe
  requirement is the only gate. Revisit the yield if mercury or redstone from cinnabar becomes
  trivial.
- [The same applies to Sulfur: 9 Raw Sulfur per block, and Sulfur Spikes add more] → Accepted. The
  Sulfur Cube drop and spike crushing are small next to it.
- [Builders lose Cinnabar they break with stone tools] → Expected, as a stated side effect of
  matching the ore tier. The polished and brick variants are unaffected.
- [Existing 26.2 test worlds lose their Raw Cinnabar Blocks] → Accepted. The maintainer expects
  fresh worlds.
- [A palette that reads well enlarged can still look off at game scale] → Review every recoloured PNG
  in game, and adjust the table and reapply in GIMP rather than painting over the result.

## Migration Plan

None. Worlds that have `voluminousenergy:raw_cinnabar_block` lose those blocks and items when they
load.
