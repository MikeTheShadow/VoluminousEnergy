package com.veteam.voluminousenergy.items.gears;

import com.veteam.voluminousenergy.items.VEItem;
import net.minecraft.world.item.Item;

public class SolariumGear extends VEItem {
    public SolariumGear(){
        super(new Item.Properties()
                .stacksTo(64)
        );
        setRegistryName("solarium_gear");
    }
}