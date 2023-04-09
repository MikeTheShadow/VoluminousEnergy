package com.veteam.voluminousenergy.items;

import net.minecraft.world.item.Item;

public class ShreddedBiomass extends VEItem {
    public ShreddedBiomass(){
        super(new Item.Properties()
                .stacksTo(64)
        );
        setRegistryName("shredded_biomass");
    }
}
