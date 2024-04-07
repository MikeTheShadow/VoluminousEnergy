package com.veteam.voluminousenergy.blocks.tiles.handlers;

import net.minecraft.world.item.ItemStack;

public interface AbstractItemStackValidator {

    boolean isItemValid(int slot, ItemStack itemStack);

}
