package com.veteam.voluminousenergy.blocks.containers;

import com.veteam.voluminousenergy.blocks.blocks.VEBlocks;
import com.veteam.voluminousenergy.blocks.inventory.slots.VEBucketSlot;
import com.veteam.voluminousenergy.blocks.inventory.slots.VEInsertSlot;
import com.veteam.voluminousenergy.blocks.tiles.VETileEntity;
import com.veteam.voluminousenergy.tools.energy.VEEnergyStorage;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.DataSlot;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.items.wrapper.InvWrapper;

public class HydroponicIncubatorContainer extends VoluminousContainer {

    public static final int NUMBER_OF_SLOTS = 8;

    public HydroponicIncubatorContainer(int id, Level world, BlockPos pos, Inventory inventory, Player player) {
        super(VEBlocks.HYDROPONIC_INCUBATOR_CONTAINER.get(), id);
        this.tileEntity =(VETileEntity) world.getBlockEntity(pos);
        this.tileEntity.getCapability(ForgeCapabilities.ITEM_HANDLER);
        this.playerEntity = player;
        this.playerInventory = new InvWrapper(inventory);

        tileEntity.getCapability(ForgeCapabilities.ITEM_HANDLER).ifPresent(h -> {
            addSlot(new VEBucketSlot(h, 0, 38, 18)); // Bucket top slot
            addSlot(new VEBucketSlot(h, 1, 38, 49)); // Bucket bottom slot
            addSlot(new VEInsertSlot(h, 2, 83, 34)); // Primary input
            addSlot(new VEInsertSlot(h, 3, 123, 8)); // Primary output
            addSlot(new VEInsertSlot(h, 4, 123, 26)); // RNG0 output
            addSlot(new VEInsertSlot(h, 5, 123, 44)); // RNG1 output
            addSlot(new VEInsertSlot(h, 6, 123, 62)); // RNG2 output
            addSlot(new VEInsertSlot(h, 7, 154,-14)); // Upgrade slot
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
        return stillValid(ContainerLevelAccess.create(tileEntity.getLevel(), tileEntity.getBlockPos()), playerEntity, VEBlocks.HYDROPONIC_INCUBATOR_BLOCK.get());
    }

    private void layoutPlayerInventorySlots(int leftCol, int topRow) {
        // Player inventory
        addSlotBox(playerInventory, 9, leftCol, topRow, 9, 18, 3, 18);

        // Hotbar
        topRow += 58;
        addSlotRange(playerInventory, 0, leftCol, topRow, 9, 18);
    }
}