## Context

Oil worldgen is driven by three feature types registered in `VEFeatures`:

- `VELakesFeature` builds a 16×8×16 mask from 4–7 random ellipsoids, rejects the spot if the mask's
  border has liquid in the upper half or a non-solid block in the lower half, fills the lower half
  with the fluid and the upper half with cave air, converts exposed dirt to grass or mycelium, and
  places `Blocks.STONE` on solid border positions (always in the lower half, 50% in the upper half).
- `SurfaceMattersLakesFeature` wraps it twice: `ve_bsc_surface_lake_feature` (`isForSurface = true`)
  places only where the origin sees the sky, and `ve_bsc_underground_lakes_feature`
  (`isForSurface = false`) ignores the origin's y and re-rolls it as `rand.nextInt(80) - 32`. So
  there is one lake algorithm with two registered instances.
- `VEOreDepositFeature` is a copy of the same mask code that fills the mask with a raw block and puts
  an ore block on the border.

Both oil configured features are placed by `VEAndedMultiBiomeModifier`, which wraps them in
`HeightRangePlacement` (uniform or triangular), `CountPlacement`, `RarityFilter`, and
`InSquarePlacement`. The underground lake biome modifier uses the anchors -64 to 320, because the
feature overrides the height.

`GeyserFeature` places a 136-block column (radius up to 6) mirrored downward, then a double-cone
bottom reservoir around layer `P`, using the loop `for (int y = pos.getY(); y > 0; --y)` with
offsets `+y` and `-y`. Offset 0 is never visited, so layer `P`, where the cone is widest (radius
20), only gets the column's radius-6 core. That leaves the uncut ring.

The host-dependent variant rule already exists in data for `minecraft:ore` features
(`saltpeter_ore_blob.json` and `galena_ore_blob.json` use a list of `{target, state}` pairs). The
sand tags `c:ore_bearing_ground/sand/colorless_sand` and `.../red_sand` are hand-written in
`src/main/resources/data/c/tags/block/`.

## Goals / Non-Goals

**Goals:**
- Choose the bituminous variant from data, using the same `{target, state}` JSON shape the mod's ore
  features already use.
- Keep surface lake behaviour identical apart from the rim block.
- Fix the geyser reservoir with the smallest change that leaves its shape otherwise the same.

**Non-Goals:**
- Changing `VEOreDepositFeature` or any of the eight ore deposits.
- The geyser's other pre-existing quirks: the reservoir size depends on absolute world y, and its
  radius of 20 can exceed the about-16-block safe write radius of a feature, so writes into far
  chunks are dropped and the outline is clipped. Each would change the geyser's shape and needs its
  own proposal.
- Textures. The maintainer supplies
  `textures/block/{bituminous_gravel,bituminous_sand,bituminous_red_sand}.png` and
  `textures/item/bitumen.png`.
- Uses for the Bitumen item (recipes, fuel value). It is a plain material item in this change.

## Decisions

### A new fluid deposit feature instead of reusing the ore deposit feature
Add `VEFluidDepositFeature`, registered as `ve_fluid_deposit_feature` in `VEFeatures`, with the
configuration `{ "fluidstate": FluidState, "perimeter_targets": [TargetBlockState] }`. It fills
every mask position with the fluid's source block and places the shell from `perimeter_targets`.
`underground_oil_lake.json` switches its `type` to this feature and keeps its id.

Alternative: give `VEOreDepositFeature` a fluid core. Rejected. Its core is a `BlockStateProvider`,
its border is skipped at random in the upper half, and its leak check allows air in the upper half.
Bending those rules for oil would change all eight ore deposits.

### Variant selection reuses vanilla `OreConfiguration.TargetBlockState`
`perimeter_targets` is a list decoded with `OreConfiguration.TargetBlockState.CODEC`, the same codec
behind `minecraft:ore` targets. For each shell position, the first target whose `RuleTest` matches
the existing block wins. The oil JSON lists colorless sand (`tag_match`), red sand (`tag_match`),
then `minecraft:always_true` mapped to `voluminousenergy:bituminous_gravel` as the default. If a datapack
leaves out a catch-all and nothing matches, the host block stays in place. It is solid, so the seal
holds. This makes the rule datapack-editable without a new codec.

### Shared mask helper
The ellipsoid mask and the "is this a border position" test move into a small helper in
`world/feature/` (for example `LakeMask`, with `generate(RandomSource)`, `isInside(x, y, z)`, and
`isBorder(x, y, z)`). `VELakesFeature` and `VEFluidDepositFeature` both use it instead of a third
copy of the mask code. The moved code is rewritten with descriptive names, per AGENTS.md.
`VEOreDepositFeature` keeps its own copy, since it is out of scope.

### Surface lakes gain an optional `perimeter_targets`
`VELakesFeature.Configuration` changes from a single-field `xmap` codec to a `RecordCodecBuilder`
that keeps `fluidstate` and adds `perimeter_targets` as an `optionalFieldOf`. The default is a
single `always_true → minecraft:stone` target, so `ve_bsc_lake_feature` and any JSON without the
field behave exactly as before. Rim placement keeps its existing conditions (solid, always in the
lower half, 50% in the upper half). Only the state now comes from the targets.
`surface_oil_lake.json` gets the same three bituminous targets as the deposit.

