package com.veteam.voluminousenergy.blocks.containers;

import com.veteam.voluminousenergy.blocks.blocks.VEBlocks;
import com.veteam.voluminousenergy.blocks.inventory.slots.TileEntitySlots.PrimitiveBlastFurnaceSlots.PrimitiveBlastFurnaceInsertSlot;
import com.veteam.voluminousenergy.blocks.inventory.slots.TileEntitySlots.PrimitiveBlastFurnaceSlots.PrimitiveBlastFurnaceOutputSlot;
import com.veteam.voluminousenergy.blocks.inventory.slots.VEInsertSlot;
import com.veteam.voluminousenergy.blocks.tiles.VETileEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.items.wrapper.InvWrapper;

import javax.annotation.Nonnull;

import static com.veteam.voluminousenergy.blocks.blocks.VEBlocks.PRIMITIVE_BLAST_FURNACE_CONTAINER;

public class PrimitiveBlastFurnaceContainer extends VoluminousContainer {

    private static final int NUMBER_OF_SLOTS = 3;

    public PrimitiveBlastFurnaceContainer(int id, Level world, BlockPos pos, Inventory inventory, Player player) {
        super(PRIMITIVE_BLAST_FURNACE_CONTAINER.get(), id);
        this.tileEntity = (VETileEntity) world.getBlockEntity(pos);
        this.tileEntity.getCapability(ForgeCapabilities.ITEM_HANDLER);
        this.playerEntity = player;
        this.playerInventory = new InvWrapper(inventory);

        tileEntity.getCapability(ForgeCapabilities.ITEM_HANDLER).ifPresent(h -> {
            addSlot(new PrimitiveBlastFurnaceInsertSlot(h, 0, 53, 33, world));
            addSlot(new PrimitiveBlastFurnaceOutputSlot(h, 1,116,33));
            addSlot(new VEInsertSlot(h,2,154, -14));
        });
        layoutPlayerInventorySlots(8, 84);
    }

    @Override
    public boolean stillValid(Player playerIn) {
        return stillValid(ContainerLevelAccess.create(tileEntity.getLevel(),tileEntity.getBlockPos()),playerEntity, VEBlocks.PRIMITIVE_BLAST_FURNACE_BLOCK.get());
    }

    private void layoutPlayerInventorySlots(int leftCol, int topRow) {
        // Player inventory
        addSlotBox(playerInventory, 9, leftCol, topRow, 9, 18, 3, 18);

        // Hotbar
        topRow += 58;
        addSlotRange(playerInventory, 0, leftCol, topRow, 9, 18);
    }
}
