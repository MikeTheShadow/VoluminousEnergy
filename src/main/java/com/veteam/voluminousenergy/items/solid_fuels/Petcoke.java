package com.veteam.voluminousenergy.items.solid_fuels;

import com.veteam.voluminousenergy.items.VEItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.block.entity.FuelValues;

import javax.annotation.Nullable;
import com.veteam.voluminousenergy.util.VERegistryHelper;

public class Petcoke extends VEItem {
    public Petcoke() {
        super(new Item.Properties()
                .setId(VERegistryHelper.currentItemId())
                .stacksTo(64)
        );
        setRegistryName("petcoke");
    }

    @Override
    public int getBurnTime(ItemStack itemStack, @Nullable RecipeType<?> recipeType, FuelValues fuelValues) {
        return 4000;
    }
}
