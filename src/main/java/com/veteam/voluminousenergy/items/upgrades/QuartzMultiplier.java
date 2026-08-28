package com.veteam.voluminousenergy.items.upgrades;

import com.veteam.voluminousenergy.items.VEItem;
import net.minecraft.world.item.Item;
import com.veteam.voluminousenergy.util.VERegistryHelper;

public class QuartzMultiplier extends VEItem {
    public QuartzMultiplier() {
        super(new Item.Properties().setId(VERegistryHelper.currentItemId())
                .stacksTo(4)
        );
        setRegistryName("quartz_multiplier");
    }
}
