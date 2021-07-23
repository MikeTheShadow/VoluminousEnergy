package com.veteam.voluminousenergy.blocks.containers;

import com.veteam.voluminousenergy.blocks.blocks.VEBlocks;
import com.veteam.voluminousenergy.blocks.inventory.slots.VEInsertSlot;
import com.veteam.voluminousenergy.blocks.screens.BatteryBoxScreen;
import com.veteam.voluminousenergy.tools.energy.VEEnergyStorage;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.DataSlot;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;
import net.minecraftforge.items.wrapper.InvWrapper;

import javax.annotation.Nonnull;

public class BatteryBoxContainer extends VoluminousContainer {

    private Player playerEntity;
    private IItemHandler playerInventory;
    private BatteryBoxScreen screen;

    public BatteryBoxContainer(int id, Level world, BlockPos pos, Inventory inventory, Player player){
        super(VEBlocks.BATTERY_BOX_CONTAINER,id);
        this.tileEntity = world.getBlockEntity(pos);
        this.tileEntity.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY);
        this.playerEntity = player;
        this.playerInventory = new InvWrapper(inventory);

        tileEntity.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).ifPresent(h -> {
            // Top slots
            addSlot(new VEInsertSlot(h, 0, 35,17));
            addSlot(new VEInsertSlot(h, 1,53,17));
            addSlot(new VEInsertSlot(h,2,71, 17));
            addSlot(new VEInsertSlot(h,3,89, 17));
            addSlot(new VEInsertSlot(h,4,107, 17));
            addSlot(new VEInsertSlot(h,5,125, 17));

            //Bottom Slots
            addSlot(new VEInsertSlot(h,6,35, 54));
            addSlot(new VEInsertSlot(h, 7,53,54));
            addSlot(new VEInsertSlot(h,8,71, 54));
            addSlot(new VEInsertSlot(h,9,89, 54));
            addSlot(new VEInsertSlot(h,10,107, 54));
            addSlot(new VEInsertSlot(h,11,125, 54));
        });
        layoutPlayerInventorySlots(8, 84);

        addDataSlot(new DataSlot() {
            @Override
            public int get() {
                return getEnergy();
            }

            @Override
            public void set(int value) {
                tileEntity.getCapability(CapabilityEnergy.ENERGY).ifPresent(h -> ((VEEnergyStorage)h).setEnergy(value));
            }
        });
    }

    public int getEnergy(){
        return tileEntity.getCapability(CapabilityEnergy.ENERGY).map(IEnergyStorage::getEnergyStored).orElse(0);
    }

    public int powerScreen(int px){
        int stored = tileEntity.getCapability(CapabilityEnergy.ENERGY).map(IEnergyStorage::getEnergyStored).orElse(0);
        int max = tileEntity.getCapability(CapabilityEnergy.ENERGY).map(IEnergyStorage::getMaxEnergyStored).orElse(0);
        int ret = (((stored*100/max*100)/100)*px)/100;
        return ret;
    }

    @Override
    public boolean stillValid(Player playerIn) {
        return stillValid(ContainerLevelAccess.create(tileEntity.getLevel(),tileEntity.getBlockPos()),playerEntity, VEBlocks.BATTERY_BOX_BLOCK);
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

            final int containerSlots = this.slots.size() - player.containerMenu.getItems().size();
            if (index < containerSlots) {
                if (!moveItemStackTo(slotStack, containerSlots, this.slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else if (!moveItemStackTo(slotStack, 0, containerSlots, false)) {
                return ItemStack.EMPTY;
            }
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


    // Unauthorized call to this method can be dangerous. Can't not be public AFAIK. :(
    public void setScreen(BatteryBoxScreen screen){
        this.screen = screen;
    }

    public void updateDirectionButton(int direction, int slotId){ this.screen.updateButtonDirection(direction,slotId); }

    public void updateStatusButton(boolean status, int slotId){
        this.screen.updateBooleanButton(status, slotId);
    }

    public void updateSlotPairButton(boolean status, int pairId){this.screen.updateSlotPairButton(status,pairId);}

    public void updateSendOutPowerButton(boolean status){this.screen.updateSendOutPowerButton(status);}
}