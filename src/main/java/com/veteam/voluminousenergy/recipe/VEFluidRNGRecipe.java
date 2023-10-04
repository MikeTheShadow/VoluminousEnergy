package com.veteam.voluminousenergy.recipe;

import com.veteam.voluminousenergy.util.recipe.FluidIngredient;
import com.veteam.voluminousenergy.util.recipe.serializers.VEFluidRNGRecipeSerializer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraftforge.fluids.FluidStack;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class VEFluidRNGRecipe extends VEFluidRecipe {

    public List<Float> rngValues;

    private static final VEFluidRNGRecipeSerializer SERIALIZER = new VEFluidRNGRecipeSerializer();

    public VEFluidRNGRecipe() {

    }

    @Override
    public @NotNull RecipeSerializer<? extends VERecipe> getSerializer() {
        return SERIALIZER;
    }

    public VEFluidRNGRecipe(List<Ingredient> i, List<FluidIngredient> fi, List<FluidStack> of, List<ItemStack> oi, int processTime, List<Float> rngOutputs) {
        super(i,fi,of,oi, processTime);
        this.rngValues = rngOutputs;

    }

    public List<Float> getRNGOutputs() {
        return rngValues;
    }

    public void setRNGOutputs(List<Float> rngOutputs) {
        this.rngValues = rngOutputs;
    }

}
