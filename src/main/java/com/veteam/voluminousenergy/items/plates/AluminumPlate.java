package com.veteam.voluminousenergy.items.plates;

import com.veteam.voluminousenergy.items.VEItem;
import net.minecraft.world.item.Item;

public class AluminumPlate extends VEItem {
    public AluminumPlate (){
        super(new Item.Properties()
                .stacksTo(64)
        );
        setRegistryName("aluminum_plate");
    }
}
