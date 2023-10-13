package com.veteam.voluminousenergy.blocks.containers;

import com.veteam.voluminousenergy.blocks.blocks.VEBlocks;
import com.veteam.voluminousenergy.blocks.inventory.slots.TileEntitySlots.ElectrolyzerInputSlot;
import com.veteam.voluminousenergy.blocks.inventory.slots.VEInsertSlot;
import com.veteam.voluminousenergy.blocks.inventory.slots.VEOutputSlot;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.items.IItemHandler;

public class ElectrolyzerContainer extends VEContainer {

    public static final int NUMBER_OF_SLOTS = 7;

    public ElectrolyzerContainer(int id, Level world, BlockPos pos, Inventory inventory, Player player){
        super(VEBlocks.ELECTROLYZER_CONTAINER.get(),id,world,pos,inventory,player,VEBlocks.ELECTROLYZER_BLOCK.get());
    }

    @Override
    protected void addSlotsToGUI(IItemHandler h) {
        addSlot(new ElectrolyzerInputSlot(h, 0, 71, 13, world));
        addSlot(new VEInsertSlot(h,1,89,13)); // Empty Bucket slot
        addSlot(new VEOutputSlot(h, 2,53,57)); //Main Output
        addSlot(new VEOutputSlot(h, 3, 71,57)); //RNG #1 Slot
        addSlot(new VEOutputSlot(h,4, 89, 57)); //RNG #2 Slot
        addSlot(new VEOutputSlot(h,5,107,57)); //RNG #3 Slot
        addSlot(new VEInsertSlot(h,6,154, -14)); // Upgrade Slot
    }
}
