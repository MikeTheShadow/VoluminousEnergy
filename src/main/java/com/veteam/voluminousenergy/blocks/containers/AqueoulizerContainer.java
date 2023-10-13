package com.veteam.voluminousenergy.blocks.containers;

import com.veteam.voluminousenergy.blocks.blocks.VEBlocks;
import com.veteam.voluminousenergy.blocks.inventory.slots.VEBucketSlot;
import com.veteam.voluminousenergy.blocks.inventory.slots.VEInsertSlot;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.items.IItemHandler;

import static com.veteam.voluminousenergy.blocks.blocks.VEBlocks.AQUEOULIZER_CONTAINER;

public class AqueoulizerContainer extends VEContainer {

    public static final int NUMBER_OF_SLOTS = 6;

    public AqueoulizerContainer(int id, Level world, BlockPos pos, Inventory inventory, Player player) {
        super(AQUEOULIZER_CONTAINER.get(), id,world,pos,inventory,player,VEBlocks.AQUEOULIZER_BLOCK.get());
    }

    @Override
    protected void addSlotsToGUI(IItemHandler handler) {
        addSlot(new VEBucketSlot(handler, 0, 38, 18)); // Bucket input; input tank
        addSlot(new VEBucketSlot(handler, 1, 38, 49)); // Bucket output; input tank
        addSlot(new VEBucketSlot(handler, 2, 137, 18)); // Bucket insert; output tank
        addSlot(new VEBucketSlot(handler, 3, 137, 49)); // Bucket output; output tank
        addSlot(new VEInsertSlot(handler, 4, 96, 32)); // Item to be dissolved
        addSlot(new VEInsertSlot(handler, 5, 130,-14)); // Upgrade Slot
    }
}