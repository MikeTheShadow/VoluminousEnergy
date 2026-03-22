package com.veteam.voluminousenergy.recipe.processor;

import com.veteam.voluminousenergy.blocks.tiles.VETileEntity;
import com.veteam.voluminousenergy.blocks.tiles.inventory.VEItemStackHandler;
import com.veteam.voluminousenergy.items.batteries.VEEnergyItem;
import com.veteam.voluminousenergy.tools.Config;
import com.veteam.voluminousenergy.tools.energy.VEEnergyStorage;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.energy.IEnergyStorage;

public class BatteryBoxProcessor implements AbstractRecipeProcessor {

    private final int POWER_MAX_TX = Config.BATTERY_BOX_TRANSFER.get();
    private final int MAX_POWER = Config.BATTERY_BOX_MAX_POWER.get();

    @Override
    public void tick(VETileEntity tile) {
        ItemStack stack = tile.getInventory().getStackInSlot(0);

        IEnergyStorage itemEnergy = stack.getCapability(Capabilities.EnergyStorage.ITEM);

        if (itemEnergy == null)
            return;

        VEEnergyStorage storage = tile.getEnergy();
        if (tile.sendsOutPower()) {
            if (dischargeItem(stack, itemEnergy, storage))
                moveItem(tile);
        } else {
            if (chargeItem(stack, itemEnergy, storage))
                moveItem(tile);
        }
    }

    void moveItem(VETileEntity tile) {
        ItemStack stack = tile.getInventory().getStackInSlot(0);
        ItemStack output = tile.getInventory().getStackInSlot(1);

        if (!output.isEmpty())
            return;

        VEItemStackHandler handler = tile.getInventory();
        ItemStack newStack = stack.copy();
        newStack.setCount(1);
        handler.insertItem(1, newStack, false);
        handler.extractItem(0, 1, false);
        tile.setChanged();
    }

    private boolean dischargeItem(ItemStack stack, IEnergyStorage itemEnergy, VEEnergyStorage tileEnergy) {
        if (tileEnergy.getEnergyStored() < tileEnergy.getMaxEnergyStored()) {
            if (itemEnergy.canExtract()) {
                int toExtract;
                if (stack.getItem() instanceof VEEnergyItem) {
                    int maxExtractItem = ((VEEnergyItem) stack.getItem()).getMaxTransfer();
                    toExtract = Math.min(itemEnergy.getEnergyStored(), maxExtractItem);
                    toExtract = Math.min(toExtract, POWER_MAX_TX);
                } else
                    toExtract = Math.min(itemEnergy.getEnergyStored(), POWER_MAX_TX);

                int amountExtracted = itemEnergy.extractEnergy(toExtract, false);
                tileEnergy.receiveEnergy(amountExtracted, false);
                return itemEnergy.getEnergyStored() == 0;
            }
        }
        return false;
    }

    private boolean chargeItem(ItemStack stack, IEnergyStorage itemEnergy, VEEnergyStorage tileEnergy) {
        if (tileEnergy.getEnergyStored() > 0) {
            if (itemEnergy.canReceive()) {
                int toReceive;
                if (stack.getItem() instanceof VEEnergyItem) {
                    int maxReceiveItem = ((VEEnergyItem) stack.getItem()).getMaxTransfer();
                    toReceive = Math.min(
                            (itemEnergy.getMaxEnergyStored() - itemEnergy.getEnergyStored()),
                            maxReceiveItem);
                    toReceive = Math.min(toReceive, POWER_MAX_TX);
                    toReceive = Math.min(toReceive, tileEnergy.getEnergyStored());
                } else
                    toReceive = Math.min((itemEnergy.getMaxEnergyStored() - itemEnergy.getEnergyStored()),
                            POWER_MAX_TX);

                int extracted = tileEnergy.extractEnergy(toReceive, false);
                itemEnergy.receiveEnergy(extracted, false);
                return itemEnergy.getEnergyStored() == itemEnergy.getMaxEnergyStored();
            }
        }
        return false;
    }

    @Override
    public AbstractRecipeProcessor copy() {
        return new BatteryBoxProcessor();
    }
}
