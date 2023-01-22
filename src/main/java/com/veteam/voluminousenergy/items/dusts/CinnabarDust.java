package com.veteam.voluminousenergy.items.dusts;

import com.veteam.voluminousenergy.items.VEItem;
import net.minecraft.world.item.Item;

public class CinnabarDust extends VEItem {
    public CinnabarDust (){
        super(new Item.Properties()
                .stacksTo(64)
        );
        setRegistryName("cinnabar_dust");
    }
}
