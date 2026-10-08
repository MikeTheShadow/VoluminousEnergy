package com.veteam.voluminousenergy.tools.sidemanager;

import com.veteam.voluminousenergy.blocks.tiles.VETileEntity;
import com.veteam.voluminousenergy.recipe.processor.BasicProcessor;
import com.veteam.voluminousenergy.tools.Config;
import com.veteam.voluminousenergy.util.VEFaceIO.Mode;
import com.veteam.voluminousenergy.util.VEIOPort;
import com.veteam.voluminousenergy.util.tiles.CapabilityMap;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.FluidUtil;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemHandlerHelper;

public class VEPortTransfer {
    private VEPortTransfer() {
    }

    public static void tick(VETileEntity tile) {
        Level level = tile.getLevel();
        if (level == null || level.isClientSide) {
            return;
        }
        int interval = Config.MACHINE_IO_INTERVAL.get();
        if (level.getGameTime() % interval != 0) {
            return;
        }
        long transferCycle = level.getGameTime() / interval;
        boolean changed = false;
        for (VEIOPort port : tile.getIOPorts()) {
            Mode mode = tile.getFaceIO().getTransferMode(port, transferCycle);
            if (mode == Mode.PASSIVE) {
                continue;
            }
            Direction face = CapabilityMap.toWorldDirection(port.getDirection(), tile);
            BlockPos neighborPos = tile.getBlockPos().relative(face);
            if (!level.hasChunkAt(neighborPos)) {
                continue;
            }
            if (port.isFluid()) {
                IFluidHandler neighbor = level.getCapability(Capabilities.FluidHandler.BLOCK, neighborPos, face.getOpposite());
                if (neighbor != null) {
                    changed |= transferFluid(tile, port, neighbor, mode);
                }
            } else {
                IItemHandler neighbor = level.getCapability(Capabilities.ItemHandler.BLOCK, neighborPos, face.getOpposite());
                if (neighbor != null) {
                    changed |= transferItems(tile, port, neighbor, mode);
                }
            }
        }
        if (changed) {
            if (tile.getRecipeProcessor() instanceof BasicProcessor processor) {
                processor.markRecipeDirty();
            }
            tile.setChanged();
            tile.updateClients();
        }
    }

    private static boolean transferItems(VETileEntity tile, VEIOPort port, IItemHandler neighbor, Mode mode) {
        int slot = port.getSlotNum();
        int limit = Config.MACHINE_IO_ITEMS.get();
        if (mode == Mode.PUSH) {
            ItemStack offered = tile.getInventory().extractItem(slot, limit, true);
            if (offered.isEmpty()) {
                return false;
            }
            int accepted = offered.getCount() - ItemHandlerHelper.insertItemStacked(neighbor, offered, true).getCount();
            if (accepted <= 0) {
                return false;
            }
            ItemStack extracted = tile.getInventory().extractItem(slot, accepted, false);
            ItemStack remainder = ItemHandlerHelper.insertItemStacked(neighbor, extracted, false);
            if (!remainder.isEmpty()) {
                ItemStack retained = tile.getInventory().getStackInSlot(slot).copy();
                if (retained.isEmpty()) {
                    retained = remainder;
                } else {
                    retained.grow(remainder.getCount());
                }
                tile.getInventory().setStackInSlot(slot, retained);
            }
            return extracted.getCount() > remainder.getCount();
        }
        for (int neighborSlot = 0; neighborSlot < neighbor.getSlots(); neighborSlot++) {
            ItemStack offered = neighbor.extractItem(neighborSlot, limit, true);
            if (offered.isEmpty()) {
                continue;
            }
            int accepted = offered.getCount() - tile.getInventory().insertItem(slot, offered, true).getCount();
            if (accepted <= 0) {
                continue;
            }
            ItemStack extracted = neighbor.extractItem(neighborSlot, accepted, false);
            if (extracted.isEmpty()) {
                continue;
            }
            // The local slot was validated by simulation and has no intervening writer on the server thread.
            ItemStack retained = tile.getInventory().getStackInSlot(slot).copy();
            if (retained.isEmpty()) {
                retained = extracted.copy();
            } else {
                retained.grow(extracted.getCount());
            }
            tile.getInventory().setStackInSlot(slot, retained);
            return true;
        }
        return false;
    }

    private static boolean transferFluid(VETileEntity tile, VEIOPort port, IFluidHandler neighbor, Mode mode) {
        IFluidHandler selected = tile.getSelectedTankHandler(port.getSlotNum());
        int limit = Config.MACHINE_IO_FLUID.get();
        if (mode == Mode.PUSH) {
            return !FluidUtil.tryFluidTransfer(neighbor, selected, limit, true).isEmpty();
        }
        for (int neighborTank = 0; neighborTank < neighbor.getTanks(); neighborTank++) {
            FluidStack offered = neighbor.getFluidInTank(neighborTank).copy();
            if (offered.isEmpty()) {
                continue;
            }
            offered.setAmount(Math.min(offered.getAmount(), limit));
            if (!FluidUtil.tryFluidTransfer(selected, neighbor, offered, true).isEmpty()) {
                return true;
            }
        }
        return false;
    }
}
