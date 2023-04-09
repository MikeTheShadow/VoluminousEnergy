package com.veteam.voluminousenergy.items;

import net.minecraft.world.item.Item;

public class TitaniumSawblade extends VEItem {
    public TitaniumSawblade (){
        super(new Item.Properties()
                .stacksTo(64)
        );
        setRegistryName("titanium_sawblade");
    }
}