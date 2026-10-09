## Context

Crusher and Hydroponic Incubator recipes are hand-written JSON under
`src/main/resources/data/voluminousenergy/recipe/`, read by `CrusherRecipe` and
`HydroponicIncubatorRecipe`. The Crusher container has one main output and one RNG slot, so a
crushing recipe has at most two results. The Incubator has a primary output and three RNG slots,
and `HydroponicParser` never consumes the input item. Neither machine restricts its input slot to
a fixed item list, so new recipes need no Java change.

## Goals / Non-Goals

**Goals:**
- Every value is taken from an existing recipe for the closest older plant, so the new plants
  neither out-earn nor under-earn their counterparts.

**Non-Goals:**
- Filling older gaps (Fern, Large Fern, Pink Tulip, Moss, Tall Grass in the incubator, and others).
  They are a separate change if wanted.
- Rebalancing existing recipes.

## Decisions

- **Counterparts used for each value.**
  - Crusher: Tall Dry Grass takes the 2-biomass base of `tall_grass.json`; Short Dry Grass gives 1,
    half its taller form (maintainer-set). Being dried stems, they give a Stick as their chance output like
    `dead_bush.json` instead of a smaller grass: 25% for Short Dry Grass and 50% for Tall Dry Grass
    (maintainer-set values). Bush and
    Firefly Bush take the 2-biomass base of grass with a 50% Stick chance output like
    `dead_bush.json`; Bush gives 2 Sticks on that roll and Firefly Bush 1 (maintainer-set).
    `azalea.json` (4 biomass, 2 guaranteed sticks) is for a block-sized shrub and would
    over-reward a plant. Leaf Litter yields 1 like `seeds.json`, because up to four pieces fill one block. Pale
    Hanging Moss copies short grass.
  - Incubator: Short Dry Grass mirrors `shrubbery/grass.json`, swapping in the plants that grow
    beside dry grass in deserts and badlands (Tall Dry Grass, Dead Bush, Cactus Flower) for Tall
    Grass, Dandelion, and Poppy. Bush mirrors `shrubbery/seagrass.json`, with Firefly Bush as the
    rare variant in place of Sea Pickle. Firefly Bush mirrors `shrubbery/small_dripleaf.json`,
    with Bush in place of Big Dripleaf at a higher 20% chance so the rarer bush is easier to
    farm back to. Pale Hanging Moss and the five flowers copy the flower pattern (9, plus 9 at
    33%). Pale Oak Sapling copies the sapling pattern, with Pale Hanging Moss as its 5% extra the
    way Jungle gives Cocoa Beans and Mangrove gives Mangrove Roots.
- **Closed and open eyeblossom each reproduce themselves.** They are separate items in vanilla;
  each recipe outputs its own state rather than converting one into the other.
- **File placement follows the existing folders:** `crushing/organics/` for all crusher recipes;
  `hydroponic_incubating/shrubbery/`, `flowers/`, and `trees/` by plant kind. File names are the
  item path (`short_dry_grass.json`), as the existing ones are.

## Risks / Trade-offs

- [Cactus Flower at 5% from Short Dry Grass gives a second route to it besides its own recipe] →
  It matches how Grass already yields Dandelion and Poppy, which also have their own recipes.
- [Bush ⇄ Firefly Bush loop lets a single Bush become a Firefly Bush farm] → The 5% rate keeps the
  first conversion slow, and the Firefly Bush already duplicates on its own.
