## Context

See proposal.md for motivation and specs/fluids/natural-gas/spec.md for the requirements.

Every mod fluid is a holder class in `fluids/` (static `ResourceLocation` texture ids, a `Block.Properties`,
static fluid/block/bucket fields, factory methods, a `VEFluidType`, and a `BaseFlowingFluid.Properties`).
`VEFluids` registers each one through `VE_FLUIDS`, `VE_FLUID_BLOCKS`, `VE_FLUID_TYPES`, and
`VE_ITEM_REGISTRY` under a `// <Name>` comment, and `VEClientSideListener` registers each fluid type's
client extension. Buckets reach the creative tab automatically because `VESetup` lists everything in
`VE_ITEM_REGISTRY`. Lang files, bucket item models, fluid tags, combustion data, and fuel recipes are all
hand-written under `src/main/resources`; datagen does not produce any of them.

Hydrogen is the most recent gas and the cleanest template: `VEFlowingGasFluid.Source`/`Flowing` with a
flow width of 4, `.air()` block properties, density 0, `PathType.LAVA`, and only an empty-bucket sound.

## Goals / Non-Goals

**Goals:**
- Add Natural Gas exactly the way Hydrogen was added, so it needs no new mechanism.
- Derive the textures from the shared fluid template with a reproducible palette, not by hand painting.

**Non-Goals:**
- Fixing the existing `block.voluminousenergy.<fluid>` lang keys, which do not match the `<fluid>_block`
  registry ids. That is worth a separate change.
- A survival source for Natural Gas and a JEI info page.
- Bucket item tags. The mod tags no buckets under `c:buckets` today, so this change does not start.

## Decisions

### Holder class `NaturalGas` modelled on `Hydrogen`
`fluids/NaturalGas.java` mirrors `Hydrogen.java` line for line with renamed members (`NATURAL_GAS`,
`FLOWING_NATURAL_GAS`, `NATURAL_GAS_BLOCK`, `NATURAL_GAS_BUCKET`, `NATURAL_GAS_FLUID_TYPE`, texture ids
`block/fluids/natural_gas_still` and `_flowing`). Fluid type properties copy Hydrogen's, which already
match the spec (no drowning, no pushing, no boating, no source conversion). The fluid block is a plain
`VEFlowingFluidBlock`.

Alternative considered: a generic gas helper that builds all of this from a name and width. It would
remove a lot of duplication across the gas classes, but it is a refactor of every fluid and out of scope.

### Registration ids
`VEFluids` gains a `// Natural Gas` block after Hydrogen registering `natural_gas`,
`flowing_natural_gas`, `natural_gas_block`, `natural_gas_bucket`, and fluid type `natural_gas`, with
supplier fields `NATURAL_GAS_REG`, `FLOWING_NATURAL_GAS_REG`, `FLOWING_NATURAL_GAS_BLOCK_REG`,
`NATURAL_GAS_BUCKET_REG`, `NATURAL_GAS_FLUID_TYPE_REG`, following the existing naming. These register on
the already-registered deferred registers, so `VoluminousEnergy` needs no change.

