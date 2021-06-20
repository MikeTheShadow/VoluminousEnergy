package com.veteam.voluminousenergy.blocks.containers;

import com.veteam.voluminousenergy.blocks.blocks.VEBlocks;
import com.veteam.voluminousenergy.blocks.inventory.slots.VEInsertSlot;
import com.veteam.voluminousenergy.blocks.screens.BlastFurnaceScreen;
import com.veteam.voluminousenergy.tools.energy.VEEnergyStorage;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IWorldPosCallable;
import net.minecraft.util.IntReferenceHolder;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.wrapper.InvWrapper;

import javax.annotation.Nonnull;

import static com.veteam.voluminousenergy.blocks.blocks.VEBlocks.BLAST_FURNACE_CONTAINER;

public class BlastFurnaceContainer extends VoluminousContainer {

    private PlayerEntity playerEntity;
    private IItemHandler playerInventory;
    private BlastFurnaceScreen screen;

    public BlastFurnaceContainer(int id, World world, BlockPos pos, PlayerInventory inventory, PlayerEntity player){
        super(BLAST_FURNACE_CONTAINER,id);
        this.tileEntity = world.getBlockEntity(pos);
        this.tileEntity.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY);
        this.playerEntity = player;
        this.playerInventory = new InvWrapper(inventory);

        tileEntity.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).ifPresent(h -> {
            addSlot(new VEInsertSlot(h, 0, 38, 18)); // Fluid input slot
            addSlot(new VEInsertSlot(h, 1,38,49)); // Extract fluid from heat tank
            addSlot(new VEInsertSlot(h, 2, 80,25)); // First input slot
            addSlot(new VEInsertSlot(h, 3, 80,43)); // Second input slot
            addSlot(new VEInsertSlot(h, 4, 134,34)); // Third input slot
            addSlot(new VEInsertSlot(h, 5,130,-14)); // Upgrade slot
        });
        layoutPlayerInventorySlots(8, 84);

        addDataSlot(new IntReferenceHolder() {
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

    @Override
    public boolean stillValid(PlayerEntity playerIn) {
        return stillValid(IWorldPosCallable.create(tileEntity.getLevel(),tileEntity.getBlockPos()),playerEntity, VEBlocks.BLAST_FURNACE_BLOCK);
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
    public ItemStack quickMoveStack(final PlayerEntity player, final int index) {
        ItemStack returnStack = ItemStack.EMPTY;
        final Slot slot = this.slots.get(index);
        if (slot != null && slot.hasItem()) {
            final ItemStack slotStack = slot.getItem();
            returnStack = slotStack.copy();

            final int containerSlots = this.slots.size() - player.inventory.items.size();
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
    public void setScreen(BlastFurnaceScreen screen){
        this.screen = screen;
    }

    public void updateDirectionButton(int direction, int slotId){ this.screen.updateButtonDirection(direction,slotId); }

    public void updateStatusButton(boolean status, int slotId){
        this.screen.updateBooleanButton(status, slotId);
    }

    public void updateStatusTank(boolean status, int id){
        this.screen.updateTankStatus(status, id);
    }

    public void updateDirectionTank(int direction, int id){
        this.screen.updateTankDirection(direction, id);
    }
}
