## MODIFIED Requirements

### Requirement: Natural Gas behaves as a rising gas
Placed Natural Gas SHALL use the mod's gas flow behaviour: it rises from the placed block and spreads up
to 4 blocks sideways, the same width as Hydrogen and Oxygen. It SHALL move entities by the Gas movement
preset, lift entities in a rising column, and asphyxiate entities whose head is in it, as defined by the
fluid movement and hazard capabilities. It SHALL NOT extinguish fire, hydrate farmland, support boats,
or convert flowing blocks into sources. A full Natural Gas Bucket SHALL be picked up from and emptied
into the world with the vanilla empty-bucket sound.

#### Scenario: Emptying a bucket on the ground
- **WHEN** a player empties a Natural Gas Bucket onto a stone floor in open air
- **THEN** a Natural Gas column forms above the target block and rises, spreading at most 4 blocks horizontally, and the player holds an empty bucket

#### Scenario: Standing in Natural Gas
- **WHEN** a survival player stands with their head inside a rising Natural Gas column
- **THEN** the player's air supply drains, the player drifts slowly upward, and the player takes asphyxiation damage once the air bar is empty
