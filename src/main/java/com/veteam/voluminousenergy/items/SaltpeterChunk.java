package com.veteam.voluminousenergy.items;

import net.minecraft.world.item.Item;

public class SaltpeterChunk extends VEItem {
    public SaltpeterChunk() {
        super(new Item.Properties().setId(com.veteam.voluminousenergy.util.VERegistryHelper.currentItemId())
                .stacksTo(64)
        );
        setRegistryName("saltpeterchunk");
    }
}
