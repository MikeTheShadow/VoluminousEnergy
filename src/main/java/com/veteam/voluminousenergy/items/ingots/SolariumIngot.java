package com.veteam.voluminousenergy.items.ingots;

import com.veteam.voluminousenergy.items.VEItem;
import net.minecraft.world.item.Item;
import com.veteam.voluminousenergy.util.VERegistryHelper;

public class SolariumIngot extends VEItem {
    public SolariumIngot() {
        super(new Item.Properties().setId(VERegistryHelper.currentItemId())
                .stacksTo(64)
        );
        setRegistryName("solarium_ingot");
    }
}
