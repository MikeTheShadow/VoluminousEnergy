package com.veteam.voluminousenergy.items.gears;

import com.veteam.voluminousenergy.items.VEItem;
import net.minecraft.world.item.Item;

public class IronGear extends VEItem {
    public IronGear() {
    super(new Item.Properties()
        .stacksTo(64)
    );
    setRegistryName("irongear");
    }
}