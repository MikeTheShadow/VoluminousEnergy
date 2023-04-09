package com.veteam.voluminousenergy.items.dusts;

import com.veteam.voluminousenergy.items.VEItem;
import net.minecraft.world.item.Item;

public class RutileDust extends VEItem {
    public RutileDust (){
        super(new Item.Properties()
                .stacksTo(64)
        );
        setRegistryName("rutile_dust");
    }
}
