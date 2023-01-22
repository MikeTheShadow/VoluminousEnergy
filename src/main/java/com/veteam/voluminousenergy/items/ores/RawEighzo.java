package com.veteam.voluminousenergy.items.ores;

import com.veteam.voluminousenergy.items.VEItem;
import net.minecraft.world.item.Item;

public class RawEighzo extends VEItem {
    public RawEighzo() {
        super(new Item.Properties()
                .stacksTo(64)
        );
        setRegistryName("raw_eighzo");
    }
}