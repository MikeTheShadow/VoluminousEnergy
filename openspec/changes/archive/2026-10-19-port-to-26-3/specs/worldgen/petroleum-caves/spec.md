## MODIFIED Requirements

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
