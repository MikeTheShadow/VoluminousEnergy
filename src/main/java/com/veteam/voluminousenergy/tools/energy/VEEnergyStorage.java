package com.veteam.voluminousenergy.tools.energy;

import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.neoforge.transfer.energy.SimpleEnergyHandler;


/**
 * The energy of a machine. Cables and items go through the transfer API, which applies the transfer
 * limit; the machine itself changes its energy with the owner operations ({@link #addEnergy},
 * {@link #consumeEnergy}, {@link #set}), which skip that limit and never open a transaction.
 */
public class VEEnergyStorage extends SimpleEnergyHandler {

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

    public void addEnergy(int energy) {
        set(Math.min(this.energy + energy, capacity));
    }

    public void consumeEnergy(int energy) {
        set(Math.max(this.energy - energy, 0));
    }

    @Override
    public void serialize(ValueOutput out) {
        super.serialize(out);
        out.putInt("energy_production", production);
        out.putInt("energy_consumption", consumption);
        out.putInt("upgrade_slot", upgradeSlotId);
    }

    @Override
    public void deserialize(ValueInput in) {
        super.deserialize(in);
        this.production = in.getIntOr("energy_production", 0);
        this.consumption = in.getIntOr("energy_consumption", 0);
        this.upgradeSlotId = in.getIntOr("upgrade_slot", -1);
    }

    public VEEnergyStorage copy() {
        return new VEEnergyStorage(capacity, maxInsert, production, consumption, upgradeSlotId);
    }

    public void setConsumption(int consumption) {
        this.consumption = consumption;
    }

    public void setProduction(int production) {
        this.production = production;
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

    public int getUpgradeSlotId() {
        return this.upgradeSlotId;
    }

    /**
     * How much energy can be stored
     *
     * @return the int value of the capacity
     */
    public int getCapacity() {
        return capacity;
    }

    public boolean isFullyCharged() {
        return this.capacity == this.energy;
    }

}
