# Spec Delta

## Purpose

Lets players connect machine item and fluid ports to visible neighboring faces and control passive and automatic transfers directly in the machine GUI.

## ADDED Requirements

### Requirement: IO mode exposes face targets
Opening IO mode SHALL show all configurable ports and six destination trays beside the machine. Loaded neighbors SHALL have previews and face labels. Air SHALL show its face letter, and unavailable neighbors SHALL be identifiable without loading chunks. No inspector menu SHALL open.

#### Scenario: Configure a Sawmill
- **WHEN** a player enables IO mode on a Sawmill with a chest behind it
- **THEN** its ports are outlined and the Back target previews the chest
- **AND** empty faces show F/B/L/R/U/D as appropriate

### Requirement: Dragging assigns a face without moving contents
Dragging a configurable slot, tank, or its tray token onto a destination tray SHALL assign that face without moving contents. Moving more than four GUI pixels SHALL distinguish dragging from clicking. Once dragging begins, returning to the source SHALL not turn it into a click. Dropping elsewhere SHALL leave settings unchanged.

#### Scenario: Assign an output to Back
- **WHEN** a player drags Sawmill output 1 onto Back
- **THEN** that output uses Back and its contents remain unchanged

#### Scenario: Cancel a drag
- **WHEN** a player drags a tank away and releases outside the face targets
- **THEN** its assignment and fluid remain unchanged

#### Scenario: Reassign from a tray
- **WHEN** a player drags the Sawmill output token from Back to Left
- **THEN** the token moves to Left, retaining its number and contents while following Left's automatic mode

### Requirement: Port clicks select and remove assignments
A left-click without dragging SHALL select a port without changing its assignment. Right-click on a port or token SHALL remove its face assignment. Unassigned ports SHALL remain identifiable and draggable from the machine. Configuring ports SHALL not transfer carried items or fluid containers. No port or face Enabled switch SHALL exist.

#### Scenario: Click an input tank
- **WHEN** a player left-clicks an input tank in IO mode and then right-clicks it
- **THEN** left-click only selects, right-click unassigns, and no bucket transfer occurs

### Requirement: Destination faces own independent automatic actions
Each relative face SHALL have independent auto-push and auto-pull flags, allowing neither, either, or both. Changing a checkbox SHALL preserve the other flag. Push SHALL appear on every section; Pull SHALL appear when assigned roles support it. Assigned output ports SHALL follow Push and assigned input ports SHALL follow Pull. Bidirectional tanks SHALL support both flags.

#### Scenario: Push all Down outputs
- **WHEN** a player assigns two outputs to Down and enables Down auto-push
- **THEN** both assigned outputs push to the block below, while an unassigned output and any input assigned Down do not transfer automatically

#### Scenario: Preconfigure an empty face
- **WHEN** a player checks Push on an empty Down section and then assigns an output to it
- **THEN** Down retains auto-push and the newly assigned output follows that mode

#### Scenario: Enable both actions
- **WHEN** a player checks Pull on an auto-pushing destination tray
- **THEN** both checkboxes remain selected and eligible inputs pull while outputs push

#### Scenario: Disable one action
- **WHEN** a player unchecks Push on a face with both actions enabled
- **THEN** Pull remains enabled, inputs continue pulling, and outputs stop automatic pushing

#### Scenario: Bidirectional tank with both actions
- **WHEN** both checkboxes are selected on a standalone tank's assigned face
- **THEN** the tank alternates push and pull between transfer passes, using at most one configured limit each pass
- **AND** it does not push and immediately pull the same fluid within one pass

#### Scenario: Reassign into an automated face
- **WHEN** a passive-face output is dragged to Down, which already uses auto-push
- **THEN** the output follows Down's auto-push setting without configuring the port

### Requirement: Port numbers link machine and tray representations
Every configurable port SHALL have a stable visible number matching its tray token when assigned. Numbers SHALL remain unchanged by reassignment, removal, content changes, and resize. Selected ports and tokens SHALL have green outlines. Hovering either representation SHALL emphasize both. Unassigned machine badges SHALL be grey; automation SHALL be indicated by tray checkboxes.

#### Scenario: Select a pushing output
- **WHEN** a player selects an assigned output on an auto-pushing face
- **THEN** the machine port and tray token show the same number and green outlines, and its destination tray indicates push

#### Scenario: Hover a token with duplicate contents
- **WHEN** two output slots contain the same item and a player hovers one tray token
- **THEN** its number identifies the matching machine slot and both representations are highlighted

### Requirement: Every destination tray lists its assigned ports
IO mode SHALL list every assigned port under its destination face simultaneously. Unassigned ports SHALL have no tray token. Tokens SHALL render current item/fluid contents or identifiable empty type placeholders. All assignments SHALL remain visible without selection, hover, or menus. Persistent connection lines and a PCB overlay SHALL be absent. Dragging SHALL show a floating token and highlight its target while retaining the overview.

