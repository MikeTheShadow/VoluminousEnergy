package com.veteam.voluminousenergy.items.dusts;

import com.veteam.voluminousenergy.items.VEItem;
import net.minecraft.world.item.Item;

public class IronDust extends VEItem {
    public IronDust (){
        super(new Item.Properties()
                .stacksTo(64)
        );
        setRegistryName("iron_dust");
    }
}
