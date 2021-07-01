package com.veteam.voluminousenergy.items.plates;

import com.veteam.voluminousenergy.setup.VESetup;
import net.minecraft.item.Item;

public class AluminumPlate extends Item {
    public AluminumPlate (){
        super(new Item.Properties()
                .stacksTo(64)
                .tab(VESetup.itemGroup)
        );
        setRegistryName("aluminum_plate");
    }
}
