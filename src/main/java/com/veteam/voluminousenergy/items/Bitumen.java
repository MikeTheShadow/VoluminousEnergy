package com.veteam.voluminousenergy.items;

import net.minecraft.world.item.Item;

public class Bitumen extends VEItem {
    public Bitumen() {
        super(new Item.Properties()
                .stacksTo(64)
        );
        setRegistryName("bitumen");
    }
}
