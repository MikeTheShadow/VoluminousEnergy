package com.veteam.voluminousenergy.items.ingots;

import com.veteam.voluminousenergy.items.VEItem;
import net.minecraft.world.item.Item;

public class AluminumIngot extends VEItem {
    public AluminumIngot (){
        super(new Item.Properties()
                .stacksTo(64)
        );
        setRegistryName("aluminum_ingot");
    }
}
