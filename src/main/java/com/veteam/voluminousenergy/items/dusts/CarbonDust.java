package com.veteam.voluminousenergy.items.dusts;

import com.veteam.voluminousenergy.items.VEItem;
import com.veteam.voluminousenergy.util.VERegistryHelper;
import net.minecraft.world.item.Item;

public class CarbonDust extends VEItem {
    public CarbonDust() {
        super(new Item.Properties().setId(VERegistryHelper.currentItemId())
                .stacksTo(64)
        );
        setRegistryName("carbondust");
    }
}
