package com.veteam.voluminousenergy.items.dusts;

import com.veteam.voluminousenergy.items.VEItem;
import net.minecraft.world.item.Item;

public class CupricOxideDust extends VEItem {
    public CupricOxideDust(){
        super(new Item.Properties()
                .stacksTo(64)
        );
        setRegistryName("cupric_oxide_dust");
    }
}
