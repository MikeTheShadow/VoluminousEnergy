package com.veteam.voluminousenergy.tools.energy;

import com.veteam.voluminousenergy.VoluminousEnergy;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.energy.EnergyStorage;

public class VEEnergyItemStorage extends VEEnergyStorage {
    private final ItemStack itemStack;

    public VEEnergyItemStorage(ItemStack itemStack, int capacity, int maxTransfer) {
        super(capacity, maxTransfer);
        this.itemStack = itemStack;
    }

    @Override
    public int receiveEnergy(int maxReceive, boolean simulate){
        if(!canReceive()) return 0;
        int energyStored = getEnergyStored();
        int energyReceived = Math.min(capacity - energyStored, Math.min(this.maxReceive, maxReceive));
        if(!simulate) writeEnergy(energyStored + energyReceived);
        return energyReceived;
    }

    @Override
    public int extractEnergy(int maxExtract, boolean simulate){
        if(!canExtract()) return 0;
        int energyExtracted = Math.min(getEnergyStored(), Math.min(this.maxExtract,maxExtract));
        VoluminousEnergy.LOGGER.debug("Extracting Energy from item. energyStored: " + getEnergyStored() + " energyExtracted: " + energyExtracted + " new total: " + (getEnergyStored() - energyExtracted) + " Max Extract: " + maxExtract);
        if(!simulate) writeEnergy(getEnergyStored() - energyExtracted);
        return energyExtracted;
    }

    @Override
    public int getEnergyStored(){return this.itemStack.getOrCreateTag().getInt("energy");}

    private void writeEnergy(int amount){this.itemStack.getOrCreateTag().putInt("energy",amount);}
}
