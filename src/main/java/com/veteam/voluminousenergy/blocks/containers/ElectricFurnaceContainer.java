package com.veteam.voluminousenergy.blocks.containers;

import com.veteam.voluminousenergy.blocks.blocks.VEBlocks;
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

public class ElectricFurnaceContainer extends VEContainer {

    private static final int NUMBER_OF_SLOTS = 3;

    public ElectricFurnaceContainer(int id, Level world, BlockPos pos, Inventory inventory, Player player){
        super(VEBlocks.ELECTRIC_FURNACE_CONTAINER.get(),id,world,pos,inventory,player,VEBlocks.ELECTRIC_FURNACE_BLOCK.get());
    }

    @Override
    protected void addSlotsToGUI(IItemHandler h) {
        addSlot(new VEInsertSlot(h, 0, 53,33)); // Furnace Input Slot
        addSlot(new VEInsertSlot(h, 1,116,33)); // Furnace Output Slot
        addSlot(new VEInsertSlot(h,2,154, -14));// Upgrade Slot
    }

    @Nonnull
    @Override
    public @NotNull ItemStack quickMoveStack(final @NotNull Player player, final int index) {
        ItemStack returnStack = ItemStack.EMPTY;
        final Slot slot = this.slots.get(index);
        if (slot != null && slot.hasItem()) {
            final ItemStack slotStack = slot.getItem();
            returnStack = slotStack.copy();

            if (handleCoreQuickMoveStackLogicWithUpgradeSlot(index, NUMBER_OF_SLOTS, 2, slotStack) != null) return ItemStack.EMPTY;

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