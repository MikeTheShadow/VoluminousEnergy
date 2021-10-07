package com.veteam.voluminousenergy.items.dusts;

import com.veteam.voluminousenergy.setup.VESetup;
import net.minecraft.world.item.Item;

public class CupricOxideDust extends Item {
    public CupricOxideDust(){
        super(new Item.Properties()
                .stacksTo(64)
                .tab(VESetup.itemGroup)
        );
        setRegistryName("cupric_oxide_dust");
    }
}
