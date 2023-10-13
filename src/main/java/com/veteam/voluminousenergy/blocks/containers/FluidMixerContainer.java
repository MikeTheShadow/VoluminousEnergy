package com.veteam.voluminousenergy.blocks.containers;

import com.veteam.voluminousenergy.blocks.blocks.VEBlocks;
import com.veteam.voluminousenergy.blocks.inventory.slots.VEBucketSlot;
import com.veteam.voluminousenergy.blocks.inventory.slots.VEInsertSlot;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.items.IItemHandler;


public class FluidMixerContainer extends VEContainer {

    private static final int NUMBER_OF_SLOTS = 7;

    public FluidMixerContainer(int id, Level world, BlockPos pos, Inventory inventory, Player player){
        super(VEBlocks.FLUID_MIXER_CONTAINER.get(),id,world,pos,inventory,player,VEBlocks.FLUID_MIXER_BLOCK.get());
    }

    @Override
    protected void addSlotsToGUI(IItemHandler h) {
        addSlot(new VEBucketSlot(h,0,38,18)); // Top input0 bucket
        addSlot(new VEBucketSlot(h,1,38,49)); // Bottom input0 bucket
        addSlot(new VEBucketSlot(h,2,86,18)); // Top input1 bucket
        addSlot(new VEBucketSlot(h,3,86,49)); // Bottom input1 bucket
        addSlot(new VEBucketSlot(h,4,136,18)); // Top output0 bucket
        addSlot(new VEBucketSlot(h,5,136,49)); // Bottom output0 bucket
        addSlot(new VEInsertSlot(h, 6,130,-14)); // Upgrade slot
    }
}