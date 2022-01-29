package com.veteam.voluminousenergy.items.ores;

import com.veteam.voluminousenergy.setup.VESetup;
import net.minecraft.world.item.Item;

public class RawCinnabar extends Item {
    public RawCinnabar() {
        super(new Item.Properties()
                .stacksTo(64)
                .tab(VESetup.itemGroup)
        );
        setRegistryName("raw_cinnabar");
    }
}
