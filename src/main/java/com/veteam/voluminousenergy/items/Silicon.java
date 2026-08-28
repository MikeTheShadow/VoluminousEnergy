package com.veteam.voluminousenergy.items;

import net.minecraft.world.item.Item;

public class Silicon extends VEItem {
    public Silicon() {
        super(new Item.Properties().setId(com.veteam.voluminousenergy.util.VERegistryHelper.currentItemId())
                .stacksTo(64)
        );
        setRegistryName("silicon");
    }
}
