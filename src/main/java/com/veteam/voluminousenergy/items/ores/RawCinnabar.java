package com.veteam.voluminousenergy.items.ores;

import com.veteam.voluminousenergy.items.VEItem;
import net.minecraft.world.item.Item;

public class RawCinnabar extends VEItem {
    public RawCinnabar() {
        super(new Item.Properties()
                .stacksTo(64)
        );
        setRegistryName("raw_cinnabar");
    }
}
