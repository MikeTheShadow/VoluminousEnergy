## 1. Presets and fluid type data

- [x] 1.1 Add `fluids/VEFluidPhysics` with the six presets and their movement and visibility numbers from design.md, plus the nested `OverlayStyle` enum; verify with `./gradlew compileJava`
- [x] 1.2 Give `VEFluidType` constructor arguments for the preset and the overlay colour, with getters, and pass both through `getFluidClientExtension`; verify with `./gradlew compileJava`
- [x] 1.3 Update the seven gas holder classes (Hydrogen, Natural Gas, Ammonia, Nitrogen, Oxygen, Compressed Air, Light Fuel) with the GAS preset, their overlay colour, the spec's density and viscosity, `fallDistanceModifier(1.0F)`, and `canDrown(true)` for all except Oxygen and Compressed Air; verify with `./gradlew compileJava`
- [x] 1.4 Update the sixteen liquid holder classes with their preset, overlay colour, density, and viscosity from the specs, and set Mercury to `canDrown(false)`; verify with `./gradlew compileJava` and a grep showing no `density(1)` or `viscosity(1)` left in `fluids/`

## 2. Movement

- [x] 2.1 Override `VEFluidType.move` with the liquid branch (drag, acceleration, gravity, submersion-scaled buoyancy, step-up assist, Depth Strider and Dolphin's Grace for THIN and WATERY only) and the GAS branch (air travel plus the flowing-column lift); verify with `./gradlew compileJava`
- [x] 2.1a Add the non-serialized gas vertical speed attachment to `VEAttachments`, and the GAS jump handling (ground impulse to `jumpFromGround`, mid-air impulse removed, sneak withholds lift); verify with `./gradlew compileJava`
- [x] 2.2 Override `VEFluidType.setItemMovement` so items fall through gases and drift by density and preset in liquids; verify with `./gradlew compileJava`
- [x] 2.3 Remove the `entityInside` override from `VEFlowingFluidBlock`, delete `CrudeOilFlowingFluidBlock`, and switch `CrudeOil` and `TreeSap` to `VEFlowingFluidBlock`; verify with `./gradlew compileJava` and no remaining `makeStuckInBlock` call under `fluids/`
- [x] 2.4 Override `getHeight(FluidState, BlockGetter, BlockPos)` in `VEFlowingGasFluid` to return a full block; verify with `./gradlew compileJava`

## 3. Damage types and breathing

- [x] 3.1 Add `util/VEDamageTypes` with the `ASPHYXIATION` and `ACID` keys and `DamageSource` helpers, the two `data/voluminousenergy/damage_type/*.json` files, and the non-replacing `data/minecraft/tags/damage_type/*.json` memberships from design.md; verify the server log shows no datapack errors on `./gradlew runGameTestServer`
- [x] 3.2 Add the four death message keys to `en_us.json` under a new `Death Messages` `_comment` group at the bottom of the file; verify the file still parses as JSON
- [x] 3.3 Override `VEFluidType.canDrownIn` to exempt entities that can breathe underwater; verify with `./gradlew compileJava`
- [x] 3.4 Add the `LivingBreatheEvent` handler to `VEGenericListener` (asphyxiant gas overrides Water Breathing and Conduit Power, breathable gas refills at the open-air rate, invulnerable players exempt); verify with `./gradlew compileJava`
- [x] 3.5 Add the `LivingDrownEvent` handler to `VEGenericListener` that removes bubbles and swaps the drowning damage for asphyxiation damage of the same amount in VE gases; verify with `./gradlew compileJava`

## 4. Acids and Mercury

- [x] 4.1 Add the acid strength constants to `SulfuricAcid`, `WhiteFumingNitricAcid`, and `RedFumingNitricAcid`, give `AcidFlowingFluidBlock` an `acidStrength` argument, deal `VEDamageTypes` acid damage scaled from `Config.ACID_DAMAGE`, and stop it setting fire; verify with `./gradlew compileJava`
- [x] 4.2 Make `FumingAcidFlowingFluidBlock` set fire to `max(remaining, Config.ACID_FIRE_DURATION)`, and update the `Acid Fire Duration` comment and default to 80 ticks in `Config`; verify with `./gradlew compileJava`
- [x] 4.3 Add `Config.MERCURY_POISON_DURATION` at the bottom of `setupGeneralSettings`, add `PoisonousFlowingFluidBlock` with the throttled Poison refresh, and switch `Mercury` to it; verify with `./gradlew compileJava`

## 5. Screen overlays

- [x] 5.1 Add the client-only `client/renderers/fluid/FluidScreenOverlayRenderer` with the flat tint quad and the colour-tinted underwater texture; verify with `./gradlew compileJava`
- [x] 5.2 Implement `modifyFogColor`, `modifyFogRender` (skipping spectators), and `renderOverlay` in `VEFluidClientExtension` from the preset and overlay colour; verify with `./gradlew compileJava`

## 6. Gametests

- [x] 6.1 Add `FluidTests` registered from `VEGameTests.all()` with required tests: every fluid type reports the spec's density and viscosity; a pig under Fire Resistance in Sulfuric Acid takes 3 acid damage and is not on fire; Red Fuming Nitric Acid deals 4.5 and ignites; a pig in Mercury is poisoned and keeps full air; verify with `./gradlew runGameTestServer`
- [x] 6.2 Add required breathing tests: a pig with Water Breathing and its head in Nitrogen loses air; a pig with half air and its head in Oxygen gains air; a zombie with its head in Nitrogen keeps full health for 200 ticks; verify with `./gradlew runGameTestServer`
- [x] 6.3 Add required item tests: an iron ingot dropped into Mercury ends above its start height, one dropped into Gasoline sinks, and one dropped into the top of a Hydrogen column falls; verify with `./gradlew runGameTestServer`

## 7. Verification

- [x] 7.1 `./gradlew compileJava` succeeds
- [x] 7.2 `./gradlew runGameTestServer` passes with all new tests required
- [x] 7.3 `./gradlew runData` produces no unexpected diff in `src/generated/resources`
- [ ] 7.4 In-game check via `./gradlew runClient`, movement:
  - wade through Crude Oil (about a third of walking speed, smooth) and climb out of a 1-deep pool
  - swim in Diesel and in Sulfuric Acid (Sulfuric clearly slower; floats you)
  - try to dive in Mercury (cannot stay under)
  - sink in Gasoline
  - walk through a Nitrogen room (near-normal speed)
  - rise in a Natural Gas column from a bucket, and descend while sneaking
  - fall 20 blocks through Hydrogen (full fall damage)
  - tune the `VEFluidPhysics` numbers until each matches the spec
- [ ] 7.5 In-game check via `./gradlew runClient`, overlays:
  - purple fog in Natural Gas, and deep blue fog in Hydrogen next to cyan in Ammonia
  - even amber tint in Diesel with no texture
  - grey tinted water texture in Sulfuric Acid
  - near-black view in Crude Oil at noon
  - no darkening as a spectator
  - HUD and inventory drawn over the effect
- [ ] 7.6 In-game check via `./gradlew runClient`, hazards:
  - asphyxiation in a 2-tall room with Nitrogen against the ceiling, with no bubbles and "asphyxiated" in chat
  - Water Breathing does not help in Natural Gas
  - Oxygen refills air
  - Fire Resistance does not stop Sulfuric Acid
  - White Fuming Nitric Acid sets you alight for about 4 seconds
  - Mercury poisons you
  - a netherite ingot dissolves in acid
- [ ] 7.7 Start `./gradlew runServer` (dedicated) with the change and confirm it reaches "Done" with no client-class errors
