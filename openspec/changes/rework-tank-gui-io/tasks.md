# Tasks

## 1. Breaking inventory layout

- [x] 1.1 Define the compact machine inventories as a breaking save change with no legacy remapping, removed-item recovery, or compatibility path; verify the proposal, design, and requirements consistently require a fresh test world.
- [x] 1.2 Remove the legacy inventory metadata, remapping, and pending-item recovery implementation; verify repository searches find no remaining compatibility code.

## 2. Server-authoritative tank transfer

- [x] 2.1 Implement a selected-tank transfer helper using `Capabilities.FluidHandler.ITEM`, simulation, tank-role checks, existing recipe/custom fluid validation, and one-item container copies; verify failed simulations leave both sides unchanged.
- [x] 2.2 Implement carried-stack result handling for single and stacked containers, including inventory insertion and overflow drop, and verify vanilla empty-bucket stacks and variable-capacity Mekanism tanks produce the correct resulting items.
- [x] 2.3 Add and register a play-to-server tank interaction payload with `TAKE` and `PUT` actions; verify its handler rejects invalid menus, distance/state, actions, tank indices, and non-capability items before mutation.
- [ ] 2.4 Mark successful transfers dirty, invalidate recipe selection where applicable, and synchronize tile/menu state; verify two connected clients see the same tank amount and carried-item result after one client transfers fluid.

## 3. Shared screen interaction and tooltips

- [x] 3.1 Add declarative tank hitboxes and shared left/right click handling to `VEContainerScreen`, consuming only clicks inside registered tanks, and verify ordinary item-slot clicking and shift-clicking are unchanged.
- [x] 3.2 Register tank rectangles in `AirCompressorScreen`, `AqueoulizerScreen`, `BlastFurnaceScreen`, `CentrifugalAgitatorScreen`, `CombustionGeneratorScreen`, `DimensionalLaserScreen`, and `DistillationUnitScreen`; verify each visible tank selects its matching relational tank index.
- [x] 3.3 Register tank rectangles in `FluidElectrolyzerScreen`, `FluidMixerScreen`, `GasFiredFurnaceScreen`, `HydroponicIncubatorScreen`, `PumpScreen`, `SawmillScreen`, `ToolingStationScreen`, and `tank/TankScreen`; verify each visible tank selects its matching relational tank index.
- [x] 3.4 Centralize tank tooltip construction in `TextUtil`/`VEContainerScreen`, remove per-screen tank-tooltip duplication, and add localized English take/put guidance; verify input/bidirectional tooltips show both actions while output-only tooltips show take only.
- [x] 3.5 Remove the interactive bucket slots before the artwork redesign; verify only registered tanks accept fluid-container clicks.

## 4. Remove bucket slots and repair slot consumers

- [x] 4.1 Remove all `BucketInputSlot`/`BucketOutputSlot` declarations from the fourteen tank-bearing machine factories and six standalone tank factories in `VEContainers`; verify retained screen coordinates plus new inventory/upgrade indices match the design table.
- [x] 4.2 Update `AqueoulizerRecipe` (`4→0`), `IndustrialBlastingRecipe` (`2..4→0..2`), `DistillationRecipe` (`6→0`), and `HydroponicIncubatorRecipe` (`2..6→0..4`); verify each parser's item positions correspond to its revised container declaration while fluid tank positions remain unchanged.
- [x] 4.3 Audit `SawmillRecipe`, `SawmillParser`, all pure-fluid recipe classes, and shared `BasicParser`/`RNGBasicParser` code; verify their item or relational-tank mappings require no unintended shift and still address declared outputs.
- [x] 4.4 Update `GasFiredFurnaceProcessor` to item slots `0/1` and the factory-derived upgrade id, and update `DimensionalLaserRecipeProcessor` to RFID slot `0`; verify both processors consume/read and output to the revised slots.
- [x] 4.5 Update `ToolingStationProcessor`, `ToolingStationInventoryValidator`, and `ToolingStationSlotWithIOListening` from old slots `2..6` to `0..4`; verify multitool insertion, bit load/unload, validation, and fluid-fuel behavior use the intended stacks.
- [x] 4.6 Remove obsolete bucket-slot records, fluid slot types, relationship fields, `VETileEntity` tick-driven bucket processing, dirty flags/calls, and bucket-specific `VEItemStackHandler`/`VEContainerFactory` branches; verify repository searches find no executable references to the removed model.
- [x] 4.7 Audit `VEContainer.quickMoveStack`, `CapabilityMap`, `MultiSlotWrapper`, energy upgrade ids, and every numeric `getStackInSlot`/`insertItem`/`extractItem` call; verify all retained inventory indices are valid for their factories and zero-item-slot tank machines open without errors.

