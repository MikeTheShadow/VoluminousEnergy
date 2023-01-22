package com.veteam.voluminousenergy.items.dusts;

import com.veteam.voluminousenergy.items.VEItem;
import net.minecraft.world.item.Item;

public class SulfurDust extends VEItem {
    public SulfurDust (){
        super(new Item.Properties()
                .stacksTo(64)
        );
        setRegistryName("sulfurdust");
    }
}
