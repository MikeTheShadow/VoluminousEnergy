package com.veteam.voluminousenergy.items.plates;

import com.veteam.voluminousenergy.items.VEItem;
import net.minecraft.world.item.Item;

public class TitaniumPlate extends VEItem {
    public TitaniumPlate (){
        super(new Item.Properties()
                .stacksTo(64)
        );
        setRegistryName("titanium_plate");
    }
}