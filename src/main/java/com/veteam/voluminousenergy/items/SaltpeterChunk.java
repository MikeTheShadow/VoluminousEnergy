package com.veteam.voluminousenergy.items;

import net.minecraft.world.item.Item;
import com.veteam.voluminousenergy.util.VERegistryHelper;

public class SaltpeterChunk extends VEItem {
    public SaltpeterChunk() {
        super(new Item.Properties().setId(VERegistryHelper.currentItemId())
                .stacksTo(64)
        );
        setRegistryName("saltpeterchunk");
    }
}
