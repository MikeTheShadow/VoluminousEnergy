## MODIFIED Requirements

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
| Compressed Air | `#c7c7c7` | Sulfuric Acid | `#ddde9a` |
| Light Fuels | `#afb05a` | Nitroglycerin | `#544c75` |
| Gasoline | `#c7cb4f` | Crude Oil | `#08040e` |
| Naphtha | `#afaf6d` | Tree Sap | `#5d430f` |
| Treethanol | `#bb9962` | Liquefied Coal | `#272727` |
| Dinitrogen Tetroxide | `#8496e9` | Liquefied Coke | `#313131` |
| Mercury | `#c7c7c7` | | |

#### Scenario: Fog matches the fluid
- **WHEN** a player puts their head into Red Fuming Nitric Acid
- **THEN** the fog and tint they see are orange

### Requirement: Syrupy liquids and Mercury use a tinted water overlay
With the camera inside a Syrupy liquid or Mercury, the player SHALL see the vanilla underwater overlay
texture tinted in the liquid's overlay colour. Fog SHALL hide terrain beyond about 3 blocks in Syrupy
liquids and about 1 block in Mercury.

#### Scenario: Head in sulfuric acid
- **WHEN** a player's head is submerged in Sulfuric Acid
- **THEN** a pale yellow-beige underwater texture is drawn over the view and terrain beyond about 3 blocks is hidden
