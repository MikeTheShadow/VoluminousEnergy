package com.veteam.voluminousenergy.items.ore;

import com.veteam.voluminousenergy.setup.VESetup;
import net.minecraft.world.item.Item;

public class RawGalena extends Item {
    public RawGalena() {
        super(new Item.Properties()
                .stacksTo(64)
                .tab(VESetup.itemGroup)
        );
        setRegistryName("raw_galena");
    }
}
