package com.veteam.voluminousenergy.items.dusts;

import com.veteam.voluminousenergy.items.VEItem;
import net.minecraft.world.item.Item;

public class TungstenDust extends VEItem {
    public TungstenDust (){
        super(new Item.Properties()
                .stacksTo(64)
        );
        setRegistryName("tungsten_dust");
    }
}