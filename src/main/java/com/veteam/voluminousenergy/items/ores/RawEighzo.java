package com.veteam.voluminousenergy.items.ores;

import com.veteam.voluminousenergy.items.VEItem;
import com.veteam.voluminousenergy.setup.VESetup;
import net.minecraft.world.item.Item;

public class RawEighzo extends VEItem {
    public RawEighzo() {
        super(new Item.Properties()
                .stacksTo(64)
                .tab(VESetup.itemGroup)
        );
        setRegistryName("raw_eighzo");
    }
}