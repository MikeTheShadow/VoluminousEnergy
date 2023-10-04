package com.veteam.voluminousenergy.recipe;

import com.veteam.voluminousenergy.util.recipe.serializers.VERNGExperienceRecipeSerializer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class VERNGExperienceRecipe extends VERNGRecipe {

    public int minExp;
    public int maxExp;

    private static final VERNGExperienceRecipeSerializer SERIALIZER = new VERNGExperienceRecipeSerializer();


    @Override
    public @NotNull RecipeSerializer<? extends VERecipe> getSerializer() {
        return SERIALIZER;
    }

    public VERNGExperienceRecipe() {

    }

    public VERNGExperienceRecipe(List<Ingredient> ingredients, List<ItemStack> results, int processTime, List<Float> rngOutputs,int minExp,int maxExp) {
        super(ingredients, results, processTime,rngOutputs);
        this.minExp = minExp;
        this.maxExp = maxExp;
    }

    public void setBoth(int min, int max) {
        this.minExp = min;
        this.maxExp = max;
    }

}
