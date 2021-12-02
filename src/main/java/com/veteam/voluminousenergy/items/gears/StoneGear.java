package com.veteam.voluminousenergy.items.gears;

import com.veteam.voluminousenergy.setup.VESetup;
import net.minecraft.world.item.Item;

public class StoneGear extends Item {
    public StoneGear(){
        super(new Item.Properties()
        .stacksTo(64)
        .tab(VESetup.itemGroup)
        );
        setRegistryName("stonegear");
    }
}
