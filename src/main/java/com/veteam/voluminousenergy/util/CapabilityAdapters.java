package com.veteam.voluminousenergy.util;

import com.veteam.voluminousenergy.tools.energy.VEEnergyStorage;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.items.IItemHandlerModifiable;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.TransferPreconditions;
import net.neoforged.neoforge.transfer.energy.EnergyHandler;
import net.neoforged.neoforge.transfer.fluid.FluidResource;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.transaction.SnapshotJournal;
import net.neoforged.neoforge.transfer.transaction.TransactionContext;

import java.util.ArrayList;
import java.util.List;

/**
 * Adapts the item, fluid, and energy storage of machines to the transfer API handlers that block
 * capability providers return. NeoForge only bridges the other way. Each adapter applies a change to
 * the wrapped storage immediately and restores a snapshot of it if the transaction is aborted, so
 * simulated transfers leave the machine unchanged.
 */
public class CapabilityAdapters {

    private CapabilityAdapters() {
    }

    public static ResourceHandler<ItemResource> toResourceHandler(IItemHandlerModifiable itemHandler) {
        return new ItemHandlerAdapter(itemHandler);
    }

    public static EnergyHandler toEnergyHandler(VEEnergyStorage energyStorage) {
        return new EnergyStorageAdapter(energyStorage);
    }

    public static ResourceHandler<FluidResource> toResourceHandler(MultiFluidSlotWrapper fluidHandler) {
        return new FluidHandlerAdapter(fluidHandler);
    }

    private static class ItemHandlerAdapter extends SnapshotJournal<List<ItemStack>> implements ResourceHandler<ItemResource> {
        private final IItemHandlerModifiable itemHandler;

        private ItemHandlerAdapter(IItemHandlerModifiable itemHandler) {
            this.itemHandler = itemHandler;
        }

        @Override
        public int size() {
            return itemHandler.getSlots();
        }

        @Override
        public ItemResource getResource(int index) {
            return ItemResource.of(itemHandler.getStackInSlot(index));
        }

        @Override
        public long getAmountAsLong(int index) {
            return itemHandler.getStackInSlot(index).getCount();
        }

        @Override
        public long getCapacityAsLong(int index, ItemResource resource) {
            return itemHandler.getSlotLimit(index);
        }

        @Override
        public boolean isValid(int index, ItemResource resource) {
            return itemHandler.isItemValid(index, resource.toStack(1));
        }

        @Override
        public int insert(int index, ItemResource resource, int amount, TransactionContext transaction) {
            TransferPreconditions.checkNonEmptyNonNegative(resource, amount);
            ItemStack remainder = itemHandler.insertItem(index, resource.toStack(amount), true);
            int inserted = amount - remainder.getCount();
            if (inserted > 0) {
                updateSnapshots(transaction);
                itemHandler.insertItem(index, resource.toStack(inserted), false);
            }
            return inserted;
        }

        @Override
        public int extract(int index, ItemResource resource, int amount, TransactionContext transaction) {
            TransferPreconditions.checkNonEmptyNonNegative(resource, amount);
            if (!resource.matches(itemHandler.getStackInSlot(index))) {
                return 0;
            }
            int extracted = itemHandler.extractItem(index, amount, true).getCount();
            if (extracted > 0) {
                updateSnapshots(transaction);
                itemHandler.extractItem(index, extracted, false);
            }
            return extracted;
        }

        @Override
        protected List<ItemStack> createSnapshot() {
            List<ItemStack> stacks = new ArrayList<>(itemHandler.getSlots());
            for (int slot = 0; slot < itemHandler.getSlots(); slot++) {
                stacks.add(itemHandler.getStackInSlot(slot).copy());
            }
            return stacks;
        }

        // Only changed slots are written back, since side wrappers report slots they do not expose as empty.
        @Override
        protected void revertToSnapshot(List<ItemStack> snapshot) {
            for (int slot = 0; slot < snapshot.size(); slot++) {
                if (!ItemStack.matches(itemHandler.getStackInSlot(slot), snapshot.get(slot))) {
                    itemHandler.setStackInSlot(slot, snapshot.get(slot));
                }
            }
        }
    }

