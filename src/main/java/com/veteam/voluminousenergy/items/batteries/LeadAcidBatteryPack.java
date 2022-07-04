package com.veteam.voluminousenergy.items.batteries;

import com.veteam.voluminousenergy.setup.VESetup;

public class LeadAcidBatteryPack extends VEEnergyItem {

    private static final int MAX_ENERGY = 750_000;
    private static final int MAX_TX = 3_000;

    public LeadAcidBatteryPack() {
        super(new Properties()
                        .tab(VESetup.itemGroup)
                        .stacksTo(1),
                MAX_ENERGY,
                MAX_TX
        );
        setRegistryName("lead_acid_battery_pack");
    }
}
