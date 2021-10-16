package com.veteam.voluminousenergy.blocks.inventory.slots;

import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.IItemHandler;

public class VEBucketSlot extends VEInsertSlot {
    public VEBucketSlot(IItemHandler itemHandler, int index, int xPos, int yPos) {
        super(itemHandler, index, xPos, yPos);
    }

    @Override
    public boolean mayPlace(ItemStack stack){
        return stack.getItem() instanceof BucketItem;
    }
}
