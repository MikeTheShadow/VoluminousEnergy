package com.veteam.voluminousenergy.items.batteries;

import com.veteam.voluminousenergy.setup.VESetup;

public class MercuryBattery extends VEEnergyItem{
    private static final int MAX_ENERGY = 500_000;
    private static final int MAX_TX = 500;

    public MercuryBattery() {
        super(new Properties()
                .group(VESetup.itemGroup)
                .maxStackSize(1),
                MAX_ENERGY,
                MAX_TX);
        setRegistryName("mercury_battery");
    }
}
