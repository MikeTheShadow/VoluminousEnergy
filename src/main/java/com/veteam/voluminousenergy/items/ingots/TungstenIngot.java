package com.veteam.voluminousenergy.items.ingots;

import com.veteam.voluminousenergy.setup.VESetup;
import net.minecraft.item.Item;

public class TungstenIngot extends Item {
    public TungstenIngot (){
        super(new Item.Properties()
                .stacksTo(64)
                .tab(VESetup.itemGroup)
        );
        setRegistryName("tungsten_ingot");
    }
}