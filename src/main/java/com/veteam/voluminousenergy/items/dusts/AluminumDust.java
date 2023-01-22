package com.veteam.voluminousenergy.items.dusts;

import com.veteam.voluminousenergy.items.VEItem;
import net.minecraft.world.item.Item;

public class AluminumDust extends VEItem {
    public AluminumDust (){
        super(new Item.Properties()
                .stacksTo(64)
        );
        setRegistryName("aluminum_dust");
    }
}
