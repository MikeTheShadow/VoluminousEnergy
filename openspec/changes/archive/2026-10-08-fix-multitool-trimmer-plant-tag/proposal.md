## Why

A multitool holding a trimmer bit shows its default model, not the trimmer model, when the player
targets Large Fern or Small Dripleaf. The multitool picks the bit to display from the
`voluminousenergy:mineable/trimmer` block tag, and these two plants, whose loot tables only yield
the block when broken with shears, were never added to it.

## What Changes

- Add `minecraft:large_fern` and `minecraft:small_dripleaf` to the trimmer tag.
- Targeting or breaking either plant with a trimmer-equipped multitool selects the trimmer bit, so
  the held model shows the trimmer and mining speed uses the trimmer tier.
- No save, data component, registry id, or config change. The tag is hand-written data in
  `src/main/resources`; no datagen output changes.

## Capabilities

### New Capabilities

- `items/multitool`: Selection of the active multitool bit for a targeted block, starting with the
  trimmer bit's coverage of shears-harvested plants.

### Modified Capabilities

None.

## Impact

- `src/main/resources/data/voluminousenergy/tags/block/mineable/trimmer.json`: two entries appended.
- Datapacks that extend `voluminousenergy:mineable/trimmer` are unaffected (`replace` stays false).
- `26.1-dev` carries its own change for the same tag that also covers the plants added after 1.21.1.
