package com.veteam.voluminousenergy.blocks.containers;

import com.veteam.voluminousenergy.blocks.inventory.slots.VEBucketSlot;
import com.veteam.voluminousenergy.blocks.inventory.slots.VEInsertSlot;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.items.IItemHandler;

import static com.veteam.voluminousenergy.blocks.blocks.VEBlocks.CENTRIFUGAL_AGITATOR_BLOCK;
import static com.veteam.voluminousenergy.blocks.blocks.VEBlocks.CENTRIFUGAL_AGITATOR_CONTAINER;

public class CentrifugalAgitatorContainer extends VEContainer {

    private static final int NUMBER_OF_SLOTS = 7;

    public CentrifugalAgitatorContainer(int id, Level world, BlockPos pos, Inventory inventory, Player player) {
        super(CENTRIFUGAL_AGITATOR_CONTAINER.get(), id,world,pos,inventory,player,CENTRIFUGAL_AGITATOR_BLOCK.get());
    }

    @Override
    protected void addSlotsToGUI(IItemHandler h) {
        addSlot(new VEBucketSlot(h, 0, 38, 18)); // Bucket insert; input tank
        addSlot(new VEBucketSlot(h, 1, 38, 49)); // Bucket extract; input tank
        addSlot(new VEBucketSlot(h, 2, 96, 18)); // Bucket insert; first output tank
        addSlot(new VEBucketSlot(h, 3, 96, 49)); // Bucket extract; first output tank
        addSlot(new VEBucketSlot(h, 4, 137, 18)); // Bucket insert; second output tank
        addSlot(new VEBucketSlot(h, 5, 137, 49)); // Bucket extract; second output tank
        addSlot(new VEInsertSlot(h, 6, 130, -14)); // Upgrade slot
    }
}
