package com.veteam.voluminousenergy.items.dusts;

import com.veteam.voluminousenergy.setup.VESetup;
import net.minecraft.item.Item;

public class SteelDust extends Item {
    public SteelDust (){
        super(new Item.Properties()
                .stacksTo(64)
                .tab(VESetup.itemGroup));
        setRegistryName("steel_dust");
    }
}
