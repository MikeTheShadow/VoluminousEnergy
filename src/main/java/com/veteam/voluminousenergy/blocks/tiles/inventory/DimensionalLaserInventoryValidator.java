package com.veteam.voluminousenergy.blocks.tiles.inventory;

import com.veteam.voluminousenergy.blocks.tiles.VETileEntity;
import com.veteam.voluminousenergy.items.tools.RFIDChip;
import com.veteam.voluminousenergy.util.TagUtil;
import net.minecraft.world.item.ItemStack;

public class DimensionalLaserInventoryValidator implements AbstractItemStackValidator {

    @Override
    public boolean allowItemInsertion(int slot, ItemStack stack,boolean simulate, VETileEntity tile) {
        if (tile.getEnergy() != null && tile.getEnergy().getUpgradeSlotId() == slot) {
            return TagUtil.isTaggedMachineUpgradeItem(stack);
        }
        if (slot == 0) {
            return stack.getItem() instanceof RFIDChip;
        }
        return false;
    }

    @Override
    public boolean allowItemExtraction(int slot, int amount, boolean simulate, VETileEntity tile) {
        return true;
    }
}
