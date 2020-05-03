package com.veteam.voluminousenergy.blocks.inventory.slots;

import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;

public class PrimitiveBlastFurnaceInsertSlot extends SlotItemHandler {
    public PrimitiveBlastFurnaceInsertSlot(IItemHandler itemHandler, int index, int xPos, int yPos){
        super(itemHandler,index,xPos,yPos);
    }

    @Override
    public boolean isItemValid(ItemStack stack){
        return super.isItemValid(stack) && stack.getItem() == Items.COAL;
    }
}
