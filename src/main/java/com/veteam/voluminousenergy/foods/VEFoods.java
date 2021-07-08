package com.veteam.voluminousenergy.foods;

import com.veteam.voluminousenergy.tools.Config;
import net.minecraft.item.Food;

public class VEFoods {
    public static final Food COOKED_RICE = (new Food.Builder())
            .nutrition(Config.COOKED_RICE_NUTRITION.get())
            .saturationMod(Config.COOKED_RICE_SATURATION.get().floatValue())
            .build();
}