package com.veteam.voluminousenergy.recipe;

import com.veteam.voluminousenergy.VoluminousEnergy;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class VERecipes {
    public static DeferredRegister<IRecipeSerializer<?>> VE_RECIPES = new DeferredRegister<>(ForgeRegistries.RECIPE_SERIALIZERS,VoluminousEnergy.MODID);

    public static final RegistryObject<IRecipeSerializer<?>> PRIMITIVE_BLAST_FURNACE_RECIPE = VE_RECIPES.register("primitive_blast_furnacing", () -> PrimitiveBlastFurnaceRecipe.serializer);
    public static final RegistryObject<IRecipeSerializer<?>> CRUSHER_RECIPE = VE_RECIPES.register("crushing", () -> CrusherRecipe.serializer);
    public static final RegistryObject<IRecipeSerializer<?>> ELECTROLYZER_RECIPE = VE_RECIPES.register("electrolyzing", () -> ElectrolyzerRecipe.serializer);
    public static final RegistryObject<IRecipeSerializer<?>> CENTRIFUGAL_AGITATOR_RECIPE = VE_RECIPES.register("centrifugal_agitating", () -> CentrifugalAgitatorRecipe.serializer);
}