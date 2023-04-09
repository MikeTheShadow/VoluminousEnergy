package com.veteam.voluminousenergy.items.dusts;

import com.veteam.voluminousenergy.items.VEItem;
import net.minecraft.world.item.Item;

public class SolariumDust extends VEItem {
    public SolariumDust (){
        super(new Item.Properties()
                .stacksTo(64)
        );
        setRegistryName("solarium_dust");
    }
}
