package com.veteam.voluminousenergy.items;

import net.minecraft.world.item.Item;
import com.veteam.voluminousenergy.util.VERegistryHelper;

public class ShreddedBiomass extends VEItem {
    public ShreddedBiomass() {
        super(new Item.Properties().setId(VERegistryHelper.currentItemId())
                .stacksTo(64)
        );
        setRegistryName("shredded_biomass");
    }
}
