## Commands and done criteria

Always `./gradlew`, never a global `gradle`.
- `compileJava` / `build`: compile check / jar.
- `runData`: regenerates `src/generated/resources` (committed output).
- `runClient`, `runServer`: dev game.
- `runGameTestServer`: exists, but no gametests are written yet.
- `--stop`: run first if `clean` fails with "Unable to delete build".

No unit tests, lint, or formatter. A task is done when:
1. `./gradlew compileJava` passes.
2. Registry, tag, loot, or model changes: `runData` run and its diff included.
3. Rendering, GUI, or machine-behaviour changes: report that an in-game `runClient` check is still needed.
