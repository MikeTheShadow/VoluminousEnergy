package com.veteam.voluminousenergy.items.upgrades;

import com.veteam.voluminousenergy.items.VEItem;
import net.minecraft.world.item.Item;

public class QuartzMultiplier extends VEItem {
    public QuartzMultiplier() {
        super(new Item.Properties().setId(com.veteam.voluminousenergy.util.VERegistryHelper.currentItemId())
                .stacksTo(4)
        );
        setRegistryName("quartz_multiplier");
    }
}
