package com.veteam.voluminousenergy.blocks.tiles.inventory;

import com.veteam.voluminousenergy.blocks.tiles.VETileEntity;
import net.minecraft.world.item.ItemStack;

public interface AbstractItemStackValidator {

    boolean allowItemInsertion(int slot, ItemStack stack, VETileEntity tile);

}
