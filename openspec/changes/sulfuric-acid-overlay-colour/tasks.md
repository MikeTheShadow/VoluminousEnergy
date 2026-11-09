## 1. Overlay colour

- [x] 1.1 In `fluids/SulfuricAcid.java`, change the overlay colour passed to `VEFluidType` from `0xB1B1B1` to `0xDDDE9A`

## 2. Verification

- [x] 2.1 `./gradlew compileJava` succeeds on `26.2-dev`
- [x] 2.2 In-game check via `./gradlew runClient` on 26.2: with your head in Sulfuric Acid in daylight, the fog and the underwater texture are pale yellow-beige, matching the placed fluid, and darken in an unlit cave
- [ ] 2.3 Merge into `26.3-dev`, `./gradlew compileJava` succeeds, and the same in-game check passes on 26.3
