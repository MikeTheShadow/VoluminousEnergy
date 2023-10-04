package com.veteam.voluminousenergy.recipe;

import com.veteam.voluminousenergy.blocks.blocks.VEBlocks;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeType;
import org.jetbrains.annotations.NotNull;

public class ElectrolyzerRecipe extends VERNGRecipe {

    public static final RecipeType<ElectrolyzerRecipe> RECIPE_TYPE = VERecipes.VERecipeTypes.ELECTROLYZING.get();

    @Override
    public @NotNull RecipeType<?> getType() {
        return RECIPE_TYPE;
    }

    @Override
    public @NotNull ItemStack getToastSymbol() {
        return new ItemStack(VEBlocks.ELECTROLYZER_BLOCK.get());
    }

}
