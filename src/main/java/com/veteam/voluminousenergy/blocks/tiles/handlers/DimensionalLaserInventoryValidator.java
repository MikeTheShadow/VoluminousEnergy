package com.veteam.voluminousenergy.blocks.tiles.handlers;

import com.veteam.voluminousenergy.items.tools.RFIDChip;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.ItemStack;

public class DimensionalLaserInventoryValidator implements AbstractItemStackValidator {

    @Override
    public boolean isItemValid(int slot, ItemStack stack) {
        if (slot == 2) {
            if (!(stack.getItem() instanceof RFIDChip)) {
                return false;
            }
            CompoundTag tag = stack.getOrCreateTag();
            if (!tag.contains("ve_x")) return false;
        }
        if (slot == 0 || slot == 1) {
            if (!(stack.getItem() instanceof BucketItem)) {
                return false;
            }
        }
        return true;
    }
}
