# Design

## Context

See `proposal.md` for motivation and `specs/machine-tank-interaction/spec.md` for the behavior contract.

Tank-bearing menus are assembled by `VEContainerFactory` from slot declarations in `VEContainers`; `VETileEntityFactory` converts those declarations into `VESlotManager` instances and sizes `VEItemStackHandler` from the resulting slot count. Each tank currently contributes a `BucketInputSlot` and `BucketOutputSlot`. `VETileEntity.processFluidIO()` polls those pairs and only understands `BucketItem`.

Tank hitboxes and tooltips are duplicated across fourteen machine screens plus the shared `TankScreen`. Recipe parsers and several custom processors refer to absolute inventory indices.

## Goals / Non-Goals

**Goals:**

- Keep tank interaction generic across factory-built machines and compatible with item fluid capabilities.
- Keep the server authoritative and make item/tank changes atomic from the player's perspective.
- Preserve input/output tank rules and the existing manual ability to drain input tanks.
- Make tank hitboxes a shared screen concern instead of duplicating click and tooltip behavior.
- Make the compact inventory layout the only supported layout.
- Use the space freed by bucket slots for clear processing flow within the existing windows while preserving uniform `12 × 50` tank interiors.

**Non-Goals:**

- Redrawing unaffected machine GUIs, JEI category artwork, or shared utility textures.
- Changing pipe-side automation, tank-side configuration, tank capacity, recipes, or configuration keys.
- Adding bespoke container or tile classes for individual machines.
- Supporting worlds or placed machine data saved before the inventory layout change.
- Porting the change to another Minecraft-version branch in the same change.

## Decisions

### 1. Describe tank hitboxes declaratively in the shared screen base

`VEContainerScreen` will own a small tank-area descriptor containing the tank index and its GUI-relative `Rect2i`. Every affected tank-bearing screen will declare its revised fluid-interior rectangles. The base screen will use those descriptors to:

- detect left and right clicks before normal slot handling;
- send a tank interaction request containing only the tank index and requested direction;
- render the shared fluid-details and control tooltip.
- render fluid and IO labels at those same coordinates.

The affected screens are `AirCompressorScreen`, `AqueoulizerScreen`, `BlastFurnaceScreen`, `CentrifugalAgitatorScreen`, `CombustionGeneratorScreen`, `DimensionalLaserScreen`, `DistillationUnitScreen`, `FluidElectrolyzerScreen`, `FluidMixerScreen`, `GasFiredFurnaceScreen`, `HydroponicIncubatorScreen`, `PumpScreen`, `SawmillScreen`, `ToolingStationScreen`, and `tank/TankScreen`.

This keeps the revised textures and their interactions aligned without independent copies of tank geometry. Automatically deriving hitboxes from tank order was rejected because positions are specific to each layout. Tank dimensions are fixed centrally at 12 pixels wide and 50 pixels tall.

### 2. Perform selected-tank transfers through a server-bound payload

A new play-to-server tank interaction payload will encode the selected tank index and an action enum (`TAKE` or `PUT`). Its handler will require the sender's active menu to be a valid `VEContainer`, call `stillValid`, obtain the tile from that menu, and bounds-check the tank index. It will use the menu's server-side carried stack rather than trusting item data from the client.

The handler will operate on a one-item copy and obtain `Capabilities.FluidHandler.ITEM`. It will simulate both sides, choose the maximum mutually accepted amount, and execute only after validation succeeds. On success it will replace a single carried item with the handler's resulting container. For a stack larger than one, it will shrink the carried stack and insert the changed container into the player's inventory, dropping only overflow at the player. The tile will be marked changed, its recipe processor marked dirty where applicable, and normal menu/tile synchronization will update clients.

Left-click extraction will directly target the selected tank and remain available for input tanks, matching the old manual bucket-slot behavior even when automated extraction from input tanks is disabled. Right-click insertion will reject `TankType.OUTPUT` and will share the recipe/custom-validator checks already represented by `MultiFluidSlotWrapper.isFluidValid`. The selected-tank logic will be factored so GUI interaction does not accidentally fill a different compatible tank.

Sending the complete item stack or fluid stack in the packet was rejected because it would duplicate authoritative state and increase spoofing and desynchronization risk. Reusing world block interaction was rejected because the GUI needs an unambiguous selected tank on multi-tank machines.

### 3. Remove the bucket-slot model after registrations are converted

