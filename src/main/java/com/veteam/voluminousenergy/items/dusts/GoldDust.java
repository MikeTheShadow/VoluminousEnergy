package com.veteam.voluminousenergy.items.dusts;

import com.veteam.voluminousenergy.items.VEItem;
import net.minecraft.world.item.Item;

public class GoldDust extends VEItem {
    public GoldDust (){
        super(new Item.Properties()
                .stacksTo(64)
        );
        setRegistryName("gold_dust");
    }
}
