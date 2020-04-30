package com.veteam.voluminousenergy.tools;

import net.minecraftforge.energy.EnergyStorage;

public class VEEnergyStorage extends EnergyStorage {

    public VEEnergyStorage(int capacity, int maxTransfer) {
        super(capacity, maxTransfer);
    }

    public void setEnergy(int energy){
        this.energy = energy;
    }

    public void addEnergy(int energy){
        this.energy += energy;
        if (this.energy > getMaxEnergyStored()){
            this.energy = getEnergyStored();
        }
    }
}
