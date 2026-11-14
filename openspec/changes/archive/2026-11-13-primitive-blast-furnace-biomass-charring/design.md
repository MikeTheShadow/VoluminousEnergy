## Context

The Primitive Blast Furnace is fully declarative. `VETileEntities.PRIMITIVE_BLAST_FURNACE_FACTORY` has
zero energy storage, no tanks, `withRecipe(PRIMITIVE_BLAST_FURNACING)`, and a `BasicProcessor`.
`VEContainers.PRIMITIVE_BLAST_FURNACE_FACTORY` has one `ItemInputSlot`, one `ItemOutputSlot`, and an
upgrade slot. `PrimitiveBlastFurnaceRecipe` parses ingredient 0 into slot 0 and result 0 into slot 1
through `BasicParser`. Its three shipped recipes (coal, coal block, kelp) are all 1:1 or 1:9 by output.

Ingredient counts above 1 already work end to end:

- `VERecipeCodecs.VE_LAZY_INGREDIENT_CODEC` reads an optional `count` and builds every candidate
  `ItemStack` with that count, for both `item` and `tag` ingredients.
- `BasicParser.isCompleteRecipe` requires `stackInSlot.getCount() >= recipe.getIngredientCount(...)`,
  and `completeRecipe` extracts exactly that count.
- `BasicParser.isPartialRecipe` accepts any matching stack regardless of count, so 1 to 15 sugar cane
  narrows the potential recipe to the cane recipe without starting it.
- `PrimitiveBlastingCategory.ingredientHandler` hands JEI `getIngredient(0).getItems()`, which carry the
  count.

Solid fuels follow one pattern: a `VEItem` subclass per fuel under `items/solid_fuels/` (or `.../tiny/`)
that overrides `getBurnTime`, registered in `VEItems` (`// Misc` for full fuels, `//Tiny fuels` for
tiny ones). Each has a hand-written `item/handheld` model, a `c:` tag, a `stirling` recipe, and for tiny
fuels a `vanilla_crafting/tiny_solid_fuel/` 1-to-8 recipe and a `c:tiny/<fuel>` tag listed in `c:tiny`.
Torches come from `vanilla_crafting/torch/` recipes keyed on fuel tags: `c:rosin` gives 2, and
`c:tallow` and `c:tiny` give 1, each with a matching soul torch recipe. Coal Coke and Petcoke have
none.

## Goals / Non-Goals

**Goals:**

- Add the three charring recipes and four fuel items as data and item classes only, without touching the
  machine, its factories, the parser, or the JEI category.
- Keep modded-input compatibility in tags a pack can edit, not in recipe JSON.

**Non-Goals:**

- No new parser, processor, or validator. No config entries: existing fuels and furnace recipes keep
  their numbers in item classes and recipe JSON, and this change follows them.
- No datagen providers. Every comparable file (tags, models, recipes, lang) is hand-written in
  `src/main/resources`.

## Decisions

**Recipes are plain `primitive_blast_furnacing` JSON with `count`.** `logs_to_charcoal.json` uses
`{"tag": "minecraft:logs_that_burn", "count": 1}` with `process_time` 800. `cactchar.json` uses the
`voluminousenergy:charrable/cactus` tag with `count` 3, and `cane_charcoal.json` uses
`voluminousenergy:charrable/sugar_cane` with `count` 16, both with `process_time` 200. The alternative,
a dedicated processor that batches inputs, adds code for behaviour `BasicParser` already has.

**Logs use `minecraft:logs_that_burn` directly.** It is what vanilla's charcoal smelting recipe uses,
it already carries every modded log that burns, and it excludes nether stems on purpose. A VE wrapper
tag would add nothing for logs.

**Plant inputs go through VE-owned wrapper tags.** `voluminousenergy:charrable/cactus` contains
`#c:crops/cactus`, and `voluminousenergy:charrable/sugar_cane` contains `#c:crops/sugar_cane`, the
NeoForge 21.1 common tags. A pack can add a modded cactus to the VE tag without changing what other
mods' `c:crops/cactus` recipes accept. Nothing names Regions Unexplored: 0.6.2 does not tag its cacti,
and the maintainer does not want its ids in the shipped jar. The sugar cane wrapper exists for
symmetry, so both plant recipes extend the same way.

