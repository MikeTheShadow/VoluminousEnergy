package com.veteam.voluminousenergy.blocks.containers;

import com.veteam.voluminousenergy.blocks.blocks.VEBlocks;
import com.veteam.voluminousenergy.blocks.inventory.slots.TileEntitySlots.CrusherInputSlot;
import com.veteam.voluminousenergy.blocks.inventory.slots.VEInsertSlot;
import com.veteam.voluminousenergy.blocks.inventory.slots.VEOutputSlot;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.items.IItemHandler;

import static com.veteam.voluminousenergy.blocks.blocks.VEBlocks.CRUSHER_CONTAINER;

public class CrusherContainer extends VEContainer {

    public static final int NUMBER_OF_SLOTS = 4;

    public CrusherContainer(int id, Level world, BlockPos pos, Inventory inventory, Player player){
        super(CRUSHER_CONTAINER.get(),id,world,pos,inventory,player,VEBlocks.CRUSHER_BLOCK.get());
    }

    @Override
    protected void addSlotsToGUI(IItemHandler h) {
        addSlot(new CrusherInputSlot(h, 0, 80, 13, world));
        addSlot(new VEOutputSlot(h, 1,71,58));//Main Output
        addSlot(new VEOutputSlot(h, 2, 89,58));//RNG Slot
        addSlot(new VEInsertSlot(h, 3,154, -14));//Upgrade Slot
    }
}
