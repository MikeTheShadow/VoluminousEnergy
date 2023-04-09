package com.veteam.voluminousenergy.items.solid_fuels;

import com.veteam.voluminousenergy.items.VEItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraftforge.common.extensions.IForgeItem;

import javax.annotation.Nullable;

public class Rosin extends VEItem implements IForgeItem {
    public Rosin() {
        super(new Item.Properties()
                .stacksTo(64)
        );
        setRegistryName("rosin");
    }

    @Override
    public int getBurnTime(ItemStack itemStack, @Nullable RecipeType<?> recipeType) {
        return 800;
    }
}
