package com.veteam.voluminousenergy.blocks.inventory.slots.TileEntitySlots.PrimitiveBlastFurnaceSlots;

import com.veteam.voluminousenergy.blocks.inventory.slots.VEInsertSlot;
import net.minecraft.world.level.Level;
import net.minecraftforge.items.IItemHandler;

public class PrimitiveBlastFurnaceInsertSlot extends VEInsertSlot
{
    Level world;
    public PrimitiveBlastFurnaceInsertSlot(IItemHandler itemHandler, int index, int xPos, int yPos, Level world){
        super(itemHandler,index,xPos,yPos);
        this.world = world;
    }
}
