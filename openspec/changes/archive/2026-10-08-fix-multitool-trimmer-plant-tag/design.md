## Context

`VEClientSideListener.onPlayerHighlightBlock` calls `Multitool.setToolState` for the targeted block.
That writes the `voluminousenergy:tool_type` and `voluminousenergy:tool_tier` data components from
`Multitool.getBestBitForBlock`, and `assets/voluminousenergy/items/multitool.json` selects the
model from those components. `getBestBitForBlock` returns the trimmer bit as soon as the block is in
`VEMultitoolBitData.MINEABLE_WITH_TRIMMER` (`voluminousenergy:mineable/trimmer`). Otherwise it
falls back to the bit with the highest `Tool` mining speed that is also correct for drops. The
trimmer's `Tool` has no rule for the dry grass blocks, so no bit is selected for them and both
components are set to 0.

Comparing the vanilla 26.1.2 block loot tables that reference `minecraft:shears` against the tag
leaves six blocks uncovered: `tall_dry_grass`, `short_dry_grass`, `bush`, `large_fern`,
`pale_hanging_moss`, and `small_dripleaf`. Leaves are covered through `#minecraft:leaves`, which is
why they already render correctly.

Diffing the 1.21.1 and 26.1.2 block lists gives the rest of the new vegetation: firefly bush, leaf
litter, wildflowers, cactus flower, open and closed eyeblossom, golden dandelion, pale moss block and
carpet, pale oak leaves, and pale oak sapling. Vanilla tags pale moss block and carpet as
`minecraft:mineable/hoe`; the flowers sit in `minecraft:flowers` or `minecraft:small_flowers`.

## Goals / Non-Goals

**Goals:**
- The six missing shears-only plants, firefly bush, and leaf litter select the trimmer bit.

**Non-Goals:**
- Changing the bit selection logic in `Multitool`, or how loot is decided.
- Pruning entries that are in the tag but do not need shears (wool, kelp, nether wart, and others).
  They are existing behaviour that players may rely on for speed.
- The stray `Setting as null!` info log in `onPlayerHighlightBlock`.

## Decisions

- **Extend the tag rather than the selection logic.** An alternative is to select the trimmer
  whenever the block's loot needs shears or the bit can perform `ItemAbilities.SHEARS_DIG`, but
  `SHEARS_DIG` is not tied to a block and loot tables are server data the client cannot query while
  highlighting. The tag is already the single source of truth for this choice and is extensible by
  datapacks.
- **List blocks by id, not through a vanilla tag.** Vanilla 26.1 has no tag for shears-harvested
  plants (`#minecraft:replaceable_by_trees` and similar include blocks that do not need shears).
- **Add new foliage only where the tag already has a counterpart.** Firefly bush matches dead bush
  and leaf litter matches leaves. Flowers are left out because the tag has never held any (not even
  dandelion or pink petals), pale moss block and carpet because regular moss is not in it either,
  and saplings for the same reason. Adding a whole category is a separate decision for the
  maintainer.
- **Append at the bottom of `values`.** New tag entries go at the end of the list, per AGENTS.md,
  even where a neighbour such as `minecraft:tall_grass` is related.

## Risks / Trade-offs

- [A future Minecraft version adds another shears-only plant] → The tag needs a manual update
  again. The comparison against vanilla loot tables in Context is repeatable on each port.
- [Mining speed for the added blocks changes] → They break instantly in vanilla, so selecting the
  trimmer tier has no visible effect on break time. None of them is in a pickaxe, axe, or shovel
  tag, so no other bit loses a block to the trimmer.

## Open Questions

- The loot tables for these plants match on the `minecraft:shears` item, yet the maintainer
  observed Tall Dry Grass dropping itself when broken with the multitool. The mechanism was not
  traced. It does not affect this change, which only concerns bit selection.
