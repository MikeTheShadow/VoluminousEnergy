package com.veteam.voluminousenergy.items.gears;

import com.veteam.voluminousenergy.items.VEItem;
import net.minecraft.world.item.Item;
import com.veteam.voluminousenergy.util.VERegistryHelper;

public class TitaniumGear extends VEItem {
    public TitaniumGear() {
        super(new Item.Properties().setId(VERegistryHelper.currentItemId())
                .stacksTo(64)
        );
        setRegistryName("titanium_gear");
    }
}
