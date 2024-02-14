package com.veteam.voluminousenergy.blocks.tiles;

import com.veteam.voluminousenergy.tools.energy.VEEnergyStorage;
import net.minecraftforge.common.util.LazyOptional;

public class VETileFactoryBuilder {

    private VETileEntity tileEntity;

    public VETileFactoryBuilder addEnergyStore(int maxPower,int transferRate) {
        VEEnergyStorage storage = new VEEnergyStorage(maxPower, transferRate);
        this.tileEntity.energy = LazyOptional.of(() -> storage);
        return this;
    }

}
