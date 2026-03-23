package com.veteam.voluminousenergy.tools.energy;

import com.veteam.voluminousenergy.util.VEDataComponents;
import net.minecraft.world.item.ItemStack;

public class VEEnergyItemStorage extends VEEnergyStorage {
    private final ItemStack itemStack;

    public VEEnergyItemStorage(ItemStack itemStack, int capacity, int maxTransfer) {
        super(capacity, maxTransfer);
        this.itemStack = itemStack;
    }

    @Override
    public int receiveEnergy(int maxReceive, boolean simulate) {
        if (!canReceive()) return 0;
        int energyStored = getEnergyStored();
        int energyReceived = Math.min(capacity - energyStored, Math.min(this.maxReceive, maxReceive));
        if (!simulate && energyReceived > 0) {
            itemStack.set(VEDataComponents.ENERGY, energyStored + energyReceived);
        }
        return energyReceived;
    }

    @Override
    public int extractEnergy(int maxExtract, boolean simulate) {
        if (!canExtract()) return 0;
        int energyStored = getEnergyStored();
        int energyExtracted = Math.min(energyStored, Math.min(this.maxExtract, maxExtract));
        if (!simulate && energyExtracted > 0) {
            itemStack.set(VEDataComponents.ENERGY, energyStored - energyExtracted);
        }
        return energyExtracted;
    }

    @Override
    public int getEnergyStored() {
        return this.itemStack.getOrDefault(VEDataComponents.ENERGY, 0);
    }
}
