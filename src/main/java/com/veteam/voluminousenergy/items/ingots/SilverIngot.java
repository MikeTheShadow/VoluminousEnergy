package com.veteam.voluminousenergy.items.ingots;


import com.veteam.voluminousenergy.items.VEItem;
import net.minecraft.world.item.Item;

public class SilverIngot extends VEItem {
    public SilverIngot (){
        super(new Item.Properties()
                .stacksTo(64)
        );
        setRegistryName("silver_ingot");
    }
}
