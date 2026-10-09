package com.veteam.voluminousenergy.items.plates;

import com.veteam.voluminousenergy.items.VEItem;
import com.veteam.voluminousenergy.util.VERegistryHelper;
import net.minecraft.world.item.Item;

public class CarbonPlate extends VEItem {
    public CarbonPlate() {
        super(new Item.Properties().setId(VERegistryHelper.currentItemId())
                .stacksTo(64)
        );
        setRegistryName("carbon_plate");
    }
}
