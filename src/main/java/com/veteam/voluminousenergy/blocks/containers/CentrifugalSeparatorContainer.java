package com.veteam.voluminousenergy.blocks.containers;

import com.veteam.voluminousenergy.blocks.blocks.VEBlocks;
import com.veteam.voluminousenergy.blocks.inventory.slots.VEInsertSlot;
import com.veteam.voluminousenergy.blocks.inventory.slots.VEOutputSlot;
import com.veteam.voluminousenergy.blocks.tiles.VETileEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.DataSlot;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.items.wrapper.InvWrapper;

public class CentrifugalSeparatorContainer extends VoluminousContainer {

    public static final int NUMBER_OF_SLOTS = 7;

    public CentrifugalSeparatorContainer(int id, Level world, BlockPos pos, Inventory inventory, Player player){
        super(VEBlocks.CENTRIFUGAL_SEPARATOR_CONTAINER.get(),id);
        this.tileEntity =(VETileEntity) world.getBlockEntity(pos);
        this.tileEntity.getCapability(ForgeCapabilities.ITEM_HANDLER);
        this.playerEntity = player;
        this.playerInventory = new InvWrapper(inventory);

        tileEntity.getCapability(ForgeCapabilities.ITEM_HANDLER).ifPresent(h -> {
            addSlot(new VEInsertSlot(h, 0, 53, 24)); // Primary input slot
            addSlot(new VEInsertSlot(h,1,53,42)); // Empty Bucket slot
            addSlot(new VEOutputSlot(h, 2,99,33)); //Main Output
            addSlot(new VEOutputSlot(h, 3, 117,15)); //RNG #1 Slot
            addSlot(new VEOutputSlot(h,4, 135, 33)); //RNG #2 Slot
            addSlot(new VEOutputSlot(h,5,117,51)); //RNG #3 Slot
            addSlot(new VEInsertSlot(h,6,155, -14)); // Upgrade Slot
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
        return stillValid(ContainerLevelAccess.create(tileEntity.getLevel(),tileEntity.getBlockPos()),playerEntity, VEBlocks.CENTRIFUGAL_SEPARATOR_BLOCK.get());
    }

    private void layoutPlayerInventorySlots(int leftCol, int topRow) {
        // Player inventory
        addSlotBox(playerInventory, 9, leftCol, topRow, 9, 18, 3, 18);

        // Hotbar
        topRow += 58;
        addSlotRange(playerInventory, 0, leftCol, topRow, 9, 18);
    }
}
