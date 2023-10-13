package com.veteam.voluminousenergy.blocks.containers;

import com.veteam.voluminousenergy.blocks.blocks.VEBlocks;
import com.veteam.voluminousenergy.blocks.inventory.slots.VEBucketSlot;
import com.veteam.voluminousenergy.blocks.inventory.slots.VEInsertSlot;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.items.IItemHandler;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;

public class FluidElectrolyzerContainer extends VEContainer {

    public static final int NUMBER_OF_SLOTS = 7;

    public FluidElectrolyzerContainer(int id, Level world, BlockPos pos, Inventory inventory, Player player){
        super(VEBlocks.FLUID_ELECTROLYZER_CONTAINER.get(),id,world,pos,inventory,player,VEBlocks.FLUID_ELECTROLYZER_BLOCK.get());
    }

    @Override
    protected void addSlotsToGUI(IItemHandler h) {
        addSlot(new VEBucketSlot(h,0,38,18)); // Top input bucket
        addSlot(new VEBucketSlot(h,1,38,49)); // Bottom input bucket
        addSlot(new VEBucketSlot(h,2,96,18)); // Top output0 bucket
        addSlot(new VEBucketSlot(h,3,96,49)); // Bottom output0 bucket
        addSlot(new VEBucketSlot(h,4,137,18)); // Top output1 bucket
        addSlot(new VEBucketSlot(h,5,137,49)); // Bottom output1 bucket
        addSlot(new VEInsertSlot(h, 6,130,-14)); // Upgrade slot
    }

    @Nonnull
    @Override
    public @NotNull ItemStack quickMoveStack(final @NotNull Player player, final int index) {
        ItemStack returnStack = ItemStack.EMPTY;
        final Slot slot = this.slots.get(index);
        if (slot != null && slot.hasItem()) {
            final ItemStack slotStack = slot.getItem();
            returnStack = slotStack.copy();

            if (handleCoreQuickMoveStackLogicWithUpgradeSlot(index, NUMBER_OF_SLOTS, this.getUpgradeSlotId(), slotStack) != null) return ItemStack.EMPTY;

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
}