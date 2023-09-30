package com.veteam.voluminousenergy.blocks.containers;

import com.veteam.voluminousenergy.blocks.blocks.VEBlocks;
import com.veteam.voluminousenergy.blocks.inventory.slots.VEInsertSlot;
import com.veteam.voluminousenergy.blocks.tiles.VETileEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.DataSlot;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.items.wrapper.InvWrapper;

import static com.veteam.voluminousenergy.blocks.blocks.VEBlocks.BLAST_FURNACE_CONTAINER;

public class BlastFurnaceContainer extends VoluminousContainer {

    public static final int NUMBER_OF_SLOTS = 6;

    public BlastFurnaceContainer(int id, Level world, BlockPos pos, Inventory inventory, Player player){
        super(BLAST_FURNACE_CONTAINER.get(),id);
        this.tileEntity =(VETileEntity) world.getBlockEntity(pos);
        this.tileEntity.getCapability(ForgeCapabilities.ITEM_HANDLER);
        this.playerEntity = player;
        this.playerInventory = new InvWrapper(inventory);

        tileEntity.getCapability(ForgeCapabilities.ITEM_HANDLER).ifPresent(h -> {
            addSlot(new VEInsertSlot(h, 0, 38, 18)); // Fluid input slot
            addSlot(new VEInsertSlot(h, 1,38,49)); // Extract fluid from heat tank
            addSlot(new VEInsertSlot(h, 2, 80,25)); // First input slot
            addSlot(new VEInsertSlot(h, 3, 80,43)); // Second input slot
            addSlot(new VEInsertSlot(h, 4, 134,34)); // Third input slot
            addSlot(new VEInsertSlot(h, 5,130,-14)); // Upgrade slot
        });
        layoutPlayerInventorySlots(8, 84);

        addDataSlot(new DataSlot() {
            @Override
            public int get() {
                return getEnergy();
            }

            @Override
            public void set(int value) {
            }
        });
    }

    @Override
    public boolean stillValid(Player playerIn) {
        return stillValid(ContainerLevelAccess.create(tileEntity.getLevel(),tileEntity.getBlockPos()),playerEntity, VEBlocks.BLAST_FURNACE_BLOCK.get());
    }

    private void layoutPlayerInventorySlots(int leftCol, int topRow) {
        // Player inventory
        addSlotBox(playerInventory, 9, leftCol, topRow, 9, 18, 3, 18);

        // Hotbar
        topRow += 58;
        addSlotRange(playerInventory, 0, leftCol, topRow, 9, 18);
    }
}
