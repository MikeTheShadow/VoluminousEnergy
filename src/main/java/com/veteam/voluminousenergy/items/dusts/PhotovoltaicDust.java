package com.veteam.voluminousenergy.items.dusts;

import com.veteam.voluminousenergy.setup.VESetup;
import net.minecraft.item.Item;

public class PhotovoltaicDust extends Item {
    public PhotovoltaicDust(){
        super(new Item.Properties()
                .maxStackSize(64)
                .group(VESetup.itemGroup));
        setRegistryName("photovoltaic_dust");
    }
}

