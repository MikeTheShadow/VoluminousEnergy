package com.veteam.voluminousenergy.recipe;

import com.veteam.voluminousenergy.VoluminousEnergy;
import com.veteam.voluminousenergy.recipe.CombustionGenerator.CombustionGeneratorFuelRecipe;
import com.veteam.voluminousenergy.recipe.CombustionGenerator.CombustionGeneratorOxidizerRecipe;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;


public class VERecipes {
    public static DeferredRegister<IRecipeSerializer<?>> VE_RECIPES = new DeferredRegister<>(ForgeRegistries.RECIPE_SERIALIZERS,VoluminousEnergy.MODID);

    /*
    public static final RegistryObject<IRecipeSerializer<?>> PRIMITIVE_BLAST_FURNACE_RECIPE = VE_RECIPES.register("primitive_blast_furnacing", () -> PrimitiveBlastFurnaceRecipe.serializer);
    public static final RegistryObject<IRecipeSerializer<?>> CRUSHER_RECIPE = VE_RECIPES.register("crushing", () -> CrusherRecipe.serializer);
    public static final RegistryObject<IRecipeSerializer<?>> ELECTROLYZER_RECIPE = VE_RECIPES.register("electrolyzing", () -> ElectrolyzerRecipe.serializer);
    public static final RegistryObject<IRecipeSerializer<?>> CENTRIFUGAL_AGITATOR_RECIPE = VE_RECIPES.register("centrifugal_agitating", () -> CentrifugalAgitatorRecipe.SERIALIZER);
    public static final RegistryObject<IRecipeSerializer<?>> COMPRESSOR_RECIPE = VE_RECIPES.register("compressing", () -> CompressorRecipe.SERIALIZER);
    public static final RegistryObject<IRecipeSerializer<?>> STIRLING_GENERATOR_RECIPE = VE_RECIPES.register("stirling", () -> StirlingGeneratorRecipe.SERIALIZER);
    public static final RegistryObject<IRecipeSerializer<?>> COMBUSTION_GENERATOR_OXIDIZER_RECIPE = VE_RECIPES.register("oxidizer_combustion", () -> CombustionGeneratorOxidizerRecipe.SERIALIZER);
    public static final RegistryObject<IRecipeSerializer<?>> COMBUSTION_GENERATOR_FUEL_RECIPE = VE_RECIPES.register("fuel_combustion", () -> CombustionGeneratorFuelRecipe.SERIALIZER);
    public static final RegistryObject<IRecipeSerializer<?>> AQUEOULIZER_RECIPE = VE_RECIPES.register("aqueoulizing", () -> AqueoulizerRecipe.SERIALIZER);
     */

    public static void init() {
        //register types
        registerType(RecipeConstants.PRIMITIVE_BLAST_FURNACING,PrimitiveBlastFurnaceRecipe.RECIPE_TYPE);
        registerType(RecipeConstants.CRUSHING,CrusherRecipe.RECIPE_TYPE);
        registerType(RecipeConstants.ELECTROLYZING,ElectrolyzerRecipe.RECIPE_TYPE);
        registerType(RecipeConstants.CENTRIFUGAL_AGITATING,CentrifugalAgitatorRecipe.RECIPE_TYPE);
        registerType(RecipeConstants.COMPRESSING,CompressorRecipe.RECIPE_TYPE);
        registerType(RecipeConstants.STIRLING,StirlingGeneratorRecipe.RECIPE_TYPE);
        registerType(RecipeConstants.OXIDIZING, CombustionGeneratorOxidizerRecipe.RECIPE_TYPE);;
        registerType(RecipeConstants.FUEL_COMBUSTION, CombustionGeneratorFuelRecipe.RECIPE_TYPE);
        registerType(RecipeConstants.AQUEOULIZING,CombustionGeneratorFuelRecipe.RECIPE_TYPE);

        //register serializers
        registerSerializer(RecipeConstants.PRIMITIVE_BLAST_FURNACING,PrimitiveBlastFurnaceRecipe.SERIALIZER);
        registerSerializer(RecipeConstants.CRUSHING,CrusherRecipe.SERIALIZER);
        registerSerializer(RecipeConstants.ELECTROLYZING,ElectrolyzerRecipe.SERIALIZER);
        registerSerializer(RecipeConstants.CENTRIFUGAL_AGITATING,CentrifugalAgitatorRecipe.SERIALIZER);
        registerSerializer(RecipeConstants.COMPRESSING,CompressorRecipe.SERIALIZER);
        registerSerializer(RecipeConstants.STIRLING,StirlingGeneratorRecipe.SERIALIZER);
        registerSerializer(RecipeConstants.OXIDIZING,CombustionGeneratorOxidizerRecipe.SERIALIZER);
        registerSerializer(RecipeConstants.FUEL_COMBUSTION,CombustionGeneratorFuelRecipe.SERIALIZER);
        registerSerializer(RecipeConstants.AQUEOULIZING,AqueoulizerRecipe.SERIALIZER);

        registerSerializer(RecipeConstants.STIRLING,StirlingGeneratorRecipe.SERIALIZER);

    }



    private static void registerSerializer(ResourceLocation name, IRecipeSerializer<?> serializer) {
        IRecipeSerializer.register(name.toString(),serializer);
    }

    private static void registerType(ResourceLocation name, IRecipeType<?> recipeType) {
        Registry.register(Registry.RECIPE_TYPE, name, recipeType);
    }
}