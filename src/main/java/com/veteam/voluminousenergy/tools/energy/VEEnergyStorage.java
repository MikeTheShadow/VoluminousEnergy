package com.veteam.voluminousenergy.tools.energy;

import com.veteam.voluminousenergy.VoluminousEnergy;
import com.veteam.voluminousenergy.blocks.tiles.VETileEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.IntTag;
import net.minecraft.nbt.Tag;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.energy.EnergyStorage;


public class VEEnergyStorage extends EnergyStorage implements INBTSerializable<Tag> {

    private int production;
    private int consumption;
    private int upgradeSlotId = -1;

    public VEEnergyStorage(int capacity, int maxTransfer) {
        super(capacity, maxTransfer);
    }

    public VEEnergyStorage(int capacity, int maxTransfer, int production, int consumption, int upgradeSlotId) {
        super(capacity, maxTransfer);
        this.production = production;
        this.consumption = consumption;
        this.upgradeSlotId = upgradeSlotId;
    }

    public void setEnergy(int energy){
        this.energy = energy;
    }

    public void addEnergy(int energy) {
        this.energy += energy;
        if (this.energy > capacity){
            this.energy = capacity;
        }
    }

    public void consumeEnergy(int energy){
        this.energy -= energy;
        if (this.energy < 0){
            this.energy = 0;
        }
    }

    /*@Override
    public CompoundTag serializeNBT(){
        CompoundTag tag = new CompoundTag();
        tag.putInt("energy", getEnergyStored());
        return tag;
    }*/

    public void serializeNBT(CompoundTag tag) {
        tag.putInt("energy", getEnergyStored());
        tag.putInt("energy_production",production);
        tag.putInt("energy_consumption",consumption);
        tag.putInt("upgrade_slot",upgradeSlotId);
    }

    public void deserializeNBT(CompoundTag tag) {
        setEnergy(tag.getInt("energy"));
        this.production = tag.getInt("energy_production");
        this.consumption = tag.getInt("energy_consumption");
        this.upgradeSlotId = tag.getInt("upgrade_slot");
    }

    public VEEnergyStorage copy() {
        return new VEEnergyStorage(capacity,maxReceive,production,consumption,upgradeSlotId);
    }

    public void setMaxReceive(int amount) {
        this.maxReceive = amount;
    }

    public void setConsumption(int consumption) {
        this.consumption = consumption;
    }

    public void setProduction(int production) {
        this.production = production;
    }

    @Override
    public void deserializeNBT(Tag nbt) {
        if (!(nbt instanceof IntTag intNbt))
            throw new IllegalArgumentException("VEEnergyStorage: Cannot deserialize to an instance that isn't the default implementation!");
        setEnergy(intNbt.getAsInt());
    }

    public void setUpgradeSlotId(int upgradeSlotId) {
        this.upgradeSlotId = upgradeSlotId;
    }

    public int getProduction() {
        return production;
    }

    public int getConsumption() {
        return consumption;
    }

    public int getMaxTransfer() {
        return maxReceive;
    }

    public int getUpgradeSlotId() {
        return this.upgradeSlotId;
    }

    /**
     * How much energy can be stored
     * @return the int value of the capacity
     */
    public int getCapacity()
    {
        return capacity;
    }

    public boolean isFullyCharged() {
        return this.capacity == this.energy;
    }

}
