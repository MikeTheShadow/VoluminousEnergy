## Purpose

Defines how entities and dropped items move inside Voluminous Energy fluids, so that each fluid moves
like the substance it represents: gases barely hinder movement, oils and tars are sticky, and dense
liquids hold entities up.

## ADDED Requirements

### Requirement: Fluid types report real density and viscosity
Every Voluminous Energy fluid type SHALL report a density and viscosity derived from the real substance,
in NeoForge's units where water is 1000 for both (density in kg/m³, viscosity in mPa·s × 1000). Gases
SHALL report a density of 0 or less, NeoForge's convention for a fluid lighter than air, because every
Voluminous Energy gas rises.

| Fluid | Density | Viscosity |
|---|---|---|
| Hydrogen | -1 | 9 |
| Natural Gas | -1 | 11 |
| Ammonia | -1 | 10 |
| Nitrogen | 0 | 18 |
| Oxygen | 0 | 20 |
| Compressed Air | 0 | 18 |
| Light Fuels | 0 | 8 |
| Gasoline | 740 | 600 |
| Naphtha | 720 | 550 |
| Treethanol | 789 | 1200 |
| Dinitrogen Tetroxide | 1440 | 420 |
| Diesel | 840 | 3000 |
| Biofuel | 880 | 5000 |
| Ammonium Nitrate Solution | 1230 | 2500 |
| White Fuming Nitric Acid | 1510 | 760 |
| Red Fuming Nitric Acid | 1550 | 1000 |
| Sulfuric Acid | 1830 | 24600 |
| Nitroglycerin | 1600 | 36000 |
| Crude Oil | 900 | 5000000 |
| Tree Sap | 1050 | 2000000 |
| Liquefied Coal | 1200 | 1000000 |
| Liquefied Coke | 1250 | 1000000 |
| Mercury | 13534 | 1530 |

#### Scenario: Another mod reads crude oil's viscosity
- **WHEN** another mod queries the viscosity of the `voluminousenergy:crude_oil` fluid type
- **THEN** it receives 5000000, a value well above water's 1000

#### Scenario: Gases are lighter than air
- **WHEN** another mod asks whether the `voluminousenergy:hydrogen` fluid type is lighter than air
- **THEN** the answer is yes, and the same holds for every Voluminous Energy gas

### Requirement: Movement presets
Every Voluminous Energy fluid SHALL move entities according to exactly one movement preset. The presets
and their fluids SHALL be:

| Preset | Fluids | Movement |
|---|---|---|
| Gas | Hydrogen, Natural Gas, Ammonia, Nitrogen, Oxygen, Compressed Air, Light Fuels | close to air: walking and falling are barely slowed, no swimming |
| Thin | Gasoline, Naphtha, Treethanol, Dinitrogen Tetroxide | slightly less resistance than water |
| Watery | Diesel, Biofuel, Ammonium Nitrate Solution, White Fuming Nitric Acid, Red Fuming Nitric Acid | about as resistant as water |
| Syrupy | Sulfuric Acid, Nitroglycerin | clearly slower than water in every direction |
| Sticky | Crude Oil, Tree Sap, Liquefied Coal, Liquefied Coke | a slow wade: horizontal speed about an eighth of walking speed on land (about a third of the speed in water), jumps barely lift, sinking is slow |
| Mercury | Mercury | resistant like Syrupy, with buoyancy strong enough that an entity cannot stay submerged |

Entities SHALL NOT be frozen in place or have their motion reversed by any fluid; motion is slowed
smoothly by the preset.

#### Scenario: Wading through crude oil
- **WHEN** a player walks across a 1-block-deep pool of Crude Oil on a flat floor without sprinting
- **THEN** they move at roughly an eighth of their normal walking speed and keep moving steadily, without stuttering

#### Scenario: Walking through a gas
- **WHEN** a player walks through a room filled with Nitrogen
- **THEN** they move at close to their normal walking speed and cannot swim

#### Scenario: Syrupy is slower than watery
- **WHEN** a player swims the same distance once in Sulfuric Acid and once in Diesel
- **THEN** the Sulfuric Acid crossing takes clearly longer

