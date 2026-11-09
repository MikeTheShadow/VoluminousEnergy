## Why

On 26.2, Sulfuric Acid was recoloured from grey to the pale yellow-beige of vanilla's Sulfur Cube
(`fluids/sulfuric-acid`). Its fog and screen overlay still use the grey `#b1b1b1` carried over from
1.21.1, so a player with their head in yellow acid sees grey fog and a grey underwater texture.

## What Changes

- Sulfuric Acid's overlay colour changes from `#b1b1b1` to `#ddde9a`, the average colour of the
  recoloured still texture. This is the same rule the screen-overlays spec already uses for every
  fluid ("the base colour of its still texture").
- The "Head in sulfuric acid" scenario describes a pale yellow-beige underwater texture instead of a
  grey one.
- Fog distance, overlay style, alpha and the acid's hazards do not change.

No save, registry, attachment or config changes.

## Capabilities

### New Capabilities

None.

### Modified Capabilities

- `fluids/screen-overlays`: Sulfuric Acid's entry in the overlay colour table, and the Sulfuric Acid
  scenario under the Syrupy liquids requirement.

## Impact

- `fluids/SulfuricAcid.java`: the overlay colour argument passed to `VEFluidType`.
- Branches: lands on `26.2-dev` and merges forward into `26.3-dev`. `1.21.1-dev` and `26.1-dev` keep
  the grey texture and the grey `#b1b1b1` overlay, so there is nothing to port back.
