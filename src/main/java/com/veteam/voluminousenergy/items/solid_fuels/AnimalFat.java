package com.veteam.voluminousenergy.items.solid_fuels;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraftforge.common.extensions.IForgeItem;

import javax.annotation.Nullable;

public class AnimalFat extends Item implements IForgeItem {
    public AnimalFat() {
        super(new Item.Properties()
                .stacksTo(64)
        );
    }

    @Override
    public int getBurnTime(ItemStack itemStack, @Nullable RecipeType<?> recipeType) {
        return 200;
    }
}
