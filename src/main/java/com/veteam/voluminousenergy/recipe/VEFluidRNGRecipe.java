package com.veteam.voluminousenergy.recipe;

import com.veteam.voluminousenergy.util.recipe.VERecipeCodecs;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

import java.util.List;

public abstract class VEFluidRNGRecipe extends VEFluidRecipe {

    public List<Float> rngValues;
    public List<VERecipeCodecs.VEChancedItemWithCount> itemResultsWithChance;

    public VEFluidRNGRecipe() {

    }

    public VEFluidRNGRecipe(List<VERecipeCodecs.RegistryIngredient> i, List<VERecipeCodecs.RegistryFluidIngredient> fi, List<FluidStack> of, List<VERecipeCodecs.VEChancedItemWithCount> oi, int processTime) {
        super(i,fi,of,oi.stream().map(VERecipeCodecs.VEChancedItemWithCount::getAsItemStack).toList(), processTime);

        this.itemResultsWithChance = oi;
        this.rngValues = oi.stream().map(VERecipeCodecs.VEChancedItemWithCount::chance).toList();
    }

    @Deprecated
    // DANGEROUS: AVOID OUTSIDE RECIPE CODE DUE TO VARIABLE LENGTHS WITH MACHINE OUTPUTS THAT YOU MUST CHECK, USE getOutputChance(id) INSTEAD
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

    @Override
    public ItemStack getResult(int id) {
        // Sometimes recipes define less that what a machine can put out (not utilizing all output slots). Therefore, return ItemStack when querying beyond result length
        if (id >= this.itemResultsWithChance.size()) {
            return ItemStack.EMPTY;
        }
        return this.itemResultsWithChance.get(id).getAsItemStack();
    }
}
