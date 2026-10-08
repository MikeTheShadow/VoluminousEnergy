# Design

## Context

See proposal.md. The uncommitted implementation already supplies `VEIOPort`, `PortConfigurationPacket`, `VEPortTransfer`, Config limits, and corrected capability face mapping. This revision moves automatic modes from ports to faces and changes common client interaction in `VEContainerScreen`; the factory-built machine architecture remains unchanged.

## Goals / Non-Goals

- One shared gesture implementation and one destination-tray overview for all configurable machines and tanks.
- Preserve committed machine artwork, 12-by-50 tank areas, slot indices, and Battery Box energy widgets.
- No filtering, multi-face assignments, energy automation, new registry ids, or legacy inspector fallback.

## Decisions

### Gesture state

Use client-only `VEIOInteraction` with an injected payload sender. A left press selects and records its source, but sends no setting until release. A latched movement threshold of more than four GUI pixels starts dragging. Valid drops send FACE; invalid drops cancel. Stationary left releases leave settings unchanged. Right-click sends UNASSIGN and retains selection, allowing the machine port to be dragged back onto a tray.

`VEContainerScreen` captures gestures before vanilla container clicks, consumes their releases/drags, and clears native drag/quick-craft state after a consumed release. Turning mode off or resizing cancels an unfinished gesture. Normal inventory/bucket controls remain available outside IO mode. This deliberately replaces vanilla item interaction on machine ports only while configuring.

### Destination tray layout

Replace `VEIOPatchbay` and its route planner with client-only `VEIODestinationTrays` and pure `VEIODestinationTrayLayout`. Arrange Up/Left/Front in the left bank and Back/Right/Down in the right bank. Size tray widths from available side margins and heights from assigned token rows, keeping all six faces and all current machine ports visible at supported GUI sizes. Recompute grouping only when faces change. Each tray has a face label, neighbor preview, and stable ordered tokens. Empty trays show an empty marker; empty ports still show item/tank placeholders. Preview lookup never loads chunks.

Expose the two tray banks as exact click/JEI exclusion regions instead of treating the enclosing widget rectangle as interactive. Bank gaps consume clicks but cannot receive drops. Keep native machine artwork visible without a grid or dimming overlay.

### Grouped destination overview

Number ports from one in their existing item-then-tank order, independent of destination and contents. Show that number on the machine port and on its tray token. Unassigned ports have grey machine badges and no tray token. Use neutral machine outlines, paired hover highlights, and green selected outlines. Port tooltips contain identity, contents, and destination or Unassigned without the gesture legend or automation instructions.

All assignments remain visible through tray grouping. Hovering a port or token emphasizes both representations; hovering a tray header emphasizes its assigned ports. Reuse `VEIOInteraction` for gestures initiated from either representation. Draw a floating token during dragging and highlight the candidate destination. No persistent wires or pathfinding remain. Alternatives were a matrix, which duplicates a full settings list, and letter-only tags, which require more destination matching.

### Face automation

Use common `VEFaceIO` with independent push/pull flags for each relative face, represented by passive/push/pull/both states in the existing NBT compound. Append Both without changing existing saved state values. `VETileEntity` owns the settings. Remove Enabled and automatic fields from ports. A nullable direction is the assignment; persist unassigned as -1. `VEPortTransfer` gates work by assignment and role. Preserve Config limits, transfer contracts, unloaded-neighbor checks, and orientation mapping.

Tray sections show independent checkbox rows for Push and Pull. Push is available on every face, including empty trays. Pull remains hidden without an assigned input/bidirectional role. Clicking either checkbox changes only that action; Both is shown in the face summary. A face retains its preferences when assignments change. Preserve the existing checkbox geometry and compact token layout.

The face payload carries a face, Push/Pull action, and explicit boolean value. Validate the action and enabling role on the server; disabling an action remains allowed even after its eligible port moves away. Apply only the requested flag so requests from different viewers do not overwrite the other checkbox. Keep the port payload and assignment invalidation unchanged. Both flags default off.

When both actions are enabled, input-only ports pull and output-only ports push each pass. Bidirectional tanks choose Push/Pull on alternating transfer-cycle parity, moving at most one configured limit per pass. Running both on one tank in a single pass would immediately pull back pushed fluid; always prioritizing one direction would starve the other. No alternate legacy setting path is added.

### Assignment gates external access

Remove the standalone-tank ignoreDirection flag and its factory call. `CapabilityMap` adds only assigned ports to their face, and exposes a filtered assigned-port item handler for null-face queries instead of the raw inventory. `MultiSlotWrapper` and `MultiFluidSlotWrapper` retain their face and reject ports that detach or move elsewhere, including held handler references. Unassigned contents and capacity are hidden externally; insert/extract/fill/drain/modification operations reject them. GUI and recipe access remain direct and unaffected. Factory-created slots retain their declared initial directions, including listened slots that previously defaulted disabled.

### Replacement cleanup

Remove `VEIOPanel`, `io_panel.png`, and the superseded active change directory to a recoverable temporary location. Do not archive the old change as verified. Rename inspector-specific screen/JEI methods to describe IO controls. Replace the inspector manual cases with drag/click/state/connection cases while retaining transfer, persistence, and server coverage.

## Risks / Trade-offs

- [Accidental changes on drag release]: Latch the threshold, cancel invalid drops, and keep stationary clicks selection-only.
- [Carried-item operations]: Capture configuration presses/releases before vanilla and cancel native drag state.
- [Compact side margins]: Size tray banks within supported viewports and test against upgrade slots, JEI, and Battery Box controls.
- [Third-party models and capabilities]: Use native render/transfer contracts and record modded cases in manual testing.
- [Colour ambiguity]: Retain face labels and checkbox labels, plus green selection and textual tooltips.
- [Concentrated assignments]: Let the occupied tray grow while empty trays retain compact headers; verify all current ports concentrated on every face at compact GUI sizes.
- [Empty or identical contents]: Stable numbers and type placeholders identify every port even when icons cannot distinguish it.

## Migration Plan

Optional assignment introduces a -1 direction value and removes experimental Enabled/automatic flags. There is no migration or legacy read path. Existing declared assignments remain assigned regardless of older Enabled flags. Keep the earlier breaking inventory contract and fresh-world tests. Manual local/dedicated verification remains deferred at the maintainer's request. The prior tank rework stays independently tracked; cross-version ports are follow-up work.
