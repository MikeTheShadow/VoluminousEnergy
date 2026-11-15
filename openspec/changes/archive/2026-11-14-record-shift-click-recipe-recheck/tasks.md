## 1. Record

- [x] 1.1 Write the `machines/recipe-inputs` baseline requirement for the behaviour 5a648f85 fixed,
  with no code changes

## 2. Verification

- [x] 2.1 `recipe_shift_click_top_up_starts_recipe` covers both scenarios (added and run with 5a648f85)
- [ ] 2.2 In-game check via `./gradlew runClient`: with 10 Bauxite Dust in a powered Electrolyzer's
  input slot, shift-clicking 2 more from the inventory starts the machine (outstanding)
