package com.veteam.voluminousenergy.items.plates;

import com.veteam.voluminousenergy.setup.VESetup;
import net.minecraft.item.Item;

public class TitaniumPlate extends Item {
    public TitaniumPlate (){
        super(new Item.Properties()
                .maxStackSize(64)
                .group(VESetup.itemGroup)
        );
        setRegistryName("titanium_plate");
    }
}