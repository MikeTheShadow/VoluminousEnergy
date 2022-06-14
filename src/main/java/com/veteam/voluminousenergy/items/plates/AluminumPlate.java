package com.veteam.voluminousenergy.items.plates;

import com.veteam.voluminousenergy.items.VEItem;
import com.veteam.voluminousenergy.setup.VESetup;
import net.minecraft.world.item.Item;

public class AluminumPlate extends VEItem {
    public AluminumPlate (){
        super(new Item.Properties()
                .stacksTo(64)
                .tab(VESetup.itemGroup)
        );
        setRegistryName("aluminum_plate");
    }
}
