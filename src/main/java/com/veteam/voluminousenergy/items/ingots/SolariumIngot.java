package com.veteam.voluminousenergy.items.ingots;

import com.veteam.voluminousenergy.items.VEItem;
import com.veteam.voluminousenergy.util.VERegistryHelper;
import net.minecraft.world.item.Item;

public class SolariumIngot extends VEItem {
    public SolariumIngot() {
        super(new Item.Properties().setId(VERegistryHelper.currentItemId())
                .stacksTo(64)
        );
        setRegistryName("solarium_ingot");
    }
}
