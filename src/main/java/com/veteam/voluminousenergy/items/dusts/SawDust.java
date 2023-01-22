package com.veteam.voluminousenergy.items.dusts;

import com.veteam.voluminousenergy.items.VEItem;
import net.minecraft.world.item.Item;

public class SawDust extends VEItem {
    public SawDust (){
        super(new Item.Properties()
                .stacksTo(64)
        );
        setRegistryName("saw_dust");
    }
}
