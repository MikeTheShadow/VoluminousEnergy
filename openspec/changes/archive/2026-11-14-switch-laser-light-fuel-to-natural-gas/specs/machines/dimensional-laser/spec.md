## ADDED Requirements

### Requirement: Shipped lasing recipes yield raw petroleum

Every shipped `dimensional_lasing` recipe SHALL have Crude Oil or Natural Gas as its region fluid; none
SHALL yield light fuel or another refined product. The recipes
`voluminousenergy:dimensional_lasing/natural_gas` and
`voluminousenergy:dimensional_lasing/natural_gas_hot_eroded_humid` SHALL yield
`voluminousenergy:natural_gas` with a deposit amount of 125 to 155 mB, under these climate ranges:

| Recipe | Continentalness | Erosion | Humidity | Temperature |
| --- | --- | --- | --- | --- |
| `natural_gas` | 0.665 to 0.75 | 0.268 to 0.415 | 0.035 to 5.5 | -0.3 to 0.2 |
| `natural_gas_hot_eroded_humid` | 0.1 to 0.35 | -0.2 to 0 | -0.3 to -0.2 | 0.8 to 0.85 |

#### Scenario: Recipe list on a fresh server

- **WHEN** a server starts with only the mod's own data and the `dimensional_lasing` recipes are listed
- **THEN** `dimensional_lasing/natural_gas` and `dimensional_lasing/natural_gas_hot_eroded_humid` are
  loaded with region fluid `voluminousenergy:natural_gas`, minimum 125 and maximum 155
- **AND** no loaded recipe has `voluminousenergy:light_fuel` as its region fluid
- **AND** no recipe with id `dimensional_lasing/light_fuel` or `dimensional_lasing/light_fuel_hot_eroded_humid` is loaded

#### Scenario: Lasing a matching chunk

- **WHEN** a player scans a chunk, not scanned before, whose sampled climate falls inside the
  `natural_gas` recipe's ranges, places the RFID chip in a built Dimensional Laser, and powers it
- **THEN** the laser's output tank fills with Natural Gas

#### Scenario: Chunk scanned before the update

- **WHEN** a world has a chunk that was scanned while the light fuel recipes were loaded, and a
  Dimensional Laser runs on that chunk's chip after the update
- **THEN** the laser keeps producing the fluid stored for that chunk at scan time (light fuel)
