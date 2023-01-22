package com.veteam.voluminousenergy.items.microchips;

import com.veteam.voluminousenergy.items.VEItem;
import net.minecraft.world.item.Item;

public class GoldMicrochip extends VEItem {
    public GoldMicrochip (){
        super(new Item.Properties()
                .stacksTo(64)
        );
        setRegistryName("gold_microchip");
    }


}

