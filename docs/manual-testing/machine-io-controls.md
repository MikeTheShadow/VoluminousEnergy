# Machine IO Destination Trays Manual Test Plan

Use fresh local and dedicated-server worlds with matching builds. Run these checks alongside the tank GUI rework plan at the end of this branch's changes. Compilation and static checks do not replace this in-game pass.

## Direct configuration

- [ ] Open every machine with item or fluid ports: Air Compressor, Aqueoulizer, Battery Box, Blast Furnace, Centrifugal Agitator, Centrifugal Separator, Combustion Generator, Compressor, Crusher, Dimensional Laser, Distillation Unit, Electric Furnace, Electrolyzer, Fluid Electrolyzer, Fluid Mixer, Gas-Fired Furnace, Hydroponic Incubator, Implosion Compressor, Primitive Blast Furnace, Primitive Stirling Generator, Pump, Sawmill, Stirling Generator, and Tooling Station.
- [ ] Open each standalone tank tier: Aluminum, Eighzo, Netherite, Nighalite, Solarium, and Titanium.
- [ ] Confirm the IO button shows six destination trays and every assigned numbered token without selecting a port. Player inventory and upgrade slots must not be outlined. Machine artwork stays visible without a PCB grid, dimming overlay, or permanent wires.
- [ ] Drag every port, including all Hydroponic Incubator and Electrolyzer ports, onto every tray. Repeat by dragging their tray tokens. Confirm the token retains its number and contents while adopting the new face's automatic mode.
- [ ] Left-click without dragging only selects a port. Test movement up to four GUI pixels, then dragging past four pixels. Invalid drops and a drag returned to its source leave settings unchanged. No Enabled switch exists for ports or faces.
- [ ] Right-click and Shift-right-click every machine port and tray token. Confirm its assignment disappears, its machine badge turns grey, and its tray token is removed. Drag it back from the machine and confirm assignment/external access resume without a separate enable action.
- [ ] Toggle the explicit Push checkbox in every section, including empty faces. Assign an output after preconfiguring Push and confirm it inherits the flag. Pull appears for assigned input/bidirectional roles. Check neither, Push only, Pull only, and both. Changing either checkbox must preserve the other; both selected must be reflected in the face tooltip.
- [ ] Verify stable matching numbers on every machine port and assigned tray token. Selection gives both representations a green outline. Unassigned ports keep their original number, including after resize and reassignment.
- [ ] Hover ports/tokens with empty and filled contents. Tooltips must contain only identity, contents, and assigned face or Unassigned. No gesture legend or automation instructions appear. Hover tray checkboxes to see their automation help.
- [ ] Assign multiple outputs and an input to Down. Enable Push and confirm assigned outputs follow it while input and unassigned output ports do not transfer. Enable Pull as well and confirm inputs pull while outputs continue pushing. Uncheck either action and confirm the other keeps operating. Repeat with mixed item/fluid ports.
- [ ] Reassign a port into an automated face and confirm it immediately inherits that face's mode. Move the last port away from a configured face, then assign an eligible port back; confirm the face retains its preference. Reassignments must not change other faces' modes.
- [ ] On the Hydroponic Incubator and Electrolyzer, read every destination without selecting or hovering ports. Every assigned token must remain visible. Repeat with empty ports and identical item/fluid contents; placeholders and numbers must distinguish them.
- [ ] Hover machine ports and tray tokens in turn. Confirm both representations highlight together and all other assignments remain visible. Hover a tray header and confirm its assigned ports highlight. Drag and confirm a floating token follows the pointer and the hovered destination highlights green.
- [ ] Assign all six ports to each face in turn, then split them across faces, including five-plus-one and four-plus-one-plus-one in the same bank. Confirm all checkbox rows, tokens, and six trays remain visible at the smallest supported GUI size. Unassign some or all ports and confirm layout/counts update without hiding any face.
- [ ] Turn IO mode off and confirm badges, outlines, and trays disappear and normal left/right, shift-click, and bucket interactions resume.
- [ ] Configure with empty hands, carried items, and fluid containers. Test shift-click, right-click, double-click, dragging, hotbar keys, and the drop key on ports, tokens, and checkboxes. Configuration must not move/drop contents or transfer fluid. Player inventory and upgrade controls between/outside the tray banks must remain usable. Clicking/releasing a checkbox must not alter a selected port's assignment.
- [ ] Resize/change GUI scale while dragging and after selecting a port. Confirm IO mode/selection survive, unfinished gestures cancel, and a subsequent release changes no setting.
- [ ] Put distinct blocks against each face. Confirm each target previews the correct block and identifies it on hover. Repeat with identical chests, modded tanks, non-inventory blocks, air, and an unloaded neighbor. Face letters must remain identifiable, and unavailable neighbors show a question mark.
- [ ] Confirm all six faces default to passive and factory slots retain their declared initial assignments on newly placed tiles, including listened slots. Unassigning a port retains the face's mode. Experimental Enabled/automatic flags must not be migrated or interpreted.
- [ ] Test multiple GUI scales and a narrow window. Check tray growth and repositioning after reassignment, especially around upgrade slots and the IO button. All six trays and all tokens must remain reachable; neighboring block previews, item/fluid icons, numbers, symbols, and tooltips must stay readable.
- [ ] Repeat with and without JEI. Only the tray banks reserve external space; the broad enclosing widget must not swallow inventory clicks or block unrelated recipe indicators. Reassignments must update reserved areas. Bank gaps consume clicks but are not valid drop targets.
- [ ] Check Battery Box's send-power control. Confirm the committed machine artwork, player inventory, and 12-by-50 tanks remain unchanged.

