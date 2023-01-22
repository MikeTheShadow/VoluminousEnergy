package com.veteam.voluminousenergy.items.dusts;

import com.veteam.voluminousenergy.items.VEItem;
import net.minecraft.world.item.Item;

public class PhotovoltaicDust extends VEItem {
    public PhotovoltaicDust(){
        super(new Item.Properties()
                .stacksTo(64)
        );
        setRegistryName("photovoltaic_dust");
    }
}

