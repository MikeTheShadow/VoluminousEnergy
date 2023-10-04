package com.veteam.voluminousenergy.recipe;

import com.veteam.voluminousenergy.util.recipe.serializers.VEEnergyRecipeSerializer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class VEEnergyRecipe extends VERecipe {

    private int energyPerTick;

    public VEEnergyRecipe() {

    }

    private static final VEEnergyRecipeSerializer SERIALIZER = new VEEnergyRecipeSerializer();

    @Override
    public @NotNull RecipeSerializer<? extends VERecipe> getSerializer() {
        return SERIALIZER;
    }

    public VEEnergyRecipe(List<Ingredient> ingredients, int processTime, int energy_per_tick) {
        super(ingredients,new ArrayList<>(),processTime);
        this.energyPerTick = energy_per_tick;
    }

    public int getEnergyPerTick() {
        return energyPerTick;
    }

    public void setEnergyPerTick(int energyPerTick) {
        this.energyPerTick = energyPerTick;
    }
}
