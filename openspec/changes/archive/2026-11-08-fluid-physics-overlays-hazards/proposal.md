## Why

Every Voluminous Energy fluid moves, looks, and harms the player the same way: water movement with a
hand-written velocity hack on top, no screen effect or fog when submerged, and no breathing rules for
gases. Crude oil barely slows a player down, standing in Natural Gas is as safe as standing in air, and
acid counts as fire, so Fire Resistance makes a player immune to it. This change makes each fluid feel
like the substance it represents.

## What Changes

- **Movement by viscosity.** Fluids move entities according to a small set of hand-tuned movement
  presets (gas, thin, watery, syrupy, sticky), plus dedicated entries for fluids that fit none of them
  (Mercury). Whether an entity sinks or floats comes from the fluid's real density. Crude oil, Tree Sap,
  and the liquefied coals become slow and sticky to wade through. Players float high in Sulfuric Acid,
  Nitroglycerin, and Dinitrogen Tetroxide, and can barely submerge in Mercury. They sink faster in the
  light fuels. Gases barely hinder movement, and a rising gas column lifts entities. Dropped items sink,
  float, or stick by the same rules. A player can always climb out of a one-block-deep pool of any fluid.
- **Real fluid properties.** Every fluid type reports a real-world density and viscosity in NeoForge
  units (water 1000/1000) instead of the current placeholder 1/1, so other mods and pipes that read these
  values get meaningful numbers.
- **Screen overlays and fog.** With the camera inside a fluid:
  - Gases show an obvious, single-colour fog in the gas's colour, with no texture.
  - Thin and watery liquids tint the view evenly, like looking through tinted glass.
  - Syrupy liquids and Mercury use the vanilla underwater overlay texture, tinted, with closer fog.
  - Sticky liquids such as crude oil black out the view to about a block.
- **Asphyxiation.** Every gas except Oxygen and Compressed Air drains the air bar and then deals
  asphyxiation damage, with its own death message and no bubble particles. Water Breathing and Conduit
  Power do not protect against it. Oxygen and Compressed Air refill the air bar. Gas now counts as
  filling its whole block, so a player's head is inside the gas where it is drawn against the ceiling.
  Entities that do not breathe (undead) are unaffected.
- **Acid chemical burns.** Acids deal a new acid damage type instead of fire damage, so Fire Resistance
  no longer grants immunity and fire-immune mobs are hurt. Damage scales with the acid: Sulfuric Acid
  1.0×, White Fuming Nitric Acid 1.25×, Red Fuming Nitric Acid 1.5× the configured base. Only the
  fuming nitric acids, being strong oxidizers, also set entities alight. Harm is on contact only.
- **Mercury poisons.** Entities in Mercury are poisoned instead of drowning or burning.
- **BREAKING (gameplay):** the Natural Gas spec currently says it never drowns or pushes entities;
  it now asphyxiates them and lifts them in a rising column. Players who mine into a worldgen Natural
  Gas reservoir can now suffocate. Acid damage no longer counts as fire, so netherite and other
  fire-resistant dropped items are destroyed by acid like any other item.

Save compatibility: no registry id, NBT, data component, or attachment changes. Two new datapack
registry entries are added, damage types `voluminousenergy:asphyxiation` and `voluminousenergy:acid`.
Fluid blocks keep their ids. Replacing `CrudeOilFlowingFluidBlock` with the shared fluid block class
does not touch the saved block state.

Config:
- `Acid Damage` keeps its key and default (3) and becomes the base that each acid's ratio scales.
- `Acid Fire Duration` keeps its key; its comment now says it applies to fuming acids only and is in
  ticks. Its default rises from 4 ticks to 80 ticks (4 s), so ignition is visible. Existing config
  files keep their current value.
- New `Mercury Poison Duration` (ticks, default 100): the Poison duration refreshed while an entity is
  in Mercury.

Other branches (follow-up, not scope): this lands on `1.21.1-dev` and merges forward to the 26.x
branches. The 26.x fog and screen-effect API differs from 1.21.1, so the overlay code needs rewriting on
the merge rather than merging cleanly. Some fluid textures also change on 26.2+, which may shift the
overlay colours there.

Future work, not scope: gases heavier than air (propane and butane in Light Fuels sink in reality, while
every VE gas rises), and toxic effects beyond asphyxiation (Ammonia).

## Capabilities

### New Capabilities
- `fluids/movement`: how entities and dropped items move inside each fluid, the movement presets and
  their fluid assignments, sinking and floating by density, gas lift, gas block occupancy, and the real
  density and viscosity each fluid type reports.
- `fluids/screen-overlays`: what the player sees with the camera inside each fluid: fog colour and
  distance, and the flat tint or textured overlay per preset.
- `fluids/hazards`: what fluids do to entities in them: drowning in liquids, asphyxiation in gases,
  breathable gases, acid chemical burns and ignition, and Mercury poisoning, with their damage types and
  death messages.

### Modified Capabilities
- `fluids/natural-gas`: "Natural Gas behaves as a rising gas" changes from never drowning or pushing
  entities to asphyxiating entities and lifting them in a rising column.

## Impact

- `fluids/`:
  - `VEFluidType` gains a movement preset, an overlay colour, and the `move`, `setItemMovement`, and
    `canDrownIn` overrides.
  - A new `VEFluidPhysics` enum holds the presets.
  - `VEFlowingGasFluid` reports a full block height.
  - Every fluid holder class gets real density and viscosity values and a preset.
  - The gas holders change `canDrown`.
- `fluids/flowingFluidBlocks/`:
  - The `makeStuckInBlock` hacks are removed from `VEFlowingFluidBlock` and `CrudeOilFlowingFluidBlock`.
    The latter class is deleted, and Crude Oil and Tree Sap use `VEFlowingFluidBlock`.
  - `AcidFlowingFluidBlock` takes an acid strength and deals acid damage.
  - `FumingAcidFlowingFluidBlock` adds ignition.
  - A new poisoning block for Mercury.
- `util/extensions/VEFluidClientExtension`: fog colour, fog distance, and overlay rendering, with a
  client-only overlay helper.
- A new common event listener for `LivingBreatheEvent` and `LivingDrownEvent`.
- `tools/Config`: comment and default for `Acid Fire Duration`, and new `Mercury Poison Duration`.
- Datagen: damage types and damage type tags. `en_us.json` gets the death messages; other locales
  fall back to English until translated.
- `src/gametest/`: new required hazard and movement gametests.
- No new dependencies.