### Requirement: Sinking and floating by density
In every liquid preset, an entity's vertical drift SHALL follow the fluid's density relative to a body
slightly denser than water. In a liquid denser than water the entity SHALL be pushed toward the surface,
more strongly the denser the liquid, and settle floating with roughly the real submerged fraction
(about half in Sulfuric Acid, under a tenth in Mercury). In a liquid less dense than water the entity
SHALL sink faster than in water. In Mercury, a player holding sneak SHALL NOT be able to stay fully
submerged.

#### Scenario: Floating in sulfuric acid
- **WHEN** a player stops all input while fully submerged in a deep pool of Sulfuric Acid
- **THEN** they rise to the surface and float there

#### Scenario: Sinking in gasoline
- **WHEN** a player stops all input at the surface of a deep pool of Gasoline
- **THEN** they sink, faster than they would in water

#### Scenario: Trying to dive in mercury
- **WHEN** a player holds sneak at the surface of a 3-block-deep pool of Mercury
- **THEN** they stay at or near the surface and do not reach the bottom

### Requirement: Rising gas lifts entities
An entity in a rising column of gas SHALL be lifted: with no input it SHALL rise slowly, and holding
sneak SHALL let it descend. Gas spreading sideways SHALL nudge entities gently in its flow direction.
A gas source block on its own, with no rising column, SHALL NOT lift.

#### Scenario: Standing in a rising Natural Gas column
- **WHEN** a player stands without input inside a Natural Gas column rising from an emptied bucket
- **THEN** the player drifts slowly upward through the column

#### Scenario: Standing at the base of a gas column
- **WHEN** a player stands without input on the floor in the source block of a Natural Gas column rising from an emptied bucket
- **THEN** the player starts drifting upward without jumping

#### Scenario: Descending through a gas column
- **WHEN** the same player holds sneak inside the column
- **THEN** the player descends

### Requirement: Jumping in gases works as in air
A player standing on the ground inside a gas SHALL jump as high as they would in open air, and holding
jump in mid-air inside a gas SHALL NOT slow their fall or let them climb. Gases SHALL NOT allow
swimming.

#### Scenario: Jumping in a nitrogen room
- **WHEN** a player standing on the floor of a Nitrogen-filled room presses jump
- **THEN** they jump the same height as in open air

#### Scenario: Holding jump in a gas
- **WHEN** a player jumps in a Hydrogen-filled room and keeps holding jump
- **THEN** they land after the same time as an ordinary jump, without floating

### Requirement: Gas fills its whole block
For entities, breathing, and the camera, a block of any Voluminous Energy gas SHALL count as filled for
its full height, whatever height the gas is drawn at. A player whose head is in a block containing gas
SHALL count as having their head in that gas.

#### Scenario: Head in a ceiling layer of gas
- **WHEN** a player stands in a 2-block-tall room whose upper block holds Nitrogen pooled against the ceiling
- **THEN** the player counts as having their head in Nitrogen

### Requirement: Fluids cushion falls by phase
Falling into a Voluminous Energy liquid SHALL cushion the fall the way water does. Falling through a gas
SHALL NOT cushion the fall: an entity that falls through a gas onto the ground SHALL take the same fall
damage as in air.

#### Scenario: Falling through a gas column
- **WHEN** a player falls 20 blocks through a Hydrogen column onto stone
- **THEN** they take the same fall damage as a 20-block fall in open air

#### Scenario: Falling into diesel
- **WHEN** a player falls 20 blocks into a 3-block-deep pool of Diesel
- **THEN** they take no fall damage

### Requirement: Dropped items follow the fluid
Dropped item entities SHALL fall through gases as through air, float to the surface of liquids denser
than water and of Mercury, sink in liquids less dense than water, and drift slowly in the Sticky
preset.

#### Scenario: Item dropped into mercury
- **WHEN** a player drops an iron ingot into a pool of Mercury
- **THEN** the ingot floats on the surface

#### Scenario: Item dropped into a gas
- **WHEN** a player drops an item into the top of a Hydrogen column
- **THEN** the item falls through the gas to the floor at the same speed as in air

### Requirement: Entities can leave any pool
In every fluid, a player SHALL be able to climb out of a 1-block-deep pool onto an adjacent full block
at the pool's rim by walking into the rim while holding jump, as they can out of water.

#### Scenario: Climbing out of crude oil
- **WHEN** a player standing in a 1-block-deep pool of Crude Oil walks into the full block at the pool's edge while holding jump
- **THEN** the player climbs up onto that block
