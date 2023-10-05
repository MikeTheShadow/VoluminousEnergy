package com.veteam.voluminousenergy.recipe.CombustionGenerator;

import com.veteam.voluminousenergy.recipe.VEFluidRecipe;
import com.veteam.voluminousenergy.recipe.VERecipe;
import com.veteam.voluminousenergy.recipe.VERecipes;
import com.veteam.voluminousenergy.util.recipe.FluidIngredient;
import com.veteam.voluminousenergy.util.recipe.serializers.VEFluidInputNoOutputRecipeSerializer;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class CombustionGeneratorFuelRecipe extends VEFluidRecipe {
    public static final RecipeType<VEFluidRecipe> RECIPE_TYPE = VERecipes.VERecipeTypes.FUEL_COMBUSTION.get();

    public static final VEFluidInputNoOutputRecipeSerializer SERIALIZER = new VEFluidInputNoOutputRecipeSerializer();
    private int volumetricEnergy;

    public CombustionGeneratorFuelRecipe() {

    }

    public CombustionGeneratorFuelRecipe(List<FluidIngredient> fi, int processTime, int volumetricEnergy) {
        super(new ArrayList<>(), fi, new ArrayList<>(), new ArrayList<>(), processTime);
        this.volumetricEnergy = volumetricEnergy;
    }

    @Override
    public @NotNull RecipeSerializer<? extends VERecipe> getSerializer(){ return SERIALIZER;}

    @Override
    public @NotNull RecipeType<VEFluidRecipe> getType(){return RECIPE_TYPE;}

    public int getVolumetricEnergy() {return volumetricEnergy;}

}
