## Why

On 26.1, gases placed in the world are invisible. Natural Gas caves and reservoirs generate (a
same-seed comparison with 1.21.1 finds about as many natural gas blocks), but players see empty caves,
and the same is true of every gas a player empties from a bucket. Every gas block counts as air, and
26.1's chunk section compiler no longer draws the fluid of any block whose state counts as air, so
`GaseousFluidRenderer` is never called for a placed gas. 1.21.1 draws fluids regardless, which is why
the bug only appears on the port.

## What Changes

- Natural Gas, Hydrogen, Oxygen, Nitrogen, Ammonia, and Light Fuel are drawn in the world again with
  their own textures and colours, including gas generated in petroleum caves and reservoirs.
- Gas blocks stop counting as air and count as silent liquids instead, like vanilla water. They stay
  replaceable, so players can still place blocks into gas, empty buckets into it, and have falling
  blocks drop through it, with no break sound or particles.
- Gas columns in open air rise to y 320, the gas code's own limit. While gases counted as air, vanilla
  dropped gas placed into an otherwise empty 16-block chunk section, so columns stopped at the top of
  the section they started in.
- Vanilla decoration that only grows or places into air (glow lichen, cave vines, dripstone, moss,
  bone meal growth, vine spread, fire) does not generate or grow inside gas, so gas caves and
  reservoirs keep their gas. This is intended. On 1.21.1 that decoration overwrites the gas, and 1.21.1
  keeps that behaviour.
- Other side effects of gas no longer counting as air, all accepted:
  - Structures and features that look for open air no longer treat a gas tunnel as open space, so a
    dungeon no longer generates inside one, and mineshafts avoid gas as they avoid water.
  - Oil lakes do not generate where gas touches their upper border, and cactus does not grow with gas
    above it.
  - The `WORLD_SURFACE` heightmap counts gas, so a gas column that rises above the terrain raises it.
    Rain and snow already stopped at gas, through the `MOTION_BLOCKING` heightmap.
- No change to gas flow, breathing, collision, or the worldgen code's own fill rules, which already
  test for air and cave air by block rather than by `isAir()`.

## Capabilities

### New Capabilities
- `fluids/gases`: how the mod's gases appear and interact with blocks once placed in the world:
  in-world rendering, replacing gas with placed blocks and fluids, and gas not counting as empty space.

### Modified Capabilities
None. The `fluids/natural-gas` appearance requirement already requires Natural Gas to render. This
change makes 26.1 meet it.

## Impact

- Code: block properties in the six gas fluid classes under `fluids/` (`Ammonia`, `Hydrogen`,
  `LightFuel`, `NaturalGas`, `Nitrogen`, `Oxygen`); a comment in `VEFluidReservoirFeature` that gives
  "natural gas counts as air" as its reason. The first in-world use of `GaseousFluidRenderer` on 26.1
  may expose problems in it that the port could not show.
- Save compatibility: no registry id, block state, NBT, data component, or attachment changes.
  Existing worlds keep their gas blocks, which now render. Sections that hold only gas now count as
  non-empty.
- Config: no new or changed options.
- Datagen: none.
- Follow-up ports, not scope: `26.2-dev` has the same section compiler check and receives this change
  when 26.1-dev is next merged into it. `1.21.1-dev` keeps `.air()`, because 1.21.1 renders gas
  correctly and its worldgen and growth behaviour depend on gas counting as air.
