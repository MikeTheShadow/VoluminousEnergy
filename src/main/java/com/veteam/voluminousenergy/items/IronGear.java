package com.veteam.voluminousenergy.items;

import com.veteam.voluminousenergy.setup.VESetup;
import net.minecraft.item.Item;

public class IronGear extends Item {
    public IronGear() {
    super(new Item.Properties()
        .maxStackSize(64)
        .group(VESetup.itemGroup)
    );
    setRegistryName("irongear");
    }
}