package com.veteam.voluminousenergy.items.plates;

import com.veteam.voluminousenergy.items.VEItem;
import net.minecraft.world.item.Item;

public class SolariumPlate extends VEItem {
    public SolariumPlate (){
        super(new Item.Properties()
                .stacksTo(64)
        );
        setRegistryName("solarium_plate");
    }
}
