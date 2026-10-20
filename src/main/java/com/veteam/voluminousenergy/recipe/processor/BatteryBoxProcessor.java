package com.veteam.voluminousenergy.recipe.processor;

import com.veteam.voluminousenergy.blocks.tiles.VETileEntity;
import com.veteam.voluminousenergy.blocks.tiles.inventory.VEItemStackHandler;
import com.veteam.voluminousenergy.items.batteries.VEEnergyItem;
import com.veteam.voluminousenergy.tools.Config;
import com.veteam.voluminousenergy.tools.energy.VEEnergyStorage;
import com.veteam.voluminousenergy.util.VEItemCapabilities;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.transfer.energy.EnergyHandler;
import net.neoforged.neoforge.transfer.energy.EnergyHandlerUtil;

public class BatteryBoxProcessor implements AbstractRecipeProcessor {

    private final int POWER_MAX_TX = Config.BATTERY_BOX_TRANSFER.get();
    private final int MAX_POWER = Config.BATTERY_BOX_MAX_POWER.get();

    @Override
    public void tick(VETileEntity tile) {
        ItemStack stack = tile.getInventory().getStack(0);

        EnergyHandler itemEnergy = VEItemCapabilities.getEnergyHandler(tile.getInventory(), 0);

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
        tile.setChanged();
    }

    void moveItem(VETileEntity tile) {
        ItemStack stack = tile.getInventory().getStack(0);
        ItemStack output = tile.getInventory().getStack(1);

        if (!output.isEmpty())
            return;

        VEItemStackHandler handler = tile.getInventory();
        handler.add(1, stack.copyWithCount(1));
        handler.take(0, 1);
        tile.setChanged();
    }

    private boolean dischargeItem(ItemStack stack, EnergyHandler itemEnergy, VEEnergyStorage tileEnergy) {
        if (!tileEnergy.isFullyCharged() && itemEnergy.getCapacityAsLong() > 0) {
            int toExtract;
            if (stack.getItem() instanceof VEEnergyItem) {
                int maxExtractItem = ((VEEnergyItem) stack.getItem()).getMaxTransfer();
                toExtract = Math.min(itemEnergy.getAmountAsInt(), maxExtractItem);
            } else
                toExtract = Math.min(itemEnergy.getAmountAsInt(), POWER_MAX_TX);

            toExtract = Math.min(toExtract, POWER_MAX_TX);
            toExtract = Math.min(toExtract, tileEnergy.getCapacity() - tileEnergy.getAmountAsInt());

            if (toExtract <= 0)
                return itemEnergy.getAmountAsInt() == 0;

            EnergyHandlerUtil.move(itemEnergy, tileEnergy, toExtract, null);
            return itemEnergy.getAmountAsInt() == 0;
        }
        return false;
    }

    private boolean chargeItem(ItemStack stack, EnergyHandler itemEnergy, VEEnergyStorage tileEnergy) {
        if (tileEnergy.getAmountAsInt() == 0 || itemEnergy.getCapacityAsLong() == 0)
            return false;
        int toReceive;
        if (stack.getItem() instanceof VEEnergyItem veEnergyItem) {
            int maxReceiveItem =  veEnergyItem.getMaxTransfer();
            toReceive = Math.min((itemEnergy.getCapacityAsInt() - itemEnergy.getAmountAsInt()), maxReceiveItem);
        } else
            toReceive = Math.min((itemEnergy.getCapacityAsInt() - itemEnergy.getAmountAsInt()),
                POWER_MAX_TX);

        toReceive = Math.min(toReceive, POWER_MAX_TX);
        toReceive = Math.min(toReceive, tileEnergy.getAmountAsInt());

        if (toReceive <= 0)
            return itemEnergy.getAmountAsInt() == itemEnergy.getCapacityAsInt();

        EnergyHandlerUtil.move(tileEnergy, itemEnergy, toReceive, null);

        return itemEnergy.getAmountAsInt() == itemEnergy.getCapacityAsInt();
    }

    @Override
    public AbstractRecipeProcessor copy() {
        return new BatteryBoxProcessor();
    }
}
