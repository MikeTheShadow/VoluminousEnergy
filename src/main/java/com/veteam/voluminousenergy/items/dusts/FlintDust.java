package com.veteam.voluminousenergy.items.dusts;

import com.veteam.voluminousenergy.items.VEItem;
import net.minecraft.world.item.Item;

public class FlintDust extends VEItem {
    public FlintDust(){
        super(new Item.Properties()
                .stacksTo(64)
        );
        setRegistryName("flint_dust");
    }
}