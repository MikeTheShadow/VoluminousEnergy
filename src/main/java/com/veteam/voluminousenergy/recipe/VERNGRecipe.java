package com.veteam.voluminousenergy.recipe;

import com.veteam.voluminousenergy.util.recipe.VERecipeCodecs;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public abstract class VERNGRecipe extends VERecipe {

    public List<Float> rngValues;
    public List<VERecipeCodecs.VEChancedItemWithCount> resultsWithChance;

    public VERNGRecipe() {

    }

    public VERNGRecipe(List<VERecipeCodecs.RegistryIngredient> ingredients, List<VERecipeCodecs.VEChancedItemWithCount> results, int processTime) {
        super(
                ingredients,
                results.stream().map(VERecipeCodecs.VEChancedItemWithCount::getAsItemStack).toList(),
                processTime
        );
        this.resultsWithChance = results;
        this.rngValues = results.stream().map(VERecipeCodecs.VEChancedItemWithCount::chance).toList();

    }

    public List<Float> getRNGOutputs() {
        return rngValues;
    }

    public float getOutputChance(int i) {
        return rngValues.get(i);
    }

    public void setRNGOutputs(List<Float> rngOutputs) {
        this.rngValues = rngOutputs;
    }

    @Override
    public ItemStack getResult(int id) {
        // Sometimes recipes define less that what a machine can put out (not utilizing all output slots). Therefore, return ItemStack when querying beyond result length
        if (id>=this.resultsWithChance.size()) {
            return ItemStack.EMPTY;
        }
        return this.resultsWithChance.get(id).getAsItemStack();
    }

}
