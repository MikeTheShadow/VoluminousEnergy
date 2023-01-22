package com.veteam.voluminousenergy.items.dusts;

import com.veteam.voluminousenergy.items.VEItem;
import net.minecraft.world.item.Item;

public class CopperCarbonateDust extends VEItem {
    public CopperCarbonateDust (){
        super(new Item.Properties()
                .stacksTo(64)
        );
        setRegistryName("copper_carbonate_dust");
    }
}