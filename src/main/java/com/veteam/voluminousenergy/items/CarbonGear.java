package com.veteam.voluminousenergy.items;

import com.veteam.voluminousenergy.setup.VESetup;
import net.minecraft.item.Item;

public class CarbonGear extends Item {
    public CarbonGear() {
        super(new Item.Properties()
            .maxStackSize(64)
            .group(VESetup.itemGroup)
        );
        setRegistryName("carbongear");
    }
}
