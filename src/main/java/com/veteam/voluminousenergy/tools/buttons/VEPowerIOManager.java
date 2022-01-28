package com.veteam.voluminousenergy.tools.buttons;

import net.minecraft.nbt.CompoundTag;

public class VEPowerIOManager {

    private boolean flipped;

    public VEPowerIOManager(boolean flipped) {
        this.flipped = flipped;
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
    }

}
