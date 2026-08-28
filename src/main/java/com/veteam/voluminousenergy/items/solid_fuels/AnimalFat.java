package com.veteam.voluminousenergy.items.solid_fuels;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.block.entity.FuelValues;

import javax.annotation.Nullable;

public class AnimalFat extends Item {
    public AnimalFat() {
        super(new Item.Properties().setId(com.veteam.voluminousenergy.util.VERegistryHelper.currentItemId())
                .stacksTo(64)
        );
    }

    @Override
    public int getBurnTime(ItemStack itemStack, @Nullable RecipeType<?> recipeType, FuelValues fuelValues) {
        return 200;
    }
}
