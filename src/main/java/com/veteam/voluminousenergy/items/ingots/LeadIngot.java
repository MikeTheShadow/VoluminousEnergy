package com.veteam.voluminousenergy.items.ingots;

import com.veteam.voluminousenergy.items.VEItem;
import net.minecraft.world.item.Item;
import com.veteam.voluminousenergy.util.VERegistryHelper;

public class LeadIngot extends VEItem {
    public LeadIngot() {
        super(new Item.Properties().setId(VERegistryHelper.currentItemId())
                .stacksTo(64)
        );
        setRegistryName("lead_ingot");
    }
}
