package com.veteam.voluminousenergy.blocks.containers;

import com.veteam.voluminousenergy.blocks.blocks.VEBlocks;
import com.veteam.voluminousenergy.blocks.inventory.slots.TileEntitySlots.PrimitiveBlastFurnaceSlots.PrimitiveBlastFurnaceInsertSlot;
import com.veteam.voluminousenergy.blocks.inventory.slots.TileEntitySlots.PrimitiveBlastFurnaceSlots.PrimitiveBlastFurnaceOutputSlot;
import com.veteam.voluminousenergy.blocks.inventory.slots.VEInsertSlot;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.items.IItemHandler;

import static com.veteam.voluminousenergy.blocks.blocks.VEBlocks.PRIMITIVE_BLAST_FURNACE_CONTAINER;

public class PrimitiveBlastFurnaceContainer extends VEContainer {

    private static final int NUMBER_OF_SLOTS = 3;

    public PrimitiveBlastFurnaceContainer(int id, Level world, BlockPos pos, Inventory inventory, Player player) {
        super(PRIMITIVE_BLAST_FURNACE_CONTAINER.get(), id,world,pos,inventory,player,VEBlocks.PRIMITIVE_BLAST_FURNACE_BLOCK.get());
    }

    @Override
    protected void addSlotsToGUI(IItemHandler h) {
        addSlot(new PrimitiveBlastFurnaceInsertSlot(h, 0, 53, 33, world));
        addSlot(new PrimitiveBlastFurnaceOutputSlot(h, 1,116,33));
        addSlot(new VEInsertSlot(h,2,154, -14));
    }
}
