package com.veteam.voluminousenergy.blocks.containers;

import com.veteam.voluminousenergy.items.VEItems;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;

import javax.annotation.Nullable;

public class VoluminousContainer extends AbstractContainerMenu {
    BlockEntity tileEntity;

    protected VoluminousContainer(@Nullable MenuType<?> p_i50105_1_, int p_i50105_2_) {
        super(p_i50105_1_, p_i50105_2_);
    }

    @Override
    public boolean stillValid(Player p_75145_1_) {
        return false;
    }

    public BlockEntity getTileEntity(){
        return tileEntity;
    }

    protected int addSlotRange(IItemHandler handler, int index, int x, int y, int amount, int dx) {
        for (int i = 0 ; i < amount ; i++) {
            addSlot(new SlotItemHandler(handler, index, x, y));
            x += dx;
            index++;
        }
        return index;
    }

    protected int addSlotBox(IItemHandler handler, int index, int x, int y, int horAmount, int dx, int verAmount, int dy) {
        for (int j = 0 ; j < verAmount ; j++) {
            index = addSlotRange(handler, index, x, y, horAmount, dx);
            y += dy;
        }
        return index;
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

    public ItemStack handleCoreQuickMoveStackLogicWithUpgradeSlot(final int index, final int containerSlots, final int upgradeSlotId, ItemStack slotStack){
        if (index < containerSlots) { // Container --> Inventory
            if (!moveItemStackTo(slotStack, containerSlots, this.slots.size(), true)) {
                return ItemStack.EMPTY;
            }
        } else { // Inventory --> Container
            if(slotStack.is(VEItems.QUARTZ_MULTIPLIER) && !moveItemStackTo(slotStack, upgradeSlotId, upgradeSlotId+1, false)) {
                return ItemStack.EMPTY;
            }

            if (!slotStack.is(VEItems.QUARTZ_MULTIPLIER) && !moveItemStackTo(slotStack, 0, upgradeSlotId, false)){
                return ItemStack.EMPTY;
            }
        }
        return null;
    }

    public ItemStack handleCoreQuickMoveStackLogic(final int index, final int containerSlots, ItemStack slotStack){
        if (index < containerSlots) {
            if (!moveItemStackTo(slotStack, containerSlots, this.slots.size(), true)) {
                return ItemStack.EMPTY;
            }
        } else if (!moveItemStackTo(slotStack, 0, containerSlots, false)) {
            return ItemStack.EMPTY;
        }
        return null;
    }

    public void updateDirectionButton(int direction, int slotId){
    }

    public void updateStatusButton(boolean status, int slotId){
    }
}