### Textures: palette swap of the shared fluid template
Survey of `textures/block/fluids/`: 15 of the 22 fluids (ammonia, ammonium nitrate solution, biofuel,
diesel, dinitrogen tetroxide, gasoline, hydrogen, light fuel, liquefied coal, liquefied coke, naphtha,
nitrogen, nitroglycerin, tree sap, treethanol) are exact palette swaps of one template. Each still texture is 16x512 (32 frames, `frametime: 2`) with 12 colour indices; each
flowing texture is 32x1024 with 19 indices and an empty `animation` block. Every index maps one-to-one
between fluids, so a fluid is fully defined by its palette. The template itself is vanilla
`water_still`/`water_flow` with each grey level mapped to a colour, plus a few hand-placed foam pixels
(indices 8, 10, 11 in still keep the template's partial alpha `e9`/`eb`/`f9`). Crude oil, oxygen,
compressed air, mercury, sulfuric acid, RFNA, and WFNA use different templates and are not references
here.

Each fluid's palette is a base colour plus a per-index RGB offset, and the offset set changes with the
hue family (yellow fuels push red and green, purples push red and blue). Nitrogen is the closest hue
analogue, so Natural Gas takes Nitrogen's offsets relative to Nitrogen's still index 0 (`#6f55a1`) and
applies them to one key colour.

Key colour: **`#752c8c`** (HSV 286°, 0.69, 0.55). Hue 286° is the purple midway between blue (240°)
and magenta-red, away from Nitrogen (≈262°, blue-lavender), Hydrogen (≈226°, deep blue), and the
red/burgundy fluids. Its blue channel leaves room for the largest offset (+109 in Nitrogen's flowing
highlight) without clamping, so every derived colour keeps the intended hue. Alternatives rendered and
compared: `#7c2692` (288°, more vivid but clamps the flowing highlight's blue channel), `#842a96`
(290°, starts to lean magenta), `#6e2786` (285°, too dark to read as gas).

Still palette (template index ordered by pixel count; offsets are Nitrogen's, relative to `#6f55a1`):

| # | Pixels | Hydrogen | Nitrogen | Offset | Natural Gas |
|---|---|---|---|---|---|
| 0 | 3299 | `#001b74` | `#6f55a1` | +0, +0, +0 | `#752c8c` |
| 1 | 3119 | `#00227c` | `#785da9` | +9, +8, +8 | `#7e3494` |
| 2 | 1106 | `#26318d` | `#8b70bb` | +28, +27, +26 | `#9147a6` |
| 3 | 280 | `#3140a9` | `#9f80d8` | +48, +43, +55 | `#a557c3` |
| 4 | 257 | `#233daf` | `#9c7bde` | +45, +38, +61 | `#a252c9` |
| 5 | 96 | `#1f2f90` | `#8b6ebe` | +28, +25, +29 | `#9145a9` |
| 6 | 22 | `#363c96` | `#987dc5` | +41, +40, +36 | `#9e54b0` |
| 7 | 4 | `#6b4ab4` | `#c09cff` | +81, +71, +94 | `#c673ea` |
| 8 | 3 | `#655f97eb` | `#b7a8c9eb` | +72, +83, +40 | `#bd7fb4eb` |
| 9 | 3 | `#2045a9` | `#9786dd` | +40, +49, +60 | `#9d5dc8` |
| 10 | 2 | `#655e96e9` | `#b6a7c8e9` | +71, +82, +39 | `#bc7eb3e9` |
| 11 | 1 | `#726097f9` | `#beaed1f9` | +79, +89, +48 | `#c485bcf9` |

Flowing palette (same key colour and reference point, so the flowing body sits slightly darker than the
still body and its highlights reuse the still highlights, as in every template fluid):

| # | Pixels | Hydrogen | Nitrogen | Offset | Natural Gas |
|---|---|---|---|---|---|
| 0 | 9130 | `#001765` | `#635092` | -12, -5, -15 | `#69277d` |
| 1 | 6813 | `#001b6b` | `#6e5699` | -1, +1, -8 | `#742d84` |
| 2 | 4352 | `#001b70` | `#6f569c` | +0, +1, -5 | `#752d87` |
| 3 | 3309 | `#001d73` | `#7259a1` | +3, +4, +0 | `#78308c` |
| 4 | 2306 | `#072174` | `#765da1` | +7, +8, +0 | `#7c348c` |
| 5 | 1286 | `#002179` | `#775da7` | +8, +8, +6 | `#7d3492` |
| 6 | 1262 | `#01227b` | `#795fa9` | +10, +10, +8 | `#7f3694` |
| 7 | 1058 | `#00155a` | `#584e87` | -23, -7, -26 | `#5e2572` |
| 8 | 1037 | `#052985` | `#8165b3` | +18, +16, +18 | `#873c9e` |
| 9 | 993 | `#2c3ea6` | `#9c7dd5` | +45, +40, +52 | `#a254c0` |
| 10 | 678 | `#18308f` | `#8b6ebe` | +28, +25, +29 | `#9145a9` |
| 11 | 348 | `#23389e` | `#9577cc` | +38, +34, +43 | `#9b4eb7` |
| 12 | 103 | `#3140a9` | `#9f80d8` | +48, +43, +55 | `#a557c3` |
| 13 | 64 | `#6b4bb3` | `#c09cff` | +81, +71, +94 | `#c673ea` |
| 14 | 17 | `#00186b` | `#6a5297` | -5, -3, -10 | `#702982` |
| 15 | 6 | `#001865` | `#635193` | -12, -4, -14 | `#69287e` |
| 16 | 3 | `#001867` | `#6a5393` | -5, -2, -14 | `#702a7e` |
| 17 | 2 | `#001564` | `#654e91` | -10, -7, -16 | `#6b257c` |
| 18 | 1 | `#08257d` | `#7c62ab` | +13, +13, +10 | `#823996` |

The PNGs are produced by remapping `hydrogen_still.png` and `hydrogen_flowing.png` pixel by pixel
through these tables, which keeps frame layout and alpha identical to the template. The `.mcmeta` files
are copies of Hydrogen's. The remap script is a one-off and is not committed; the tables above are the
record.

### Bucket texture
Fluid bucket textures are the vanilla bucket greys plus five fluid colours in fixed pixel slots, a dark
to light ramp. `hydrogen_bucket.png` fills the slots from its still palette (slot colours `#001b74`,
`#00227c`, `#26318d`, `#233daf`, `#3140a9` are still indices 0, 1, 2, 4, 3); other buckets (Nitrogen,
Diesel) were hand-edited and are not exact swaps. Natural Gas remaps `hydrogen_bucket.png` with the same
index choice:

| Hydrogen slot | Still index | Natural Gas |
|---|---|---|
| `#001b74` | 0 | `#752c8c` |
| `#00227c` | 1 | `#7e3494` |
| `#26318d` | 2 | `#9147a6` |
| `#233daf` | 4 | `#a252c9` |
| `#3140a9` | 3 | `#a557c3` |

The item model is a copy of `hydrogen_bucket.json` pointing at `voluminousenergy:item/natural_gas_bucket`.

### Tags and combustion data
- `data/c/tags/fluid/natural_gas.json`: `replace: false`, value `voluminousenergy:natural_gas`.
- `data/c/tags/fluid/gaseous.json`: append `voluminousenergy:natural_gas`.
- `data/voluminousenergy/tags/fluid/combustible.json`: append `voluminousenergy:natural_gas`.
- `data/voluminousenergy/fluid_data/combustion/natural_gas.json`: `tag: c:natural_gas`,
  `energy_per_tick: 24`.
- `data/voluminousenergy/recipe/fuel_combustion/natural_gas.json`: copy of `crude_oil.json` with
  `c:natural_gas`.

Combustion data is keyed on the conventional tag, not the fluid id, so another mod's natural gas added
to `c:natural_gas` burns too. 24 FE/tick sits between crude oil (16) and naphtha (32): a raw fuel that
burns better than crude but worse than refined products. The generator multiplies this by the
oxidizer's multiplier.

### Localisation
`en_us.json` and `ja_jp.json` get `fluid.`, `fluid_type.`, `block.`, and `item.` entries next to
Hydrogen's. The block key is `block.voluminousenergy.natural_gas_block`, matching the registry id,
rather than copying the existing mismatched `block.voluminousenergy.<fluid>` form.

## Risks / Trade-offs

- [Purple reads differently on some monitors or under biome lighting] → in-game check beside Nitrogen
  and in a Combustion Generator tank; if it reads too blue or too magenta, shift the key colour's hue a
  few degrees and regenerate from the tables' offsets.
- [Natural Gas has no survival source, so it is creative-only until a follow-up lands] → accepted;
  proposal records the follow-up.

## Migration Plan

Additive. No migration; rollback is reverting the change, which drops any placed Natural Gas.
