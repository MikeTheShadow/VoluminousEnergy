package com.veteam.voluminousenergy.items;

import com.veteam.voluminousenergy.setup.VESetup;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraftforge.common.extensions.IForgeItem;

import javax.annotation.Nullable;

public class PetCoke extends Item implements IForgeItem {
    public PetCoke()
    {
        super(new Item.Properties()
        .stacksTo(64)
        .tab(VESetup.itemGroup));
        setRegistryName("petcoke");
    }

    @Override
    public int getBurnTime(ItemStack itemStack, @Nullable RecipeType<?> recipeType) {
        return 4000;
    }
}
