package com.veteam.voluminousenergy.items.dusts;

import com.veteam.voluminousenergy.items.VEItem;
import net.minecraft.world.item.Item;

public class SoulsandDust extends VEItem {
    public SoulsandDust (){
        super(new Item.Properties()
                .stacksTo(64)
        );
        setRegistryName("soulsand_dust");
    }
}
