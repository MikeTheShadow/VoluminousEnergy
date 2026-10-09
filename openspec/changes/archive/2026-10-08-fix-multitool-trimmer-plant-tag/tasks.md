## 1. Trimmer tag

- [x] 1.1 Append `minecraft:large_fern` and `minecraft:small_dripleaf` to the bottom of `values` in `src/main/resources/data/voluminousenergy/tags/block/mineable/trimmer.json`, and verify the file is valid JSON and the diff touches only those entries and the preceding comma

## 2. Verification

- [x] 2.1 Run `./gradlew compileJava` and verify it succeeds (no `runData` needed; the tag is hand-written and `src/generated/resources` is untouched)
- [ ] 2.2 In-game check via `./gradlew runClient`: with a multitool holding a trimmer bit, target Large Fern and Small Dripleaf and confirm each shows the trimmer model; confirm oak leaves still do and that a drill-only multitool shows no trimmer model on Large Fern (needs a human)

Task 2.2 was still outstanding when this change was archived.
