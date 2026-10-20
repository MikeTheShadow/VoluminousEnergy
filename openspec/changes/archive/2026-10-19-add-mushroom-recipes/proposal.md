## Why

No mushroom has a Crusher or Hydroponic Incubator recipe, so Brown and Red Mushrooms can be neither
farmed nor turned into Shredded Biomass. 26.3-dev added both for its Dappled Forest plants, and the
mushroom recipes apply to every version that has the two mushrooms.

## What Changes

- **Hydroponic Incubator** duplicates Brown and Red Mushroom, each consuming 250 mB of Ammonium
  Nitrate Solution over 200 ticks and keeping the input, like every existing incubator recipe:

  | Input | Main output | Chance output |
  |---|---|---|
  | Brown Mushroom | 9 Brown Mushroom | 9 Brown Mushroom (33%) |
  | Red Mushroom | 9 Red Mushroom | 9 Red Mushroom (33%) |

- **Crusher** shreds any item in `c:mushrooms` (Brown Mushroom, Red Mushroom, and mushrooms other
  mods add to the tag) into 1 Shredded Biomass, one item per 200-tick cycle with no experience.
- No save, data component, registry id, or config change. Recipes are hand-written data in
  `src/main/resources`; no datagen output changes.

## Capabilities

### New Capabilities

- `machines/crusher`: Which plant items the Crusher accepts as organic input and what it yields,
  starting with mushrooms.
- `machines/hydroponic-incubator`: Which plant items the Hydroponic Incubator duplicates and what it
  yields, starting with mushrooms.

### Modified Capabilities

None.

## Impact

- `src/main/resources/data/voluminousenergy/recipe/crushing/organics/mushrooms.json`.
- `src/main/resources/data/voluminousenergy/recipe/hydroponic_incubating/mushrooms/brown_mushroom.json`
  and `red_mushroom.json`, in a new folder next to `flowers/` and `shrubbery/`.
- JEI picks the recipes up through the existing recipe types; no compat change.
- The recipe files are byte-identical to the ones on 26.3-dev, so merging forward through 26.1-dev
  and 26.2-dev adds them without conflict. On those branches the requirements land in the existing
  `machines/crusher` and `machines/hydroponic-incubator` specs.
