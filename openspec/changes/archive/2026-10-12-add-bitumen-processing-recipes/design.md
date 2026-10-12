## Context

Both machines are data-driven. `CentrifugalSeparatorRecipe` reads two ingredients (input slot, bucket
slot) and up to four `item_results`, the first going to the main output slot and the rest to the
three chanced slots. `CrusherRecipe` reads one ingredient and up to two `item_results`, the first
guaranteed and the second usually chanced, plus an `experience` range. Recipe JSON is hand-written
under `src/main/resources/data/voluminousenergy/recipe/`; neither machine has a recipe datagen
provider, so no `runData` output changes.

The closest existing recipes, and the numbers they set:

| Recipe | Input | Outputs |
|---|---|---|
| `centrifugal_separation/soulsand_dust.json` | 16 Soul Sand Dust (4 blocks), bucket | 10 Sand Dust, 4 Carbon Dust, Crude Oil bucket, 1 Sulfur Dust |
| `centrifugal_separation/gravel.json` | 4 Gravel | 6 Sand Dust, Flint 75%, Raw Sulfur 5% |
| `crushing/ores/saltpeterore.json` | 1 Saltpeter Ore | 6 Saltpeter Chunk, 3 Sand Dust |

## Goals / Non-Goals

**Goals:**
- Give Bitumen a single clear use: the crude oil chain.
- Let Silk Touched bituminous blocks be processed without losing their Bitumen.

**Non-Goals:**
- Any new item, fluid, or machine. Gravel has no dust in the mod, so the gravel host returns as a
  Gravel block instead of a new Gravel Dust.
- Changing the bituminous loot tables or tags.

## Decisions

### Bitumen to crude oil: 4 Bitumen per bucket, Crude Oil bucket as the main output
A bituminous block drops 3 to 7 Bitumen (5 on average), so one block mined without Fortune is worth
about 1.25 buckets. That is richer than Soul Sand (4 blocks per bucket), which fits: Bitumen is
petroleum itself rather than a material with oil trapped in it. Every source of Bitumen sits beside
an oil lake or deposit holding far more crude, so the recipe is a way to use up Bitumen, not a
competing oil source. The Crude Oil bucket goes first so it lands in the main output slot as the
product of the recipe; Sand Dust and Carbon Dust are byproducts in the chanced slots.

### Byproducts follow real bitumen
Real oil-sands bitumen is mostly hydrocarbon with residual mineral fines, roughly 80 to 85% carbon by
mass. Most of that carbon leaves in the upgraded oil; coking, the usual upgrading step for bitumen,
rejects about 15 to 20% of the feed as solid petroleum coke. So:

- **Sand Dust, 2 at 100%**: the fines carried in from the sand or gravel the Bitumen came from. More
  sand than carbon, as requested, and modest so the recipe does not become a sand source.
- **Carbon Dust, 1 at 25%**: about one quarter of operations leave a solid carbon residue, close to
  the coke fraction above. 25% is also the most common chance in the existing recipes.

Alternatives considered: a guaranteed Carbon Dust (makes Bitumen a better carbon source than the
coal chain, and does not match the minor coke fraction); 33% (no real-world basis over 25%, and an
uncommon value in the codebase). A Sulfur Dust byproduct is realistic (Athabasca bitumen is about
4 to 5% sulfur) and the fourth output slot is free, but it was not requested and is left out.

### Crushing: Bitumen first, host material second
The Crusher has one guaranteed output and one secondary. Bitumen is the guaranteed output so a Silk
Touched block yields what mining it would have; the count is 5, the midpoint of the 3 to 7 loot
range, matching how `saltpeterore.json` sits within its own loot range.

- **Sands**: 3 Sand Dust at 100%, the same secondary as Saltpeter Ore.
- **Gravel**: 1 Gravel at 25%. Bitumen fills the spaces between the gravel, so once enough of it is
  ground out the leftover stone is still gravel; four blocks returning one Gravel mirrors the four
  Bitumen per bucket above.

Experience is 0 to 0, since mining the blocks gives none.

### Ingredients
The separator recipe uses the `c:bitumen` item tag, as other separator recipes use `c:dusts/...`
tags, so another mod's bitumen works. The crushing recipes name each block by item id, because the
three blocks give different outputs and `c:ores/bitumen` covers all three.

### File placement
The crushing recipes go in `crushing/ores/` beside `saltpeterore.json`, as `bituminous_sand.json`,
`bituminous_red_sand.json`, and `bituminous_gravel.json`. The separator recipe is
`centrifugal_separation/bitumen.json`. All use `process_time` 200 like their neighbours.

## Risks / Trade-offs

- [Bitumen to crude is cheap compared with Soul Sand] → Acceptable because every Bitumen source is
  next to a much larger body of crude; numbers live in plain JSON if balance changes later.
- [Crushing a Silk Touched block gives 5 Bitumen, more than an unlucky shovel and less than Fortune
  III] → Same trade-off saltpeter ore already makes; Fortune stays the better route.
