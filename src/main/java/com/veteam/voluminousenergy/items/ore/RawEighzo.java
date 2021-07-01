package com.veteam.voluminousenergy.items.ore;

import com.veteam.voluminousenergy.setup.VESetup;
import net.minecraft.item.Item;

public class RawEighzo extends Item {
    public RawEighzo() {
        super(new Item.Properties()
                .stacksTo(64)
                .tab(VESetup.itemGroup)
        );
        setRegistryName("raw_eighzo");
    }
}