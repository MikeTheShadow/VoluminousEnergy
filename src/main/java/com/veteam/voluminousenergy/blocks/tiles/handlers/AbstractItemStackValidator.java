package com.veteam.voluminousenergy.blocks.tiles.handlers;

import com.veteam.voluminousenergy.blocks.tiles.VETileEntity;
import net.minecraft.world.item.ItemStack;

public interface AbstractItemStackValidator {

    boolean isItemValid(int slot, ItemStack itemStack, VETileEntity tile);

}
