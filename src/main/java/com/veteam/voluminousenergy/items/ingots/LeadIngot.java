package com.veteam.voluminousenergy.items.ingots;

import com.veteam.voluminousenergy.items.VEItem;
import net.minecraft.world.item.Item;

public class LeadIngot extends VEItem {
    public LeadIngot (){
        super(new Item.Properties()
                .stacksTo(64)
        );
        setRegistryName("lead_ingot");
    }
}
