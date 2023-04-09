package com.veteam.voluminousenergy.items.dusts;

import com.veteam.voluminousenergy.items.VEItem;
import net.minecraft.world.item.Item;

public class SandDust extends VEItem {
    public SandDust (){
        super(new Item.Properties()
                .stacksTo(64)
        );
        setRegistryName("sand_dust");
    }
}
