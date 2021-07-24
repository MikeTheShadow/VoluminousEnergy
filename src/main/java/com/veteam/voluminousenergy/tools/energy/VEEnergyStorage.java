package com.veteam.voluminousenergy.tools.energy;

import com.veteam.voluminousenergy.VoluminousEnergy;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.IntTag;
import net.minecraft.nbt.Tag;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.energy.EnergyStorage;


public class VEEnergyStorage extends EnergyStorage implements INBTSerializable<Tag> {

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

    @Override
    public CompoundTag serializeNBT(){
        CompoundTag tag = new CompoundTag();
        tag.putInt("energy", getEnergyStored());
        return tag;
    }

    @Override
    public void deserializeNBT(Tag nbt){
        if (!(nbt instanceof IntTag intNbt)){
            VoluminousEnergy.LOGGER.debug(nbt);
        } else {
            setEnergy(((IntTag) nbt).getAsInt());

        }
    }
}
