package com.veteam.voluminousenergy.tools.energy;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.IntNBT;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.energy.EnergyStorage;


public class VEEnergyStorage extends EnergyStorage implements INBTSerializable<CompoundNBT> {

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

    public void consumeEnergy(int energy){
        this.energy -= energy;
        if (this.energy < 0){
            this.energy = 0;
        }
    }

    public void serializeNBT(CompoundNBT tag) {
        tag.putInt("energy", getEnergyStored());
    }

    @Override
    public CompoundNBT serializeNBT(){
        CompoundNBT tag = new CompoundNBT();
        tag.putInt("energy", getEnergyStored());
        return tag;
    }

    @Override
    public void deserializeNBT(CompoundNBT tag) {
        setEnergy(tag.getInt("energy"));
    }
}
