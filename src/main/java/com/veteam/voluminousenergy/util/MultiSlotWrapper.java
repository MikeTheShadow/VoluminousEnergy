package com.veteam.voluminousenergy.util;

import com.veteam.voluminousenergy.blocks.tiles.inventory.VEItemStackHandler;
import com.veteam.voluminousenergy.tools.sidemanager.VESlotManager;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.TransferPreconditions;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.transaction.TransactionContext;

import java.util.HashMap;
import java.util.List;

/**
 * The view of a machine's inventory from one side. Only the slots assigned to that side are visible,
 * output slots refuse insertion, input slots refuse extraction, and disabled slots refuse both.
 */
public class MultiSlotWrapper implements ResourceHandler<ItemResource> {

    private final VEItemStackHandler inventory;
    HashMap<Integer, VESlotManager> managerHashMap = new HashMap<>();

    public MultiSlotWrapper(VEItemStackHandler inventory, List<VESlotManager> slotManager) {
        this.inventory = inventory;
        slotManager.forEach(m -> managerHashMap.put(m.getSlotNum(), m));
    }

    @Override
    public int size() {
        return inventory.size();
    }

    @Override
    public ItemResource getResource(int slot) {
        if (managerHashMap.containsKey(slot)) {
            return inventory.getResource(slot);
        }
        return ItemResource.EMPTY;
    }

    @Override
    public long getAmountAsLong(int slot) {
        if (managerHashMap.containsKey(slot)) {
            return inventory.getAmountAsLong(slot);
        }
        return 0;
    }

    @Override
    public long getCapacityAsLong(int slot, ItemResource resource) {
        return inventory.getCapacityAsLong(slot, resource);
    }

    @Override
    public boolean isValid(int slot, ItemResource resource) {
        return inventory.isValid(slot, resource);
    }

    @Override
    public int insert(int slot, ItemResource resource, int amount, TransactionContext transaction) {
        TransferPreconditions.checkNonEmptyNonNegative(resource, amount);
        VESlotManager manager = managerHashMap.get(slot);
        if (manager == null || manager.getSlotType() == SlotType.OUTPUT || !manager.getStatus()) return 0;
        return inventory.insert(slot, resource, amount, transaction);
    }

    @Override
    public int extract(int slot, ItemResource resource, int amount, TransactionContext transaction) {
        TransferPreconditions.checkNonEmptyNonNegative(resource, amount);
        VESlotManager manager = managerHashMap.get(slot);
        if (manager == null || manager.getSlotType() == SlotType.INPUT || !manager.getStatus()) return 0;
        return inventory.extract(slot, resource, amount, transaction);
    }

    public void addSlotManager(VESlotManager manager) {
        managerHashMap.put(manager.getSlotNum(), manager);
    }

    public void removeSlotManager(VESlotManager manager) {
        managerHashMap.remove(manager.getSlotNum());
    }
}
