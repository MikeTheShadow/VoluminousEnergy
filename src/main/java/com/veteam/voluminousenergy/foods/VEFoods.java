package com.veteam.voluminousenergy.foods;

import com.veteam.voluminousenergy.tools.Config;
import net.minecraft.world.food.FoodProperties;

public class VEFoods {
    public static final FoodProperties COOKED_RICE = (new FoodProperties.Builder())
            .nutrition(Config.COOKED_RICE_NUTRITION.get())
            .saturationMod(Config.COOKED_RICE_SATURATION.get().floatValue())
            .build();
}