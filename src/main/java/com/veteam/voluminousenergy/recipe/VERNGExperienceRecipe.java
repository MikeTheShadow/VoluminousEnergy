package com.veteam.voluminousenergy.recipe;

import com.veteam.voluminousenergy.util.recipe.VERecipeCodecs;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.List;

public abstract class VERNGExperienceRecipe extends VERNGRecipe {

    public int minExp;
    public int maxExp;

    public VERNGExperienceRecipe() {

    }

    public VERNGExperienceRecipe(List<VERecipeCodecs.RegistryIngredient> ingredients, List<ItemStack> results, int processTime, List<Float> rngOutputs, int minExp, int maxExp) {
        super(ingredients, results, processTime,rngOutputs);
        this.minExp = minExp;
        this.maxExp = maxExp;
    }

    public void setBoth(int min, int max) {
        this.minExp = min;
        this.maxExp = max;
    }

}
