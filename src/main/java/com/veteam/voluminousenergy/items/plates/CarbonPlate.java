package com.veteam.voluminousenergy.items.plates;

import com.veteam.voluminousenergy.setup.VESetup;
import net.minecraft.item.Item;

public class CarbonPlate extends Item {
    public CarbonPlate (){
        super(new Item.Properties()
                .maxStackSize(64)
                .group(VESetup.itemGroup)
        );
        setRegistryName("carbon_plate");
    }
}
