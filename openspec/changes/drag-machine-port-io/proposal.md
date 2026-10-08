# Proposal

## Why

Players see each slot and tank grouped under its destination face, so crowded machines remain readable without tracing overlapping wires. Direct dragging and clicking configure item and fluid IO in one view.

## What Changes

- Replace the circuit-board patchbay with six destination trays beside the machine. Each tray shows its face, neighboring block preview, and every assigned port as a numbered item/fluid token. Empty ports retain identifiable placeholders; air uses its face letter and unloaded neighbors remain distinct.
- Drag a slot/tank to a face to assign it. Left-click selects; right-click removes the assignment. All six faces are available without enable switches. A port with no assignment has no external item/fluid access, including unsided item queries and previously obtained handlers.
- Each tray contains independent Push and Pull checkboxes. Push is available even while empty; Pull appears when assigned roles support it. Either, both, or neither can be selected. Inputs pull and outputs push when both are active; bidirectional tanks alternate direction between transfer passes.
- Drag either a machine port or its tray token to another tray. Matching stable numbers identify both representations. Hover highlights both and selection stays green. Unassigned ports remain visible on the machine with grey badges and can be dragged back onto a face. Port tooltips contain identity, contents, and assigned face or Unassigned only. Mode controls and their help appear on trays.
- Retain bounded item/fluid transfers, Config settings, server validation, and relative-face rotation. Move automatic mode ownership and persistence from individual ports to the six machine-relative faces; reassigned ports follow their new face's mode.
- Keep machine window sizes, inventory indices, recipes, 12-by-50 tanks, ordinary bucket interaction outside IO mode, and Battery Box energy controls.
- Remove the inspector widget, its texture, and its active change artifacts. Keep a recoverable temporary copy rather than archiving an unverified change as complete.
- **BREAKING**: Remove port Enabled and automatic flags and their payload settings. Save an optional assignment per port and one mode per face, default passive. Standalone tanks respect their assigned face. Experimental Enabled/automatic flags are not migrated or read; no legacy fallback is added. Registry ids, components, dependencies, and transfer Config settings remain unchanged. Port other Minecraft branches separately.
- Update local and dedicated-server manual checks, still deferred until the branch's remaining work is ready.

## Capabilities

### New Capabilities

- `machines/port-io`: Optional face assignment, automatic item/fluid transfers, visible destinations, persistence, and server authority.

### Modified Capabilities

None. No main capabilities are archived yet. The separate tank-interaction change remains untouched.

## Impact

- Shared machine screen, client IO gesture/tray rendering, optional JEI exclusions, and localization.
- Existing port objects, server payload, automatic transfer utility, capability wrappers, and Config remain in scope as the retained backend.
- Replace inspector-specific artifacts and revise the machine IO manual checklist.
