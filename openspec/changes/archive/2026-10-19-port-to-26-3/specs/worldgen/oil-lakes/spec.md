## MODIFIED Requirements

### Requirement: Configured feature ids are stable

The features `voluminousenergy:surface_oil_lake` and `voluminousenergy:underground_oil_lake` SHALL
keep their ids, so existing biome modifiers and datapacks referring to them still resolve. On
Minecraft 26.3 and later they SHALL be defined in the `worldgen/feature` registry, with their
settings as top-level fields.

#### Scenario: Datapack disables underground oil

- **WHEN** a datapack removes `voluminousenergy:underground_oil_lake` from overworld biomes by id
- **THEN** no underground oil deposits generate

#### Scenario: Datapack overrides the surface lake

- **WHEN** a datapack provides `data/voluminousenergy/worldgen/feature/surface_oil_lake.json` with its fluid state set to Lava
- **THEN** new surface oil lakes generate filled with Lava
