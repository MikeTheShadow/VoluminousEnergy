package com.veteam.voluminousenergy.items.microchips;

import com.veteam.voluminousenergy.items.VEItem;
import com.veteam.voluminousenergy.setup.VESetup;
import net.minecraft.world.item.Item;

public class SilverMicrochip extends VEItem {
    public SilverMicrochip(){
        super(new Item.Properties()
            .stacksTo(64)
            .tab(VESetup.itemGroup)
        );
        setRegistryName("silver_microchip");
    }
}
