package com.veteam.voluminousenergy.blocks.tiles.inventory;

import com.veteam.voluminousenergy.blocks.tiles.VETileEntity;
import com.veteam.voluminousenergy.items.tools.RFIDChip;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.material.Fluids;

public class DimensionalLaserInventoryValidator implements AbstractItemStackValidator {

    @Override
    public boolean isItemValid(int slot, ItemStack stack, VETileEntity tile) {
        if (slot == 2) {
            if (!(stack.getItem() instanceof RFIDChip)) {
                return false;
            }
            CompoundTag tag = stack.getOrCreateTag();
            if (!tag.contains("ve_x")) return false;
        }
        if (slot == 1) {
            return stack.getItem() instanceof BucketItem;
        }
        if (slot == 0) {
            return stack.getItem() instanceof BucketItem bucketItem && bucketItem.getFluid().isSame(Fluids.EMPTY);
        }
        return true;
    }
}
