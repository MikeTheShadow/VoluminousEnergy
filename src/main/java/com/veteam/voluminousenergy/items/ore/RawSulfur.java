package com.veteam.voluminousenergy.items.ore;

import com.veteam.voluminousenergy.setup.VESetup;
import net.minecraft.item.Item;

public class RawSulfur extends Item {
    public RawSulfur() {
        super(new Item.Properties()
                .stacksTo(64)
                .tab(VESetup.itemGroup)
        );
        setRegistryName("raw_sulfur");
    }
}
