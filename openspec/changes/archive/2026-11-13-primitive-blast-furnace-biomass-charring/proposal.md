## Why

Early on, the Primitive Blast Furnace only makes coal coke and dried kelp, so a player without coal has no
fuel-free way to char what they can farm. Logs, cactus, and sugar cane are the most common renewable
plant matter in an early base. Turning them into solid fuel in the furnace gives a coal-free path
into the Stirling generators and furnaces.

## What Changes

- **Logs to charcoal.** Any item in `minecraft:logs_that_burn` (the tag vanilla's charcoal smelting
  uses) chars into 1 charcoal in 800 ticks. That is four times as long as a vanilla furnace because the
  Primitive Blast Furnace needs no fuel.
- **Cactus to Cactchar.** 3 cactus char into 1 Cactchar in 200 ticks. The input is a new item tag,
  `voluminousenergy:charrable/cactus`, which contains `#c:crops/cactus`. Modded cacti count once their
  mod or a modpack adds them to either tag.
- **Sugar cane to Cane Charcoal.** 16 sugar cane char into 1 Cane Charcoal in 200 ticks, through a
  matching tag, `voluminousenergy:charrable/sugar_cane`, which contains `#c:crops/sugar_cane`.
- **Two new solid fuels.** Cactchar and Cane Charcoal each burn for 1600 ticks in a furnace and in the
  Primitive Stirling Generator and Stirling Generator, the same as charcoal. The higher input counts
  balance them against the Hydroponic Incubator, which multiplies cactus and sugar cane about ninefold.
- **Tiny variants.** Tiny Cactchar and Tiny Cane Charcoal work like the existing tiny fuels: one full
  item crafts into 8, each burns for 200 ticks in furnaces and Stirling generators, and each joins
  `c:tiny` under its own `c:tiny/<fuel>` tag.
- **Common tags.** `c:cactchar` and `c:cane_charcoal` hold the full items, the same way `c:coal_coke`
  and `c:rosin` hold the existing full fuels.
- **Torches scale with burn time.** A full-size solid fuel over a stick crafts 1 torch per 400 ticks
  of burn time, the ratio vanilla coal and charcoal give (1600 ticks, 4 torches). With a soul fire base
  block underneath, it crafts the same number of soul torches. Vanilla's recipes list coal and charcoal
  by item rather than by tag, so VE adds its own:
  - Cactchar and Cane Charcoal make 4 through a new tag, `voluminousenergy:charcoal_grade_fuels`. A
    later 1600-tick fuel joins the tag instead of adding recipes.
  - Coal Coke (3200 ticks) makes 8 and Petcoke (4000 ticks) makes 10. Neither has a torch recipe today,
    though their tiny variants do.
  - Rosin (800 ticks, 2 torches) already follows the ratio. Tallow and every tiny fuel, including the
    new ones, keep their existing 1-torch recipes, so a fuel under 400 ticks still makes 1.

Compatibility:

- **Registry ids:** four new items: `voluminousenergy:cactchar`, `voluminousenergy:cane_charcoal`,
  `voluminousenergy:tiny_cactchar`, and `voluminousenergy:tiny_cane_charcoal`. No existing id changes.
- **Saves and network:** no NBT, data component, attachment, or packet format changes.
- **Config:** none. Burn times and recipe numbers live in item classes and recipe JSON, as they do for
  the existing fuels.
- **Existing items:** Coal Coke and Petcoke gain torch and soul torch recipes. Nothing else about an
  existing item changes.
- **Datapacks and modpacks:** all three charring recipes are ordinary `primitive_blast_furnacing` JSON. Packs can
  add modded cacti or sugar cane to the `charrable` tags without editing shared `c:` tags.
- **Regions Unexplored:** version 0.6.2 (in ATM10 Aero) does not tag its saguaro or barrel cactus as
  `c:crops/cactus`, and the mod ships nothing that names it. Those cacti stay out until Regions
  Unexplored or a pack tags them.

Out of scope: storage blocks for the new fuels, a creosote or other fluid byproduct, adding the new fuels
to `minecraft:coals` (that tag feeds the campfire recipe and other mods' recipes), other machines, and changes to the existing
Primitive Blast Furnace recipes.

## Capabilities

### New Capabilities

- `machines/primitive-blast-furnace`: which biomass the Primitive Blast Furnace chars, how many items
  one operation takes and gives, how long it runs, and how modded inputs join through tags.
- `items/solid-fuels`: Cactchar, Cane Charcoal, and their tiny variants (burn times, Stirling
  generation, crafting into tiny pieces, and tags), and how many torches every solid fuel crafts.

### Modified Capabilities

None.

## Impact

- **Code:** four item classes under `items/solid_fuels/` and `items/solid_fuels/tiny/`, registered at the
  bottom of their groups in `items/VEItems.java`. The machine needs no code change: `BasicParser`
  already checks and consumes ingredient counts above 1.
- **Data (hand-written, `src/main/resources`):** three `primitive_blast_furnacing` recipes, two
  tiny-fuel crafting recipes, six torch and soul torch recipes, four `stirling` recipes, the
  `charrable`, `charcoal_grade_fuels`, and `c:` item tags, four item models, and `en_us.json` names.
- **Assets:** four item textures, hand-drawn by the maintainer.
- **Gametests:** recipe tests for the 16:1 and 3:1 counts, the logs tag, and torch yield against burn
  time.
- **Follow-up, not scope:** merge forward to `26.1-dev`, `26.2-dev`, and `26.3-dev` once this lands on
  `1.21.1-dev`.