## 5. Integration verification

- [x] 5.1 Run `./gradlew compileJava` with Java 21 and resolve all compilation errors; verify the task completes successfully.
- [x] 5.2 Run `./gradlew runClient` and verify left-click extraction plus right-click insertion with vanilla buckets on input, output, bidirectional, full, empty, incompatible, and multi-tank cases across both a machine GUI and standalone `TankScreen`. Maintainer verified.
- [x] 5.3 In the same client run, verify capability-backed Mekanism tanks support full and partial transfers and stacked-container result handling without duplication or fluid loss. Maintainer verified.
- [ ] 5.4 Process recipes in the aqueoulizer, blast furnace, distillation unit, hydroponic incubator, gas-fired furnace, dimensional laser, sawmill, and tooling station in a fresh local world; verify item outputs, fluid outputs, upgrades, validators, listeners, automation, and shift-click target the correct slots.
- [ ] 5.5 Repeat the documented tank and affected-tile checks on a dedicated server with two connected clients, then restart the server; verify client synchronization and current-layout inventory, tank, settings, and recipe state persist in the fresh world.
- [x] 5.6 Add a reusable manual test document listing every tank-bearing tile and the recipe-related GUI cases affected by the slot changes; verify it separates local and dedicated-server coverage and does not require pre-change saves.
- [x] 5.7 Run `git diff --check` and review the initial implementation diff for unrelated edits, generated artifacts, Serena changes, or unrequested commits.

## 6. Affected GUI artwork and layout

- [x] 6.1 Use local Aseprite to redesign the fourteen affected GUI textures, removing bucket-slot artwork and applying the design table; verify unchanged canvas dimensions, window borders, player inventory, and animation sprites, then visually inspect every revised panel.
- [x] 6.2 Update the fifteen affected screen layouts and moved item coordinates in `VEContainers`; verify tank fluid rendering, click/hover rectangles, IO labels, item backgrounds, and indicators match the revised artwork and keep compact inventory order.
- [x] 6.3 Update affected JEI recipe click regions and progress/fuel hover areas; verify moved indicators share coordinates with their handler rectangles and no obsolete hard-coded recipe region remains.
- [x] 6.4 Extend `docs/manual-testing/tank-gui-rework.md` with revised layout, title clearance, IO label, hover, and JEI checks; verify local and dedicated-server coverage lists every affected screen.
- [x] 6.5 Run `./gradlew compileJava`, strict OpenSpec validation, and `git diff --check`; verify only scoped GUI textures and source changes appear and compilation succeeds.
- [ ] 6.6 At the end of the branch's changes, run the documented in-game layout checks with and without JEI; verify revised rendering, hover/click alignment, recipe access, and window dimensions on local and dedicated-server worlds.
- [x] 6.7 Restore uniform `12 × 50` tank interiors across all affected screens and textures, retaining the revised tank centers and other component positions; verify artwork, fluid rendering, click/hover regions, and IO labels share the original size and no component regions overlap.
- [x] 6.8 Align the Aqueoulizer plus sign with its input, spread the Distillation Unit output tanks, and restore the Hydroponic Incubator's original layout with only bucket IO removed; verify exact original Hydroponic Incubator pixels outside the removed bucket artwork, matching screen/factory coordinates, and tank/item/indicator clearance.

## Workflow follow-up

- Defer manual recipe, server synchronization, persistence, and layout testing until the branch's other changes are ready, as requested by the maintainer.
- After maintainer approval and implementation verification, archive the OpenSpec change in a separate archive step.
- Port the approved tank interaction behavior to other maintained Minecraft-version branches as separate changes.
