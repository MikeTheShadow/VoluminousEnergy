package com.veteam.voluminousenergy.items.dusts;

import com.veteam.voluminousenergy.setup.VESetup;
import net.minecraft.item.Item;

public class TungstenDust extends Item {
    public TungstenDust (){
        super(new Item.Properties()
                .stacksTo(64)
                .tab(VESetup.itemGroup));
        setRegistryName("tungsten_dust");
    }
}