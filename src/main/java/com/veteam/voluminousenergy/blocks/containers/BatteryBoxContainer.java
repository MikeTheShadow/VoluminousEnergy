package com.veteam.voluminousenergy.blocks.containers;

import com.veteam.voluminousenergy.blocks.blocks.VEBlocks;
import com.veteam.voluminousenergy.blocks.inventory.slots.VEInsertSlot;
import com.veteam.voluminousenergy.blocks.screens.BatteryBoxScreen;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.items.IItemHandler;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;

public class BatteryBoxContainer extends VEContainer {

    private static final int NUMBER_OF_SLOTS = 12;

    public BatteryBoxContainer(int id, Level world, BlockPos pos, Inventory inventory, Player player){
        super(VEBlocks.BATTERY_BOX_CONTAINER.get(),id,world,pos,inventory,player,VEBlocks.BATTERY_BOX_BLOCK.get());
    }

    @Override
    protected void addSlotsToGUI(IItemHandler h) {
        addSlot(new VEInsertSlot(h, 0, 35,17));
        addSlot(new VEInsertSlot(h, 1,53,17));
        addSlot(new VEInsertSlot(h,2,71, 17));
        addSlot(new VEInsertSlot(h,3,89, 17));
        addSlot(new VEInsertSlot(h,4,107, 17));
        addSlot(new VEInsertSlot(h,5,125, 17));

        //Bottom Slots
        addSlot(new VEInsertSlot(h,6,35, 54));
        addSlot(new VEInsertSlot(h, 7,53,54));
        addSlot(new VEInsertSlot(h,8,71, 54));
        addSlot(new VEInsertSlot(h,9,89, 54));
        addSlot(new VEInsertSlot(h,10,107, 54));
        addSlot(new VEInsertSlot(h,11,125, 54));
    }

    @Nonnull
    @Override
    public @NotNull ItemStack quickMoveStack(final @NotNull Player player, final int index) {
        ItemStack returnStack = ItemStack.EMPTY;
        final Slot slot = this.slots.get(index);

        if (slot != null && slot.hasItem()) {
            final ItemStack slotStack = slot.getItem();
            returnStack = slotStack.copy();

            if (handleCoreQuickMoveStackLogic(index, NUMBER_OF_SLOTS, slotStack) != null) return ItemStack.EMPTY;

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

    public void updateSendOutPowerButton(boolean status){
        if(this.screen instanceof BatteryBoxScreen batteryBoxScreen) {
            batteryBoxScreen.updateSendOutPowerButton(status);
        }
    }


}