package com.veteam.voluminousenergy.items.dusts;

import com.veteam.voluminousenergy.items.VEItem;
import net.minecraft.world.item.Item;

public class BasaltDust extends VEItem {
    public BasaltDust(){
        super(new Item.Properties()
                .stacksTo(64)
        );
        setRegistryName("basalt_dust");
    }
}