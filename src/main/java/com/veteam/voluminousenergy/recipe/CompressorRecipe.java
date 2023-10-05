package com.veteam.voluminousenergy.recipe;

import com.veteam.voluminousenergy.blocks.blocks.VEBlocks;
import com.veteam.voluminousenergy.util.recipe.serializers.VERecipeSerializer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import org.jetbrains.annotations.NotNull;

public class CompressorRecipe extends VERecipe {
    public static final RecipeType<CompressorRecipe> RECIPE_TYPE = VERecipes.VERecipeTypes.COMPRESSING.get();

    public static final VERecipeSerializer SERIALIZER = new VERecipeSerializer();

    @Override
    public @NotNull RecipeSerializer<? extends VERecipe> getSerializer() {
        return SERIALIZER;
    }

    @Override
    public @NotNull RecipeType<?> getType() {
        return RECIPE_TYPE;
    }

    @Override
    public @NotNull ItemStack getToastSymbol() {
        return new ItemStack(VEBlocks.COMPRESSOR_BLOCK.get());
    }
}
