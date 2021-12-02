package com.veteam.voluminousenergy.items.ingots;

import com.veteam.voluminousenergy.setup.VESetup;
import net.minecraft.world.item.Item;

public class LeadIngot extends Item {
    public LeadIngot (){
        super(new Item.Properties()
                .stacksTo(64)
                .tab(VESetup.itemGroup)
        );
        setRegistryName("lead_ingot");
    }
}
