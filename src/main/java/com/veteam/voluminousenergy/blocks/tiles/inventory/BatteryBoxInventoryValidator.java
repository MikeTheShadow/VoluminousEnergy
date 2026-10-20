package com.veteam.voluminousenergy.blocks.tiles.inventory;

import com.veteam.voluminousenergy.blocks.tiles.VETileEntity;
import com.veteam.voluminousenergy.util.VEItemCapabilities;
import net.minecraft.world.item.ItemStack;

public class BatteryBoxInventoryValidator implements AbstractItemStackValidator {

    @Override
    public boolean allowItemInsertion(int slot, ItemStack itemStack, VETileEntity tile) {
        return VEItemCapabilities.getEnergyHandler(itemStack) != null;
    }
}
