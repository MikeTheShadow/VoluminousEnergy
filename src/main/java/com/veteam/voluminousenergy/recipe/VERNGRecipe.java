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
        this.rngValues = results.stream().map(VERecipeCodecs.VEChancedItemWithCount::chance).toList();

    }

    /**
     * A variable list of rng floats of variable length that can change depending on the recipe requirements
     * Should only be used in serialization
     * @return the raw rng values
     */
    public List<Float> getRNGOutputs() {
        return rngValues;
    }

    public float getOutputChance(int i) {
        if (i >= rngValues.size()) {
            return 1;
        }
        return rngValues.get(i);
    }

    public void setRNGOutputs(List<Float> rngOutputs) {
        this.rngValues = rngOutputs;
    }

}
