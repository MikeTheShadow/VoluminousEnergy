package com.veteam.voluminousenergy.items;

import com.veteam.voluminousenergy.setup.VESetup;
import net.minecraft.world.item.Item;

public class ShreddedBiomass extends VEItem {
    public ShreddedBiomass(){
        super(new Item.Properties()
                .stacksTo(64)
                .tab(VESetup.itemGroup));
        setRegistryName("shredded_biomass");
    }
}
