## Purpose

Defines which fluids a chunk holds, and how much of each, when the fluid scanner scans it, so the
Dimensional Laser can draw them, and keeps amounts already found stable across versions of the mod.

## ADDED Requirements

### Requirement: Deposit amounts depend only on seed, position, and climate
The fluids and amounts the fluid scanner reports for a position SHALL be computed only from the
world seed, the block position, and vanilla's climate values (continentalness, erosion, humidity,
and temperature) at that position. Which fluids a position holds, and each fluid's minimum and
maximum amount, SHALL come from the Dimensional Laser recipes' climate ranges, as on 26.2, and each
amount SHALL stay within its recipe's minimum and maximum. Scanning the same position twice in one
world SHALL give the same result.

#### Scenario: Scanning twice
- **WHEN** a player scans a chunk with the fluid scanner, breaks nothing, and scans it again
- **THEN** both scans report the same fluids and amounts

#### Scenario: Amount within its range
- **WHEN** the fluid scanner reports Crude Oil in a chunk
- **THEN** the amount is between the minimum and maximum set by Crude Oil's Dimensional Laser recipe

#### Scenario: Water and lava fallback
- **WHEN** a chunk whose climate matches no Dimensional Laser recipe is scanned
- **THEN** the scanner reports either water or lava, chosen and sized from the seed, position, and climate

### Requirement: Scanned chunks keep their saved amounts
A chunk that was scanned before the world was opened with the 26.3 build SHALL keep the fluids and
amounts saved for it, and a Dimensional Laser reading its RFID chip SHALL keep drawing them. Chunks
first scanned on the 26.3 build MAY get different amounts than the same chunk would have had on 26.2.

#### Scenario: Laser keeps drawing after the upgrade
- **WHEN** a chunk scanned on the 26.2 build with 50 000 mB of Crude Oil left is opened on the 26.3 build and its RFID chip is placed in a Dimensional Laser
- **THEN** the laser fills its tank with Crude Oil, up to the 50 000 mB saved for the chunk in each cycle
