# Tank GUI Rework Manual Test Plan

## Scope

This plan verifies the tank interaction and compact inventory changes on `1.21.1-dev-tank-gui-rework`. The compact layouts are a breaking save change. Use fresh local and dedicated-server worlds; pre-change machine inventories and placed-machine data are unsupported.

Use the same Voluminous Energy build and dependency versions on every client and server. Include Mekanism when checking a variable-capacity fluid item.

Manual recipe, synchronization, persistence, and revised-layout checks are deferred until the branch's remaining changes are ready. Completed interaction checks below describe the earlier interaction implementation; the relocated tank regions still require the layout pass.

## Completed interaction checks

- [x] Vanilla buckets take fluid with left click and put fluid with right click.
- [x] Mekanism tanks support full and partial transfers, including stacked-container result handling, without fluid loss or item duplication.

These checks were confirmed by the maintainer. Repeat them when later breaking changes touch tank capabilities, menu networking, carried-stack handling, or tank screens.

## Recipe and custom-processing coverage

These tiles have recipe, parser, validator, listener, or custom-processor behavior tied to item slots changed by the bucket-slot removal.

| Tile | Compact item layout | Required checks |
| --- | --- | --- |
| Aqueoulizer | Input `0`, upgrade `1` | Run a recipe that consumes the item and input fluid, produces output fluid, and responds to the upgrade. Check shift-click and item automation. |
| Blast Furnace | Inputs `0` and `1`, output `2`, upgrade `3` | Form the multiblock and run an industrial blasting recipe with its fluid input. Check both inputs, the item output, upgrade behavior, shift-click, and automation. |
| Distillation Unit | Item output `0`, upgrade `1` | Form the multiblock and run a recipe using the input tank and both output tanks. Check the item output, upgrade behavior, shift-click, and output automation. |
| Hydroponic Incubator | Input `0`, outputs `1` through `4`, upgrade `5` | Run enough recipes to exercise the primary and random outputs. Check the fluid input, upgrade behavior, shift-click, and automation for every item slot. |
| Gas-Fired Furnace | Input `0`, output `1`, upgrade `2` | Insert valid fuel fluid and smelt an item. Check its custom validator, processor, output experience listener, upgrade behavior, shift-click, and automation. |
| Dimensional Laser | RFID input `0`, upgrade `1` | Form the multiblock and run the custom processor with a valid RFID chip. Check the fluid output, inventory validator, upgrade behavior, shift-click, and automation. |
| Sawmill | Input `0`, outputs `1` and `2`, upgrade `3` | Run a recipe that produces its item outputs and fluid output. Check the unchanged recipe item positions, the moved upgrade slot, shift-click, and automation. |
| Tooling Station | Multitool `0`, bits `1` through `4` | Load and unload all bit positions through the slot listener. Check validation, combustible-fluid insertion and extraction, saved multitool state, shift-click, and automation. |

## Other tank-bearing tile coverage

These tiles lost bucket slots but did not require the item-slot remapping listed above.

| Tile | Tank roles | Required checks |
| --- | --- | --- |
| Air Compressor | One output | Produce air, take it with a fluid item, and check upgrade slot `0`. |
| Centrifugal Agitator | One input, two outputs | Run a recipe, interact with each distinct tank, and check upgrade slot `0`. |
| Combustion Generator | Two inputs | Confirm the zero-item-slot menu opens, each tank selects the correct relational tank, and fuel processing produces power. |
| Fluid Electrolyzer | One input, two outputs | Run a recipe, interact with each distinct tank, and check upgrade slot `0`. |
| Fluid Mixer | Two inputs, one output | Run a recipe, confirm the input tanks are not swapped, take from the output, and check upgrade slot `0`. |
| Pump | One output | Confirm the zero-item-slot menu opens, pumping fills the tank, and fluid can be taken. |
| Aluminum Tank | One bidirectional | Confirm the zero-item-slot menu opens and supports both tank actions. |
| Eighzo Tank | One bidirectional | Confirm the zero-item-slot menu opens and supports both tank actions. |
| Netherite Tank | One bidirectional | Confirm the zero-item-slot menu opens and supports both tank actions. |
| Nighalite Tank | One bidirectional | Confirm the zero-item-slot menu opens and supports both tank actions. |
| Solarium Tank | One bidirectional | Confirm the zero-item-slot menu opens and supports both tank actions. |
| Titanium Tank | One bidirectional | Confirm the zero-item-slot menu opens and supports both tank actions. |

