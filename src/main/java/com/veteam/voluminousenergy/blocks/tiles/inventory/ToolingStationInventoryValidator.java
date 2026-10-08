package com.veteam.voluminousenergy.blocks.tiles.inventory;

import com.veteam.voluminousenergy.blocks.tiles.VETileEntity;
import com.veteam.voluminousenergy.items.tools.multitool.Multitool;
import com.veteam.voluminousenergy.items.tools.multitool.bits.BitItem;
import net.minecraft.world.item.ItemStack;

public class ToolingStationInventoryValidator implements AbstractItemStackValidator {

    @Override
    public boolean allowItemInsertion(int slot, ItemStack stack, boolean simulate, VETileEntity tile) {
        if(slot == 0) {
            return stack.getItem() instanceof Multitool;
        }
        if(!(stack.getItem() instanceof BitItem insertedBit)) return false;
        if(!(tile.getInventory().getStackInSlot(0).getItem() instanceof Multitool)) return false;
        for(int slotNum = 1; slotNum < 5; slotNum++) {
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
