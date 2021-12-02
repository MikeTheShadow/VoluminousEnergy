package com.veteam.voluminousenergy.items.batteries;

import com.veteam.voluminousenergy.setup.VESetup;

import net.minecraft.world.item.Item.Properties;

public class LeadAcidBattery extends VEEnergyItem{
    private static final int MAX_ENERGY = 250_000;
    private static final int MAX_TX = 1000;

    public LeadAcidBattery() {
        super(new Properties()
                        .tab(VESetup.itemGroup)
                        .stacksTo(1),
                MAX_ENERGY,
                MAX_TX);
        setRegistryName("lead_acid_battery");
    }
}
