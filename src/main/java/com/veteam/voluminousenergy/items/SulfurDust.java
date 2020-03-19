package com.veteam.voluminousenergy.items;

import com.veteam.voluminousenergy.setup.VESetup;
import net.minecraft.item.Item;

public class SulfurDust extends Item {
    public SulfurDust (){
        super(new Item.Properties()
                .maxStackSize(64)
                .group(VESetup.itemGroup));
        setRegistryName("sulfurdust");
    }
}
