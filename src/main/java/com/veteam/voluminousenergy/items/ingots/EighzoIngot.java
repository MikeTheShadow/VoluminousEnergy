package com.veteam.voluminousenergy.items.ingots;

import com.veteam.voluminousenergy.items.VEItem;
import net.minecraft.world.item.Item;

public class EighzoIngot extends VEItem {
    public EighzoIngot (){
        super(new Item.Properties()
                .stacksTo(64)
        );
        setRegistryName("eighzo_ingot");
    }
}
