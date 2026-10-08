## Voluminous Energy (`26.1-dev`)

NeoForge mod for Minecraft 26.1, mod id `voluminousenergy`, Java 25 toolchain, official Mojang names.
Resource ids are `Identifier`. JEI is compileOnly, gated by `VoluminousEnergy.JEI_LOADED`.

- Code, comment, and commit style: `AGENTS.md` at the repo root. It is authoritative; memories do not repeat it.
- Agent changes go through OpenSpec (`openspec/`, workflow in AGENTS.md). Behaviour specs live in
  `openspec/specs/`; read the relevant capability before changing an area.
- Source root `src/main/java/com/veteam/voluminousenergy/`. Entrypoint `VoluminousEnergy.java` registers every
  `VE_*_REGISTRY` on the mod event bus under comment banners.
- Machine wiring across `blocks`, `recipe/processor`, and `setup`: `mem:blocks_structure`.
- Build, datagen, run commands and done criteria: `mem:suggested_commands`.
- Cutting a release from this branch: `mem:release_process`.
