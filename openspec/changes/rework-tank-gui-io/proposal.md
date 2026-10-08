# Proposal

## Why

Players currently have to move vanilla buckets through two dedicated inventory slots for every machine tank. Replacing those slots with direct tank interaction makes fluid handling clearer, supports NeoForge-compatible modded fluid containers, and allows the affected GUIs to use that space for clearer tank and processing layouts.

## What Changes

- Remove the dedicated bucket input/output slots from every machine and standalone tank container.
- Redesign only the affected tank GUI textures with local Aseprite, removing bucket-slot artwork and arranging tanks around the retained item slots and processing indicators without changing window dimensions. Every displayed tank retains the original `12 × 50` fluid-interior size.
- Move rendering, container slot coordinates, tank interaction regions, IO labels, progress/fuel hover areas, and JEI recipe click regions together whenever a component moves.
- Let a player left-click a rendered tank with a compatible carried fluid container to take fluid and right-click it to put fluid into tanks that accept input.
- Use item fluid capabilities so vanilla buckets and compatible modded containers can participate, with server-authoritative and transactional updates.
- Add localized tank-hover guidance for the left-click and right-click controls, while retaining fluid name, amount, and capacity.
- Update recipe parser mappings and custom processors, validators, and slot listeners whose absolute inventory indices change when the bucket slots are removed.
- Remove the obsolete tick-driven bucket-slot transfer path and its slot types after all registrations stop using it.
- **BREAKING**: Change machine inventory layouts without legacy remapping, item recovery, or saved-world compatibility. This branch will receive additional breaking changes and requires a fresh test world.
- Keep registry ids, configuration values, player-inventory placement, and window dimensions unchanged.
- Follow-up outside this branch: port the approved behavior to other maintained Minecraft-version branches separately.

## Capabilities

### New Capabilities

- `machine-tank-interaction`: Direct GUI interaction, tooltip guidance, validation, synchronization, and compact inventory layouts for machine and standalone tanks.

### Modified Capabilities

None; the repository has no existing OpenSpec capabilities.

## Impact

- Container and tile factories: `VEContainers`, `VEContainerFactory`, `VETileEntityFactory`, `VETileEntity`, `VEItemStackHandler`, and related slot/tank wrappers.
- Client GUI: `VEContainerScreen`, the fourteen affected machine screens, shared `TankScreen`, their fourteen GUI textures, `TextUtil`, English localization, and affected JEI GUI handlers.
- Verification: extend `docs/manual-testing/tank-gui-rework.md` with layout, hover, and JEI checks. In-game verification is deferred until the branch's other changes are ready.
- Networking: payload registration plus a new client-to-server tank interaction payload and handler.
- Recipes and machine logic: `AqueoulizerRecipe`, `IndustrialBlastingRecipe`, `DistillationRecipe`, `HydroponicIncubatorRecipe`, `GasFiredFurnaceProcessor`, `DimensionalLaserRecipeProcessor`, and tooling-station processing, validation, and slot-listener code.
- Saved worlds and placed machine data: pre-change machine inventories are unsupported and are not migrated.
- Dependencies and config: no new external dependency and no configuration-key changes.
