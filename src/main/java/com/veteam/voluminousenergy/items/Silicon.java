package com.veteam.voluminousenergy.items;

import com.veteam.voluminousenergy.util.VERegistryHelper;
import net.minecraft.world.item.Item;

public class Silicon extends VEItem {
    public Silicon() {
        super(new Item.Properties().setId(VERegistryHelper.currentItemId())
                .stacksTo(64)
        );
        setRegistryName("silicon");
    }
}
