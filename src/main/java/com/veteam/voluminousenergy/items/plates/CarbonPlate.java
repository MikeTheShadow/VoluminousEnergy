package com.veteam.voluminousenergy.items.plates;

import com.veteam.voluminousenergy.items.VEItem;
import net.minecraft.world.item.Item;

public class CarbonPlate extends VEItem {
    public CarbonPlate (){
        super(new Item.Properties()
                .stacksTo(64)
        );
        setRegistryName("carbon_plate");
    }
}
