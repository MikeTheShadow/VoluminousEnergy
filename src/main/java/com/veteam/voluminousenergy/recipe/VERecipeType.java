package com.veteam.voluminousenergy.recipe;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeType;

public class VERecipeType<T extends Recipe<?>> implements RecipeType<T> {
    private ResourceLocation name;

    public VERecipeType(ResourceLocation name){
        this.name = name;
    }

    @Override
    public String toString(){
        return name.toString();
    }

}
