package com.veteam.voluminousenergy.blocks.tiles.inventory;

import com.veteam.voluminousenergy.blocks.tiles.VETileEntity;
import com.veteam.voluminousenergy.util.VEItemCapabilities;
import net.minecraft.world.item.ItemStack;

public class BatteryBoxInventoryValidator implements AbstractItemStackValidator {

    @Override
    public boolean allowItemInsertion(int slot, ItemStack itemStack,boolean simulate, VETileEntity tile) {
        return VEItemCapabilities.getEnergyStorage(itemStack) != null;
    }

    @Override
    public boolean allowItemExtraction(int slot, int amount, boolean simulate, VETileEntity tile) {
        return true;
    }
}
