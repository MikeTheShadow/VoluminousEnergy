package com.veteam.voluminousenergy.items.dusts;

import com.veteam.voluminousenergy.items.VEItem;
import net.minecraft.world.item.Item;

public class LeadDust extends VEItem {
    public LeadDust(){
        super(new Item.Properties()
                .stacksTo(64)
        );
        setRegistryName("lead_dust");
    }
}
