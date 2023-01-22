package com.veteam.voluminousenergy.items.dusts;

import com.veteam.voluminousenergy.items.VEItem;
import net.minecraft.world.item.Item;

public class NetherrackDust extends VEItem {
    public NetherrackDust(){
        super(new Item.Properties()
                .stacksTo(64)
        );
        setRegistryName("netherrack_dust");
    }
}