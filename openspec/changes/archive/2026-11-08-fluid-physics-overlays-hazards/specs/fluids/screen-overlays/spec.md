## Purpose

Defines what a player sees with the camera inside a Voluminous Energy fluid: the fog colour and
distance, and the screen tint or overlay, chosen per movement preset so that gases read as coloured
haze and crude oil is hard to see through.

## ADDED Requirements

### Requirement: Each fluid has an overlay colour
Every Voluminous Energy fluid SHALL have an overlay colour, the base colour of its still texture, used
for its fog and screen effect:

| Fluid | Colour | Fluid | Colour |
|---|---|---|---|
| Hydrogen | `#07237e` | Diesel | `#cc933e` |
| Natural Gas | `#7f3596` | Biofuel | `#688100` |
| Ammonia | `#00afd6` | Ammonium Nitrate Solution | `#7f71db` |
| Nitrogen | `#795eab` | White Fuming Nitric Acid | `#c7c7c7` |
| Oxygen | `#a5bcbc` | Red Fuming Nitric Acid | `#fb7707` |
| Compressed Air | `#c7c7c7` | Sulfuric Acid | `#b1b1b1` |
| Light Fuels | `#afb05a` | Nitroglycerin | `#544c75` |
| Gasoline | `#c7cb4f` | Crude Oil | `#08040e` |
| Naphtha | `#afaf6d` | Tree Sap | `#5d430f` |
| Treethanol | `#bb9962` | Liquefied Coal | `#272727` |
| Dinitrogen Tetroxide | `#8496e9` | Liquefied Coke | `#313131` |
| Mercury | `#c7c7c7` | | |

#### Scenario: Fog matches the fluid
- **WHEN** a player puts their head into Red Fuming Nitric Acid
- **THEN** the fog and tint they see are orange

### Requirement: Gases show a coloured fog
With the camera inside a gas, the player SHALL see a fog in the gas's overlay colour that starts close
to the camera and hides terrain beyond about 24 blocks. The fog colour SHALL be strong enough that the
gas is identifiable by colour alone. No textured overlay SHALL be drawn.

#### Scenario: Inside a Natural Gas cloud
- **WHEN** a player stands with their head in Natural Gas in daylight
- **THEN** their view fades into purple fog and terrain 24 blocks away is hidden, with no texture drawn over the screen

#### Scenario: Telling gases apart
- **WHEN** a player puts their head into Hydrogen and then into Ammonia
- **THEN** the fog is deep blue in Hydrogen and cyan in Ammonia

### Requirement: Thin and watery liquids tint the view
With the camera inside a Thin or Watery liquid, the whole view SHALL be tinted evenly in the liquid's
overlay colour, like looking through tinted glass, with no texture drawn. Distant terrain SHALL fade at
about 16 blocks in Thin liquids and about 12 blocks in Watery liquids.

#### Scenario: Swimming in diesel
- **WHEN** a player swims with their head in Diesel
- **THEN** the entire view is evenly tinted amber with no moving texture pattern, and terrain within about 12 blocks stays visible

### Requirement: Syrupy liquids and Mercury use a tinted water overlay
With the camera inside a Syrupy liquid or Mercury, the player SHALL see the vanilla underwater overlay
texture tinted in the liquid's overlay colour. Fog SHALL hide terrain beyond about 3 blocks in Syrupy
liquids and about 1 block in Mercury.

#### Scenario: Head in sulfuric acid
- **WHEN** a player's head is submerged in Sulfuric Acid
- **THEN** a grey-tinted underwater texture is drawn over the view and terrain beyond about 3 blocks is hidden

### Requirement: Sticky liquids black out the view
With the camera inside a Sticky liquid, the view SHALL be dark: a strong overlay in the liquid's overlay
colour over the whole screen, with fog that hides terrain beyond about 1 block.

#### Scenario: Head in crude oil
- **WHEN** a player's head is submerged in Crude Oil at noon
- **THEN** the screen is nearly black and only blocks within about 1 block are faintly visible

### Requirement: Overlay scope
Fluid fog and screen effects SHALL apply only while the camera is inside the fluid, SHALL NOT cover the
HUD, chat, or open screens, and SHALL NOT apply to spectators. They SHALL be client-side only and SHALL
NOT affect a dedicated server.

#### Scenario: Leaving the fluid
- **WHEN** a player lifts their head out of Crude Oil
- **THEN** the overlay and fog disappear on the next frame

#### Scenario: Spectating inside a fluid
- **WHEN** a player in spectator mode flies into Crude Oil
- **THEN** their view is not darkened

#### Scenario: Dedicated server
- **WHEN** a dedicated server with the mod starts and a player joins and swims in every fluid
- **THEN** the server starts and runs without errors
