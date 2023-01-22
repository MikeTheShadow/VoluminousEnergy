package com.veteam.voluminousenergy.items.gears;

import com.veteam.voluminousenergy.items.VEItem;
import net.minecraft.world.item.Item;

public class StoneGear extends VEItem {
    public StoneGear(){
        super(new Item.Properties()
        .stacksTo(64)
        );
        setRegistryName("stonegear");
    }
}
