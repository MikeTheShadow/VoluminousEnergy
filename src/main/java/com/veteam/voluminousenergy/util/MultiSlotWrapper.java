package com.veteam.voluminousenergy.util;

import com.google.common.base.Preconditions;
import com.veteam.voluminousenergy.tools.sidemanager.VESlotManager;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.IItemHandlerModifiable;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.List;

public class MultiSlotWrapper implements IItemHandlerModifiable {

    private final IItemHandlerModifiable inventory;
    HashMap<Integer,VESlotManager> managerHashMap = new HashMap<>();

    public MultiSlotWrapper(IItemHandlerModifiable inventory, List<VESlotManager> slotManager) {

        Preconditions.checkArgument(!slotManager.isEmpty(), "You need to have at least one slot defined!");
        this.inventory = inventory;
        slotManager.forEach(m -> managerHashMap.put(m.getSlotNum(),m));
    }

    @Override
    public int getSlots() {
        return inventory.getSlots();
    }

    @Override
    @Nonnull
    public ItemStack getStackInSlot(int slot) {
        if(managerHashMap.containsKey(slot)) {
            return inventory.getStackInSlot(slot);
        }
        return ItemStack.EMPTY;
    }

    @Override
    @Nonnull
    public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
        if(managerHashMap.containsKey(slot)) {
            VESlotManager manager = managerHashMap.get(slot);
            if(manager.getSlotType() == SlotType.OUTPUT) return stack;
            return inventory.insertItem(manager.getSlotNum(), stack, simulate);
        }
        return stack;
    }

    @Override
    @Nonnull
    public ItemStack extractItem(int slot, int amount, boolean simulate) {
        if(managerHashMap.containsKey(slot)) {
            VESlotManager manager = managerHashMap.get(slot);
            if(manager.getSlotType() == SlotType.INPUT) return ItemStack.EMPTY;
            return inventory.extractItem(managerHashMap.get(slot).getSlotNum(), amount, simulate);
        }
        return ItemStack.EMPTY;
    }

    @Override
    public void setStackInSlot(int slot, @Nonnull ItemStack stack) {
        if (checkSlot(slot)) {
            inventory.setStackInSlot(slot, stack);
        }
    }

    @Override
    public int getSlotLimit(int slot) {
        if (checkSlot(slot)) {
            return inventory.getSlotLimit(slot);
        }
        return 0;
    }

    @Override
    public boolean isItemValid(int slot, @Nonnull ItemStack stack) {
        if (checkSlot(slot)) {
            return inventory.isItemValid(slot, stack);
        }
        return false;
    }

    private boolean checkSlot(int localSlot) {
        return localSlot < getSlots();
    }

}