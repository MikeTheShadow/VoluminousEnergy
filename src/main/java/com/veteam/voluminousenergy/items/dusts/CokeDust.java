package com.veteam.voluminousenergy.items.dusts;

import com.veteam.voluminousenergy.items.VEItem;
import net.minecraft.world.item.Item;

public class CokeDust extends VEItem {
    public CokeDust (){
        super(new Item.Properties()
            .stacksTo(64)
        );
        setRegistryName("cokedust");
    }
}
