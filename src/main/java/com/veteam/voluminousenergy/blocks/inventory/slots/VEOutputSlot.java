package com.veteam.voluminousenergy.blocks.inventory.slots;

import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;

import javax.annotation.Nullable;

public class VEOutputSlot extends SlotItemHandler {

    public VEOutputSlot(IItemHandler itemHandler, int index, int xPos, int yPos) {
        super(itemHandler, index, xPos, yPos);
    }

    /*
    We override this and return false since no item can
    be valid in an output slot. This is to prevent
    users from putting anything in the output slot
     */

    @Override // mayPlace, as in, you may place it if it's valid. (MCP: isItemValid)
    public boolean mayPlace(@Nullable ItemStack stack){
        return false;
    }
}
