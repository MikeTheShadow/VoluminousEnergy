package com.veteam.voluminousenergy.items.solid_fuels;

import com.veteam.voluminousenergy.items.VEItem;
import com.veteam.voluminousenergy.util.VERegistryHelper;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.storage.loot.providers.number.ints.ContextIntProviders;

public class Cactchar extends VEItem {

    public Cactchar() {
        super(new Item.Properties().setId(VERegistryHelper.currentItemId())
                .stacksTo(64)
                .cookingFuel(ContextIntProviders.COOKING_TIME_COAL)
        );
        setRegistryName("cactchar");
    }
}
