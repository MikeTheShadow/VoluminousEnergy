package com.veteam.voluminousenergy.items.dusts;

import net.minecraft.world.item.Item;
import com.veteam.voluminousenergy.util.VERegistryHelper;

public class FlourDust extends Item {
    public FlourDust() {
        super(new Item.Properties().setId(VERegistryHelper.currentItemId())
                .stacksTo(64)
        );
    }
}