package com.veteam.voluminousenergy.items.gears;

import com.veteam.voluminousenergy.items.VEItem;
import net.minecraft.world.item.Item;

public class AluminumGear extends VEItem {
    public AluminumGear() {
        super(new Item.Properties()
                .stacksTo(64)
        );
        setRegistryName("aluminum_gear");
    }
}
