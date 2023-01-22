package com.veteam.voluminousenergy.items.ingots;

import com.veteam.voluminousenergy.items.VEItem;
import net.minecraft.world.item.Item;

public class TungstenIngot extends VEItem {
    public TungstenIngot (){
        super(new Item.Properties()
                .stacksTo(64)
        );
        setRegistryName("tungsten_ingot");
    }
}