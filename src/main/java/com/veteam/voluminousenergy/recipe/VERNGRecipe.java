package com.veteam.voluminousenergy.recipe;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.List;

public abstract class VERNGRecipe extends VERecipe {

    public List<Float> rngValues;

    public VERNGRecipe() {

    }

    public VERNGRecipe(List<Ingredient> ingredients, List<ItemStack> results, int processTime, List<Float> rngOutputs) {
        super(ingredients, results, processTime);
        this.rngValues = rngOutputs;

    }

    public List<Float> getRNGOutputs() {
        return rngValues;
    }

    public void setRNGOutputs(List<Float> rngOutputs) {
        this.rngValues = rngOutputs;
    }

}
