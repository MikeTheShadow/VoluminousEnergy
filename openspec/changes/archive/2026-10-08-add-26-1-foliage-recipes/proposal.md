## Why

Minecraft added new plants after 1.21.1 (dry grass, bushes, leaf litter, pale hanging moss, new
flowers, and the pale oak sapling), and none of them has a Crusher or Hydroponic Incubator recipe
on 26.1. Players can turn the older grasses into Shredded Biomass and duplicate the older flowers,
shrubs, and saplings, but not the new ones.

## What Changes

- **Crusher** turns six new plants into Shredded Biomass, following the existing grass and shrub
  recipes:

  | Input | Main output | Chance output |
  |---|---|---|
  | Short Dry Grass | 1 Shredded Biomass | 1 Stick (25%) |
  | Tall Dry Grass | 2 Shredded Biomass | 1 Stick (50%) |
  | Bush | 2 Shredded Biomass | 2 Stick (50%) |
  | Firefly Bush | 2 Shredded Biomass | 1 Stick (50%) |
  | Leaf Litter | 1 Shredded Biomass | none |
  | Pale Hanging Moss | 2 Shredded Biomass | none |

- **Hydroponic Incubator** duplicates ten new plants, each consuming 250 mB of Ammonium Nitrate
  Solution over 200 ticks and keeping the input, like every existing incubator recipe:

  | Input | Main output | Chance outputs |
  |---|---|---|
  | Short Dry Grass | 9 Short Dry Grass | 4 Tall Dry Grass (33%), 3 Dead Bush (15%), 2 Cactus Flower (5%) |
  | Bush | 9 Bush | 4 Bush (66%), 1 Firefly Bush (5%) |
  | Firefly Bush | 6 Firefly Bush | 4 Firefly Bush (66%), 2 Bush (20%) |
  | Pale Hanging Moss | 9 Pale Hanging Moss | 9 Pale Hanging Moss (33%) |
  | Open Eyeblossom | 9 Open Eyeblossom | 9 Open Eyeblossom (33%) |
  | Closed Eyeblossom | 9 Closed Eyeblossom | 9 Closed Eyeblossom (33%) |
  | Golden Dandelion | 9 Golden Dandelion | 9 Golden Dandelion (33%) |
  | Wildflowers | 9 Wildflowers | 9 Wildflowers (33%) |
  | Cactus Flower | 9 Cactus Flower | 9 Cactus Flower (33%) |
  | Pale Oak Sapling | 9 Pale Oak Log | 1 Pale Oak Sapling (75%), 4 Stick (25%), 1 Pale Hanging Moss (5%) |

- Not added, to match what older plants of the same kind have:
  - Crusher recipes for the new flowers and the Pale Oak Sapling (no flower or sapling has one).
  - An Incubator recipe for Tall Dry Grass, since Tall Grass has none; Short Dry Grass yields it.
  - Any recipe for Pale Moss Block, Pale Moss Carpet, or Pale Oak Leaves (regular moss and leaves
    have none).
  - An Incubator recipe for Leaf Litter, by maintainer decision.
- No save, data component, registry id, or config change. Recipes are hand-written data in
  `src/main/resources`; no datagen output changes.

## Capabilities

### New Capabilities

- `machines/crusher`: Which plant items the Crusher turns into Shredded Biomass and what it yields.
- `machines/hydroponic-incubator`: Which plant items the Hydroponic Incubator duplicates and what it
  yields.

### Modified Capabilities

None.

## Impact

- Six new files in `src/main/resources/data/voluminousenergy/recipe/crushing/organics/`.
- Ten new files under `src/main/resources/data/voluminousenergy/recipe/hydroponic_incubating/`:
  `shrubbery/` for the grass, bushes, and moss, `flowers/` for the five flowers, and `trees/` for
  the sapling.
- JEI picks the recipes up through the existing recipe types; no compat change.
- No port to `1.21.1-dev`: none of these plants exist in 1.21.1.
