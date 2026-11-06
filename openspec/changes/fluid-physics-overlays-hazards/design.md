## Context

See proposal.md for motivation and the three new `fluids/` specs for the requirements.

Current state, as read from the 1.21.1 code and NeoForge 21.1.222 sources:

- Each fluid is a holder class in `fluids/` that builds a `VEFluidType` from
  `FluidType.Properties`. All 23 use placeholder `density(1)` or `density(0)`, `viscosity(1)`,
  `motionScale(0)`, `canPushEntity(false)`, `fallDistanceModifier(0)`, and `canSwim(false)`. No fluid
  calls `setColourTint`; colour comes only from the textures.
- `LivingEntity.travel` gives every non-lava modded fluid the vanilla water movement branch unless
  `FluidType.move(...)` returns `true`. No VE fluid overrides it. On top of water movement,
  `VEFlowingFluidBlock.entityInside` calls `makeStuckInBlock` (0.9, with Y flipped to -0.9 while
  rising), and `CrudeOilFlowingFluidBlock` (used by Crude Oil and Tree Sap) calls it with
  (0.8, 0.75, 0.8). `makeStuckInBlock` zeroes velocity every tick, which is where the jerky feel comes
  from.
- Gases are `VEFlowingGasFluid` with `.air()` block properties. Their `getOwnHeight` is `amount / 9`,
  measured from the floor, while `GaseousFluidRenderer` draws them hanging from the ceiling. The renderer
  and the gas flow code read `getOwnHeight` only. Entity fluid heights, eye-in-fluid checks, and the
  camera fog checks in `ClientHooks` read `FluidState.getHeight`.
- Breathing goes through `CommonHooks.onLivingBreathe`: an entity loses air if the fluid at its eyes
  is not air and `canDrownInFluidType` is true, then fires `LivingBreatheEvent` and, at zero air,
  `LivingDrownEvent`. Water Breathing and Conduit Power set `canBreathe` before the event fires.
- With the camera in a modded fluid, `Camera.getFluidInCamera` is `FogType.NONE`. The client extension
  hooks `modifyFogColor`, `modifyFogRender`, and `renderOverlay` are the only per-fluid screen effects;
  `ScreenEffectRenderer` calls `renderOverlay` for non-spectators whose eye is in a non-air fluid type.
- Acids: `AcidFlowingFluidBlock.entityInside` deals `inFire` damage of `Config.ACID_DAMAGE` and sets
  fire for `Config.ACID_FIRE_DURATION` ticks (default 4). `FumingAcidFlowingFluidBlock` adds smoke
  particles.
- There are no VE damage types yet. Fluid tags and other data are hand-written under
  `src/main/resources/data`.

## Goals / Non-Goals

**Goals:**
- One shared, data-driven movement path for every VE fluid, replacing the `makeStuckInBlock` hacks.
- Presets are named and reusable, so a new fluid picks a preset plus two real numbers.
- Server-safe: movement and hazards are common code; screen effects are client-only.

**Non-Goals:**
- Swimming pose in VE fluids (`canSwim` stays false).
- Changing gas flow, spreading, or rendering. Only the reported height changes.
- Heavier-than-air gases, toxic effects beyond asphyxiation, and fume damage away from contact.
- Translating the new death messages beyond `en_us`.

## Decisions

### `VEFluidPhysics` enum holds the presets
A new enum `fluids/VEFluidPhysics` with constants `GAS`, `THIN`, `WATERY`, `SYRUPY`, `STICKY`,
`MERCURY`. Each constant carries the movement numbers and the visibility numbers for its preset:

| Preset | Horizontal retained / tick | Vertical retained / tick | Input acceleration × water | Buoyancy scale | Fog end (blocks) | Screen effect |
|---|---|---|---|---|---|---|
| GAS | air × 0.98 | air | 1.0 (air) | none | 24 | none |
| THIN | 0.82 | 0.82 | 1.0 | 1.0 | 16 | flat tint, alpha 0.15 |
| WATERY | 0.80 | 0.80 | 1.0 | 1.0 | 12 | flat tint, alpha 0.25 |
| SYRUPY | 0.70 | 0.65 | 0.95 | 1.0 | 3 | tinted water texture |
| STICKY | 0.60 | 0.45 | 0.9 | 0.5 | 1 | flat tint, alpha 0.9, darkened |
| MERCURY | 0.70 | 0.50 | 0.95 | 1.0 | 1 | tinted water texture |

WATERY matches vanilla water (0.8 drag, 0.02 acceleration) so it is the reference point. Terminal
horizontal speed is `0.02 × acceleration × retention / (1 − retention)` blocks per tick: about
1.6 blocks/s for WATERY, 1.8 for THIN, 0.9 for SYRUPY, and 0.55 for STICKY (an eighth of walking
speed). The numbers are starting values; the in-game check tunes them, and tuning changes only this
table.

