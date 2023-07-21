package com.veteam.voluminousenergy.items;

import net.minecraft.world.item.Item;

public class Silicon extends VEItem {
    public Silicon (){
        super(new Item.Properties()
                .stacksTo(64)
        );
        setRegistryName("silicon");
    }
}
