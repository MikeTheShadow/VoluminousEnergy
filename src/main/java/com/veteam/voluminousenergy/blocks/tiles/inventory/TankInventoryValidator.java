package com.veteam.voluminousenergy.blocks.tiles.inventory;

import com.veteam.voluminousenergy.blocks.tiles.VETileEntity;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.ItemStack;

public class TankInventoryValidator implements AbstractItemStackValidator {
    @Override
    public boolean isItemValid(int slot, ItemStack itemStack, VETileEntity tile) {
        return itemStack.getItem() instanceof BucketItem;
    }
}
