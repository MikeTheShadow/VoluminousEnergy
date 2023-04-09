package com.veteam.voluminousenergy.items.dusts;

import com.veteam.voluminousenergy.items.VEItem;
import net.minecraft.world.item.Item;

public class BauxiteDust extends VEItem {
    public BauxiteDust (){
        super(new Item.Properties()
                .stacksTo(64)
        );
        setRegistryName("bauxite_dust");
    }
}
