## Why

Minecraft 26.3 adds the Dappled Forest, with poplar trees, Red Shrub, and Shelf Mushroom, and none
of its plants has a Crusher or Hydroponic Incubator recipe. No mushroom has an Incubator recipe at
all, so the Dappled Forest's other common plant, the Brown Mushroom, cannot be farmed either.

## What Changes

- **Hydroponic Incubator** grows six more plants, each consuming 250 mB of Ammonium Nitrate Solution
  over 200 ticks and keeping the input, like every existing incubator recipe:

  | Input | Main output | Chance outputs |
  |---|---|---|
  | Poplar Sapling | 9 Poplar Log | 1 Poplar Sapling (75%), 4 Stick (25%), 1 Shelf Mushroom (5%) |
  | Red Shrub | 9 Red Shrub | 4 Red Shrub (66%), 1 Brown Mushroom (5%) |
  | Shelf Mushroom | 9 Shelf Mushroom | 9 Shelf Mushroom (33%) |
  | Brown Mushroom | 9 Brown Mushroom | 9 Brown Mushroom (33%) |
  | Red Mushroom | 9 Red Mushroom | 9 Red Mushroom (33%) |

- **Crusher** shreds two more kinds of plant, one item per 200-tick cycle with no experience:

  | Input | Main output | Chance output |
  |---|---|---|
  | Red Shrub | 2 Shredded Biomass | 2 Stick (50%) |
  | Any item in `c:mushrooms` | 1 Shredded Biomass | none |

- Not added, to match what older plants of the same kind have:
  - A Crusher recipe for the Poplar Sapling (no sapling has one).
  - Any recipe for Orange, Red, or Yellow Poplar Leaves (no leaves have one).
- The Sawmill already saws Poplar Log into Poplar Planks by pairing `_log` and `_planks` item names,
  so it needs no recipe.
- Manufactured 26.3 items (Straw Bed, cushions, wool and concrete slabs and stairs, explorer maps)
  are out of scope.
- No save, data component, registry id, or config change. Recipes are hand-written data in
  `src/main/resources`; no datagen output changes.

## Capabilities

### New Capabilities

None.

### Modified Capabilities

- `machines/crusher`: adds Red Shrub and mushroom shredding; adds the Poplar Sapling and the three
  poplar leaves to the items the Crusher does not accept.
- `machines/hydroponic-incubator`: adds Poplar Sapling, Red Shrub, and mushroom duplication; adds
  the three poplar leaves to the items the Incubator does not accept.

## Impact

- Two new files in `src/main/resources/data/voluminousenergy/recipe/crushing/organics/`.
- Five new files under `src/main/resources/data/voluminousenergy/recipe/hydroponic_incubating/`.
- The `c:mushrooms` crushing recipe also accepts mushrooms other mods add to that tag.
- JEI picks the recipes up through the existing recipe types; no compat change.
- Follow-up port, not in this change: the Brown and Red Mushroom Incubator recipes and the
  `c:mushrooms` Crusher recipe to `1.21.1-dev`, `26.1-dev`, and `26.2-dev`, which have both
  mushrooms. Poplar, Red Shrub, and Shelf Mushroom exist only in 26.3.
