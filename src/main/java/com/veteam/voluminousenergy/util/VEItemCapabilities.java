package com.veteam.voluminousenergy.util;

import com.veteam.voluminousenergy.blocks.tiles.inventory.VEItemStackHandler;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.access.ItemAccess;
import net.neoforged.neoforge.transfer.energy.EnergyHandler;
import net.neoforged.neoforge.transfer.fluid.FluidResource;
import org.jetbrains.annotations.Nullable;

/**
 * Looks up item capabilities on a stack the caller holds, so changes made through them are written
 * back to that same stack, or on the stack in a machine slot, so changes are written back to the
 * slot. Each method returns null for an empty stack or one without the capability.
 */
public class VEItemCapabilities {

    private VEItemCapabilities() {
    }

    @Nullable
    public static EnergyHandler getEnergyHandler(ItemStack stack) {
        if (stack.isEmpty()) {
            return null;
        }
        return ItemAccess.forStack(stack).getCapability(Capabilities.Energy.ITEM);
    }

    @Nullable
    public static EnergyHandler getEnergyHandler(VEItemStackHandler inventory, int slot) {
        if (inventory.getResource(slot).isEmpty()) {
            return null;
        }
        return ItemAccess.forHandlerIndexStrict(inventory, slot).getCapability(Capabilities.Energy.ITEM);
    }

    @Nullable
    public static ResourceHandler<FluidResource> getFluidHandler(ItemStack stack) {
        if (stack.isEmpty()) {
            return null;
        }
        return ItemAccess.forStack(stack).getCapability(Capabilities.Fluid.ITEM);
    }

    @Nullable
    public static ResourceHandler<FluidResource> getFluidHandler(VEItemStackHandler inventory, int slot) {
        if (inventory.getResource(slot).isEmpty()) {
            return null;
        }
        return ItemAccess.forHandlerIndexStrict(inventory, slot).getCapability(Capabilities.Fluid.ITEM);
    }
}
