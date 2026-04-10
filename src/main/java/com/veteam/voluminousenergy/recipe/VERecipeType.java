package com.veteam.voluminousenergy.recipe;

import net.minecraft.resources.Identifier;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeType;

public class VERecipeType<T extends Recipe<?>> implements RecipeType<T> {
    private Identifier name;

    public VERecipeType(Identifier name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name.toString();
    }

}