The enum lives in common code and holds plain numbers plus a small `OverlayStyle` enum
(`NONE`, `TINT`, `TEXTURE`). It references no client classes.

Alternatives considered:
- One formula from real viscosity. The maintainer chose presets: viscosity spans seven orders of
  magnitude across these fluids, so a log curve still needs hand clamps at both ends.
- Per-fluid numbers. These recreate the hand-written table this change removes.

### Movement in `VEFluidType.move`
`VEFluidType` takes the preset and an overlay colour as constructor arguments. Every holder class
passes them, so each fluid's choice sits next to its other properties. `VEFluidType` overrides
`FluidType.move` and always returns `true`, so the vanilla water branch never runs for VE fluids.

- **Liquid presets.** The method follows the vanilla water branch of `LivingEntity.travel`:
  - `moveRelative` with `0.02 × acceleration`, scaled by the `SWIM_SPEED` attribute.
  - `move(MoverType.SELF, ...)`.
  - Multiply velocity by the preset's horizontal and vertical retention.
  - Apply a combined gravity and buoyancy term instead of vanilla's fixed `gravity / 16` sink:
    `gravity × 0.375 × buoyancyScale × (density × submerged / 1010 − 1)`. Here `submerged` is
    `getFluidTypeHeight / getBbHeight`, clamped to 1, and 1010 kg/m³ is a body slightly denser than
    water. This is Archimedes with a softened gravity. A floating entity settles at roughly the real
    submerged fraction (`1010 / density`, so about half in Sulfuric Acid and 7% in Mercury). The
    term is negative for liquids lighter than the body, so those sink faster than water. The 0.375
    makes a Diesel sink match vanilla's water sink speed. The result is clamped to ±0.1 per tick so
    Mercury cannot launch an entity.
  - Keep the vanilla step-up assist (`horizontalCollision && isFree(...)` sets Y to 0.3) in every
    preset. This is what guarantees the "can leave any pool" requirement.
  - THIN and WATERY honour Depth Strider (`WATER_MOVEMENT_EFFICIENCY`), Dolphin's Grace, and the
    sprint bonus, as water does. SYRUPY, STICKY, and MERCURY ignore all three: an enchantment for
    swimming in water does not help in tar.
- **GAS.** It uses `LivingEntity.handleRelativeFrictionAndCalculateMovement(input, friction)`, the
  public part of vanilla air travel. It then applies gravity and air drag like the vanilla ground and air
  branch, with an extra 0.98 horizontal retention.
- **Gas lift.** Lift applies when the gas at the entity's feet is a flowing (non-source) state and
  the block below holds the same gas. The entity gets an upward acceleration that slightly exceeds
  gravity, capped at a slow rise speed, so it drifts upward through the column. Sneaking withholds the
  lift, so the entity falls. A stagnant pocket of source gas, such as a
  worldgen reservoir, does not lift. This mirrors how only falling water drags entities down.
  - A geometric test is used rather than the `FALLING` flag, because `VEFlowingGasFluid` sets that flag
    from inverted downward-flow logic that is not reliable for "this column is rising".
- **Jumping in gas.** Any non-air fluid type at the entity's position makes vanilla call NeoForge's
  `jumpInFluid` (+0.04 per tick while jump is held) instead of a real jump. Fluid types have no hook
  for this, and `LivingEntity.jumping` is protected. The maintainer ruled out access transformers,
  mixins, reflection, and `canSwim(true)`, so the GAS branch infers the impulse from velocity:
  - **On the ground:** our own GAS branch leaves a fixed resting vertical speed of
    `−gravity × 0.98`. An entry speed 0.04 above that is the paddle impulse, and is replaced by
    `jumpFromGround()`. This needs no stored state.
  - **In mid-air:** the vertical speed the GAS branch set at the end of the previous tick is kept in
    a non-serialized `VEAttachments` attachment. An entry speed exactly 0.04 above it (within a small
    tolerance) is the impulse, and is removed.
  - Mobs that cannot float get +0.3 from `Mob.jumpInFluid` instead. That already behaves like a jump,
    so it is left alone.
  - **Sneaking:** vanilla only applies `sinkInFluid` when `canSwim` is true, which no VE fluid sets.
    The GAS branch reads `isShiftKeyDown()` and withholds the lift instead.
- **Sideways nudge.** The GAS branch adds the fluid's horizontal `getFlow` vector × 0.003 to the
  entity's motion. Vanilla fluid pushing (`canPushEntity`) is not used, because it also adds vertical
  speed between ticks, which would hide the jump impulse from the detection above. Every fluid keeps
  `canPushEntity(false)`, as today.

