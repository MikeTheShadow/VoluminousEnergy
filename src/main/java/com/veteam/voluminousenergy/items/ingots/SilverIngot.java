package com.veteam.voluminousenergy.items.ingots;


import com.veteam.voluminousenergy.setup.VESetup;
import net.minecraft.item.Item;

public class SilverIngot extends Item {
    public SilverIngot (){
        super(new Item.Properties()
                .maxStackSize(64)
                .group(VESetup.itemGroup)
        );
        setRegistryName("silver_ingot");
    }
}
