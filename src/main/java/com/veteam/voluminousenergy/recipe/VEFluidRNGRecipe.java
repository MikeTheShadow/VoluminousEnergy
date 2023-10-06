package com.veteam.voluminousenergy.recipe;

import com.veteam.voluminousenergy.util.recipe.FluidIngredient;
import com.veteam.voluminousenergy.util.recipe.VERecipeCodecs;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.fluids.FluidStack;

import java.util.List;

public abstract class VEFluidRNGRecipe extends VEFluidRecipe {

    public List<Float> rngValues;

    public VEFluidRNGRecipe() {

    }

    public VEFluidRNGRecipe(List<VERecipeCodecs.RegistryIngredient> i, List<VERecipeCodecs.RegistryFluidIngredient> fi, List<FluidStack> of, List<ItemStack> oi, int processTime, List<Float> rngOutputs) {
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
