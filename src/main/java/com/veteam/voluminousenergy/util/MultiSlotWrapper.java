package com.veteam.voluminousenergy.util;

import com.veteam.voluminousenergy.tools.sidemanager.VESlotManager;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.IItemHandlerModifiable;
import org.jetbrains.annotations.Nullable;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.List;

public class MultiSlotWrapper implements IItemHandlerModifiable {

    private final IItemHandlerModifiable inventory;
    private final @Nullable Direction face;
    HashMap<Integer, VESlotManager> managerHashMap = new HashMap<>();

    public MultiSlotWrapper(IItemHandlerModifiable inventory, List<VESlotManager> slotManager, @Nullable Direction face) {
        this.inventory = inventory;
        this.face = face;
        slotManager.forEach(m -> managerHashMap.put(m.getSlotNum(), m));
    }

    @Override
    public int getSlots() {
        return inventory.getSlots();
    }

    @Override
    @Nonnull
    public ItemStack getStackInSlot(int slot) {
        if (checkSlot(slot)) {
            return inventory.getStackInSlot(slot);
        }
        return ItemStack.EMPTY;
    }

    @Override
    @Nonnull
    public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
        if (checkSlot(slot)) {
            VESlotManager manager = managerHashMap.get(slot);
            if (manager.getSlotType() == SlotType.OUTPUT) {
                return stack;
            }
            return inventory.insertItem(manager.getSlotNum(), stack, simulate);
        }
        return stack;
    }

    @Override
    @Nonnull
    public ItemStack extractItem(int slot, int amount, boolean simulate) {
        if (checkSlot(slot)) {
            VESlotManager manager = managerHashMap.get(slot);
            if (manager.getSlotType() == SlotType.INPUT) {
                return ItemStack.EMPTY;
            }
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
        return localSlot >= 0 && localSlot < getSlots() && managerHashMap.containsKey(localSlot)
                && managerHashMap.get(localSlot).isAssigned()
                && (face == null || managerHashMap.get(localSlot).getDirection() == face);
    }

    public void addSlotManager(VESlotManager manager) {
        managerHashMap.put(manager.getSlotNum(), manager);
    }

    public void removeSlotManager(VESlotManager manager) {
        managerHashMap.remove(manager.getSlotNum());
    }
}
