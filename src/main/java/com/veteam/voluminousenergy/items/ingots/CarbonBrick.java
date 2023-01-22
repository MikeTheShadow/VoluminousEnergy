package com.veteam.voluminousenergy.items.ingots;

import com.veteam.voluminousenergy.items.VEItem;
import net.minecraft.world.item.Item;

public class CarbonBrick extends VEItem {
    public CarbonBrick (){
        super(new Item.Properties()
            .stacksTo(64)
        );
        setRegistryName("carbonbrick");
    }
}
