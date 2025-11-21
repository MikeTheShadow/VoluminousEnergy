package com.veteam.voluminousenergy.blocks.tiles.inventory;

import com.veteam.voluminousenergy.blocks.tiles.VETileEntity;
import com.veteam.voluminousenergy.items.tools.multitool.Multitool;
import com.veteam.voluminousenergy.items.tools.multitool.bits.BitItem;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.ItemStack;

public class ToolingStationInventoryValidator implements AbstractItemStackValidator {

    @Override
    public boolean allowItemInsertion(int slot, ItemStack stack, boolean simulate, VETileEntity tile) {
        if(slot == 0 || slot == 1) return stack.getItem() instanceof BucketItem;
        if(slot == 2) {
            return stack.getItem() instanceof Multitool;
        }
        if(!(stack.getItem() instanceof BitItem insertedBit)) return false;
        if(!(tile.getInventory().getStackInSlot(2).getItem() instanceof Multitool)) return false;
        for(int slotNum = 3; slotNum < 7; slotNum++) {
            ItemStack bit = tile.getInventory().getStackInSlot(slotNum);
            if (bit.getItem() instanceof BitItem bitItem && insertedBit.getBitItemData().getToolType() == bitItem.getBitItemData().getToolType()) {
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean allowItemExtraction(int slot, int amount, boolean simulate, VETileEntity tile) {
        return true;
    }
}
