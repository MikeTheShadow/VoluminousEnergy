package com.veteam.voluminousenergy.tools.buttons.batteryBox;

import net.minecraft.nbt.CompoundTag;

public class VEBatterySwitchManager {

    private final int slot;
    private boolean flipped;

    public VEBatterySwitchManager(int slot, boolean flipped) {
        this.slot = slot;
        this.flipped = flipped;
    }

    public int getSlot() {
        return slot;
    }

    public boolean isFlipped() {
        return flipped;
    }

    public void setFlipped(boolean flipped) {
        this.flipped = flipped;
    }

    public void write(CompoundTag nbt, String prefix){
        nbt.putBoolean(prefix + "_enabled", isFlipped());
    }

    public void read(CompoundTag nbt, String prefix){
        setFlipped(nbt.getBoolean(prefix + "_enabled"));
        int sideInt = nbt.getInt(prefix + "_direction");
    }
}
