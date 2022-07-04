package com.veteam.voluminousenergy.items.batteries;

import com.veteam.voluminousenergy.setup.VESetup;

public class MercuryBatteryPack extends VEEnergyItem {

    private static final int MAX_ENERGY = 1_500_000;
    private static final int MAX_TX = 1_500;

    public MercuryBatteryPack() {
        super(new Properties()
                .tab(VESetup.itemGroup)
                .stacksTo(1),
                MAX_ENERGY,
                MAX_TX
        );
        setRegistryName("mercury_battery_pack");
    }
}
