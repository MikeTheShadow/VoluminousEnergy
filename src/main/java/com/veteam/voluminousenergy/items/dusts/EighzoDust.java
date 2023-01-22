package com.veteam.voluminousenergy.items.dusts;

import com.veteam.voluminousenergy.items.VEItem;
import net.minecraft.world.item.Item;

public class EighzoDust extends VEItem {
    public EighzoDust (){
        super(new Item.Properties()
                .stacksTo(64)
        );
        setRegistryName("eighzo_dust");
    }
}
