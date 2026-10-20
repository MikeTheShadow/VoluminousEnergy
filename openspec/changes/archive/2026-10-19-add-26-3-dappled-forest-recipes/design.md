## Context

Crusher and Hydroponic Incubator recipes are hand-written JSON under
`src/main/resources/data/voluminousenergy/recipe/`, read by `CrusherRecipe` and
`HydroponicIncubatorRecipe`. The Crusher has one main output and one RNG slot; the Incubator has a
primary output and three RNG slots, and `HydroponicParser` never consumes the input. Neither machine
restricts its input slot to a fixed item list, so new recipes need no Java change. `SawmillParser`
pairs every registered `<name>_log` with `<name>_planks`, which already covers `poplar_log`.

## Goals / Non-Goals

**Goals:**
- Every value is taken from an existing recipe for the closest older plant, so the new plants
  neither out-earn nor under-earn their counterparts.

**Non-Goals:**
- Filling older gaps (Fern, Moss, Tall Grass in the Incubator, Crimson and Warped Fungus, and
  others). They are a separate change if wanted.
- Changing how the Sawmill matches logs to planks.

## Decisions

- **Counterparts used for each value.**
  - Incubator Poplar Sapling copies `trees/jungle_sapling.json`, with Shelf Mushroom as its 5% extra
    the way Pale Oak gives Pale Hanging Moss. Shelf Mushrooms grow on fallen poplar logs in the
    Dappled Forest.
  - Incubator Red Shrub copies `shrubbery/bush.json` (Red Shrub has no flower tag or dye recipe and
    drops itself without shears, so it is a bush, not a flower), with Brown Mushroom as its 5% extra.
    Brown Mushroom patches generate in the Dappled Forest at 1 in 2 chunks against 1 in 256 in
    ordinary biomes.
  - Incubator mushrooms copy the flower pattern (9, plus 9 at 33%) as maintainer-set values.
  - Crusher Red Shrub copies `organics/bush.json`. Crusher mushrooms yield 1 Shredded Biomass like
    `organics/seeds.json`, as a maintainer-set value.
- **One tag recipe for crushing, one file per mushroom for incubating.** A crushing recipe has a
  fixed output, so a single `c:mushrooms` recipe serves every mushroom and follows `seeds.json`
  (`c:seeds`) and `azalea.json` (`c:azalea`). An incubating recipe outputs its own input, so each
  mushroom needs its own file. `c:mushrooms` is chosen over `minecraft:mushrooms` because the mod's
  tag ingredients use the `c` namespace; NeoForge defines it as `#minecraft:mushrooms`.
- **File placement follows the existing folders:** `crushing/organics/red_shrub.json` and
  `crushing/organics/mushrooms.json`; `hydroponic_incubating/trees/poplar_sapling.json`,
  `hydroponic_incubating/shrubbery/red_shrub.json`, and a new `hydroponic_incubating/mushrooms/`
  folder for the three mushrooms, since they are neither flowers nor shrubbery.

## Risks / Trade-offs

- [`c:mushrooms` lets the Crusher shred modded mushrooms the mod has never seen] → Accepted as
  intended; `c:seeds` already does the same for modded seeds.
- [Poplar Sapling and Red Shrub give a second route to Shelf and Brown Mushroom besides their own
  recipes] → The same as Grass yielding Dandelion and Poppy, which also have their own recipes.
