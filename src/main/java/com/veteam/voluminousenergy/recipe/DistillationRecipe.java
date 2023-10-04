package com.veteam.voluminousenergy.recipe;

import com.veteam.voluminousenergy.blocks.blocks.VEBlocks;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeType;
import org.jetbrains.annotations.NotNull;

public class DistillationRecipe extends VEFluidRNGRecipe {
    public static final RecipeType<VEFluidRecipe> RECIPE_TYPE = VERecipes.VERecipeTypes.DISTILLING.get();

    @Override
    public @NotNull ItemStack getToastSymbol(){
        return new ItemStack(VEBlocks.DISTILLATION_UNIT_BLOCK.get());
    }

    @Override
    public @NotNull RecipeType<VEFluidRecipe> getType(){return RECIPE_TYPE;}
}
