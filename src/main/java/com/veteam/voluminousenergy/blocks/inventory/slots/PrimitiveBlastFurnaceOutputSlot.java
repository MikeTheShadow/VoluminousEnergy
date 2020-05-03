package com.veteam.voluminousenergy.blocks.inventory.slots;

import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;

import javax.annotation.Nullable;

public class PrimitiveBlastFurnaceOutputSlot extends SlotItemHandler {
    public PrimitiveBlastFurnaceOutputSlot(IItemHandler itemHandler, int index, int xPos, int yPos){
        super(itemHandler, index, xPos, yPos);
    }

    @Override
    public boolean isItemValid(@Nullable ItemStack stack){
        return false;
    }
}
