# Spec Delta

## Purpose

Defines how players transfer fluids directly through machine and standalone-tank GUIs while preserving recipe behavior in the new compact layouts.

## ADDED Requirements

### Requirement: Tank GUIs do not expose container-processing slots
Every machine or standalone tank with a rendered fluid tank SHALL omit the dedicated fluid-container input and output slots from its menu inventory and background artwork.

#### Scenario: Open a tank-bearing GUI
- **WHEN** a player opens any machine or standalone tank GUI that renders a fluid tank
- **THEN** no dedicated bucket input or bucket output slot is interactive
- **AND** all non-fluid-container item and upgrade slots remain available at their revised screen positions

### Requirement: Redesign only the affected tank GUIs within their existing windows
The fourteen affected machine screens and shared standalone-tank screen SHALL retain their existing window dimensions and player-inventory placement. Their textures SHALL arrange tanks, retained item slots, and processing indicators to reflect the compact layout. Unaffected machine GUIs and shared utility textures SHALL remain unchanged.

#### Scenario: Open a redesigned GUI
- **WHEN** a player opens an affected machine or standalone tank GUI
- **THEN** its window and player inventory occupy the same area as before
- **AND** its tank and item layout contains no obsolete bucket-slot artwork

### Requirement: Component interactions follow their rendered positions
Each relocated component SHALL use matching rendering, hover, click, and IO-label coordinates. Tank interaction areas SHALL match the rendered fluid interiors. JEI recipe click areas and progress or fuel tooltips SHALL follow their corresponding indicators without overlapping retained slots or tanks.

#### Scenario: Interact with a relocated tank
- **WHEN** a player hovers or clicks a tank in its revised position
- **THEN** the tooltip, fluid transfer, and IO label address that displayed tank

#### Scenario: Open recipes from a relocated indicator
- **WHEN** JEI is loaded and a player clicks a relocated processing indicator
- **THEN** JEI opens the corresponding machine recipes
- **AND** the old indicator position has no leftover recipe click region

#### Scenario: Hover processing indicators without JEI
- **WHEN** JEI is absent and a player hovers a processing or fuel indicator
- **THEN** its existing tooltip appears over the revised indicator position

### Requirement: Reviewed machine layouts retain their intended arrangement
The Aqueoulizer's plus sign SHALL align vertically with its item input. The Distillation Unit output tanks SHALL leave space on both sides of the item output slot. The Hydroponic Incubator SHALL retain its original tank, item input, processing indicator, and vertical four-slot output arrangement, removing only the dedicated bucket IO from its original artwork.

#### Scenario: Review the three adjusted layouts
- **WHEN** a player opens the Aqueoulizer, Distillation Unit, or Hydroponic Incubator
- **THEN** the Aqueoulizer plus sign aligns with the item input center
- **AND** the Distillation Unit output tanks are spaced around the item output
- **AND** the Hydroponic Incubator outputs form their original vertical column with matching item click regions

### Requirement: Tank dimensions remain uniform
Every displayed fluid tank in the affected machine and standalone-tank GUIs SHALL retain the original `12 × 50` fluid-interior dimensions. Layout changes SHALL move tanks without resizing them.

#### Scenario: Compare tanks across affected GUIs
- **WHEN** a player opens any affected machine or standalone-tank GUI
- **THEN** every tank has a fluid interior 12 GUI pixels wide and 50 GUI pixels tall
- **AND** rendering and interaction regions use those same dimensions

### Requirement: Left click takes fluid from a displayed tank
The GUI SHALL use a left click on a displayed tank to transfer as much fluid as both the tank and the compatible carried fluid container permit, regardless of whether the displayed tank is classified as input, output, or bidirectional.

#### Scenario: Fill an empty vanilla bucket from a tank
- **WHEN** a player carries one empty bucket and left-clicks a tank containing at least 1000 mB of bucketable fluid
- **THEN** the carried item becomes the corresponding filled bucket
- **AND** the tank loses exactly 1000 mB of that fluid

