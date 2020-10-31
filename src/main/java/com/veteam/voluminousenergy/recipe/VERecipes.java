package com.veteam.voluminousenergy.recipe;

import com.veteam.voluminousenergy.VoluminousEnergy;
import com.veteam.voluminousenergy.recipe.CombustionGenerator.CombustionGeneratorFuelRecipe;
import com.veteam.voluminousenergy.recipe.CombustionGenerator.CombustionGeneratorOxidizerRecipe;
import com.veteam.voluminousenergy.util.RecipeConstants;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;


public class VERecipes {

    public static void init() {
        //register types
        registerType(RecipeConstants.PRIMITIVE_BLAST_FURNACING,PrimitiveBlastFurnaceRecipe.RECIPE_TYPE);
        registerType(RecipeConstants.CRUSHING,CrusherRecipe.RECIPE_TYPE);
        registerType(RecipeConstants.ELECTROLYZING,ElectrolyzerRecipe.RECIPE_TYPE);
        registerType(RecipeConstants.CENTRIFUGAL_AGITATING, CentrifugalAgitatorRecipe.RECIPE_TYPE);
        registerType(RecipeConstants.COMPRESSING,CompressorRecipe.RECIPE_TYPE);
        registerType(RecipeConstants.STIRLING,StirlingGeneratorRecipe.RECIPE_TYPE);
        registerType(RecipeConstants.OXIDIZING,CombustionGeneratorOxidizerRecipe.RECIPE_TYPE);
        registerType(RecipeConstants.FUEL_COMBUSTION,CombustionGeneratorFuelRecipe.RECIPE_TYPE);
        registerType(RecipeConstants.AQUEOULIZING,AqueoulizerRecipe.RECIPE_TYPE);
        registerType(RecipeConstants.DISTILLING,DistillationRecipe.RECIPE_TYPE);

        //register serializers
        registerSerializer(RecipeConstants.PRIMITIVE_BLAST_FURNACING,PrimitiveBlastFurnaceRecipe.SERIALIZER);
        registerSerializer(RecipeConstants.CRUSHING,CrusherRecipe.SERIALIZER);
        registerSerializer(RecipeConstants.ELECTROLYZING,ElectrolyzerRecipe.SERIALIZER);
        registerSerializer(RecipeConstants.CENTRIFUGAL_AGITATING, CentrifugalAgitatorRecipe.SERIALIZER);
        registerSerializer(RecipeConstants.COMPRESSING,CompressorRecipe.SERIALIZER);
        registerSerializer(RecipeConstants.STIRLING,StirlingGeneratorRecipe.SERIALIZER);
        registerSerializer(RecipeConstants.OXIDIZING,CombustionGeneratorOxidizerRecipe.SERIALIZER);
        registerSerializer(RecipeConstants.FUEL_COMBUSTION,CombustionGeneratorFuelRecipe.SERIALIZER);
        registerSerializer(RecipeConstants.AQUEOULIZING,AqueoulizerRecipe.SERIALIZER);
        registerSerializer(RecipeConstants.DISTILLING,DistillationRecipe.SERIALIZER);
    }



    private static void registerSerializer(ResourceLocation name, IRecipeSerializer<?> serializer) {
        VoluminousEnergy.LOGGER.info("RSerializing: " + name.toString());
        IRecipeSerializer.register(name.toString(),serializer);
    }

    private static void registerType(ResourceLocation name, IRecipeType<?> recipeType) {
        VoluminousEnergy.LOGGER.info("RType: " + name.toString());
        Registry.register(Registry.RECIPE_TYPE, name, recipeType);
    }
}