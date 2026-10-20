package com.veteam.voluminousenergy.items.solid_fuels;

import com.veteam.voluminousenergy.util.VERegistryHelper;
import net.minecraft.world.item.Item;

public class AnimalFat extends Item {
    public AnimalFat() {
        super(new Item.Properties().setId(VERegistryHelper.currentItemId())
                .stacksTo(64)
                .cookingFuel(VECookingFuels.TIME_ANIMAL_FAT)
        );
    }
}
