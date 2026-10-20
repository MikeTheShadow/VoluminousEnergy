package com.veteam.voluminousenergy.items.solid_fuels;

import com.veteam.voluminousenergy.items.VEItem;
import com.veteam.voluminousenergy.util.VERegistryHelper;
import net.minecraft.world.item.Item;

public class Petcoke extends VEItem {
    public Petcoke() {
        super(new Item.Properties()
                .setId(VERegistryHelper.currentItemId())
                .stacksTo(64)
                .cookingFuel(VECookingFuels.TIME_PETCOKE)
        );
        setRegistryName("petcoke");
    }
}
