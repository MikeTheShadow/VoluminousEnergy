package com.veteam.voluminousenergy.items;

import net.minecraft.world.item.Item;
import com.veteam.voluminousenergy.util.VERegistryHelper;

public class TitaniumSawblade extends VEItem {
    public TitaniumSawblade() {
        super(new Item.Properties().setId(VERegistryHelper.currentItemId())
                .stacksTo(64)
        );
        setRegistryName("titanium_sawblade");
    }
}