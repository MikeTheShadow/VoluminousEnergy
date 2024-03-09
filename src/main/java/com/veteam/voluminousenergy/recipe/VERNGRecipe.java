package com.veteam.voluminousenergy.recipe;

import com.veteam.voluminousenergy.util.recipe.VERecipeCodecs;
import net.minecraftforge.fluids.FluidStack;

import java.util.List;

public abstract class VERNGRecipe extends VERecipe {

    public List<Float> rngValues;
    public List<VERecipeCodecs.VEChancedItemWithCount> itemResultsWithChance;

    public VERNGRecipe() {

    }

    public VERNGRecipe(List<VERecipeCodecs.RegistryIngredient> i, List<VERecipeCodecs.RegistryFluidIngredient> fi, List<FluidStack> of, List<VERecipeCodecs.VEChancedItemWithCount> oi, int processTime) {
        super(i,fi,of,oi.stream().map(VERecipeCodecs.VEChancedItemWithCount::getAsItemStack).toList(), processTime);

        this.itemResultsWithChance = oi;
        this.rngValues = oi.stream().map(VERecipeCodecs.VEChancedItemWithCount::chance).toList();
    }

    public List<Float> getRNGOutputs() {
        return rngValues;
    }

    public void setRNGOutputs(List<Float> rngOutputs) {
        this.rngValues = rngOutputs;
    }

    public float getOutputChance(int i) {
        if (i >= rngValues.size()) {
            return 1;
        }
        return rngValues.get(i);
    }
}
