package com.veteam.voluminousenergy.blocks.tiles.inventory;

import com.veteam.voluminousenergy.blocks.tiles.VETileEntity;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.capabilities.Capabilities;

public class BatteryBoxInventoryValidator implements AbstractItemStackValidator {

    @Override
    public boolean allowItemInsertion(int slot, ItemStack itemStack,boolean simulate, VETileEntity tile) {
        return itemStack.getCapability(Capabilities.EnergyStorage.ITEM) != null;
    }

    @Override
    public boolean allowItemExtraction(int slot, int amount, boolean simulate, VETileEntity tile) {
        return true;
    }
}
