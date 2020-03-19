package com.veteam.voluminousenergy.items;

import com.veteam.voluminousenergy.setup.VESetup;
import net.minecraft.item.Item;

public class CarbonBrick extends Item {
    public CarbonBrick (){
        super(new Item.Properties()
            .maxStackSize(64)
            .group(VESetup.itemGroup)
        );
        setRegistryName("carbonbrick");
    }
}
