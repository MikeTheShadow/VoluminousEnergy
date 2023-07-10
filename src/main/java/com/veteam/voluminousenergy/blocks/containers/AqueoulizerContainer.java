package com.veteam.voluminousenergy.blocks.containers;

import com.veteam.voluminousenergy.blocks.blocks.VEBlocks;
import com.veteam.voluminousenergy.blocks.inventory.slots.VEBucketSlot;
import com.veteam.voluminousenergy.blocks.inventory.slots.VEInsertSlot;
import com.veteam.voluminousenergy.blocks.tiles.IVEPoweredTileEntity;
import com.veteam.voluminousenergy.tools.energy.VEEnergyStorage;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.DataSlot;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.items.wrapper.InvWrapper;

import javax.annotation.Nonnull;

import static com.veteam.voluminousenergy.blocks.blocks.VEBlocks.AQUEOULIZER_CONTAINER;

public class AqueoulizerContainer extends VoluminousContainer {

    public static final int NUMBER_OF_SLOTS = 6;

    public AqueoulizerContainer(int id, Level world, BlockPos pos, Inventory inventory, Player player) {
        super(AQUEOULIZER_CONTAINER.get(), id);
        this.tileEntity = world.getBlockEntity(pos);
        this.tileEntity.getCapability(ForgeCapabilities.ITEM_HANDLER);
        this.playerEntity = player;
        this.playerInventory = new InvWrapper(inventory);

        tileEntity.getCapability(ForgeCapabilities.ITEM_HANDLER).ifPresent(h -> {
            addSlot(new VEBucketSlot(h, 0, 38, 18)); // Bucket input; input tank
            addSlot(new VEBucketSlot(h, 1, 38, 49)); // Bucket output; input tank
            addSlot(new VEBucketSlot(h, 2, 137, 18)); // Bucket insert; output tank
            addSlot(new VEBucketSlot(h, 3, 137, 49)); // Bucket output; output tank
            addSlot(new VEInsertSlot(h, 4, 96, 32)); // Item to be dissolved
            addSlot(new VEInsertSlot(h, 5, 130,-14)); // Upgrade Slot
        });
        layoutPlayerInventorySlots(8, 84);

        addDataSlot(new DataSlot() {
            @Override
            public int get() {
                return getEnergy();
            }

            @Override
            public void set(int value) {
                tileEntity.getCapability(ForgeCapabilities.ENERGY).ifPresent(h -> ((VEEnergyStorage) h).setEnergy(value));
            }
        });
    }

    public int getEnergy() {
        return tileEntity.getCapability(ForgeCapabilities.ENERGY).map(IEnergyStorage::getEnergyStored).orElse(0);
    }

    public int powerScreen(int px) {
        int stored = tileEntity.getCapability(ForgeCapabilities.ENERGY).map(IEnergyStorage::getEnergyStored).orElse(0);
        int max = tileEntity.getCapability(ForgeCapabilities.ENERGY).map(IEnergyStorage::getMaxEnergyStored).orElse(0);
        int ret = (((stored * 100 / max * 100) / 100) * px) / 100;
        return ret;
    }

    @Override
    public boolean stillValid(Player playerIn) {
        return stillValid(ContainerLevelAccess.create(tileEntity.getLevel(), tileEntity.getBlockPos()), playerEntity, VEBlocks.AQUEOULIZER_BLOCK.get());
    }

    private void layoutPlayerInventorySlots(int leftCol, int topRow) {
        // Player inventory
        addSlotBox(playerInventory, 9, leftCol, topRow, 9, 18, 3, 18);

        // Hotbar
        topRow += 58;
        addSlotRange(playerInventory, 0, leftCol, topRow, 9, 18);
    }

    @Nonnull
    @Override
    public ItemStack quickMoveStack(final Player player, final int index) {
        ItemStack returnStack = ItemStack.EMPTY;
        final Slot slot = this.slots.get(index);

        if (slot != null && slot.hasItem()) {
            final ItemStack slotStack = slot.getItem();
            returnStack = slotStack.copy();

            if (handleCoreQuickMoveStackLogicWithUpgradeSlot(index, NUMBER_OF_SLOTS, ((IVEPoweredTileEntity) this.tileEntity).getUpgradeSlotId(), slotStack) != null)
                return ItemStack.EMPTY;

            if (slotStack.getCount() == 0) {
                slot.set(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }

            if (slotStack.getCount() == returnStack.getCount()) {
                return ItemStack.EMPTY;
            }

            slot.onTake(player, slotStack);
        }
        return returnStack;
    }
}