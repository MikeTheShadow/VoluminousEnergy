package com.veteam.voluminousenergy.items.dusts;

import com.veteam.voluminousenergy.items.VEItem;
import net.minecraft.world.item.Item;

public class NetheriteDust extends VEItem {
    public NetheriteDust(){
        super(new Item.Properties()
                .stacksTo(64)
        );
        setRegistryName("netherite_dust");
    }
}
