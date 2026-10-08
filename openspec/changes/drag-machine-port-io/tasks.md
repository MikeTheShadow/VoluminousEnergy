# Tasks

## 1. Retained backend

- [x] 1.1 Replace port Enabled with persistent optional assignment and a validated unassign payload; verify factory defaults, -1 persistence, and absence of legacy flag reads.
- [x] 1.2 Gate sided/unsided and held item/fluid handlers plus automatic transfers by assignment and role; remove standalone all-side bypass and verify detach/reassign access without changing GUI/recipe access.
- [x] 1.3 Make face Push/Pull flags independent in state, NBT, payload, and transfer selection; verify four states, targeted updates, role checks, and alternating bidirectional passes within existing limits.

## 2. Direct configuration UI

- [x] 2.1 Retire the old inspector/change into a recoverable temporary location; verify the old active change, widget, and texture are absent while the separate tank change remains.
- [x] 2.2 Make left-click select and right-click unassign while retaining drag thresholds and cancellation; verify reassigning unassigned ports and no carried-item movement.
- [x] 2.3 Fit checkbox sections and compact token rows at supported GUI sizes; verify concentrated and uneven assignments, null assignments, and exact hit regions.
- [x] 2.4 Show independent checkbox states and a Both summary while preserving assignment visuals; verify toggling one preserves the other and unsupported Pull remains hidden.
- [x] 2.5 Keep mouse/JEI regions and compact tooltips consistent with assignment-only access; verify no inventory click-through, unchanged machine/tank sizes, and localization.
- [x] 2.6 Update manual coverage for simultaneous Push/Pull, bidirectional alternation, and two-viewer requests; verify existing assignment/capability coverage remains intact.

## 3. Integration verification

- [x] 3.1 Run temporary independent-flag, NBT, payload, checkbox, and transfer-selection checks; verify all four combinations, role/assignment gates, and bidirectional alternation without a client.
- [x] 3.2 Run `./gradlew compileJava`; verify Java 21 compilation succeeds.
- [x] 3.3 Run strict OpenSpec validation and `git diff --check`, then review the diff; verify no exclusive checkbox logic, port Enabled flags, Serena edits, datagen changes, or unrelated files are included.
- [ ] 3.4 At the end of branch work, run the documented local and dedicated-server checks via `./gradlew runClient`; verify rendering, mouse/keyboard interaction, transfer quantities, JEI, synchronization, and persistence.

## Workflow follow-up

- The maintainer keeps the current IO implementation active; further UI iteration remains possible.
- Manual client/server verification remains deferred by the maintainer until the branch's remaining changes are ready.
- Archive after verification and maintainer review; do not archive the superseded inspector as complete.
- Port other Minecraft-version branches separately.
