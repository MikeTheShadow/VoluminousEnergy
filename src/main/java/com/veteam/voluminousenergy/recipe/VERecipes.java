package com.veteam.voluminousenergy.recipe;

import com.veteam.voluminousenergy.VoluminousEnergy;
import com.veteam.voluminousenergy.recipe.CombustionGenerator.CombustionGeneratorFuelRecipe;
import com.veteam.voluminousenergy.recipe.CombustionGenerator.CombustionGeneratorOxidizerRecipe;
import com.veteam.voluminousenergy.util.RecipeConstants;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Supplier;

public class VERecipes {

    /* TODO: Verify recipe registration
    public static final DeferredRegister<RecipeSerializer<?>> RECIPE_SERIALIZERS = create(ForgeRegistries.RECIPE_SERIALIZERS);

    private static <T extends IForgeRegistryEntry<T>> DeferredRegister<T> create(IForgeRegistry<T> registry) {
        return DeferredRegister.create(registry, VoluminousEnergy.MODID);
    }*/
    public static final DeferredRegister<RecipeSerializer<?>> VE_RECIPE_SERIALIZERS_REGISTRY = DeferredRegister.create(ForgeRegistries.RECIPE_SERIALIZERS, VoluminousEnergy.MODID);
    public static final DeferredRegister<RecipeType<?>> VE_RECIPE_TYPES_REGISTRY = DeferredRegister.create(ForgeRegistries.RECIPE_TYPES, VoluminousEnergy.MODID);

    public static final class VERecipeTypes{
        public static final RegistryObject<RecipeType<PrimitiveBlastFurnaceRecipe>> PRIMITIVE_BLAST_FURNACING = registerType(RecipeConstants.PRIMITIVE_BLAST_FURNACING);
        public static final RegistryObject<RecipeType<CrusherRecipe>> CRUSHING = registerType(RecipeConstants.CRUSHING);
        public static final RegistryObject<RecipeType<ElectrolyzerRecipe>> ELECTROLYZING = registerType(RecipeConstants.ELECTROLYZING);
        public static final RegistryObject<RecipeType<VEFluidRecipe>> CENTRIFUGAL_AGITATING = registerType(RecipeConstants.CENTRIFUGAL_AGITATING);
        public static final RegistryObject<RecipeType<CompressorRecipe>> COMPRESSING = registerType(RecipeConstants.COMPRESSING);
        public static final RegistryObject<RecipeType<StirlingGeneratorRecipe>> STIRLING = registerType(RecipeConstants.STIRLING);
        public static final RegistryObject<RecipeType<CombustionGeneratorOxidizerRecipe>> OXIDIZING = registerType(RecipeConstants.OXIDIZING);
        public static final RegistryObject<RecipeType<VEFluidRecipe>> FUEL_COMBUSTION = registerType(RecipeConstants.FUEL_COMBUSTION);
        public static final RegistryObject<RecipeType<VEFluidRecipe>> AQUEOULIZING = registerType(RecipeConstants.AQUEOULIZING);
        public static final RegistryObject<RecipeType<VEFluidRecipe>> DISTILLING = registerType(RecipeConstants.DISTILLING);
        public static final RegistryObject<RecipeType<CentrifugalSeparatorRecipe>> CENTRIFUGAL_SEPARATION = registerType(RecipeConstants.CENTRIFUGAL_SEPARATION);
        public static final RegistryObject<RecipeType<ImplosionCompressorRecipe>> IMPLOSION_COMPRESSING = registerType(RecipeConstants.IMPLOSION_COMPRESSING);
        public static final RegistryObject<RecipeType<IndustrialBlastingRecipe>> INDUSTRIAL_BLASTING = registerType(RecipeConstants.INDUSTRIAL_BLASTING);
        public static final RegistryObject<RecipeType<ToolingRecipe>> TOOLING = registerType(RecipeConstants.TOOLING);
        public static final RegistryObject<RecipeType<SawmillingRecipe>> SAWMILLING = registerType(RecipeConstants.SAWMILLING);
    }

