package com.veteam.voluminousenergy.items.microchips;

import com.veteam.voluminousenergy.items.VEItem;
import net.minecraft.world.item.Item;
import com.veteam.voluminousenergy.util.VERegistryHelper;

public class SilverMicrochip extends VEItem {
    public SilverMicrochip() {
        super(new Item.Properties().setId(VERegistryHelper.currentItemId())
                .stacksTo(64)
        );
        setRegistryName("silver_microchip");
    }
}
