package com.veteam.voluminousenergy.items.gears;

import com.veteam.voluminousenergy.setup.VESetup;
import net.minecraft.item.Item;

public class CarbonGear extends Item {
    public CarbonGear() {
        super(new Item.Properties()
            .stacksTo(64)
            .tab(VESetup.itemGroup)
        );
        setRegistryName("carbongear");
    }
}
