## Why

A multitool holding a trimmer bit shows its default model, not the trimmer model, when the player
targets Tall Dry Grass, though it harvests the block. Leaves switch to the trimmer model as
expected. The multitool picks the bit to display from the `voluminousenergy:mineable/trimmer` block
tag, and that tag predates the foliage added in recent Minecraft versions, so those plants never
select the trimmer bit.

## What Changes

- Add the vanilla 26.1 plants whose loot tables require shears and that the trimmer tag does not
  already cover: `minecraft:tall_dry_grass`, `minecraft:short_dry_grass`, `minecraft:bush`,
  `minecraft:large_fern`, `minecraft:pale_hanging_moss`, and `minecraft:small_dripleaf`.
- Add the other foliage introduced after 1.21.1 that matches what the tag already holds:
  `minecraft:firefly_bush` (a bush, like dead bush) and `minecraft:leaf_litter` (ground foliage,
  like leaves).
- Leave out the other new vegetation, matching the tag's existing scope: flowers (open and closed
  eyeblossom, golden dandelion, wildflowers, cactus flower), pale moss block and carpet (vanilla
  hoe blocks, like regular moss), and pale oak sapling. Pale oak leaves are already covered by
  `#minecraft:leaves`.
- Targeting or breaking any added block with a trimmer-equipped multitool selects the trimmer bit,
  so the held model shows the trimmer and mining speed uses the trimmer tier.
- No save, data component, registry id, or config change. The tag is hand-written data in
  `src/main/resources`; no datagen output changes.

## Capabilities

### New Capabilities

- `items/multitool`: Selection of the active multitool bit for a targeted block, starting with the
  trimmer bit's coverage of shears-harvested plants and foliage.

### Modified Capabilities

None.

## Impact

- `src/main/resources/data/voluminousenergy/tags/block/mineable/trimmer.json`: eight entries
  appended.
- Datapacks that extend `voluminousenergy:mineable/trimmer` are unaffected (`replace` stays false).
- `1.21.1-dev` received `minecraft:large_fern` and `minecraft:small_dripleaf` in 22650a66; the
  other six blocks do not exist in 1.21.1. No further port is needed.
