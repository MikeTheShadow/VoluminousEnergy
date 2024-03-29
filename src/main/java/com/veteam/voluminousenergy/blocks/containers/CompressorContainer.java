package com.veteam.voluminousenergy.blocks.containers;

import com.veteam.voluminousenergy.blocks.blocks.VEBlocks;
import com.veteam.voluminousenergy.blocks.inventory.slots.TileEntitySlots.CompressorInputSlot;
import com.veteam.voluminousenergy.blocks.inventory.slots.VEInsertSlot;
import com.veteam.voluminousenergy.blocks.inventory.slots.VEOutputSlot;
import com.veteam.voluminousenergy.blocks.tiles.VETileEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.wrapper.InvWrapper;

import static com.veteam.voluminousenergy.blocks.blocks.VEBlocks.COMPRESSOR_CONTAINER;

public class CompressorContainer extends VEContainer {
    public static final int NUMBER_OF_SLOTS = 3;

    public CompressorContainer(int id, Level world, BlockPos pos, Inventory inventory, Player player) {
        super(COMPRESSOR_CONTAINER.get(), id, world, pos, inventory, player, VEBlocks.COMPRESSOR_BLOCK.get());
        this.tileEntity = (VETileEntity) world.getBlockEntity(pos);
        this.tileEntity.getCapability(ForgeCapabilities.ITEM_HANDLER);
        this.playerEntity = player;
        this.playerInventory = new InvWrapper(inventory);


    }

    @Override
    protected void addSlotsToGUI(IItemHandler h) {
        addSlot(new CompressorInputSlot(h, 0, 80, 13, world));
        addSlot(new VEOutputSlot(h, 1, 80, 58));//Main Output
        addSlot(new VEInsertSlot(h, 2, 154, -14));//Upgrade slot
    }
}
