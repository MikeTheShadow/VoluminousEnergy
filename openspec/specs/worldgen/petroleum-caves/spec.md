# worldgen/petroleum-caves Specification

## Purpose

Defines the small sealed petroleum cave tunnels, filled with crude oil or natural gas, that generate in the
overworld, how they are sealed in bituminous blocks, and how they coexist with vanilla caves.

## Requirements

### Requirement: Petroleum cave tunnels generate in the overworld

Overworld chunks SHALL be able to contain cave tunnels filled with crude oil, generating between
y -40 and y 30, and separate cave tunnels filled with natural gas, generating between y -48 and
y 16. A tunnel system SHALL be one winding tunnel, sometimes starting from a small chamber with up to
two more tunnels branching from it, and SHALL span no more than 48 blocks horizontally from where it
starts. Petroleum tunnels SHALL start in far fewer chunks than vanilla caves. They SHALL generate in
every overworld biome and in no other dimension.

#### Scenario: Exploring a new world

- **WHEN** a player explores the overworld below y 30 in a newly generated world
- **THEN** they occasionally find a winding tunnel full of crude oil, and below y 16 occasionally one full of natural gas, each far rarer than ordinary caves

#### Scenario: Other dimensions

- **WHEN** chunks generate in the Nether or the End
- **THEN** no petroleum tunnels generate there

### Requirement: Petroleum tunnels are completely filled

Every position inside a petroleum tunnel SHALL be a source block of its fluid, with no air pockets, even
where the tunnel crosses a cave, ravine, or aquifer water carved or generated before it. Solid
blocks that vanilla carvers cannot replace SHALL be left in place inside the tunnel. The fluid SHALL sit still until
something next to it changes; generation SHALL NOT make it flow.

#### Scenario: Tunnel crossing an existing cave

- **WHEN** an oil tunnel's path passes through the air of a vanilla cave
- **THEN** the part of the cave inside the tunnel's outline is crude oil, not air

#### Scenario: Fluid at rest

- **WHEN** a player watches a fully enclosed gas tunnel through a glass window they placed in its shell
- **THEN** the gas does not flow or change while the shell is intact

### Requirement: Petroleum tunnels are sealed in bituminous blocks

Every position that shares a face with a tunnel's fluid and is not itself part of the tunnel SHALL
become a bituminous block, chosen from the block it replaces: Bituminous Sand for blocks in
`c:ore_bearing_ground/sand/colorless_sand`, Bituminous Red Sand for blocks in
`c:ore_bearing_ground/sand/red_sand`, and Bituminous Gravel for every other block, including air,
water, stone, and deepslate. The shell SHALL NOT replace blocks tagged
`minecraft:features_cannot_replace`, such as bedrock. The variant rule SHALL be data-driven in the
tunnel's feature JSON.

#### Scenario: Mining into an oil tunnel

- **WHEN** a player tunnels sideways into an oil cave tunnel through deepslate
- **THEN** they break a layer of Bituminous Gravel and then reach crude oil source blocks

#### Scenario: Tunnel crossing a cave

- **WHEN** a gas tunnel crosses a vanilla cave
- **THEN** both sides of the crossing, where the cave meets the tunnel's outline, are plugged with bituminous blocks, and no gas reaches the cave's air

#### Scenario: Breaching a gas tunnel

- **WHEN** a player breaks a shell block of a gas tunnel
- **THEN** natural gas flows out through the opening and rises

### Requirement: Vanilla caves do not cut through petroleum tunnels

Petroleum tunnels SHALL generate after every vanilla cave and canyon in the chunk has been carved,
so no vanilla carver ever replaces a tunnel's fluid or bituminous shell. A tunnel and its shell MAY
cut into caves and canyons carved before it. Where a vanilla cave meets a tunnel, the cave SHALL
end at the tunnel's bituminous shell.

#### Scenario: Vanilla cave meeting a tunnel

- **WHEN** a vanilla cave in a newly generated chunk reaches an oil tunnel
- **THEN** the cave stops at the bituminous blocks and the oil stays enclosed

#### Scenario: Canyon crossing a tunnel's path

- **WHEN** a vanilla canyon runs through the space a gas tunnel occupies
- **THEN** the tunnel's gas and shell fill that part of the canyon, and no part of the shell is missing

### Requirement: Petroleum tunnels are configurable by datapack

The tunnels `voluminousenergy:crude_oil_cave` and `voluminousenergy:natural_gas_cave` SHALL be
defined in data as features in the `worldgen/feature` registry, with their fluid, height range,
start probability, tunnel radius, shell variants, the blocks a tunnel may fill, the biomes a tunnel
may start in, and a salt that keeps different kinds of tunnel apart as top-level JSON fields. The biome modifier that adds them SHALL be a data file, so a datapack can
remove them. A tunnel that starts in an allowed biome SHALL be complete and sealed even where it
runs under other biomes.

#### Scenario: Datapack disables gas tunnels

- **WHEN** a datapack overrides the mod's biome modifier to add only `voluminousenergy:crude_oil_cave`
- **THEN** new chunks contain oil tunnels and no gas tunnels

#### Scenario: Datapack limits oil tunnels to deserts

- **WHEN** a datapack sets the crude oil cave's biomes to `#c:is_desert`
- **THEN** oil tunnels start only under deserts, and a tunnel that runs under a neighbouring plains biome is still filled and sealed there

#### Scenario: Datapack changes the fluid

- **WHEN** a datapack provides `data/voluminousenergy/worldgen/feature/crude_oil_cave.json` with the fluid set to `minecraft:lava`
- **THEN** those tunnels generate filled with lava inside the same bituminous shell
