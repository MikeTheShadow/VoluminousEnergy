## Purpose

Natural Gas is a raw gaseous fuel that sits beside crude oil in the energy chain. This capability covers
how the fluid is identified, how it behaves when placed, how it looks, which tags expose it, and how it
burns as a combustion fuel.

## ADDED Requirements

### Requirement: Natural Gas registry ids
The mod SHALL register Natural Gas under these ids: source fluid `voluminousenergy:natural_gas`, flowing
fluid `voluminousenergy:flowing_natural_gas`, fluid type `voluminousenergy:natural_gas`, fluid block
`voluminousenergy:natural_gas_block`, and bucket item `voluminousenergy:natural_gas_bucket`. The bucket
SHALL stack to 1 and leave an empty bucket when used as a crafting ingredient.

#### Scenario: Ids resolve on a dedicated server
- **WHEN** a dedicated server starts with the mod and an operator runs `/give @p voluminousenergy:natural_gas_bucket`
- **THEN** the server starts without errors and the player receives one Natural Gas Bucket

#### Scenario: Bucket appears in the creative tab
- **WHEN** a player opens the Voluminous Energy creative tab
- **THEN** the Natural Gas Bucket is listed alongside the other fluid buckets

### Requirement: Natural Gas behaves as a rising gas
Placed Natural Gas SHALL use the mod's gas flow behaviour: it rises from the placed block and spreads up
to 4 blocks sideways, the same width as Hydrogen and Oxygen. It SHALL NOT drown entities, extinguish
fire, hydrate farmland, push entities, support boats, or convert flowing blocks into sources. A full
Natural Gas Bucket SHALL be picked up from and emptied into the world with the vanilla empty-bucket
sound.

#### Scenario: Emptying a bucket on the ground
- **WHEN** a player empties a Natural Gas Bucket onto a stone floor in open air
- **THEN** a Natural Gas column forms above the target block and rises, spreading at most 4 blocks horizontally, and the player holds an empty bucket

#### Scenario: Standing in Natural Gas
- **WHEN** a player stands inside placed Natural Gas
- **THEN** the player's air supply does not drop and the player is not pushed

### Requirement: Natural Gas appearance
Natural Gas SHALL render with its own animated still and flowing textures, and the Natural Gas Bucket
SHALL show its own fluid colour. The colour SHALL be a saturated mid purple, halfway between blue and red
in hue (about 286°), visibly distinct from Nitrogen's blue-lavender, Hydrogen's deep blue, and the red
and burgundy fluids. Tanks and GUIs that draw a fluid's still texture SHALL show the same purple.

#### Scenario: Placed beside Nitrogen
- **WHEN** Natural Gas and Nitrogen are placed side by side in daylight
- **THEN** Natural Gas reads as purple and Nitrogen as blue-lavender, and the two are not mistaken for each other

#### Scenario: Shown in a machine tank
- **WHEN** a Combustion Generator fuel tank holds Natural Gas
- **THEN** the tank gauge draws the purple Natural Gas texture

### Requirement: Natural Gas conventional tags
The fluid tag `c:natural_gas` SHALL contain `voluminousenergy:natural_gas`, and the existing `c:gaseous`
fluid tag SHALL also contain it. Both tags SHALL be non-replacing so other mods can add their own
natural gas.

#### Scenario: Tag query
- **WHEN** an operator runs a datapack function or tag query for fluids in `#c:natural_gas` and `#c:gaseous`
- **THEN** `voluminousenergy:natural_gas` is a member of both

#### Scenario: Another mod's natural gas
- **WHEN** another mod adds its own fluid to `c:natural_gas`
- **THEN** that fluid is accepted wherever a recipe asks for `c:natural_gas`

### Requirement: Natural Gas is a combustion fuel
Natural Gas SHALL be listed in the `voluminousenergy:combustible` fluid tag and SHALL burn in the
Combustion Generator with any oxidizer at a base output of 24 FE per tick, scaled by the oxidizer's
multiplier like every other fuel. The fuel recipe SHALL be keyed on `c:natural_gas` with 250 mB of fuel
and 250 mB of oxidizer per operation, matching the crude oil fuel recipe's amounts.

#### Scenario: Burning with oxygen
- **WHEN** a Combustion Generator has Natural Gas in its fuel tank and Oxygen in its oxidizer tank
- **THEN** it generates 24 FE per tick multiplied by Oxygen's oxidizer multiplier, consuming 250 mB of each fluid per operation

#### Scenario: Shown in JEI
- **WHEN** JEI is installed and a player looks up uses of the Natural Gas Bucket
- **THEN** the combustion recipe lists Natural Gas as a fuel

#### Scenario: Datapack override
- **WHEN** a datapack replaces `voluminousenergy:fluid_data/combustion/natural_gas.json` with a different `energy_per_tick`
- **THEN** after `/reload` the Combustion Generator uses the datapack's value for Natural Gas

### Requirement: Natural Gas names
The fluid, fluid type, block, and bucket SHALL have English names "Natural Gas" and "Natural Gas Bucket",
and Japanese names "天然ガス" and "天然ガスバケツ".

#### Scenario: English tooltip
- **WHEN** a player with English (US) selected hovers over the bucket
- **THEN** the tooltip reads "Natural Gas Bucket"