## Item transfers

- [ ] Put a chest against an input face, set that tray to Pull, and provide accepted recipe inputs mixed with incompatible items. Only accepted items enter its assigned input slots.
- [ ] Configure two inputs on different faces with distinct neighbors. Confirm each uses its own face and slot.
- [ ] Put a chest against an output face, set that tray to Push, and complete recipes. Only outputs assigned to that face enter the chest.
- [ ] Leave room for 3 items in the receiver, offer more, and confirm only 3 move while the remainder stays in the machine.
- [ ] Check empty sources, full destinations, missing inventories, sided restrictions, stacked components, custom validators, and modded inventories. Confirm no loss or duplication.
- [ ] Unassign configured ports. Confirm hoppers/pipes and automatic transfers stop while manual GUI access and recipes remain available. Reassigning resumes the destination face's mode.
- [ ] Test a modded unsided inventory query and handlers retained before removal/reassignment. Unassigned contents and capacity must be hidden; insert/extract/set/validation/limits must reject unassigned ports. A held old-face handler must also reject a port moved to another face. Upgrade slots must not be exposed by the unsided view.

## Fluid transfers

- [ ] Put a fluid handler against an input face and set that tray to Pull. Accepted fluids enter only its assigned input/bidirectional tanks; incompatible fluids remain in the source.
- [ ] Fill multiple machine tanks with distinct fluids and configure separate faces. Confirm relational tank indices are not swapped.
- [ ] Set an output tank's face tray to Push. Confirm only accepted fluid enters the neighbor, including a receiver with 100 mB free.
- [ ] Check empty/full tanks, mismatched components, custom fuel validators, modded handlers, and absent capabilities. Confirm quantities across both endpoints are conserved.
- [ ] Test a standalone tank with each flag combination. With both selected, confirm successive transfer passes alternate push and pull and remain within one configured limit per pass. No pass may push fluid and immediately pull it back. Only the assigned face is used; empty/full endpoints must not lose or duplicate fluid.
- [ ] Unassign tanks and check passive fill, resource-specific drain, amount-based drain, fluid validity, contents, and capacity through pipes and retained handlers. All external access must stop; manual bucket IO and recipes remain available.
- [ ] Test standalone tanks on all six faces. Only the assigned face may expose fluid. Unassign it, then reassign to a different face and confirm a held old-face handler cannot read, fill, or drain it.

## Rotation, limits, and persistence

- [ ] Repeat Front, Back, Left, Right, Up, and Down with machines facing North, South, East, and West. Passive pipes and active transfers must use the same physical face.
- [ ] Verify Machine IO config: Transfer Interval (default 8 ticks), Item Transfer Limit (default 16 per port), and Fluid Transfer Limit (default 250 mB per port).
- [ ] Unload a selected neighboring chunk. Transfers must not load it solely to move contents.
- [ ] Save/reload locally and restart the dedicated server. Confirm assigned and unassigned ports plus each face's mode persist for item, input/output fluid, and standalone tank ports. Confirm removal/mode changes synchronize to two viewers and survive item-form placement when applicable.

## Dedicated server

- [ ] Have two players view a machine while one changes each setting. Confirm both converge on server state.
- [ ] Have one player enable Push while another enables Pull on the same face. Both targeted requests must survive, synchronize, and persist on restart. Then have one disable Push and confirm Pull remains active. Check all four combinations on local save/reload as well.
- [ ] Repeat item/fluid cases, including Mekanism inventories/tanks when installed.
- [ ] Close the GUI or move out of range before pending requests arrive. Stale requests must not change ports.
- [ ] Review logs for slot indices, invalid payloads, capability errors, and client-class loading errors.

## Result log

| Date | Build | Mode | Tester | Result | Notes |
| --- | --- | --- | --- | --- | --- |
|  |  | Local |  |  |  |
|  |  | Dedicated server |  |  |  |