#### Scenario: Review a dense machine without selecting ports
- **WHEN** a player opens IO mode on the Hydroponic Incubator
- **THEN** every slot and tank's numbered token is visible under its destination without selecting it
- **AND** hovering one port emphasizes its matching token while all other assignments remain visible

#### Scenario: Concentrate assignments
- **WHEN** all six Electrolyzer ports are assigned to Back at a compact supported GUI scale
- **THEN** all six Back tokens and all other face trays remain visible and reachable

### Requirement: Assignment gates external access
All six faces SHALL be available for assigned ports without enable flags. Unassigned ports SHALL reject passive and automatic IO and hide external contents/capacity, including unsided item queries and held handlers. Assigned ports SHALL accept external IO only through their selected face and role. Standalone tanks SHALL obey this assignment. Recipes and manual GUI access SHALL remain available. Every face SHALL default to passive.

#### Scenario: Detach and reassign a tank
- **WHEN** a player right-clicks a tank token and then drags its machine port back onto a face
- **THEN** all external access stops while unassigned and resumes through the assigned face using that face's mode

#### Scenario: Held and unsided inventory handlers
- **WHEN** a pipe holds a handler for an output before the player unassigns it
- **THEN** that handler and a null-face query cannot read, extract, insert, or modify the unassigned slot

#### Scenario: Reassign a standalone tank
- **WHEN** a standalone tank is moved from Down to Back
- **THEN** passive fluid access is available on Back and unavailable on the other five faces, including a held Down handler

### Requirement: Item transfers respect neighboring inventories
Assigned automatic item ports SHALL transfer through their selected neighboring face, respecting handler permissions, validation, components, limits, and space. Only accepted quantities SHALL move.

#### Scenario: Partly full chest
- **WHEN** an output offers 16 items to a chest that accepts 3
- **THEN** 3 move and the remainder stays in the machine

### Requirement: Fluid transfers respect selected tanks
Assigned automatic fluid ports SHALL transfer through the selected neighboring face, respecting fluid components, selected-tank validation, and capacity. Missing handlers, empty sources, or full destinations SHALL not lose or duplicate fluid.

#### Scenario: Partly full input tank
- **WHEN** a tank with 100 mB free pulls an accepted fluid
- **THEN** at most 100 mB enters it and the remainder stays in the source

### Requirement: Faces track machine orientation
Passive IO, automatic IO, previews, and connections SHALL use the same machine-relative face mapping. Up and Down SHALL remain vertical world faces.

#### Scenario: Rotate the machine
- **WHEN** Back is assigned on machines facing each horizontal direction
- **THEN** each preview and transfer uses its physical back face

### Requirement: Automatic transfer work is bounded
Automatic transfers SHALL run on the logical server at a configurable interval with per-port item/fluid limits. Transfers and previews SHALL not load neighboring chunks solely for IO.

#### Scenario: Unloaded neighbor
- **WHEN** the selected adjacent chunk is not loaded
- **THEN** no transfer or preview loads it and machine contents remain unchanged

### Requirement: Configuration persists and remains server-authoritative
Optional port assignment and each face's automatic mode SHALL persist across reloads and synchronize to viewers. Requests SHALL require a valid current menu, tile, target, value, face, and applicable mode. Experimental per-port Enabled/automatic flags SHALL not be read or migrated.

#### Scenario: Closed menu
- **WHEN** a configuration request arrives after its menu closes
- **THEN** it does not modify a tile

#### Scenario: Independent requests from two viewers
- **WHEN** one player enables Push while another enables Pull on the same face
- **THEN** both requests update only their targeted action and both flags persist across reload

### Requirement: Port hover remains compact
Port and token tooltips SHALL contain only identity, contents, and assigned face or Unassigned. Automation controls and explanations SHALL appear on destination trays rather than port hover.

#### Scenario: Hover an output assigned to an automated face
- **WHEN** a player hovers a Sawmill output token assigned to an auto-pushing Down face
- **THEN** the tooltip shows its identity, contents, and Down without a gesture legend or automatic-mode instructions

### Requirement: IO controls preserve machine layouts and normal interaction
IO controls SHALL preserve machine window, inventory, recipe, and tank sizes and keep all ports reachable at supported GUI scales. Turning IO mode off SHALL restore ordinary slot/bucket interactions. Mode and selected port SHALL survive screen resize, while unfinished gestures SHALL cancel. Controls SHALL not activate covered inventory or JEI regions.

#### Scenario: Compact GUI
- **WHEN** IO mode is opened on the Hydroponic Incubator at a compact supported GUI scale
- **THEN** all six trays and every assigned token remain reachable without moving its machine slots or tanks
- **AND** Battery Box energy controls remain available on that machine