All `BucketInputSlot` and `BucketOutputSlot` declarations will be removed from the twenty affected `VEContainers` factories: fourteen machines and six standalone tank tiers. Retained inventory indices remain stable through the artwork redesign, while selected item-slot coordinates move with their backgrounds.

Once no declaration uses them, remove `VETileEntityFactory.BucketInputSlot`, `BucketOutputSlot`, the `SlotType.FLUID_INPUT` and `FLUID_OUTPUT` branches, and bucket-only relationship fields on `VESlotManager` if they have no remaining caller. Remove `VETileEntity.inputFluid`, `outputFluid`, `checkOutputSlotForEmptyOrBucket`, `processFluidIO`, `fluidInputDirty`, and obsolete dirty notifications. Simplify `VEItemStackHandler` and `VEContainerFactory` to handle retained input, output, and upgrade slots only.

Keeping hidden inventory slots solely to preserve old indices was rejected: those slots would remain serialized and automatable, and their inaccessible contents would contradict the requested removal.

### 4. Update every absolute retained-slot mapping

Fluid parser positions are relational-tank indices and do not change. Item and upgrade positions change only when former bucket slots preceded them:

| Machine | Old retained indices | New retained indices | Call sites requiring review |
| --- | --- | --- | --- |
| Air compressor | upgrade `2` | upgrade `0` | factory-derived upgrade id |
| Aqueoulizer | item `4`, upgrade `5` | item `0`, upgrade `1` | `AqueoulizerRecipe` |
| Blast furnace | items `2..4`, upgrade `5` | items `0..2`, upgrade `3` | `IndustrialBlastingRecipe` |
| Centrifugal agitator | upgrade `6` | upgrade `0` | factory-derived upgrade id |
| Dimensional laser | RFID `2`, upgrade `3` | RFID `0`, upgrade `1` | `DimensionalLaserRecipeProcessor` |
| Distillation unit | result `6`, upgrade `7` | result `0`, upgrade `1` | `DistillationRecipe` |
| Fluid electrolyzer | upgrade `6` | upgrade `0` | factory-derived upgrade id |
| Fluid mixer | upgrade `6` | upgrade `0` | factory-derived upgrade id |
| Gas-fired furnace | input `2`, output `3`, upgrade `4` | input `0`, output `1`, upgrade `2` | `GasFiredFurnaceProcessor` |
| Hydroponic incubator | items `2..6`, upgrade `7` | items `0..4`, upgrade `5` | `HydroponicIncubatorRecipe` and `HydroponicParser` behavior |
| Sawmill | items `0..2`, upgrade `5` | items `0..2`, upgrade `3` | verify `SawmillRecipe` and `SawmillParser`; item mappings remain unchanged |
| Tooling station | main tool/bits `2..6` | main tool/bits `0..4` | `ToolingStationProcessor`, `ToolingStationInventoryValidator`, `ToolingStationSlotWithIOListening` |

Combustion generator, pump, and the six standalone tanks have no retained inventory slots. Pure-fluid recipe parsers keep their tank indices. Gas-fired furnace upgrade access will use the energy storage's factory-derived upgrade slot id instead of a new hardcoded value.

### 5. Treat the compact inventory layout as a breaking change

Factories will declare only their current item and upgrade slots. No legacy layout metadata, inventory version, slot remapping, or removed-item recovery path will be added. Pre-change worlds and placed machine data are outside this change's compatibility boundary because this development branch will receive additional breaking changes.

Keeping a one-off migration was rejected because it would add persistent compatibility machinery for a development branch that does not promise save stability. Manual verification will use fresh local and dedicated-server worlds.

### 6. Build role-aware localized tooltips centrally

`TextUtil` will return a component list for tank details plus localized control lines. The shared screen will include the left-click line for every tank and the right-click line only for input or bidirectional tanks. English keys will be added under `assets/voluminousenergy/lang/en_us.json`; untranslated locales will use Minecraft's normal fallback behavior.

Changing the current fluid name/amount line was rejected because the request only adds interaction guidance.

### 7. Redesign the affected textures with Aseprite

Use Aseprite's batch Lua API to edit the fourteen affected PNGs, preserving their canvas dimensions, outer frames, player-inventory artwork, and off-window animation sprites. The Pump shares the Air Compressor texture, and all six standalone tank tiers share `tank_gui.png`.

