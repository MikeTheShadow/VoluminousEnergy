package com.veteam.voluminousenergy.items.dusts;

import com.veteam.voluminousenergy.items.VEItem;
import net.minecraft.world.item.Item;

public class SilverDust extends VEItem {
    public SilverDust(){
        super(new Item.Properties()
                .stacksTo(64)
        );
        setRegistryName("silver_dust");
    }
}
