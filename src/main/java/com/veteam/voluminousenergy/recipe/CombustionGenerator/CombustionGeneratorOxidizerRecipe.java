package com.veteam.voluminousenergy.recipe.CombustionGenerator;

import com.veteam.voluminousenergy.recipe.VEFluidRecipe;
import com.veteam.voluminousenergy.recipe.VERecipe;
import com.veteam.voluminousenergy.recipe.VERecipes;
import com.veteam.voluminousenergy.util.recipe.FluidIngredient;
import com.veteam.voluminousenergy.util.recipe.serializers.VEFluidInputNoOutputRecipeSerializer;
import com.veteam.voluminousenergy.util.recipe.serializers.VEFluidRecipeSerializer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraftforge.fluids.FluidStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class CombustionGeneratorOxidizerRecipe extends VEFluidRecipe {
    public static final RecipeType<VEFluidRecipe> RECIPE_TYPE = VERecipes.VERecipeTypes.OXIDIZING.get();

    public CombustionGeneratorOxidizerRecipe() {

    }

    public CombustionGeneratorOxidizerRecipe(List<FluidIngredient> fi, int processTime) {
        super(new ArrayList<>(), fi, new ArrayList<>(), new ArrayList<>(), processTime);
    }

    public static final VEFluidRecipeSerializer SERIALIZER = new VEFluidRecipeSerializer();
    @Override
    public @NotNull RecipeSerializer<? extends VERecipe> getSerializer(){ return SERIALIZER;}

    @Override
    public @NotNull RecipeType<VEFluidRecipe> getType() {
        return RECIPE_TYPE;
    }

}