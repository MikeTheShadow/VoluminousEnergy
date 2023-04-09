package com.veteam.voluminousenergy.items.batteries;

public class LeadAcidBattery extends VEEnergyItem{
    private static final int MAX_ENERGY = 250_000;
    private static final int MAX_TX = 1000;

    public LeadAcidBattery() {
        super(new Properties()
                        .stacksTo(1),
                MAX_ENERGY,
                MAX_TX);
        setRegistryName("lead_acid_battery");
    }
}
