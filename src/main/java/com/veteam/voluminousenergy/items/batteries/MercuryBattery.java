package com.veteam.voluminousenergy.items.batteries;
import com.veteam.voluminousenergy.util.VERegistryHelper;

public class MercuryBattery extends VEEnergyItem {
    private static final int MAX_ENERGY = 500_000;
    private static final int MAX_TX = 500;

    public MercuryBattery() {
        super(new Properties().setId(VERegistryHelper.currentItemId())
                        .stacksTo(1),
                MAX_ENERGY,
                MAX_TX);
        setRegistryName("mercury_battery");
    }
}