    private static class EnergyStorageAdapter extends SnapshotJournal<Integer> implements EnergyHandler {
        private final VEEnergyStorage energyStorage;

        private EnergyStorageAdapter(VEEnergyStorage energyStorage) {
            this.energyStorage = energyStorage;
        }

        @Override
        public long getAmountAsLong() {
            return energyStorage.getEnergyStored();
        }

        @Override
        public long getCapacityAsLong() {
            return energyStorage.getMaxEnergyStored();
        }

        @Override
        public int insert(int amount, TransactionContext transaction) {
            TransferPreconditions.checkNonNegative(amount);
            int inserted = energyStorage.receiveEnergy(amount, true);
            if (inserted > 0) {
                updateSnapshots(transaction);
                energyStorage.receiveEnergy(inserted, false);
            }
            return inserted;
        }

        @Override
        public int extract(int amount, TransactionContext transaction) {
            TransferPreconditions.checkNonNegative(amount);
            int extracted = energyStorage.extractEnergy(amount, true);
            if (extracted > 0) {
                updateSnapshots(transaction);
                energyStorage.extractEnergy(extracted, false);
            }
            return extracted;
        }

        @Override
        protected Integer createSnapshot() {
            return energyStorage.getEnergyStored();
        }

        @Override
        protected void revertToSnapshot(Integer snapshot) {
            energyStorage.setEnergy(snapshot);
        }
    }

    private static class FluidHandlerAdapter extends SnapshotJournal<List<FluidStack>> implements ResourceHandler<FluidResource> {
        private final MultiFluidSlotWrapper fluidHandler;

        private FluidHandlerAdapter(MultiFluidSlotWrapper fluidHandler) {
            this.fluidHandler = fluidHandler;
        }

        @Override
        public int size() {
            return fluidHandler.getTanks();
        }

        @Override
        public FluidResource getResource(int index) {
            return FluidResource.of(fluidHandler.getFluidInTank(index));
        }

        @Override
        public long getAmountAsLong(int index) {
            return fluidHandler.getFluidInTank(index).getAmount();
        }

        @Override
        public long getCapacityAsLong(int index, FluidResource resource) {
            return fluidHandler.getTankCapacity(index);
        }

        @Override
        public boolean isValid(int index, FluidResource resource) {
            return fluidHandler.isFluidValid(index, resource.toStack(1));
        }

        @Override
        public int insert(int index, FluidResource resource, int amount, TransactionContext transaction) {
            TransferPreconditions.checkNonEmptyNonNegative(resource, amount);
            int filled = fluidHandler.fill(index, resource.toStack(amount), IFluidHandler.FluidAction.SIMULATE);
            if (filled > 0) {
                updateSnapshots(transaction);
                fluidHandler.fill(index, resource.toStack(filled), IFluidHandler.FluidAction.EXECUTE);
            }
            return filled;
        }

        @Override
        public int extract(int index, FluidResource resource, int amount, TransactionContext transaction) {
            TransferPreconditions.checkNonEmptyNonNegative(resource, amount);
            int drained = fluidHandler.drain(index, resource.toStack(amount), IFluidHandler.FluidAction.SIMULATE).getAmount();
            if (drained > 0) {
                updateSnapshots(transaction);
                fluidHandler.drain(index, resource.toStack(drained), IFluidHandler.FluidAction.EXECUTE);
            }
            return drained;
        }

        @Override
        protected List<FluidStack> createSnapshot() {
            List<FluidStack> fluids = new ArrayList<>(fluidHandler.getTanks());
            for (int tank = 0; tank < fluidHandler.getTanks(); tank++) {
                fluids.add(fluidHandler.getFluidInTank(tank).copy());
            }
            return fluids;
        }

        @Override
        protected void revertToSnapshot(List<FluidStack> snapshot) {
            for (int tank = 0; tank < snapshot.size(); tank++) {
                if (!FluidStack.matches(fluidHandler.getFluidInTank(tank), snapshot.get(tank))) {
                    fluidHandler.setFluidInTank(tank, snapshot.get(tank));
                }
            }
        }
    }
}
