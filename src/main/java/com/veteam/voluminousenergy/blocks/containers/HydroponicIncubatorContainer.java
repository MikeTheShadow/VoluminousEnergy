package com.veteam.voluminousenergy.blocks.containers;

import com.veteam.voluminousenergy.blocks.blocks.VEBlocks;
import com.veteam.voluminousenergy.blocks.inventory.slots.VEBucketSlot;
import com.veteam.voluminousenergy.blocks.inventory.slots.VEInsertSlot;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.items.IItemHandler;

public class HydroponicIncubatorContainer extends VEContainer {

    public static final int NUMBER_OF_SLOTS = 8;

    public HydroponicIncubatorContainer(int id, Level world, BlockPos pos, Inventory inventory, Player player) {
        super(VEBlocks.HYDROPONIC_INCUBATOR_CONTAINER.get(), id,world,pos,inventory,player,VEBlocks.HYDROPONIC_INCUBATOR_BLOCK.get());
    }

    @Override
    protected void addSlotsToGUI(IItemHandler h) {
        addSlot(new VEBucketSlot(h, 0, 38, 18)); // Bucket top slot
        addSlot(new VEBucketSlot(h, 1, 38, 49)); // Bucket bottom slot
        addSlot(new VEInsertSlot(h, 2, 83, 34)); // Primary input
        addSlot(new VEInsertSlot(h, 3, 123, 8)); // Primary output
        addSlot(new VEInsertSlot(h, 4, 123, 26)); // RNG0 output
        addSlot(new VEInsertSlot(h, 5, 123, 44)); // RNG1 output
        addSlot(new VEInsertSlot(h, 6, 123, 62)); // RNG2 output
        addSlot(new VEInsertSlot(h, 7, 154,-14)); // Upgrade slot
    }
}