## Why

Bitumen and the three bituminous blocks drop from oil lakes and deposits but have no recipes, so
players pick up an item with no use and Silk Touched blocks that cannot be broken down. Bitumen is
the heavy fraction of petroleum, so it should feed back into the crude oil chain.

## What Changes

- The Centrifugal Separator turns 4 Bitumen and an empty bucket into a Crude Oil bucket, 2 Sand Dust,
  and a 25% chance of 1 Carbon Dust.
- The Crusher turns Bituminous Sand and Bituminous Red Sand into 5 Bitumen and 3 Sand Dust.
- The Crusher turns Bituminous Gravel into 5 Bitumen and a 25% chance of 1 Gravel.
- JEI shows the new recipes through the existing Centrifugal Separator and Crusher categories.

No registry ids, NBT, data components, attachments, or config options are added or changed, so
existing saves are unaffected. The recipes are ordinary datapack JSON that modpacks can override.

Assumption recorded for review: the request named sand dust and gravel as the crushing outputs.
The Crusher's first output is always guaranteed, and a block crushed without Bitumen would lose the
Bitumen it is made of, so Bitumen is the primary output and sand dust or gravel the secondary.

## Capabilities

### New Capabilities
- `recipes/bitumen-processing`: the machine recipes that consume Bitumen and the bituminous blocks.

### Modified Capabilities

None. The blocks, the item, their drops, and their tags are specified by the in-progress
`rework-overworld-oil-worldgen` change under `blocks/bitumen`; this change only adds recipes that use
them.

## Impact

- Four new recipe files under `src/main/resources/data/voluminousenergy/recipe/`: one in
  `centrifugal_separation/` and three in `crushing/ores/`.
- No Java, datagen, tag, or loot changes.
- Depends on `rework-overworld-oil-worldgen` (which registers Bitumen and the bituminous blocks) and
  ships on the same branch, `1.21.1-petroleum-worldgen`.
- Follow-up: port to `26.1-dev` and `26.2-dev` together with the bituminous blocks when that
  worldgen work is ported.
