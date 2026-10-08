package com.veteam.voluminousenergy.items.batteries;
import com.veteam.voluminousenergy.util.VERegistryHelper;

public class LeadAcidBattery extends VEEnergyItem {
    private static final int MAX_ENERGY = 250_000;
    private static final int MAX_TX = 1000;

    public LeadAcidBattery() {
        super(new Properties().setId(VERegistryHelper.currentItemId())
                        .stacksTo(1),
                MAX_ENERGY,
                MAX_TX);
        setRegistryName("lead_acid_battery");
    }
}
