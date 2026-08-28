package com.veteam.voluminousenergy.util;

import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.energy.IEnergyStorage;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.energy.EnergyHandler;
import net.neoforged.neoforge.transfer.fluid.FluidResource;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.transaction.TransactionContext;

/**
 * Bridges the mod's legacy (pre-26.1) IItemHandler / IFluidHandler / IEnergyStorage based
 * storage implementations onto NeoForge's newer transfer API.
 * <p>
 * NeoForge only ships a bridge in the new-to-old direction ({@code IItemHandler.of(ResourceHandler)},
 * {@code IEnergyStorage.of(EnergyHandler)}, {@code IFluidHandler.of(ResourceHandler)} - see
 * {@link com.veteam.voluminousenergy.items.tools.multitool.Multitool}). There is no equivalent
 * old-to-new bridge anywhere in NeoForge (confirmed via javap against the 26.1 universal jar and
 * the decompiled sources), which is what RegisterCapabilitiesEvent providers need to return.
 * This class fills that gap so the mod's existing IItemHandler/IFluidHandler/IEnergyStorage backed
 * storage classes (VEEnergyStorage, ItemStackHandler-based wrappers, FluidHandlerItemStack, etc.)
 * can still be registered without rewriting their internals.
 * <p>
 * Note: these adapters execute immediately against the wrapped legacy handler and do not
 * participate in the transaction rollback machinery (the legacy handlers have no concept of
 * snapshot/restore). The old behaviour never supported atomic multi-handler transactions either,
 * so this introduces no functional regression versus pre-port behaviour.
 */
public final class CapabilityAdapters {

    private CapabilityAdapters() {
    }

    public static ResourceHandler<ItemResource> toResourceHandler(IItemHandler itemHandler) {
        return new ResourceHandler<>() {
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
            public int insert(int index, ItemResource resource, int amount, TransactionContext context) {
                ItemStack remainder = itemHandler.insertItem(index, resource.toStack(amount), false);
                return amount - remainder.getCount();
            }

            @Override
            public int extract(int index, ItemResource resource, int amount, TransactionContext context) {
                ItemStack current = itemHandler.getStackInSlot(index);
                if (current.isEmpty() || !resource.matches(current)) return 0;
                return itemHandler.extractItem(index, amount, false).getCount();
            }
        };
    }

    public static EnergyHandler toEnergyHandler(IEnergyStorage energyStorage) {
        return new EnergyHandler() {
            @Override
            public long getAmountAsLong() {
                return energyStorage.getEnergyStored();
            }

            @Override
            public long getCapacityAsLong() {
                return energyStorage.getMaxEnergyStored();
            }

            @Override
            public int insert(int amount, TransactionContext context) {
                return energyStorage.receiveEnergy(amount, false);
            }

            @Override
            public int extract(int amount, TransactionContext context) {
                return energyStorage.extractEnergy(amount, false);
            }
        };
    }

    public static ResourceHandler<FluidResource> toResourceHandler(IFluidHandler fluidHandler) {
        return new ResourceHandler<>() {
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
            public int insert(int index, FluidResource resource, int amount, TransactionContext context) {
                return fluidHandler.fill(resource.toStack(amount), IFluidHandler.FluidAction.EXECUTE);
            }

            @Override
            public int extract(int index, FluidResource resource, int amount, TransactionContext context) {
                return fluidHandler.drain(resource.toStack(amount), IFluidHandler.FluidAction.EXECUTE).getAmount();
            }
        };
    }
}
