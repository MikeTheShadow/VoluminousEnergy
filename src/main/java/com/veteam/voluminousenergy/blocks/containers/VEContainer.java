package com.veteam.voluminousenergy.blocks.containers;

import com.veteam.voluminousenergy.VoluminousEnergy;
import com.veteam.voluminousenergy.blocks.tiles.VETileEntity;
import com.veteam.voluminousenergy.tools.energy.VEEnergyStorage;
import com.veteam.voluminousenergy.util.RegistryLookups;
import com.veteam.voluminousenergy.util.TagUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.SlotItemHandler;
import net.neoforged.neoforge.items.wrapper.InvWrapper;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public abstract class VEContainer extends AbstractContainerMenu {

    VETileEntity tileEntity;

    Player playerEntity;
    IItemHandler playerInventory;

    Block block;

    ContainerLevelAccess access;
    Level world;

    protected VEContainer(@Nullable MenuType<?> menuType, int id, Level world, BlockPos pos, Inventory inventory,
            Player player, Block block) {
        super(menuType, id);
        this.tileEntity = (VETileEntity) world.getBlockEntity(pos);
        this.playerEntity = player;
        this.playerInventory = new InvWrapper(inventory);
        this.block = block;
        this.access = ContainerLevelAccess.create(this.tileEntity.getLevel(), this.tileEntity.getBlockPos());
        this.world = world;

        // we add slots to GUI here
        if (tileEntity.getInventory() != null) {
            this.addSlotsToGUI(tileEntity.getInventory());
        }

        // layout player inventory slots here
        layoutPlayerInventorySlots();

        // We assume if it's a powered tile entity that it requires a dataslot for
        // energy
        if (this.tileEntity.getEnergy() != null) {
            addDataSlot(new DataSlot() {
                @Override
                public int get() {
                    return getEnergy();
                }

                // Setting anything here seems to cause a flickering on the client
                @Override
                public void set(int value) {
                }
            });

            addDataSlot(new DataSlot() {
                @Override
                public int get() {
                    return tileEntity.getEnergy().getProduction();
                }

                @Override
                public void set(int value) {
                    tileEntity.getEnergy().setProduction(value);
                }
            });

            addDataSlot(new DataSlot() {
                @Override
                public int get() {
                    return tileEntity.getEnergy().getConsumption();
                }

                @Override
                public void set(int value) {
                    tileEntity.getEnergy().setConsumption(value);
                }
            });
        }
    }

    protected abstract void addSlotsToGUI(IItemHandler h);

    /**
     * Override this if you wish to move where the inventory is displayed or disable
     * the inventory display entirely
     */
    void layoutPlayerInventorySlots() {
        // Player inventory
        addSlotBox(playerInventory, 9, 8, 84, 9, 18, 3, 18);

        // Hotbar
        int hotBar = 84 + 58;
        addSlotRange(playerInventory, 0, 8, hotBar, 9, 18);
    }

    @Override
    public boolean stillValid(@NotNull Player player) {
        return stillValid(this.access, this.playerEntity, this.block);
    }

    public VETileEntity getTileEntity() {
        return tileEntity;
    }

    protected int addSlotRange(IItemHandler handler, int index, int x, int y, int amount, int dx) {
        for (int i = 0; i < amount; i++) {
            addSlot(new SlotItemHandler(handler, index, x, y));
            x += dx;
            index++;
        }
        return index;
    }

    protected int addSlotBox(IItemHandler handler, int index, int x, int y, int horAmount, int dx, int verAmount,
            int dy) {
        for (int j = 0; j < verAmount; j++) {
            index = addSlotRange(handler, index, x, y, horAmount, dx);
            y += dy;
        }
        return index;
    }

    public int getEnergy() {
        VEEnergyStorage storage = tileEntity.getEnergy();
        return storage == null ? 0 : tileEntity.getEnergy().getEnergyStored();
    }

    public int powerScreen(int px) {
        int stored = tileEntity.getEnergy().getEnergyStored();
        int max = tileEntity.getEnergy().getMaxEnergyStored();
        return (((stored * 100 / max * 100) / 100) * px) / 100;
    }

    public void setTileEntity(VETileEntity tileEntity) {
        this.tileEntity = tileEntity;
    }

    public int getUpgradeSlotId() {
        VEEnergyStorage storage = this.tileEntity.getEnergy();
        if (storage != null) {
            return storage.getUpgradeSlotId();
        }
        VoluminousEnergy.LOGGER.error(
                "A container called getUpgradeSlotId when tile doesn't support upgrade slots! Offending tile is: "
                        + RegistryLookups.getBlockEntityTypeKey(tileEntity.getType()));
        return 0;
    }

    @Nonnull
    @Override
    public ItemStack quickMoveStack(final @NotNull Player player, final int index) {
        ItemStack returnStack = ItemStack.EMPTY;
        final Slot slot = this.slots.get(index);

        int tileEntitySlotCount = this.tileEntity.getSlotManagers().size();

        if (slot.hasItem()) {
            final ItemStack slotStack = slot.getItem();
            returnStack = slotStack.copy();

            if (index <= tileEntitySlotCount) {

                if(slot instanceof VEContainerFactory.VESlot veSlot) {
                    veSlot.preQuickMoveStack(slotStack);
                }

                if (!this.moveItemStackTo(slotStack, tileEntitySlotCount, this.slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
                slot.onQuickCraft(slotStack, returnStack);
            } else {
                if (TagUtil.isTaggedMachineUpgradeItem(slotStack)) {
                    int upgradeSlotId = getUpgradeSlotId();
                    if (!this.moveItemStackTo(slotStack, upgradeSlotId, upgradeSlotId + 1, false)) {
                        return ItemStack.EMPTY;
                    }
                } else if (!this.moveItemStackTo(slotStack, 0, tileEntitySlotCount, false)) {
                    return ItemStack.EMPTY;
                }
            }

            if (slotStack.isEmpty()) {
                slot.set(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }

            if (slotStack.getCount() == returnStack.getCount()) {
                return ItemStack.EMPTY;
            }

            int amountTaken = returnStack.getCount() - slotStack.getCount();
            if (amountTaken > 0) {
                ItemStack takenStack = returnStack.copy();
                takenStack.setCount(amountTaken);
                slot.onTake(player, takenStack);
            }
        }

        return returnStack;
    }

}
