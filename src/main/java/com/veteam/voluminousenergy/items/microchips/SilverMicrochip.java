package com.veteam.voluminousenergy.items.microchips;

import com.veteam.voluminousenergy.items.VEItem;
import net.minecraft.world.item.Item;

public class SilverMicrochip extends VEItem {
    public SilverMicrochip(){
        super(new Item.Properties()
            .stacksTo(64)
        );
        setRegistryName("silver_microchip");
    }
}