### Underground height moves from code to the biome modifier
`underground_oil_lakes_overworld.json` changes its anchors from -64/320 to -32/47, with uniform
placement. That gives the same distribution as `rand.nextInt(80) - 32`, and the new feature uses
its origin as given. `SurfaceMattersLakesFeature` loses `isForSurface` and its underground branch,
and `VE_BSC_LAKE_UNDERGROUND_FEATURE` is removed from `VEFeatures`. Feature types are not stored in
chunk data, so removing the type does not affect saves.

Alternative: keep the y re-roll inside the new feature. Rejected, because it hides the placement
height from datapacks and would make the biome modifier's anchors meaningless.

### Deposit seal and protection checks
Before writing anything, the deposit walks the mask. It aborts if any mask or border position holds
a block in `BlockTags.FEATURES_CANNOT_REPLACE`, which closes the TODO in `VEFeatures` for this
feature. It also aborts if any border position is non-solid or has a non-empty fluid state. Carvers
and aquifers run before the `lakes` step, so this rejects deposits that would open into a cave or
touch water. All 8 layers of the mask become oil, unlike the lake's air top half. The deposit
schedules no fluid ticks: every oil block is a source block enclosed by solid blocks, so there is
nothing to flow.

Deviation from vanilla: vanilla lakes never fill their upper half. The sealed shell is what lets
the deposit fill it without spilling.

### Bituminous blocks and the Bitumen item
`BituminousBlock extends VEBlock` lives in `blocks/blocks/ores/`, next to `SaltpeterOre`, and takes
its registry name as a constructor argument. It is one class for all three variants. It is a plain
block, not `FallingBlock` or `ColoredFallingBlock` like `SaltpeterOre`, so it never falls. Its
properties are `SoundType.GRAVEL`, `strength(0.6F)` like gravel, `NoteBlockInstrument.SNARE`,
`MapColor.COLOR_BLACK`, and `requiresCorrectToolForDrops()`. Like `SaltpeterOre`, it calls
`VETagDataGenerator.setRequiresShovel(this)` and `setRequiresWoodAndBlacklistLowerTiers(this)`, so it
drops only for a shovel. Unlike saltpeter ore it gives no experience, since nothing asks for it. The
three blocks are registered with `registerWithBlockItemSupport` in `VEBlocks` under `// Ores`. The
item is `Bitumen extends VEItem` in `items/`, following `SaltpeterChunk`, registered as `bitumen` in
`VEItems`. Both registries feed the creative tab.

Loot tables copy the shape of `saltpeterore.json`: an `alternatives` entry that drops the block
under Silk Touch, otherwise `voluminousenergy:bitumen` with `set_count` uniform 3 to 7, Fortune
`ore_drops`, and `explosion_decay`. Blockstates, `cube_all` block models, block item models, the
`item/handheld` Bitumen model (matching `saltpeterchunk` and `rosin`), loot tables, and lang entries are hand-written in
`src/main/resources`, as saltpeter's are. The `mineable/shovel` and `needs_wood_tool` tags are
datagen output.

### Tags mirror saltpeter ore
The `c:` tags for saltpeter are hand-written under `src/main/resources/data/c/tags/{block,item}/`,
and the bituminous tags go there too, with each block tag mirrored as an item tag:

- New `c:ores/bitumen` (all three), added to `c:ores` next to `#c:ores/saltpeter`.
- New item tag `c:bitumen` with only the Bitumen item, matching `c:rosin` and `c:tallow`.

Saltpeter ore is also in `c:ores_in_ground/colorless_sand` and `red_sand`, but the bituminous
blocks are left out of every `c:ores_in_ground` tag. They are a shell placed around oil, not an ore
blob generated inside a host block. They are also not in `c:ore_bearing_ground`, which lists the
hosts that ores generate in, not the ores themselves.

### Geyser reservoir fix
Change the bottom loop bound at `GeyserFeature.java:114` from `y > 0` to `y >= 0`. At `y == 0`,
`fy == 20` fills layer `P` to the full radius. The existing `y != 0` guard on the mirrored write
stops layer `P` being written twice.

Alternative: loop over a relative offset `0..height1`. Rejected for this change. That also decouples
the reservoir from absolute world y, but it changes the reservoir's depth and size, which the spec
keeps as they are.

## Risks / Trade-offs

- [The stricter seal check makes underground oil rarer than the old lakes, especially in cave-dense
  terrain] → The in-game check compares deposit density in fresh chunks. If deposits are too rare,
  lower `rarity` in `underground_oil_lakes_overworld.json`, which needs no code change.
- [Missing textures render the purple and black placeholder until the maintainer adds them] → The
  models reference the final texture paths, so dropping in the PNGs is enough.
- [A datapack using `voluminousenergy:ve_bsc_underground_lakes_feature` stops loading] → The proposal
  flags this as breaking. No known datapack uses it.
- [Bituminous Sand only appears where underground sand exists, mostly desert and beach subsurfaces in
  the y -32 to 47 range, so it is rare in deposits and common on desert surface lakes] → Accepted.
  It follows the maintainer's host rule.
- [A deposit shell is hundreds of blocks at 3 to 7 Bitumen each, so one deposit yields a lot] →
  Accepted as the maintainer's chosen rate. The count lives in the loot tables, so it can be tuned
  without code.

## Migration Plan

None. Already generated chunks keep their old lakes and geysers. New chunks use the new generation.
Rollback is reverting the branch. The new block and item ids would then be missing from saves that
already contain them, and vanilla drops missing blocks and items on load.
