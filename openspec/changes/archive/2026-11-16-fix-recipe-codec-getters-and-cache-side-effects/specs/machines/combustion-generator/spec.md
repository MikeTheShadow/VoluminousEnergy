## ADDED Requirements

### Requirement: Combustion fuel data encodes to the energy it was loaded with

Encoding a combustion fuel data entry SHALL write its own `energy_per_tick` alongside its `tag` or
`fluid`. Decoding that output SHALL give an entry with the same tag or fluid and the same FE/t.

#### Scenario: Shipped diesel fuel data

- **WHEN** the shipped diesel combustion fuel entry (tag `c:diesel`, 160 FE/t) is decoded and encoded
  back to JSON
- **THEN** the JSON has tag `c:diesel` and `energy_per_tick` 160, not 1

#### Scenario: Every shipped fuel round-trips

- **WHEN** each shipped combustion fuel data entry is decoded, encoded, and decoded again
- **THEN** the second decode has the same tag or fluid and FE/t as the first
