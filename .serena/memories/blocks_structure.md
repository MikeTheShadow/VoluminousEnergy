## Machine wiring

Machines are factory-assembled. Generic `VETileEntity` and `VEContainer`; no per-machine tile or container class.

- `VEBlocks`: one `BlockTileMenuRegistry` per machine (`.block()`, `.tile()`, `.container()`).
- `blocks/blocks/machines/<Name>Block`: `newBlockEntity` delegates to its factory.
- `VETileEntities`: `VETileEntityFactory` builder (energy, `FluidInputTank`/`FluidOutputTank`, recipe type,
  inventory validator, processor). Energy numbers come from `Config`.
- `VEContainers`: `VEContainerFactoryBuilder().create(...).addSlot(x, y, <SlotType>(direction))...build()`.
- `blocks/screens/<Name>Screen`: one per machine, registered in `VESetup`.
- `recipe/processor/`: behaviour (`BasicProcessor`, or an `AbstractRecipeProcessor` subclass).
- `blocks/tiles/inventory/<Name>InventoryValidator`: slot insertion rules.

A machine change usually touches several of these; check all of them by machine name.
