package com.veteam.voluminousenergy.items.dusts;

import net.minecraft.world.item.Item;

public class FlourDust extends Item {
    public FlourDust() {
        super(new Item.Properties().setId(com.veteam.voluminousenergy.util.VERegistryHelper.currentItemId())
                .stacksTo(64)
        );
    }
}