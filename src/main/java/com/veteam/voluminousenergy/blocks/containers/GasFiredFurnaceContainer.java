package com.veteam.voluminousenergy.blocks.containers;

import com.veteam.voluminousenergy.blocks.blocks.VEBlocks;
import com.veteam.voluminousenergy.blocks.inventory.slots.VEBucketSlot;
import com.veteam.voluminousenergy.blocks.inventory.slots.VEInsertSlot;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.items.IItemHandler;

import static com.veteam.voluminousenergy.blocks.blocks.VEBlocks.GAS_FIRED_FURNACE_CONTAINER;

public class GasFiredFurnaceContainer extends VEContainer {

    private static final int NUMBER_OF_SLOTS = 5;

    public GasFiredFurnaceContainer(int id, Level world, BlockPos pos, Inventory inventory, Player player) {
        super(GAS_FIRED_FURNACE_CONTAINER.get(), id, world, pos, inventory, player, VEBlocks.GAS_FIRED_FURNACE_BLOCK.get());
    }

    @Override
    protected void addSlotsToGUI(IItemHandler h) {
        addSlot(new VEBucketSlot(h, 0, 8, 18)); // Fluid input slot
        addSlot(new VEBucketSlot(h, 1, 8, 49)); // Extract fluid from input
        addSlot(new VEInsertSlot(h, 2, 53, 33)); // Item input slot
        addSlot(new VEInsertSlot(h, 3, 116, 33)); // Item output slot
        addSlot(new VEInsertSlot(h, 4, 154, -14)); // Upgrade slot
    }
}
