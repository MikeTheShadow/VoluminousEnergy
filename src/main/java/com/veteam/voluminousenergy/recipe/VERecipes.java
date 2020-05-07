package com.veteam.voluminousenergy.recipe;

import com.veteam.voluminousenergy.VoluminousEnergy;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.ObjectHolder;

public class VERecipes {
    public static DeferredRegister<IRecipeSerializer<?>> VE_RECIPES = new DeferredRegister<>(ForgeRegistries.RECIPE_SERIALIZERS,VoluminousEnergy.MODID);

    public static final RegistryObject<IRecipeSerializer<?>> PRIMITIVE_BLAST_FURNACE_RECIPE = VE_RECIPES.register("primitive_blast_furnacing", () -> PrimitiveBlastFurnaceRecipe.serializer);

}