package com.veteam.voluminousenergy.items.gears;

import com.veteam.voluminousenergy.setup.VESetup;
import net.minecraft.item.Item;

public class SolariumGear extends Item {
    public SolariumGear(){
        super(new Item.Properties()
                .stacksTo(64)
                .tab(VESetup.itemGroup)
        );
        setRegistryName("solarium_gear");
    }
}