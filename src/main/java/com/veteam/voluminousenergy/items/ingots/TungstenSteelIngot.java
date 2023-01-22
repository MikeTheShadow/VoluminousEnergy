package com.veteam.voluminousenergy.items.ingots;

import com.veteam.voluminousenergy.items.VEItem;
import net.minecraft.world.item.Item;

public class TungstenSteelIngot extends VEItem {
    public TungstenSteelIngot (){
        super(new Item.Properties()
                .stacksTo(64)
        );
        setRegistryName("tungsten_steel_ingot");
    }
}