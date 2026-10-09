package com.veteam.voluminousenergy.util;

import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.energy.IEnergyStorage;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.access.ItemAccess;
import net.neoforged.neoforge.transfer.energy.EnergyHandler;
import net.neoforged.neoforge.transfer.fluid.FluidResource;
import org.jetbrains.annotations.Nullable;

/**
 * Looks up item capabilities on a stack the caller holds, so changes made through them are written
 * back to that same stack. Each method returns null for an empty stack or one without the capability.
 */
public class VEItemCapabilities {

    private VEItemCapabilities() {
    }

    @Nullable
    public static IEnergyStorage getEnergyStorage(ItemStack stack) {
        if (stack.isEmpty()) {
            return null;
        }
        EnergyHandler handler = ItemAccess.forStack(stack).getCapability(Capabilities.Energy.ITEM);
        return handler == null ? null : IEnergyStorage.of(handler);
    }

    @Nullable
    public static IFluidHandler getFluidHandler(ItemStack stack) {
        if (stack.isEmpty()) {
            return null;
        }
        ResourceHandler<FluidResource> handler = ItemAccess.forStack(stack).getCapability(Capabilities.Fluid.ITEM);
        return handler == null ? null : IFluidHandler.of(handler);
    }
}
