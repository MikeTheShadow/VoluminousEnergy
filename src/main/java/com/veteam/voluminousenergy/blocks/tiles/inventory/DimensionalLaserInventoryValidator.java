package com.veteam.voluminousenergy.blocks.tiles.inventory;

import com.veteam.voluminousenergy.blocks.tiles.VETileEntity;
import com.veteam.voluminousenergy.items.tools.RFIDChip;
import com.veteam.voluminousenergy.util.VEDataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.material.Fluids;

public class DimensionalLaserInventoryValidator implements AbstractItemStackValidator {

    @Override
    public boolean isItemValid(int slot, ItemStack stack, VETileEntity tile) {
        if (slot == 2) {
            return stack.has(VEDataComponents.MULTIPLIER_DATA);
        }
        if (slot == 1) {
            return stack.getItem() instanceof BucketItem;
        }
        if (slot == 0) {
            return stack.getItem() instanceof BucketItem bucketItem && bucketItem.content.isSame(Fluids.EMPTY);
        }
        return true;
    }
}
