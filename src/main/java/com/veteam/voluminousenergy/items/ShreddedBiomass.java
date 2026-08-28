package com.veteam.voluminousenergy.items;

import net.minecraft.world.item.Item;

public class ShreddedBiomass extends VEItem {
    public ShreddedBiomass() {
        super(new Item.Properties().setId(com.veteam.voluminousenergy.util.VERegistryHelper.currentItemId())
                .stacksTo(64)
        );
        setRegistryName("shredded_biomass");
    }
}
