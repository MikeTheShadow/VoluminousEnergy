package com.veteam.voluminousenergy.recipe;

import com.veteam.voluminousenergy.util.recipe.VERecipeCodecs;

import java.util.ArrayList;
import java.util.List;

public abstract class VEEnergyRecipe extends VEFluidRecipe {

    private int energyPerTick;

    public VEEnergyRecipe() {

    }

    public VEEnergyRecipe(List<VERecipeCodecs.RegistryIngredient> ingredients, int processTime, int energy_per_tick) {
        super(ingredients,new ArrayList<>(),new ArrayList<>(),new ArrayList<>(),processTime);
        this.energyPerTick = energy_per_tick;
    }

    public int getEnergyPerTick() {
        return energyPerTick;
    }

    public void setEnergyPerTick(int energyPerTick) {
        this.energyPerTick = energyPerTick;
    }
}