`makeStuckInBlock` is removed:
- `VEFlowingFluidBlock` loses its `entityInside` override.
- `CrudeOilFlowingFluidBlock` is deleted, and Crude Oil and Tree Sap build a plain
  `VEFlowingFluidBlock`.
- Registry ids and block state properties are unchanged, so saved worlds are unaffected.

### Dropped items in `VEFluidType.setItemMovement`
NeoForge skips item gravity whenever an item is inside a non-air fluid type and calls
`setItemMovement` instead.
- GAS applies the item's normal gravity and air drag, so items fall through gas as through air.
- Liquid presets apply the preset's retention and the same density-based drift. STICKY scales the
  drift down so items creep.
- This replaces the item branch of the deleted `makeStuckInBlock` calls.

### Fall cushioning
Liquids keep `fallDistanceModifier(0)`, which resets fall distance like water does. Gases change to
`fallDistanceModifier(1.0F)`, which leaves fall distance untouched, so falls through gas keep their
damage.

### Gas occupancy: `VEFlowingGasFluid.getHeight` returns a full block
`VEFlowingGasFluid` overrides `getHeight(FluidState, BlockGetter, BlockPos)` to return `1.0F`.
- Entity fluid heights, eye checks, and the camera fog checks all read this method, so gas counts as
  filling its block for movement, breathing, and fog.
- The renderer and the flow code read `getOwnHeight`, so the gas looks and spreads exactly as before.
- Fluid ray clips also use it, which makes gas sources slightly easier to target with a bucket.

Alternative: model the occupied region as the top `ownHeight` of the block. Entity fluid height is
computed in vanilla `Entity` code from the bottom of the block, so this would need a mixin. Gas diffuses
through its block anyway, so a full block is the simpler and more realistic choice.

### Breathing through `canDrownIn` and two event handlers in `VEGenericListener`
- `VEFluidType.canDrownIn(entity)` returns `canDrown && !entity.canBreatheUnderwater()`. Vanilla only
  consults `canBreatheUnderwater` for water. Without this, undead and other non-breathers (the
  `#minecraft:can_breathe_under_water` tag) would drown in modded fluids.
- Gas holder classes set `canDrown(true)`, except Oxygen and Compressed Air. A breathable gas is
  therefore a `GAS` preset with `canDrown` false, and no extra flag is needed.
- `LivingBreatheEvent` handler:
  - **Eye in an asphyxiant gas,** and the entity can drown in it and is not an invulnerable player: set
    `canBreathe` to false. This overrides the Water Breathing and Conduit Power exemption NeoForge
    applied earlier. The consume amount already includes Respiration, because NeoForge computes it
    through `decreaseAirSupply`.
  - **Eye in a breathable gas:** set `canBreathe` to true and the refill amount to vanilla's open-air
    refill of 4 per tick. NeoForge zeroes the refill for any non-air fluid. The 4 lives in a named
    constant, because vanilla's `increaseAirSupply` is protected.
- `LivingDrownEvent` handler, for an eye in a VE gas:
  - Set the bubble count to 0.
  - Read the event's damage amount, set it to 0, and hurt the entity with
    `voluminousenergy:asphyxiation` for that amount. This keeps vanilla's drowning cadence and amount.

Alternative: a per-tick air drain in a tick handler. This duplicates NeoForge's breathing logic and
fights other mods that use the same events.

### Damage types as hand-written data, keys in `VEDamageTypes`
- A new `util/VEDamageTypes` utility class, beside `VEDataComponents` and `VEAttachments`, holds
  `ResourceKey<DamageType>` constants `ASPHYXIATION` and `ACID`. It also has helpers that build a
  `DamageSource` from the level's registry access.
- `data/voluminousenergy/damage_type/asphyxiation.json`: message id
  `voluminousenergy.asphyxiation`, exhaustion 0.
- `data/voluminousenergy/damage_type/acid.json`: message id `voluminousenergy.acid`, exhaustion 0.1,
  effects `burning` for the sizzle sound.
- Non-replacing tags under `data/minecraft/tags/damage_type/`. Each type copies the vanilla tags of its
  closest vanilla equivalent:
  - **Asphyxiation, copying `drown`:** `bypasses_armor`, `bypasses_wolf_armor`, `no_impact`,
    `no_knockback`, `wither_immune_to`, `is_drowning`. The last one means the `drowningDamage` game rule
    also disables asphyxiation.
  - **Acid, copying `in_fire` minus the fire tags:** `no_knockback` and `panic_environmental_causes`.
    It is deliberately not in `is_fire` or `ignites_armor_stands`.
- This matches the repo's practice of hand-writing tags under `src/main/resources/data`, so no new
  datagen provider is needed.
