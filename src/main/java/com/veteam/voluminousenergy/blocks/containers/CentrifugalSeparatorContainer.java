package com.veteam.voluminousenergy.blocks.containers;

import com.veteam.voluminousenergy.blocks.blocks.VEBlocks;
import com.veteam.voluminousenergy.blocks.inventory.slots.VEInsertSlot;
import com.veteam.voluminousenergy.blocks.inventory.slots.VEOutputSlot;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.items.IItemHandler;

public class CentrifugalSeparatorContainer extends VEContainer {

    public static final int NUMBER_OF_SLOTS = 7;

    public CentrifugalSeparatorContainer(int id, Level world, BlockPos pos, Inventory inventory, Player player){
        super(VEBlocks.CENTRIFUGAL_SEPARATOR_CONTAINER.get(),id,world,pos,inventory,player,VEBlocks.CENTRIFUGAL_SEPARATOR_BLOCK.get());
    }

    @Override
    protected void addSlotsToGUI(IItemHandler h) {
        addSlot(new VEInsertSlot(h, 0, 53, 24)); // Primary input slot
        addSlot(new VEInsertSlot(h,1,53,42)); // Empty Bucket slot
        addSlot(new VEOutputSlot(h, 2,99,33)); //Main Output
        addSlot(new VEOutputSlot(h, 3, 117,15)); //RNG #1 Slot
        addSlot(new VEOutputSlot(h,4, 135, 33)); //RNG #2 Slot
        addSlot(new VEOutputSlot(h,5,117,51)); //RNG #3 Slot
        addSlot(new VEInsertSlot(h,6,155, -14)); // Upgrade Slot
    }
}
