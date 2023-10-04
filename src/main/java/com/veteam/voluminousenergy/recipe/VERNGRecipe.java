package com.veteam.voluminousenergy.recipe;

import com.veteam.voluminousenergy.util.recipe.serializers.VERNGRecipeSerializer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class VERNGRecipe extends VERecipe {

    public List<Float> rngValues;

    private static final VERNGRecipeSerializer SERIALIZER = new VERNGRecipeSerializer();

    public VERNGRecipe() {

    }

    @Override
    public @NotNull RecipeSerializer<? extends VERecipe> getSerializer() {
        return SERIALIZER;
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
