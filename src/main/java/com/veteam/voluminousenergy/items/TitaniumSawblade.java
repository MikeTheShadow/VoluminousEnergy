package com.veteam.voluminousenergy.items;

import com.veteam.voluminousenergy.setup.VESetup;
import net.minecraft.world.item.Item;

public class TitaniumSawblade extends VEItem {
    public TitaniumSawblade (){
        super(new Item.Properties()
                .stacksTo(64)
                .tab(VESetup.itemGroup)
        );
        setRegistryName("titanium_sawblade");
    }
}