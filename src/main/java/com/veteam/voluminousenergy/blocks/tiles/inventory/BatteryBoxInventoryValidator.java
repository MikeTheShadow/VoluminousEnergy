package com.veteam.voluminousenergy.blocks.tiles.inventory;

import com.veteam.voluminousenergy.blocks.tiles.VETileEntity;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.common.capabilities.ForgeCapabilities;

public class BatteryBoxInventoryValidator implements AbstractItemStackValidator {
    @Override
    public boolean isItemValid(int slot, ItemStack itemStack, VETileEntity tile) {
        return itemStack.getCapability(ForgeCapabilities.ENERGY).isPresent();
    }
}
