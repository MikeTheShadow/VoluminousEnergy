package com.veteam.voluminousenergy.blocks.containers;

import com.veteam.voluminousenergy.blocks.blocks.VEBlocks;
import com.veteam.voluminousenergy.blocks.inventory.slots.VEInsertSlot;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.items.IItemHandler;

import static com.veteam.voluminousenergy.blocks.blocks.VEBlocks.BLAST_FURNACE_CONTAINER;

public class BlastFurnaceContainer extends VEContainer {

    public static final int NUMBER_OF_SLOTS = 6;

    public BlastFurnaceContainer(int id, Level world, BlockPos pos, Inventory inventory, Player player){
        super(BLAST_FURNACE_CONTAINER.get(),id,world,pos,inventory,player,VEBlocks.BLAST_FURNACE_BLOCK.get());
    }

    @Override
    protected void addSlotsToGUI(IItemHandler h) {
        addSlot(new VEInsertSlot(h, 0, 38, 18)); // Fluid input slot
        addSlot(new VEInsertSlot(h, 1,38,49)); // Extract fluid from heat tank
        addSlot(new VEInsertSlot(h, 2, 80,25)); // First input slot
        addSlot(new VEInsertSlot(h, 3, 80,43)); // Second input slot
        addSlot(new VEInsertSlot(h, 4, 134,34)); // Third input slot
        addSlot(new VEInsertSlot(h, 5,130,-14)); // Upgrade slot
    }
}
