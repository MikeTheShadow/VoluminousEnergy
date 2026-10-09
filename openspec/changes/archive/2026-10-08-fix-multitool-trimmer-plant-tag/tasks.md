## 1. Trimmer tag

- [x] 1.1 Append `minecraft:tall_dry_grass`, `minecraft:short_dry_grass`, `minecraft:bush`, `minecraft:large_fern`, `minecraft:pale_hanging_moss`, `minecraft:small_dripleaf`, `minecraft:firefly_bush`, and `minecraft:leaf_litter` to the bottom of `values` in `src/main/resources/data/voluminousenergy/tags/block/mineable/trimmer.json`, and verify the file is valid JSON and the diff touches only those entries and the preceding comma

## 2. Verification

- [x] 2.1 Run `./gradlew compileJava` and verify it succeeds (no datagen run needed; the tag is hand-written and `src/generated/resources` is untouched)
- [x] 2.2 In-game check via `./gradlew runClient`: with a multitool holding a trimmer bit, target Tall Dry Grass, Short Dry Grass, Bush, Large Fern, Pale Hanging Moss, Small Dripleaf, Firefly Bush, and Leaf Litter and confirm each shows the trimmer model; confirm oak leaves still do, that an Open Eyeblossom and a Pale Moss Carpet do not, and that a drill-only multitool shows no trimmer model on Tall Dry Grass (needs a human)
