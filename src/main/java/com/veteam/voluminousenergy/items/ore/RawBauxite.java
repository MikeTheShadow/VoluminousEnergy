package com.veteam.voluminousenergy.items.ore;

import com.veteam.voluminousenergy.setup.VESetup;
import net.minecraft.item.Item;

public class RawBauxite extends Item {
    public RawBauxite(){
        super(new Item.Properties()
                .stacksTo(64)
                .tab(VESetup.itemGroup)
        );
        setRegistryName("raw_bauxite");
    }
}
