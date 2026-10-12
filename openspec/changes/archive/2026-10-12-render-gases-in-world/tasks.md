## 1. Gas block properties

- [x] 1.1 Remove `.air()` from `stdProp` in `Ammonia`, `Hydrogen`, `LightFuel`, `NaturalGas`, `Nitrogen`, and `Oxygen` under `fluids/`, keeping `replaceable()` and the rest of each chain unchanged; verify `git grep -n "\.air()" -- src/main/java` returns nothing
- [x] 1.2 Delete the Javadoc on `VEFluidReservoirFeature.canFill` that gives "natural gas counts as air" as its reason, leaving the method body unchanged; verify the diff touches only that comment
- [x] 1.3 Add `.liquid().sound(SoundType.EMPTY)` to the same six `stdProp` chains, importing `SoundType`; verify each of the six files has exactly one `stdProp` ending in `.liquid().sound(SoundType.EMPTY);`

## 2. Verification

- [x] 2.1 Run `./gradlew compileJava` and confirm it succeeds
- [x] 2.2 Run `./gradlew runClientData` and confirm `src/generated/resources` has no diff (block properties are not datagen output)
- [x] 2.3 In-game check via `./gradlew runClient` (maintainer). Check that:
  - each of the six gas buckets, emptied onto stone in daylight and in a dark cave, shows a visible rising column in that gas's colour, with sensible lighting and no missing or inside-out faces;
  - a natural gas petroleum cave and a natural gas reservoir in a new world show purple gas (`/locate` cannot find them; search in spectator mode below y 20);
  - an existing 26.1 world opened after the change shows its placed and generated gas;
  - placing stone into Natural Gas puts the stone there, emptying a water bucket into Hydrogen puts water there, sand falls through an Oxygen column, and emptying a Nitrogen Bucket into a Nitrogen column plays no break sound or particles;
  - an Oxygen column emptied in open sky rises well past the top of its starting 16-block section;
  - standing in gas does not drain air or push the player, and gas still rises and spreads up to 4 blocks sideways;
  - bone meal used on a grass block scatters grass and flowers only into open air, never into positions holding Nitrogen, the same as with water.

  Fix small `GaseousFluidRenderer` problems found here in this change; record anything larger as a follow-up change.
