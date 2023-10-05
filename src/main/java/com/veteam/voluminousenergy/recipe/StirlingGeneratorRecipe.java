package com.veteam.voluminousenergy.recipe;

import com.veteam.voluminousenergy.blocks.blocks.VEBlocks;
import com.veteam.voluminousenergy.util.recipe.serializers.VEEnergyRecipeSerializer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import org.jetbrains.annotations.NotNull;

public class StirlingGeneratorRecipe extends VEEnergyRecipe {

    public static final RecipeType<StirlingGeneratorRecipe> RECIPE_TYPE = VERecipes.VERecipeTypes.STIRLING.get();

    public static final VEEnergyRecipeSerializer SERIALIZER = new VEEnergyRecipeSerializer();
    @Override
    public @NotNull RecipeSerializer<? extends VERecipe> getSerializer(){ return SERIALIZER;}

    @Override
    public @NotNull RecipeType<?> getType(){
        return RECIPE_TYPE;
    }

    @Override
    public @NotNull ItemStack getToastSymbol(){
        return new ItemStack(VEBlocks.STIRLING_GENERATOR_BLOCK.get());
    }

}