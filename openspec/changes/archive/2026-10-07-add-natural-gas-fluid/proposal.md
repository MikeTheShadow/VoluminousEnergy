## Why

Crude oil is the only raw fossil fuel in the mod. Natural Gas gives the energy chain a second raw input,
and the gaseous variant sits beside crude oil the way hydrogen, oxygen, and nitrogen sit beside the
liquid chemistry fluids.

## What Changes

- Players and modpacks get a new gaseous fluid, Natural Gas (`voluminousenergy:natural_gas`), with a
  flowing form, a placeable fluid block that rises like the other gases, and a Natural Gas Bucket in the
  Voluminous Energy creative tab.
- Natural Gas renders with its own still and flowing animated textures and bucket texture, coloured a
  saturated mid purple (hue about 286°) that is clearly distinct from Nitrogen's blue-lavender and from
  the red and burgundy fluids.
- Conventional fluid tags: a new `c:natural_gas` tag, and `voluminousenergy:natural_gas` joins the
  existing `c:gaseous` tag, so other mods and datapacks can address it.
- Natural Gas is a combustion fuel: it joins `voluminousenergy:combustible`, gets a
  `fluid_data/combustion` entry, and a `fuel_combustion` recipe keyed on `c:natural_gas`, so the
  Combustion Generator burns it with any oxidizer. Proposed base output is 24 FE/tick before the
  oxidizer multiplier, between crude oil (16) and naphtha (32).
- English and Japanese names for the fluid, fluid type, block, and bucket.
- Out of scope: any way to obtain Natural Gas in survival (worldgen, Dimensional Laser region fluid,
  machine recipes) and a JEI info page. A follow-up change adds a source.

Save compatibility: additive only. New registry ids `voluminousenergy:natural_gas`,
`voluminousenergy:flowing_natural_gas`, `voluminousenergy:natural_gas_block`,
`voluminousenergy:natural_gas_bucket`, and fluid type `voluminousenergy:natural_gas`. No existing id,
NBT, data component, attachment, or config option changes. Worlds that later remove the mod lose any
placed Natural Gas blocks and buckets, as with any modded fluid.

Config: none. The combustion value is datapack data, not a `Config` entry, matching every other fuel.

Other branches: none required. A port to `1.18.2-exp-next` (0.4) would be a separate follow-up if the
maintainers want the fluid there.

## Capabilities

### New Capabilities
- `fluids/natural-gas`: the Natural Gas fluid: registration and ids, gaseous placement behaviour,
  bucket, appearance, conventional tags, localisation, and its use as a combustion fuel.

### Modified Capabilities

None. `openspec/specs/` has no existing capabilities.

## Impact

- `fluids/`: new `NaturalGas` holder class; four registrations plus a fluid type in `VEFluids`.
- `events/VEClientSideListener`: client fluid type extension registration.
- `src/main/resources/assets/voluminousenergy/`: still and flowing textures with `.mcmeta`, bucket
  texture, bucket item model, `en_us.json` and `ja_jp.json` entries.
- `src/main/resources/data/`: `c/tags/fluid/natural_gas.json`, `c/tags/fluid/gaseous.json`,
  `voluminousenergy/tags/fluid/combustible.json`, `fluid_data/combustion/natural_gas.json`,
  `recipe/fuel_combustion/natural_gas.json`.
- No new dependencies. JEI picks up the bucket and fluid automatically through existing registries.
