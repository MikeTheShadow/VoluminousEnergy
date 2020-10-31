package com.veteam.voluminousenergy.items.microchips;

import com.veteam.voluminousenergy.setup.VESetup;
import net.minecraft.item.Item;

public class SilverMicrochip extends Item {
    public SilverMicrochip(){
        super(new Item.Properties()
            .maxStackSize(64)
            .group(VESetup.itemGroup)
        );
        setRegistryName("silver_microchip");
    }
}
