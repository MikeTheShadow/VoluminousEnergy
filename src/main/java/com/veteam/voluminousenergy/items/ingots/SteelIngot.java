package com.veteam.voluminousenergy.items.ingots;

import com.veteam.voluminousenergy.items.VEItem;
import net.minecraft.world.item.Item;

public class SteelIngot extends VEItem {
    public SteelIngot (){
        super(new Item.Properties()
                .stacksTo(64)
        );
        setRegistryName("steel_ingot");
    }
}