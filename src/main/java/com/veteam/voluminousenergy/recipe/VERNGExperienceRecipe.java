package com.veteam.voluminousenergy.recipe;

import com.veteam.voluminousenergy.util.recipe.VERecipeCodecs;

import java.util.List;

public abstract class VERNGExperienceRecipe extends VERNGRecipe {

    @Deprecated
    public int minExp;
    @Deprecated
    public int maxExp;

    public VERecipeCodecs.VERecipeExperience experience;

    public VERNGExperienceRecipe() {

    }

    public VERNGExperienceRecipe(List<VERecipeCodecs.RegistryIngredient> ingredients, List<VERecipeCodecs.VEChancedItemWithCount> results, int processTime, VERecipeCodecs.VERecipeExperience experience) {
        super(ingredients, results, processTime);
        this.experience = experience;
        this.minExp = experience.minimum();
        this.maxExp = experience.maximum();
    }

    public int getMinExp() {
        return minExp;
    }

    public void setMinExp(int minExp) {
        this.minExp = minExp;
    }

    public int getMaxExp() {
        return maxExp;
    }

    public void setMaxExp(int maxExp) {
        this.maxExp = maxExp;
    }

    public void setExperience(int min, int max) {
        this.experience = new VERecipeCodecs.VERecipeExperience(min, max);
    }

}
