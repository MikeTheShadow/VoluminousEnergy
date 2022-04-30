package com.veteam.voluminousenergy.blocks.containers;

import com.veteam.voluminousenergy.blocks.blocks.VEBlocks;
import com.veteam.voluminousenergy.blocks.inventory.slots.VEBucketSlot;
import com.veteam.voluminousenergy.tools.energy.VEEnergyStorage;
import com.veteam.voluminousenergy.util.RecipeUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.DataSlot;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.wrapper.InvWrapper;

import javax.annotation.Nonnull;

import static com.veteam.voluminousenergy.blocks.blocks.VEBlocks.COMBUSTION_GENERATOR_CONTAINER;

public class CombustionGeneratorContainer extends VoluminousContainer {

    private static final int NUMBER_OF_SLOTS = 4;

    public CombustionGeneratorContainer(int id, Level world, BlockPos pos, Inventory inventory, Player player){
        super(COMBUSTION_GENERATOR_CONTAINER,id);
        this.tileEntity = world.getBlockEntity(pos);
        this.tileEntity.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY);
        this.playerEntity = player;
        this.playerInventory = new InvWrapper(inventory);

        tileEntity.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).ifPresent(h -> {
            addSlot(new VEBucketSlot(h, 0, 38, 18)); // Oxidizer input slot
            addSlot(new VEBucketSlot(h, 1,38,49)); // Extract fluid from oxidizer slot
            addSlot(new VEBucketSlot(h, 2, 138,18)); // Fuel input slot
            addSlot(new VEBucketSlot(h, 3, 138,49)); // Extract fluid from fuel output
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

    public int getEnergy(){
        return tileEntity.getCapability(CapabilityEnergy.ENERGY).map(IEnergyStorage::getEnergyStored).orElse(0);
    }

    public int powerScreen(int px){
        int stored = tileEntity.getCapability(CapabilityEnergy.ENERGY).map(IEnergyStorage::getEnergyStored).orElse(0);
        int max = tileEntity.getCapability(CapabilityEnergy.ENERGY).map(IEnergyStorage::getMaxEnergyStored).orElse(0);
        return (((stored*100/max*100)/100)*px)/100;
    }

    @Override
    public boolean stillValid(Player playerIn) {
        return stillValid(ContainerLevelAccess.create(tileEntity.getLevel(),tileEntity.getBlockPos()),playerEntity, VEBlocks.COMBUSTION_GENERATOR_BLOCK);
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

            if (handleCoreQuickMoveStackLogic(index, NUMBER_OF_SLOTS, slotStack) != null) return ItemStack.EMPTY;

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

    @Override
    public ItemStack handleCoreQuickMoveStackLogic(final int index, final int containerSlots, ItemStack slotStack){
        if (index < containerSlots) { // Container --> Inventory
            if (!moveItemStackTo(slotStack, containerSlots, this.slots.size(), true)) {
                return ItemStack.EMPTY;
            }
        } else { // Inventory --> Container
            if (slotStack.getItem() instanceof BucketItem){
                // Handle Empty bucket
                if (slotStack.getItem().equals(Items.BUCKET)){
                    // Equally distribute empty buckets
                    if (!this.slots.get(0).hasItem() && !moveItemStackTo(slotStack, 0, 1, false)) return ItemStack.EMPTY;
                    if (!this.slots.get(2).hasItem() && !moveItemStackTo(slotStack, 2, 3, false)) return ItemStack.EMPTY;
                    return null;
                }

                // Handle bucket with fluid
                Fluid slotFluid = ((BucketItem) slotStack.getItem()).getFluid();

                if (RecipeUtil.isOxidizer(slotFluid, this.tileEntity.getLevel()) && !moveItemStackTo(slotStack, 0, 1, false)){
                    return ItemStack.EMPTY;
                } else if (RecipeUtil.isCombustibleFuel(slotFluid, this.tileEntity.getLevel()) && !moveItemStackTo(slotStack, 2, 3, false)){
                    return ItemStack.EMPTY;
                }
            }
        }
        return null;
    }

}
