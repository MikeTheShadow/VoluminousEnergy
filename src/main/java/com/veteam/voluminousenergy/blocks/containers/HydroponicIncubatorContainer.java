package com.veteam.voluminousenergy.blocks.containers;

import com.veteam.voluminousenergy.blocks.blocks.VEBlocks;
import com.veteam.voluminousenergy.blocks.inventory.slots.VEBucketSlot;
import com.veteam.voluminousenergy.blocks.inventory.slots.VEInsertSlot;
import com.veteam.voluminousenergy.tools.energy.VEEnergyStorage;
import com.veteam.voluminousenergy.util.recipe.RecipeUtil;
import com.veteam.voluminousenergy.util.TagUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.DataSlot;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.items.wrapper.InvWrapper;

import javax.annotation.Nonnull;

public class HydroponicIncubatorContainer extends VoluminousContainer {

    public static final int NUMBER_OF_SLOTS = 8;

    public HydroponicIncubatorContainer(int id, Level world, BlockPos pos, Inventory inventory, Player player) {
        super(VEBlocks.HYDROPONIC_INCUBATOR_CONTAINER.get(), id);
        this.tileEntity = world.getBlockEntity(pos);
        this.tileEntity.getCapability(ForgeCapabilities.ITEM_HANDLER);
        this.playerEntity = player;
        this.playerInventory = new InvWrapper(inventory);

        tileEntity.getCapability(ForgeCapabilities.ITEM_HANDLER).ifPresent(h -> {
            addSlot(new VEBucketSlot(h, 0, 38, 18)); // Bucket top slot
            addSlot(new VEBucketSlot(h, 1, 38, 49)); // Bucket bottom slot
            addSlot(new VEInsertSlot(h, 2, 83, 34)); // Primary input
            addSlot(new VEInsertSlot(h, 3, 123, 8)); // Primary output
            addSlot(new VEInsertSlot(h, 4, 123, 26)); // RNG0 output
            addSlot(new VEInsertSlot(h, 5, 123, 44)); // RNG1 output
            addSlot(new VEInsertSlot(h, 6, 123, 62)); // RNG2 output
            addSlot(new VEInsertSlot(h, 7, 154,-14)); // Upgrade slot
        });
        layoutPlayerInventorySlots(8, 84);

        addDataSlot(new DataSlot() {
            @Override
            public int get() {
                return getEnergy();
            }

            @Override
            public void set(int value) {
                tileEntity.getCapability(ForgeCapabilities.ENERGY).ifPresent(h -> ((VEEnergyStorage) h).setEnergy(value));
            }
        });
    }

    public int getEnergy() {
        return tileEntity.getCapability(ForgeCapabilities.ENERGY).map(IEnergyStorage::getEnergyStored).orElse(0);
    }

    public int powerScreen(int px) {
        int stored = tileEntity.getCapability(ForgeCapabilities.ENERGY).map(IEnergyStorage::getEnergyStored).orElse(0);
        int max = tileEntity.getCapability(ForgeCapabilities.ENERGY).map(IEnergyStorage::getMaxEnergyStored).orElse(0);
        int ret = (((stored * 100 / max * 100) / 100) * px) / 100;
        return ret;
    }

    @Override
    public boolean stillValid(Player playerIn) {
        return stillValid(ContainerLevelAccess.create(tileEntity.getLevel(), tileEntity.getBlockPos()), playerEntity, VEBlocks.HYDROPONIC_INCUBATOR_BLOCK.get());
    }

    private void layoutPlayerInventorySlots(int leftCol, int topRow) {
        // Player inventory
        addSlotBox(playerInventory, 9, leftCol, topRow, 9, 18, 3, 18);

        // Hotbar
        topRow += 58;
        addSlotRange(playerInventory, 0, leftCol, topRow, 9, 18);
    }

    @Nonnull
    @Override
    public ItemStack quickMoveStack(final Player player, final int index) {
        ItemStack returnStack = ItemStack.EMPTY;
        final Slot slot = this.slots.get(index);

        if (slot != null && slot.hasItem()) {
            final ItemStack slotStack = slot.getItem();
            returnStack = slotStack.copy();

            ItemStack processedStack = handleCoreQuickMoveStackLogicWithUpgradeSlot(index, NUMBER_OF_SLOTS, 7, slotStack);
            if (processedStack != null && processedStack.getCount() > 0) {
                return processedStack;
            }

            if (slotStack.getCount() == 0) {
                slot.set(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }

            if (slotStack.getCount() == returnStack.getCount()) {
                return ItemStack.EMPTY;
            }

            slot.onTake(player, slotStack);
        }
        return returnStack;
    }

    @Override
    public ItemStack handleCoreQuickMoveStackLogicWithUpgradeSlot(final int index, final int containerSlots, final int upgradeSlotId, ItemStack slotStack){
        if (index < containerSlots) {
            if (!moveItemStackTo(slotStack, containerSlots, this.slots.size(), true)) {
                return ItemStack.EMPTY;
            } // Container --> Inventory
        }  else { // Inventory --> Container

            if (slotStack.getItem() instanceof BucketItem){
                if (!this.slots.get(0).hasItem() && !moveItemStackTo(slotStack, 0, 1, false)) return ItemStack.EMPTY;
                return null;
            }

            // Checks upgrade slot
            if(TagUtil.isTaggedMachineUpgradeItem(slotStack) && !moveItemStackTo(slotStack, upgradeSlotId, upgradeSlotId+1, false)) {
                return ItemStack.EMPTY;
            }

            // Checks primary input slot
            if (!RecipeUtil.getHydroponicIncubatorRecipesFromItemInput(this.tileEntity.getLevel(), slotStack.getItem()).isEmpty()
                && this.slots.get(2).getItem().getCount() < 1) {

                if (slotStack.getCount() > 1) {
                    ItemStack tempStack = slotStack.copy();
                    tempStack.setCount(1);

                    if (moveItemStackTo(tempStack, 2, 3, false)) {

                        // If the shift-clicking slot with valid item >1 decrement the stack in that slot stack by 1
                        slotStack.setCount(slotStack.getCount()-1);
                        return slotStack;
                    }
                } else if (moveItemStackTo(slotStack, 2, 3, false)) {
                    return ItemStack.EMPTY;
                }
            }

        }
        return null;
    }
}