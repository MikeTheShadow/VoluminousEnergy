## Purpose

Defines what Voluminous Energy fluids do to the entities inside them: drowning in liquids, asphyxiation
in unbreathable gases, breathable gases, chemical burns from acids, and poisoning from Mercury.

## ADDED Requirements

### Requirement: Liquids drown
Every Voluminous Energy liquid except Mercury SHALL drown an entity whose head is submerged, the way
water does, acids included. Entities that do not breathe, such as undead mobs, SHALL NOT drown in them.

#### Scenario: Head under diesel
- **WHEN** a survival player keeps their head under Diesel for 20 seconds
- **THEN** their air bar empties and they take drowning damage

#### Scenario: Zombie in crude oil
- **WHEN** a zombie stands fully submerged in Crude Oil for 30 seconds
- **THEN** it takes no drowning damage

### Requirement: Unbreathable gases asphyxiate
Every Voluminous Energy gas except Oxygen and Compressed Air SHALL asphyxiate an entity whose head is in
it: the air bar SHALL drain at the underwater rate, and once empty the entity SHALL take asphyxiation
damage at the same rate and amount as vanilla drowning. Asphyxiation SHALL NOT emit bubble particles.
Water Breathing, Conduit Power, and the turtle shell's Water Breathing SHALL NOT protect against it.
Respiration SHALL slow the air drain as it does underwater. Players in creative or spectator mode, and
entities that do not breathe, such as undead mobs, SHALL NOT asphyxiate.

#### Scenario: Head in nitrogen
- **WHEN** a survival player stands with their head in Nitrogen for 20 seconds
- **THEN** their air bar empties and they take asphyxiation damage, with no bubble particles

#### Scenario: Water breathing in natural gas
- **WHEN** a survival player under a Potion of Water Breathing keeps their head in Natural Gas
- **THEN** their air bar drains and they asphyxiate as without the potion

#### Scenario: Creative player in hydrogen
- **WHEN** a creative-mode player stands with their head in Hydrogen
- **THEN** their air bar does not drain

#### Scenario: Skeleton in light fuels
- **WHEN** a skeleton stands with its head in Light Fuels for 30 seconds
- **THEN** it takes no asphyxiation damage

### Requirement: Breathable gases restore air
Oxygen and Compressed Air SHALL be breathable: an entity whose head is in either SHALL NOT lose air, and
its air bar SHALL refill at the rate it refills in open air.

#### Scenario: Surfacing into oxygen
- **WHEN** a player with a half-empty air bar puts their head into an Oxygen cloud
- **THEN** the air bar refills at the open-air rate

### Requirement: Asphyxiation damage type
Asphyxiation SHALL be dealt as the damage type `voluminousenergy:asphyxiation`, which SHALL bypass armour
like vanilla drowning. A player killed by it SHALL show the death message "<player> asphyxiated", or
"<player> asphyxiated while trying to escape <attacker>" when an attacker was involved.

#### Scenario: Death message
- **WHEN** a survival player dies with their head in Ammonia
- **THEN** chat shows "<player> asphyxiated"

#### Scenario: Armour does not help
- **WHEN** two players, one in full diamond armour and one without, both asphyxiate in Nitrogen
- **THEN** both take the same asphyxiation damage

### Requirement: Acids deal chemical burns
Entities in an acid SHALL take damage of the type `voluminousenergy:acid` each time they can be hurt,
scaled from the `Acid Damage` config value (default 3) by the acid's strength:

| Acid | Strength |
|---|---|
| Sulfuric Acid | 1.0× |
| White Fuming Nitric Acid | 1.25× |
| Red Fuming Nitric Acid | 1.5× |

The acid damage type SHALL NOT be a fire damage type: Fire Resistance and fire immunity SHALL NOT
prevent it, and armour SHALL reduce it like ordinary damage. Dropped items in an acid SHALL be destroyed,
including items that survive fire and lava. Acids SHALL only harm entities in contact with the fluid.
A player killed by acid SHALL show the death message "<player> was burned by acid", or "<player> was
burned by acid while trying to escape <attacker>" when an attacker was involved.

#### Scenario: Fire resistance in sulfuric acid
- **WHEN** a survival player under a Potion of Fire Resistance wades into Sulfuric Acid with `Acid Damage` at 3
- **THEN** they take 3 acid damage per hurt and are not set on fire

#### Scenario: Red fuming is strongest
- **WHEN** a player with no armour enters Red Fuming Nitric Acid with `Acid Damage` at 3
- **THEN** they take 4.5 acid damage per hurt

#### Scenario: Blaze in acid
- **WHEN** a blaze is pushed into White Fuming Nitric Acid
- **THEN** it takes acid damage

#### Scenario: Netherite in acid
- **WHEN** a player drops a netherite ingot into Sulfuric Acid
- **THEN** the ingot is destroyed

#### Scenario: Standing beside an acid
- **WHEN** a player stands on a block next to a pool of Red Fuming Nitric Acid without touching it
- **THEN** they take no damage

### Requirement: Fuming nitric acids ignite
White and Red Fuming Nitric Acid SHALL also set entities in them on fire for the `Acid Fire Duration`
config value, in ticks (default 80). Sulfuric Acid SHALL NOT set entities on fire.

#### Scenario: Stepping into white fuming nitric acid
- **WHEN** a player without Fire Resistance steps into White Fuming Nitric Acid and straight back out with `Acid Fire Duration` at 80
- **THEN** they are on fire for about 4 seconds after leaving

#### Scenario: Sulfuric acid does not ignite
- **WHEN** a player steps into Sulfuric Acid and straight back out
- **THEN** they are not on fire

### Requirement: Mercury poisons
An entity in Mercury SHALL receive Poison I, refreshed to the `Mercury Poison Duration` config value in
ticks (default 100) while it stays in contact. Mercury SHALL NOT drown, burn, or set entities on fire.
Entities immune to Poison, such as undead mobs, SHALL be unaffected.

#### Scenario: Wading in mercury
- **WHEN** a survival player wades through Mercury and steps out
- **THEN** they are poisoned and the Poison lasts about 5 seconds after leaving

#### Scenario: No drowning in mercury
- **WHEN** a player is held with their head inside Mercury
- **THEN** their air bar does not drain
