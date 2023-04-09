package com.veteam.voluminousenergy.items.upgrades;

import com.veteam.voluminousenergy.items.VEItem;
import com.veteam.voluminousenergy.setup.VESetup;
import net.minecraft.world.item.Item;

public class QuartzMultiplier extends VEItem {
    public QuartzMultiplier() {
        super(new Item.Properties()
                .stacksTo(4)
        );
        setRegistryName("quartz_multiplier");
    }
}
