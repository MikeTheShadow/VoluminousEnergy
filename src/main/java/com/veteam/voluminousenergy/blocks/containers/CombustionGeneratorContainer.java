package com.veteam.voluminousenergy.blocks.containers;

import com.veteam.voluminousenergy.blocks.blocks.VEBlocks;
import com.veteam.voluminousenergy.blocks.inventory.slots.VEBucketSlot;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.items.IItemHandler;

import static com.veteam.voluminousenergy.blocks.blocks.VEBlocks.COMBUSTION_GENERATOR_CONTAINER;

public class CombustionGeneratorContainer extends VEContainer {

    private static final int NUMBER_OF_SLOTS = 4;

    public CombustionGeneratorContainer(int id, Level world, BlockPos pos, Inventory inventory, Player player) {
        super(COMBUSTION_GENERATOR_CONTAINER.get(), id,world,pos,inventory,player,VEBlocks.COMBUSTION_GENERATOR_BLOCK.get());
    }

    @Override
    protected void addSlotsToGUI(IItemHandler h) {
        addSlot(new VEBucketSlot(h, 0, 38, 18)); // Oxidizer input slot
        addSlot(new VEBucketSlot(h, 1, 38, 49)); // Extract fluid from oxidizer slot
        addSlot(new VEBucketSlot(h, 2, 138, 18)); // Fuel input slot
        addSlot(new VEBucketSlot(h, 3, 138, 49)); // Extract fluid from fuel output
    }
}