    public static final RegistryObject<RecipeSerializer<?>> PRIMITIVE_BLAST_FURNACING = registerSerializer(RecipeConstants.PRIMITIVE_BLAST_FURNACING, () -> PrimitiveBlastFurnaceRecipe.SERIALIZER);
    public static final RegistryObject<RecipeSerializer<?>> CRUSHING = registerSerializer(RecipeConstants.CRUSHING, () -> CrusherRecipe.SERIALIZER);
    public static final RegistryObject<RecipeSerializer<?>> ELECTROLYZING = registerSerializer(RecipeConstants.ELECTROLYZING, () -> ElectrolyzerRecipe.SERIALIZER);
    public static final RegistryObject<RecipeSerializer<?>> CENTRIFUGAL_AGITATING = registerSerializer(RecipeConstants.CENTRIFUGAL_AGITATING, () -> CentrifugalAgitatorRecipe.SERIALIZER);
    public static final RegistryObject<RecipeSerializer<?>> COMPRESSING = registerSerializer(RecipeConstants.COMPRESSING, () -> CompressorRecipe.SERIALIZER);
    public static final RegistryObject<RecipeSerializer<?>> STIRLING = registerSerializer(RecipeConstants.STIRLING, () -> StirlingGeneratorRecipe.SERIALIZER);
    public static final RegistryObject<RecipeSerializer<?>> OXIDIZING = registerSerializer(RecipeConstants.OXIDIZING, () -> CombustionGeneratorOxidizerRecipe.SERIALIZER);
    public static final RegistryObject<RecipeSerializer<?>> FUEL_COMBUSTION = registerSerializer(RecipeConstants.FUEL_COMBUSTION, () -> CombustionGeneratorFuelRecipe.SERIALIZER);
    public static final RegistryObject<RecipeSerializer<?>> AQUEOULIZING = registerSerializer(RecipeConstants.AQUEOULIZING, () -> AqueoulizerRecipe.SERIALIZER);
    public static final RegistryObject<RecipeSerializer<?>> DISTILLING = registerSerializer(RecipeConstants.DISTILLING, () -> DistillationRecipe.SERIALIZER);
    public static final RegistryObject<RecipeSerializer<?>> CENTRIFUGAL_SEPARATION = registerSerializer(RecipeConstants.CENTRIFUGAL_SEPARATION, () -> CentrifugalSeparatorRecipe.SERIALIZER);
    public static final RegistryObject<RecipeSerializer<?>> IMPLOSION_COMPRESSING = registerSerializer(RecipeConstants.IMPLOSION_COMPRESSING, () -> ImplosionCompressorRecipe.SERIALIZER);
    public static final RegistryObject<RecipeSerializer<?>> INDUSTRIAL_BLASTING = registerSerializer(RecipeConstants.INDUSTRIAL_BLASTING, () -> IndustrialBlastingRecipe.SERIALIZER);
    public static final RegistryObject<RecipeSerializer<?>> TOOLING = registerSerializer(RecipeConstants.TOOLING, () -> ToolingRecipe.SERIALIZER);
    public static final RegistryObject<RecipeSerializer<?>> SAWMILLING = registerSerializer(RecipeConstants.SAWMILLING, () -> SawmillingRecipe.SERIALIZER);

    private static RegistryObject<RecipeSerializer<?>> registerSerializer(ResourceLocation name, Supplier<RecipeSerializer<?>> serializer) {
        VoluminousEnergy.LOGGER.info("Registering Serializer for Recipe: " + name.toString());
        return VE_RECIPE_SERIALIZERS_REGISTRY.register(name.getPath(), serializer);

        //IRecipeSerializer.register(name.toString(),serializer);
    }

    private static <T extends Recipe<?>> RegistryObject<RecipeType<T>> /*<T extends Recipe<?>> RecipeType<T>*/ registerType(ResourceLocation name){
        VoluminousEnergy.LOGGER.info("Registering Recipe Type: " + name.toString());
        /*return Registry.register(Registry.RECIPE_TYPE, name, new RecipeType<T>() {
            @Override
            public String toString() {
                return name.toString();
            }
        });*/
        return VE_RECIPE_TYPES_REGISTRY.register(name.toString(), () -> new RecipeType<T>(){
            @Override
            public String toString(){
                return name.toString();
            }
        });
        //Registry.register(Registry.RECIPE_TYPE, name, recipeType);
    }
}