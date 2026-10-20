package com.veteam.voluminousenergy.items.solid_fuels.tiny;

import com.veteam.voluminousenergy.items.VEItem;
import com.veteam.voluminousenergy.items.solid_fuels.VECookingFuels;
import com.veteam.voluminousenergy.util.VERegistryHelper;
import net.minecraft.world.item.Item;

public class TinyRosin extends VEItem {

    public TinyRosin() {
        super(new Item.Properties().setId(VERegistryHelper.currentItemId())
                .stacksTo(64)
                .cookingFuel(VECookingFuels.TIME_TINY_FUEL)
        );
        setRegistryName("tiny_rosin");
    }
}