Keep the established recessed grey pixel-art style. Every machine and standalone tank retains the original `12 × 50` fluid interior and its `14 × 52` recessed frame. Single-tank screens use a centered reservoir. Input/output machines place input tanks on the left and output tanks on the right. Multi-output machines group their output tanks around the recipe indicator. Retained item-slot backgrounds follow the factory coordinates. The Hydroponic Incubator retains its original vertical output column, input slot, tank, and indicator positions; only its bucket IO artwork is removed.

| Screen | Revised fluid interiors `(x, y, width, height)` | Other layout changes |
| --- | --- | --- |
| Air Compressor and Pump | `(82, 18, 12, 50)` | Shared centered reservoir |
| Standalone tanks | `(82, 18, 12, 50)` | Centered reservoir |
| Aqueoulizer | `(44, 18, 12, 50)`, `(141, 18, 12, 50)` | Item at `(85, 32)`, plus at `(71, 36)` aligned with the item center, arrow at `(111, 31)` |
| Blast Furnace | `(44, 18, 12, 50)` | Retain item and arrow coordinates |
| Centrifugal Agitator and Fluid Electrolyzer | `(44, 18, 12, 50)`, `(112, 18, 12, 50)`, `(144, 18, 12, 50)` | Retain arrow at `(81, 31)` |
| Combustion Generator | `(55, 18, 12, 50)`, `(125, 18, 12, 50)` | Retain flame at `(89, 36)` |
| Dimensional Laser | `(135, 18, 12, 50)` | RFID at `(60, 33)`, laser indicator at `(97, 34)` |
| Distillation Unit | `(44, 18, 12, 50)`, `(104, 18, 12, 50)`, `(152, 18, 12, 50)` | Item output at `(126, 62)` with space on both sides, arrow at `(81, 31)` |
| Fluid Mixer | `(42, 18, 12, 50)`, `(78, 18, 12, 50)`, `(141, 18, 12, 50)` | Plus at `(64, 34)`, arrow at `(108, 31)` |
| Gas-Fired Furnace | `(35, 18, 12, 50)` | Input at `(65, 33)`, output at `(125, 33)`, arrow at `(99, 31)`, flame at `(66, 54)` |
| Hydroponic Incubator | `(61, 18, 12, 50)` | Original outputs at `(123, 8)`, `(123, 26)`, `(123, 44)`, `(123, 62)`; only bucket IO removed |
| Sawmill | `(133, 18, 12, 50)` | Retain item and arrow coordinates |
| Tooling Station | `(44, 18, 12, 50)` | Retain multitool, bit, and indicator coordinates |

Update `VEContainers` only for moved item backgrounds, preserving inventory order and upgrade ids. Derive IO labels from actual menu slots and registered tank rectangles. Replace the Dimensional Laser's stale per-slot IO controls with the shared menu builder. Use the screen's progress rectangle for relocated progress rendering and JEI registrations; remove obsolete extra hard-coded recipe click registrations that would leave active regions at old positions.

Resizing tanks was rejected by the maintainer; every tank uses the same original dimensions. Increasing window dimensions was rejected because the existing inventory and screen footprint are explicit constraints. Creating new JEI category textures was rejected because recipe presentation outside the affected machine GUIs is outside this redesign.

## Risks / Trade-offs

- **[Third-party fluid handlers simulate inconsistently]** → Determine the transfer amount from simulation on both sides, execute in a fixed order only after both agree, and test at least one variable-capacity modded container in addition to buckets.
- **[Packet spam or forged tank indices]** → Validate the active menu, distance, tile identity through the menu, tank bounds, action, carried capability, and transferable amount on every request.
- **[Absolute indices are missed]** → Use the mapping table as a checklist, search all numeric inventory access and parser declarations, and exercise every listed machine in `runClient`.
- **[Pre-change worlds load with obsolete inventory data]** → Treat those worlds as unsupported and perform verification in fresh worlds.
- **[Texture and component positions diverge]** → Use registered tank rectangles for rendering, clicks, hover, and labels; compare item backgrounds with factory coordinates and audit every relocated indicator and JEI region.
- **[Tooltip changes are repeated or overlap JEI tooltips]** → Render tank tooltips once from `VEContainerScreen` after the existing screen-specific tooltip checks, using the same registered rectangle for clicks and hover.

## Migration Plan

No migration is provided. Test and deploy the branch with a fresh world. Restoring a backup made on the same code revision is the rollback path; opening pre-change machine data is unsupported.

Manual recipe, dedicated-server, and revised-layout checks remain open until the branch's other changes are ready, as directed by the maintainer.