## Local integrated-server checklist

- [ ] Start a fresh world and open every tile listed above.
- [ ] Confirm obsolete bucket-slot artwork is absent and no hidden bucket slots remain.
- [ ] Hover every visible tank. Input and bidirectional tanks must describe left-click take and right-click put; output tanks must describe take only.
- [ ] Check empty, partially filled, full, incompatible-fluid, and multi-tank interaction states.
- [ ] Complete every row in the recipe and custom-processing table.
- [ ] Complete every row in the other tank-bearing tile table.
- [ ] Check shift-click routes items only to valid compact-layout slots.
- [ ] Check sided item and fluid automation for every affected role.
- [ ] Save and reload the fresh world. Confirm current-layout inventory, tank, settings, recipe progress, and multitool state persist where applicable.

## Revised GUI layout checklist

Run these checks for every tile listed in both coverage tables, first locally and then on a dedicated server. The Pump shares the Air Compressor texture, and all six standalone tanks share the tank texture. Each window remains `176 × 166` GUI pixels; each texture canvas remains `256 × 256` pixels. Every tank retains the original `12 × 50` fluid interior and `14 × 52` recessed frame.

- [ ] Open each GUI at multiple GUI scales. Confirm the window, player inventory, and hotbar retain their original dimensions and placement.
- [ ] Confirm titles, inventory labels, upgrades, IO controls, item backgrounds, tanks, and processing indicators do not overlap or clip.
- [ ] Compare tanks across all affected GUIs, including the Distillation Unit outputs and standalone tanks. Confirm every fluid interior is `12 × 50` GUI pixels.
- [ ] Fill every tank partway and fully. Confirm fluid stays inside its revised frame.
- [ ] Hover and click the edges and interior of each tank. Confirm fluid details and take/put actions select the displayed tank, and old tank positions have no leftover interaction region.
- [ ] Open the IO configuration view. Confirm tank labels follow their tank frames and slot labels use the retained inventory indices at the item positions.
- [ ] Confirm the Dimensional Laser IO view offers its RFID slot and output tank without removed-bucket controls or slot-index errors.
- [ ] Insert and shift-click items in the moved Aqueoulizer, Dimensional Laser, Distillation Unit, and Gas-Fired Furnace slots. Confirm the item sprites, highlight, tooltip, and click regions match their backgrounds.
- [ ] Check every Hydroponic Incubator output in its original vertical column, including all random outputs. Confirm the original input, tank, and processing indicator positions are retained and bucket IO artwork is absent.
- [ ] Check the Aqueoulizer plus sign is vertically aligned with its item input and the Distillation Unit output tanks leave space around its item output slot.
- [ ] With JEI loaded, hover and click each processing indicator to open that machine's recipes. Check the relocated Aqueoulizer and Fluid Mixer arrows, the Sawmill arrow, and both Gas-Fired Furnace indicators. Confirm prior indicator positions have no leftover recipe regions.
- [ ] With JEI absent, hover each processing indicator and the Gas-Fired Furnace fuel flame. Confirm the existing progress and fuel tooltips appear at the revised positions.
- [ ] Form and break each affected multiblock. Confirm warning overlays and valid-machine artwork remain aligned with the same window.

## Dedicated-server checklist

- [ ] Start a fresh dedicated-server world with matching client and server builds.
- [ ] Connect two clients and have both players observe the same machine or tank.
- [ ] Transfer fluid from one client. Confirm both clients receive the same tank amount and the initiating player's carried-stack result.
- [ ] Repeat with a vanilla bucket and a Mekanism tank, including a partial transfer.
- [ ] Confirm invalid or stale interactions do not mutate items or fluid after closing the menu, moving out of range, or changing the carried item before the server handles the action.
- [ ] Complete every row in the recipe and custom-processing table on the server.
- [ ] Complete the revised GUI layout checklist on the server with matching builds on both clients.
- [ ] Open the Combustion Generator, Pump, and each standalone tank to cover zero-item-slot menus.
- [ ] Disconnect and reconnect both clients. Confirm inventory and tank state remain synchronized.
- [ ] Restart the server. Confirm current-layout inventory, tank, settings, recipe progress, and multitool state persist where applicable.
- [ ] Review the server log for menu, payload, slot-index, recipe, and serialization errors.

## Result log

| Date | Build or commit | Mode | Tester | Result | Notes |
| --- | --- | --- | --- | --- | --- |
|  |  | Local |  |  |  |
|  |  | Dedicated server |  |  |  |
