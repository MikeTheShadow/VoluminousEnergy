package com.veteam.voluminousenergy.items.dusts;

import com.veteam.voluminousenergy.items.VEItem;
import net.minecraft.world.item.Item;

public class QuartzDust extends VEItem {
    public QuartzDust (){
        super(new Item.Properties()
                .stacksTo(64)
        );
        setRegistryName("quartz_dust");
    }
}
