package com.veteam.voluminousenergy.items.microchips;

import com.veteam.voluminousenergy.items.VEItem;
import net.minecraft.world.item.Item;
import com.veteam.voluminousenergy.util.VERegistryHelper;

public class GoldMicrochip extends VEItem {
    public GoldMicrochip() {
        super(new Item.Properties().setId(VERegistryHelper.currentItemId())
                .stacksTo(64)
        );
        setRegistryName("gold_microchip");
    }


}