#### Scenario: Fill a modded fluid container
- **WHEN** a player carries an item exposing a compatible fluid-item handler and left-clicks a non-empty tank
- **THEN** the maximum amount accepted by both handlers is transferred from the tank into one carried item
- **AND** the updated container item and tank amount are synchronized to the player

#### Scenario: Use one container from a stack
- **WHEN** a successful transfer changes one item from a carried stack containing multiple items
- **THEN** one item is consumed from the carried stack
- **AND** the changed container is placed in the player's inventory or dropped at the player's position if that inventory is full

### Requirement: Right click puts fluid into tanks that accept input
The GUI SHALL use a right click on a displayed input or bidirectional tank to transfer as much fluid as both the compatible carried item and the selected tank permit. The transfer SHALL honor tank capacity, current fluid compatibility, recipe acceptance, and custom fluid validators.

#### Scenario: Empty a vanilla fluid bucket into an input tank
- **WHEN** a player carries a filled vanilla bucket and right-clicks a compatible input tank with at least 1000 mB free
- **THEN** the bucket's 1000 mB is added to that tank
- **AND** the resulting empty bucket is returned through the carried-stack rules

#### Scenario: Partially drain a modded fluid container
- **WHEN** a player right-clicks a compatible input or bidirectional tank with a modded container holding more fluid than the tank can accept
- **THEN** only the amount accepted by the tank is removed from the item
- **AND** the updated item retains the untransferred fluid

#### Scenario: Reject insertion into an output-only tank
- **WHEN** a player right-clicks an output-only tank with a fluid-filled item
- **THEN** no fluid or item state changes

### Requirement: Tank interaction failures are atomic
A tank interaction SHALL be validated and executed on the logical server so a rejected or stale request cannot change either the carried item or tank.

#### Scenario: Incompatible item or fluid
- **WHEN** a player clicks a tank with an item that has no fluid handler, an incompatible fluid, or no transferable amount
- **THEN** the carried item and tank remain unchanged

#### Scenario: Invalid menu request
- **WHEN** the server receives a tank interaction for a menu the player is not validly using or a tank index outside that menu's tile
- **THEN** the request is rejected without changing inventory or fluid state

### Requirement: Tank tooltips explain direct interaction
Hovering a displayed tank SHALL show its fluid name, stored amount, capacity, and localized instructions for each interaction supported by that tank.

#### Scenario: Hover an input or bidirectional tank
- **WHEN** a player hovers an input or bidirectional tank in a GUI
- **THEN** the tooltip includes the fluid details, a left-click instruction for taking fluid, and a right-click instruction for inserting fluid

#### Scenario: Hover an output-only tank
- **WHEN** a player hovers an output-only tank in a GUI
- **THEN** the tooltip includes the fluid details and a left-click instruction for taking fluid
- **AND** it does not claim that right-click insertion is supported

### Requirement: Slot removal preserves machine behavior
After bucket slots are removed, every retained item and upgrade slot SHALL use the correct inventory position, and recipe matching, completion, custom processing, validation, slot listening, automation, and shift-click behavior SHALL continue to target those retained slots.

#### Scenario: Process affected recipe machines
- **WHEN** valid recipes run in the aqueoulizer, blast furnace, distillation unit, or hydroponic incubator after the layout change
- **THEN** each machine consumes the intended item and fluid inputs and places every result in its intended item or fluid output

#### Scenario: Run affected custom processors
- **WHEN** the gas-fired furnace, dimensional laser, or tooling station processes valid contents after the layout change
- **THEN** its processor, validator, upgrade lookup, and slot listener read and write the intended retained slots

#### Scenario: Use machines whose item slots precede removed slots
- **WHEN** the sawmill or another tank-bearing machine already has retained item slots before its former bucket slots
- **THEN** its existing recipe slot mapping remains correct and is not shifted unnecessarily

### Requirement: Compact layouts are a breaking save change
The compact machine inventories SHALL NOT provide legacy slot remapping, removed-item recovery, or another compatibility path for machine data saved before this change.

#### Scenario: Use the reworked layouts
- **WHEN** a player or server tests this change
- **THEN** the test uses a fresh world or accepts that pre-change machine data is unsupported
