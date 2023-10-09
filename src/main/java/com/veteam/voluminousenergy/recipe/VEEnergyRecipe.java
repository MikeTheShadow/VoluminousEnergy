package com.veteam.voluminousenergy.recipe;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.veteam.voluminousenergy.util.recipe.FluidSerializerHelper;
import com.veteam.voluminousenergy.util.recipe.VERecipeCodecs;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.crafting.CraftingRecipeCodecs;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public abstract class VEEnergyRecipe extends VERecipe {

    private int energyPerTick;

    public VEEnergyRecipe() {

    }

    public VEEnergyRecipe(List<VERecipeCodecs.RegistryIngredient> ingredients, int processTime, int energy_per_tick) {
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
