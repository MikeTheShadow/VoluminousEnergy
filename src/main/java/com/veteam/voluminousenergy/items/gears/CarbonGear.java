package com.veteam.voluminousenergy.items.gears;

import com.veteam.voluminousenergy.items.VEItem;
import net.minecraft.world.item.Item;

public class CarbonGear extends VEItem {
    public CarbonGear() {
        super(new Item.Properties()
            .stacksTo(64)
        );
        setRegistryName("carbongear");
    }
}
