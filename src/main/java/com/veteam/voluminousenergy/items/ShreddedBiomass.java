package com.veteam.voluminousenergy.items;

import com.veteam.voluminousenergy.setup.VESetup;
import net.minecraft.item.Item;

public class ShreddedBiomass extends Item {
    public ShreddedBiomass(){
        super(new Item.Properties()
                .maxStackSize(64)
                .group(VESetup.itemGroup));
        setRegistryName("shredded_biomass");
    }
}
