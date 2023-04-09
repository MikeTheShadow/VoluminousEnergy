package com.veteam.voluminousenergy.items.gears;

import com.veteam.voluminousenergy.items.VEItem;
import net.minecraft.world.item.Item;

public class TitaniumGear extends VEItem {
    public TitaniumGear() {
        super(new Item.Properties()
                .stacksTo(64)
        );
        setRegistryName("titanium_gear");
    }
}