- Death messages `death.attack.voluminousenergy.asphyxiation`, `.asphyxiation.player`,
  `death.attack.voluminousenergy.acid`, and `.acid.player` go into `en_us.json` under a new
  `"_comment": "Death Messages"` group at the bottom of the file.

### Acids: strength on the block, ignition only for fuming acids
- `AcidFlowingFluidBlock` gains an `acidStrength` constructor argument, and `entityInside` deals
  `VEDamageTypes.acid` for `Config.ACID_DAMAGE × acidStrength`. The strengths are named constants on
  the holder classes (`SulfuricAcid`, `WhiteFumingNitricAcid`, `RedFumingNitricAcid`) and are passed in
  where each builds its block.
- `FumingAcidFlowingFluidBlock` overrides `entityInside` to also set
  `max(remainingFireTicks, ACID_FIRE_DURATION)`. Using the max stops the acid from cutting short a
  longer burn the entity already has.
- Acid damage is no longer fire, so `ItemEntity.hurt` destroys fire-resistant items too. The spec
  records this as intended.
- `Config.ACID_FIRE_DURATION` keeps its key, its comment changes to say it is in ticks and applies to
  fuming acids, and its default becomes 80.

The strengths are fixed ratios rather than config values. They encode the chemistry ranking, and the one
knob players tune, the base damage, is already in `Config`.

### Mercury: `PoisonousFlowingFluidBlock`
- A new `fluids/flowingFluidBlocks/PoisonousFlowingFluidBlock` applies Poison I to living entities in
  `entityInside` on the server. It refreshes only when the remaining duration has fallen at least
  20 ticks below `Config.MERCURY_POISON_DURATION`. Re-adding the effect every tick would send an effect
  update packet every tick.
- Mercury builds this block and sets `canDrown(false)`.
- `MERCURY_POISON_DURATION` (`Mercury Poison Duration`, default 100, range 0 to `Integer.MAX_VALUE`)
  goes at the bottom of `setupGeneralSettings`.

### Screen effects in `VEFluidClientExtension`
`VEFluidClientExtension` receives the preset and the overlay colour from `VEFluidType` and implements:
- **`modifyFogColor`:** returns the overlay colour as RGB floats. STICKY darkens it further. Returning a
  colour while in `FogType.NONE` also sets the clear colour, so the sky reads as the haze colour too.
- **`modifyFogRender`:** sets `RenderSystem.setShaderFogStart` to 0 or slightly negative and
  `setShaderFogEnd` to the preset's fog end, with a spherical shape. It skips spectators, as vanilla does
  for lava.
- **`renderOverlay`:** calls a new client-only helper `client/renderers/fluid/FluidScreenOverlayRenderer`:
  - `TINT` draws a full-screen quad in the overlay colour at the preset's alpha.
  - `TEXTURE` draws vanilla `textures/misc/underwater.png` with the shader colour set to the overlay
    colour × light brightness, following `ScreenEffectRenderer.renderFluid`.
  - `NONE` draws nothing.

`ScreenEffectRenderer.renderFluid` only applies brightness, not colour, which is why the helper exists.
`renderOverlay` runs before the GUI is drawn, so the HUD and screens are not covered.

## Risks / Trade-offs

- [Preset numbers feel wrong in play] → All tuning lives in the `VEFluidPhysics` table, and the tasks
  include a named in-game pass per preset before archive.
- [Mining into a worldgen Natural Gas reservoir now suffocates players] → This is intended, and the
  asphyxiation timer is vanilla's 15 seconds. The reservoirs' source gas does not lift, so players can
  walk back out. Worth stating in the release notes.
- [Overriding Water Breathing in gas conflicts with another mod's breathing gear] → The handler only
  acts when the eye fluid is a VE gas, and runs at normal priority, so other mods can still change the
  result in the same event.
- [Players' movement is predicted on the client] → `FluidType.move` runs in common code on both sides
  with the same inputs, as vanilla water movement does, so no extra sync is needed.
- [Full-height gas changes fluid ray clips] → Only bucket targeting of gas sources is affected, and it
  becomes more forgiving.
- [Forward merge to 26.x] → The 26.x fog and screen-effect APIs differ, so `VEFluidClientExtension` and
  `FluidScreenOverlayRenderer` need a rewrite on merge. Movement, hazards, and data should merge as is.
  The follow-up is listed in the proposal.
- [Gametests with placed gas] → Gas blocks tick and spread. Tests place source states, which hold
  still, and read results within a few seconds, before spreading matters.

## Migration Plan

No world or save migration. Existing config files keep their `Acid Fire Duration` value; only new
configs get 80. Rollback is reverting the change; no data written by it persists in saves.
