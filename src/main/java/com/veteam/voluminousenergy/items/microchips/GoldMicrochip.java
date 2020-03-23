package com.veteam.voluminousenergy.items.microchips;

import com.veteam.voluminousenergy.setup.VESetup;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class GoldMicrochip extends Item {
    public GoldMicrochip (){
        super(new Item.Properties()
                .maxStackSize(64)
                .group(VESetup.itemGroup)
        );
        setRegistryName("gold_microchip");
    }


}

