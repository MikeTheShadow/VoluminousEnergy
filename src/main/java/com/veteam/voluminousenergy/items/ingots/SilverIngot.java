package com.veteam.voluminousenergy.items.ingots;


import com.veteam.voluminousenergy.items.VEItem;
import com.veteam.voluminousenergy.setup.VESetup;
import net.minecraft.world.item.Item;

public class SilverIngot extends VEItem {
    public SilverIngot (){
        super(new Item.Properties()
                .stacksTo(64)
                .tab(VESetup.itemGroup)
        );
        setRegistryName("silver_ingot");
    }
}
