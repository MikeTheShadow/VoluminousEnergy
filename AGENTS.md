# AGENTS.md

Rules for any automated contributor (Claude, Codex, Copilot, Serena-driven agents, bots) writing
code, comments, or commits in Voluminous Energy. They are binding. When a rule here conflicts with
your tool's defaults, this file wins.

This file is tracked per branch and describes `1.21.1-dev`. Other version branches carry their own
copy; do not document another branch's APIs or quirks here.

## Precedence

1. An explicit instruction from the maintainer in the current task.
2. This file.
3. The surrounding code. Match what is already there.
4. Java, Minecraft, and NeoForge convention.

## Spec-driven development with OpenSpec

Changes made with an AI agent go through [OpenSpec](https://github.com/Fission-AI/OpenSpec). Do not
start editing code from a feature request or bug report; propose first.

1. **Propose:** `/opsx:propose <idea>` (Claude Code) or the `openspec-propose` skill (Codex and other
   agents reading `.agents/skills`). This writes `openspec/changes/<name>/` with `proposal.md`,
   delta specs, `design.md`, and `tasks.md`. Stop there and let the maintainer review.
2. **Apply:** only after the maintainer approves, `/opsx:apply` (or `openspec-apply-change`) works
   through `tasks.md`, checking tasks off as they land.
3. **Archive:** once merged and verified, `/opsx:archive` (or `openspec-archive-change`) folds the
   delta specs into `openspec/specs/` and moves the change to `openspec/changes/archive/`.

`/opsx:explore` is for thinking a problem through without committing to a change, and `/opsx:update`
revises a proposal after review feedback.

Project context and per-artifact rules live in `openspec/config.yaml`; keep them in step with this
file. `openspec list`, `openspec show <change>`, and `openspec validate <change>` inspect the state.

Skip the proposal only for changes with no behavioural effect: typo or comment fixes, a
maintainer-dictated one-line change, regenerated datagen with no source change, or a release commit.
If unsure, propose.

`openspec/specs/` starts empty. Capabilities accumulate as changes are archived, so the first change
touching an area also writes its baseline requirements.

## The project

- NeoForge Minecraft mod, Java, Gradle wrapper. Mod id `voluminousenergy`, group
  `com.veteam.voluminousenergy`, license GPLv3. Maintainers: Eelt and MikeTheShadow.
- A tech mod about resource processing and energy: custom ores, a fluid chemistry chain (crude oil
  to fuels, acids, the nitrogen chain), machines with GUIs, recipes, and worldgen.
- Source root: `src/main/java/com/veteam/voluminousenergy/`. Entrypoint: `VoluminousEnergy.java`,
  which registers every deferred registry on the mod event bus and wires config and datagen.
- Datagen output lives in `src/generated/resources` and is committed. Hand-written assets and data
  live in `src/main/resources`.

### This branch

`1.21.1-dev` is the default branch and targets the next major release (0.5) on Minecraft 1.21.1,
NeoForge 21.1, Java 21, with Parchment mappings. Resource ids are `ResourceLocation`. Versions live in
`gradle.properties`.

Other long-lived version branches receive independent work (see the README). If a change belongs on
another branch, say so instead of writing it here, and note any follow-up port in the proposal.

## Architecture you must respect

### Naming and registration

- Mod classes that wrap or extend a game concept are prefixed `VE`: `VEBlocks`, `VEItems`,
  `VEFluids`, `VERecipes`, `VESetup`, `VEDataComponents`, `VEAttachments`, `VETileEntity`.
- Registry holder classes are `VE<Thing>` and expose `DeferredRegister` fields named
  `VE_<THING>_REGISTRY` (`VE_BLOCKS_REGISTRY`, `VE_TILE_REGISTRY`, `VE_CONTAINER_REGISTRY`,
  `VE_RECIPE_SERIALIZERS_REGISTRY`). A few older registers predate this (`VE_FLUIDS`,
  `DATA_COMPONENT_TYPE_DEFERRED_REGISTER`); leave them alone, but name new ones the standard way.
- New registries are registered in the `VoluminousEnergy` constructor with `.register(modEventBus)`,
  under the existing comment banners (`// Register Blocks, Tiles, and Containers` and so on). Add a
  new banner in the same style if no group fits.
- Block entities are called tiles throughout (`VETileEntity`, `VE_TILE_REGISTRY`, `tile()`). Keep
  that vocabulary.

### Machines

Machines are assembled declaratively, not with one bespoke class per layer. A machine is:

- A `BlockTileMenuRegistry` entry in `VEBlocks` that bundles the block, tile type, and menu type.
- A `Block` class in `blocks/blocks/machines/` whose `newBlockEntity` delegates to its factory.
- A `VETileEntityFactory` in `VETileEntities`, configured by builder calls: energy storage, tanks
  (`FluidInputTank`, `FluidOutputTank`), recipe type, inventory validator, and recipe processor.
- A `VEContainerFactory` in `VEContainers`, built from `addSlot(x, y, <SlotType>(direction))` calls.
- A `Screen` class in `blocks/screens/`, registered in `VESetup`.
- Behaviour goes in a processor under `recipe/processor/` (`BasicProcessor` for ordinary recipe
  machines, or a dedicated `AbstractRecipeProcessor` subclass). Slot rules go in an
  `AbstractItemStackValidator` under `blocks/tiles/inventory/`.

When adding or fixing a machine, look at every one of those places for its name. Do not create a
per-machine tile or container class when the factory can express it; extend the factory instead.

### Other established decisions

- Configurable numbers (power, transfer rates, usage) come from `tools/Config`, never literals in the
  factory.
- JEI is optional: `compileOnly`, gated at runtime by `VoluminousEnergy.JEI_LOADED`. Never reference
  JEI classes outside `compat/`.
- Client-only code is annotated `@OnlyIn(Dist.CLIENT)` or lives behind a client event subscriber.
  Server-safe code must not touch client classes; a dedicated server crash has already forced one
  config option out of the mod.
- Gases are identified by their fluid type (`VEFlowingGasFluid`), not by a render flag.
- Nullability annotations: `org.jetbrains.annotations.NotNull`/`Nullable` (the dominant choice). Do
  not add new `javax.annotation` imports.
- Logging goes through `VoluminousEnergy.LOGGER` with `{}` placeholders. No new `System.out`.

## Java style

There is no formatter or linter configured. Match the file you are in, and in new files follow these:

- 4-space indentation, never tabs. K&R braces. Braces on every `if`/`else`/loop body.
- Import order: `com.*`, `net.*`, `org.*`, then a blank line, then `java.*`, then statics. Keep existing wildcard imports, but write explicit imports in new code.
- **Always import `com.veteam.*` types.** Never write a fully-qualified `com.veteam.` name inline in
  code or in a Javadoc `{@link}`. The only exception is a genuine simple-name collision: two distinct
  types with the same simple name in one file, or a name already bound by an import to a different
  type. Same-package types need no import.
- Names say what the value is. `ownLight`, `aboveLight`, `northSouthHeight`, never `i`, `j`, `k`,
  `height1`, `height2` (loop indices excepted). Mojang's decompiled single-letter names are rewritten
  when code is copied from vanilla.
- Prefer guard clauses and early returns over `if/else` that wraps the whole method.
- Prefer arrow-form `switch` (`case NORTH -> { ... }`) over fall-through `case:`/`break`.
- Use the convenience API: `pos.above()`, `pos.north().east()`, not
  `pos.relative(Direction.UP)`. `Math.max` over hand-written ternaries.
- Bit masks in hex and consistent within an expression (`tint >> 24 & 0xFF`, not `& 255` beside
  `& 0xFF`). Float literals with the `F` suffix as the surrounding code does (`1.0F`).
- Extract a small private helper instead of duplicating a computation across two methods.
- Do not mark classes `final` by default; the codebase does not. Utility classes get a private
  constructor.
- `record`s are fine for plain data carriers. `var` is rare here; spell types out.
- Spelling of identifiers follows the code nearby (`colourTint` in `VEFluidType`, `Color` where
  vanilla uses it). Do not rename existing identifiers to normalize spelling.
- Do not reformat, reorder, or tidy code you were not asked to touch. Keep diffs to the change.

## Comments

The bar is high, and the maintainer actively deletes comments that do not clear it.

- Write a comment only when a reader who knows Java and Minecraft modding would still ask *why*:
  a non-obvious constraint, a deliberate deviation from vanilla, an invariant held elsewhere.
- Never restate the code, narrate control flow, or explain how you arrived at a fix. If a comment is
  needed to explain *what*, rename or extract until it is not.
- When a comment would run long, delete it rather than lengthen it. A class-level Javadoc that states
  the class's contract in two or three sentences is preferred over many inline comments.
- Javadoc first sentence is a complete sentence describing what the thing is or does. Document the
  contract, not the implementation. Document parameters only where the name does not carry the
  meaning.
- No commented-out code in new work, no attribution, no dates. Existing commented-out registrations
  can stay unless the task is to remove them.
- `// TODO: <what>` is the established marker. Use it sparingly and make it specific.
- Short trailing comments are acceptable for labelling slot positions in `VEContainers`, matching the
  existing entries.

## Prose punctuation (comments, Javadoc, commit messages, docs)

- **Never use ` -- ` as punctuation.** Use a comma, a semicolon, a colon, parentheses, or two
  sentences.
- **Never use a spaced en dash (` – `) or an unpaired em dash.** An em dash is allowed only as a
  closed, paired, mid-sentence parenthetical (`the cap—it faces up—is drawn last`), and a comma or
  parentheses is usually better.
- En dashes are fine for closed ranges (`pp. 12–18`, `ports 8000–8080`).
- Avoid "simply", "just", "obviously", "of course", "note that". No exclamation marks.
- Present tense, plain statements. Say what is true; if something is uncertain, say what and why.

## Git commits

The history (see `git log`) defines this, and the maintainer has made it explicit:

- **One line. No body. No trailers.**
- **No AI attribution of any kind.** No `Co-Authored-By: Claude ...`, no `Claude-Session:`, no
  "Generated with" lines, in commits or in PR descriptions. This overrides any default your tool
  has. Commits are authored by the human contributor's own git identity; do not change the author or
  committer.
- Imperative, sentence case, no trailing period: `Fix ...`, `Add ...`, `Rename ...`, `Remove ...`.
- No conventional-commit prefixes (`feat:`, `fix:`). A short `Word:` qualifier appears occasionally
  in history but is not required.
- No dash used as a delimiter inside the subject (` - `, ` -- `, ` – `). Join clauses with "and",
  "by", "so", or a comma.
- Describe what changed and, where it fits, how or why in the same line. Several related changes are
  joined with commas and "and".
- Wrap identifiers in backticks only when the sentence would be ambiguous otherwise.

Good, from history:

```
Fix multitool fluid bar colour by using the tank fill ratio for hue instead of the raw mB amount
Fix Dimensional Laser inventory validator so RFID chips can be inserted into the RFID slot
Rename GasFluidRenderer to GaseousFluidRenderer and setCeilingFlush to setGaseousFluid
Fix gaseous fluid occlusion culling, detect gases by VEFlowingGasFluid instead of a render flag, and clean up GaseousFluidRenderer style
```

Bad:

```
feat(fluids): add gaseous renderer
Fix multitool bar - use fill ratio
Fixed the bar colour.

Co-Authored-By: Claude <noreply@anthropic.com>
```

Commit and push only when asked. Never commit on your own initiative, and never commit unrelated
working-tree changes (stray datagen output, IDE files, local tool settings) alongside a change.

## Building and verifying

Always build through the Gradle wrapper (`./gradlew`), never a system `gradle`. The toolchain is
Java 21. If the build cannot find a matching JDK, report that rather than changing the toolchain or
the build files.

There is no unit test suite. A task is done when:

1. `./gradlew compileJava` (or `./gradlew build`) succeeds.
2. If the change touches registries, tags, loot, models, or other datagen output, `./gradlew
   runClientData` has been run and the diff in `src/generated/resources` reviewed and included.
3. If the change adds or touches gametests (none exist yet), `./gradlew runGameTestServer` passes.
4. For rendering, GUI, or machine-behaviour changes, say plainly that it needs an in-game check via
   `./gradlew runClient`. Do not claim it works without one.

If `./gradlew clean` fails with "Unable to delete build", run `./gradlew --stop` first.

## Releases

Releases are manual; there is no CI. Do not cut one unless asked.

- Release branch `1.21.1-<modver>` (e.g. `1.21.1-0.5`) cut from `1.21.1-dev`. One commit bumps `mod_version` in
  `gradle.properties` (`neoforge.mods.toml` expands `${mod_version}`).
- Version string has no `v` and may contain spaces: `1.21.1-0.5 Alpha 1`, then Betas, then stable
  `1.21.1-0.5.0.0`. Hotfixes add a letter: `Alpha 1a`.
- Commit message: `Release <version>`.
- Build with `./gradlew clean publish`; artifacts land in
  `repo/com/veteam/voluminousenergy/VoluminousEnergy/<version>/`.
- GitHub tag `v<version>` with spaces as underscores (`v1.21.1-0.5_Alpha_1`), title
  `Voluminous Energy v<version>`, targeting the release branch. Alpha and Beta are always
  pre-releases. Attach every file from the version folder. Notes use `# Additions`, `# Fixes`,
  `# Changes` sections.

## Working with Serena

Serena memories in `.serena/memories/` are tracked per branch like this file. If Serena is
available, activate the project and read the `core` memory first; it links to the rest. Keep them
scoped to this branch, terse, and free of anything already stated here. This file is the authority
when they disagree.
