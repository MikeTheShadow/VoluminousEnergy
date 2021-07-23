package com.veteam.voluminousenergy.items.microchips;

import com.veteam.voluminousenergy.setup.VESetup;
import net.minecraft.world.item.Item;

public class GoldMicrochip extends Item {
    public GoldMicrochip (){
        super(new Item.Properties()
                .stacksTo(64)
                .tab(VESetup.itemGroup)
        );
        setRegistryName("gold_microchip");
    }


}