**Higher input counts, not lower burn time, balance the plants.** The maintainer chose charcoal parity
(1600 ticks) for both fuels so they slot into every place charcoal's numbers already work. 3 cactus and
16 sugar cane per fuel offset the Hydroponic Incubator's roughly ninefold cactus and sugar cane output.
Logs take 800 ticks, four times a vanilla furnace, because the Primitive Blast Furnace burns nothing.

**Fuel items copy the existing class-per-fuel pattern.** `Cactchar` and `CaneCharcoal` in
`items/solid_fuels/` return 1600 from `getBurnTime`. `TinyCactchar` and `TinyCaneCharcoal` in
`items/solid_fuels/tiny/` return 200. NeoForge 21.1's `furnace_fuels` data map would also work, but
every existing VE fuel uses the override, and mixing the two would split burn times across two places.
The new classes import `org.jetbrains.annotations.Nullable` per AGENTS.md, not the `javax` import the
older fuel classes use.

**Registration order.** `CACTCHAR` and `CANE_CHARCOAL` go at the bottom of `// Misc` in `VEItems`
(after `BITUMEN`), and `TINY_CACTCHAR` and `TINY_CANE_CHARCOAL` at the bottom of `//Tiny fuels`. The
creative tab iterates `VE_ITEM_REGISTRY`, so no tab code changes. `en_us.json` entries go at the bottom
of the `"Items"` group, before the `"Multitool Items"` banner.

**Torch yield is 1 per 400 ticks of burn time, one recipe pair per yield tier.** Vanilla coal and
charcoal set the ratio (1600 ticks, 4 torches), and rosin (800, 2) already follows it. Vanilla's
`torch` and `soul_torch` list coal and charcoal by item, so no tag can extend them, and adding fuels to
`minecraft:coals` would also change the campfire and every other mod recipe on that tag. VE adds its
own recipes in `vanilla_crafting/torch/`, following the rosin files:

- `torch_from_charcoal_grade_fuels.json` and `soul_torch_from_charcoal_grade_fuels.json` give 4, keyed
  on a new `voluminousenergy:charcoal_grade_fuels` tag holding `#c:cactchar` and `#c:cane_charcoal`.
- `torch_from_coal_coke.json` and `soul_torch_from_coal_coke.json` give 8, keyed on `c:coal_coke`.
- `torch_from_petcoke.json` and `soul_torch_from_petcoke.json` give 10, keyed on `c:petcoke`.

A tag per yield tier means a future fuel at an existing burn time needs only a tag entry. Coke and
petcoke sit alone in their tiers, so a wrapper tag would add a file without saving a recipe. They key
on their existing `c:` tags. Tallow and tiny fuels burn for 200 ticks, which would round to 0 torches.
They keep their 1-torch recipes as a floor, the alternative being no torch recipe at all.

A gametest pins the rule: for every recipe whose id is under `vanilla_crafting/torch/`, the result
count equals `max(1, burnTime / 400)` for each item the fuel ingredient accepts. Any later burn-time
change that leaves its torch recipe behind fails the test.

**Stirling recipes use the `c:` tags.** `stirling/cactchar.json` and `stirling/cane_charcoal.json` use
`process_time` 1600, and `stirling/tiny/cactchar.json` and `stirling/tiny/cane_charcoal.json` use 200,
all at `energy_per_tick` 128, matching `stirling/coal.json` and `stirling/tiny/charcoal.json`. Both
generator tiles use the same `STIRLING` recipe type, so one set covers both.

## Risks / Trade-offs

- [Tag-based ingredients resolve to an item list when recipes load] → Items added to a `charrable` tag
  by a datapack take effect on the next `/reload`, which is how the existing tag-based Stirling recipes
  already behave.
- [The furnace stays unlit with 1 to 15 sugar cane, which can look broken] → JEI shows the 16 count. The
  in-game check confirms the furnace starts as soon as the 16th item arrives.
- [Logs at 800 ticks may feel slow next to a furnace] → It is a balance number in recipe JSON, so a
  datapack can change it and so can a later change.
- [10 torches from one Petcoke is a large yield for an early-game crafting recipe] → Petcoke comes
  from the crude oil chain, so by the time a player has it, torches are cheap anyway. The yield follows
  from its burn time, which this change does not touch.
- [Textures do not exist yet] → The maintainer draws all four in GIMP from the vanilla charcoal palette
  before the in-game check. Until then the items show the missing-texture checkerboard.

## Migration Plan

New ids and recipes only. Existing worlds pick them up on load; removing the change leaves any crafted
items as unknown-item stacks, the same as removing any item.
