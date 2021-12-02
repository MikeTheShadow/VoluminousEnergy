package com.veteam.voluminousenergy.items.upgrades;

import com.veteam.voluminousenergy.setup.VESetup;
import net.minecraft.world.item.Item;

public class QuartzMultiplier extends Item {
    public QuartzMultiplier() {
        super(new Item.Properties()
                .stacksTo(4)
                .tab(VESetup.itemGroup)
        );
        setRegistryName("quartz_multiplier");
    }
}
