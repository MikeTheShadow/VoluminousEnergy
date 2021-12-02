package com.veteam.voluminousenergy.items.ingots;

import com.veteam.voluminousenergy.setup.VESetup;
import net.minecraft.world.item.Item;

public class EighzoIngot extends Item {
    public EighzoIngot (){
        super(new Item.Properties()
                .stacksTo(64)
                .tab(VESetup.itemGroup));
        setRegistryName("eighzo_ingot");
    }
}
