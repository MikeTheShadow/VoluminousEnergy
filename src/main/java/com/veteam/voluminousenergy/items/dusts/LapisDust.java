package com.veteam.voluminousenergy.items.dusts;

import com.veteam.voluminousenergy.setup.VESetup;
import net.minecraft.item.Item;

public class LapisDust extends Item {
    public LapisDust (){
        super(new Item.Properties()
            .maxStackSize(64)
            .group(VESetup.itemGroup));
        setRegistryName("lapisdust");
    }
}
