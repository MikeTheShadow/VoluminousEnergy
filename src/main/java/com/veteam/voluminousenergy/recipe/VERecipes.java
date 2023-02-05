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
import net.minecraftforge.registries.*;

import java.util.function.Supplier;

public class VERecipes {

    public static final DeferredRegister<RecipeSerializer<?>> RECIPE_SERIALIZERS = create(ForgeRegistries.RECIPE_SERIALIZERS);

    private static <T extends IForgeRegistryEntry<T>> DeferredRegister<T> create(IForgeRegistry<T> registry) {
        return DeferredRegister.create(registry, VoluminousEnergy.MODID);
    }

    public static final class VERecipeTypes{
        public static final RecipeType<PrimitiveBlastFurnaceRecipe> PRIMITIVE_BLAST_FURNACING = registerType(RecipeConstants.PRIMITIVE_BLAST_FURNACING);
        public static final RecipeType<CrusherRecipe> CRUSHING = registerType(RecipeConstants.CRUSHING);
        public static final RecipeType<ElectrolyzerRecipe> ELECTROLYZING = registerType(RecipeConstants.ELECTROLYZING);
        public static final RecipeType<VEFluidRecipe> CENTRIFUGAL_AGITATING = registerType(RecipeConstants.CENTRIFUGAL_AGITATING);
        public static final RecipeType<CompressorRecipe> COMPRESSING = registerType(RecipeConstants.COMPRESSING);
        public static final RecipeType<StirlingGeneratorRecipe> STIRLING = registerType(RecipeConstants.STIRLING);
        public static final RecipeType<CombustionGeneratorOxidizerRecipe> OXIDIZING = registerType(RecipeConstants.OXIDIZING);
        public static final RecipeType<VEFluidRecipe> FUEL_COMBUSTION = registerType(RecipeConstants.FUEL_COMBUSTION);
        public static final RecipeType<VEFluidRecipe> AQUEOULIZING = registerType(RecipeConstants.AQUEOULIZING);
        public static final RecipeType<VEFluidRecipe> DISTILLING = registerType(RecipeConstants.DISTILLING);
        public static final RecipeType<CentrifugalSeparatorRecipe> CENTRIFUGAL_SEPARATION = registerType(RecipeConstants.CENTRIFUGAL_SEPARATION);
        public static final RecipeType<ImplosionCompressorRecipe> IMPLOSION_COMPRESSING = registerType(RecipeConstants.IMPLOSION_COMPRESSING);
        public static final RecipeType<IndustrialBlastingRecipe> INDUSTRIAL_BLASTING = registerType(RecipeConstants.INDUSTRIAL_BLASTING);
        public static final RecipeType<ToolingRecipe> TOOLING = registerType(RecipeConstants.TOOLING);
        public static final RecipeType<SawmillingRecipe> SAWMILLING = registerType(RecipeConstants.SAWMILLING);
        public static final RecipeType<VEFluidRecipe> DIMENSIONAL_LASING = registerType(RecipeConstants.DIMENSIONAL_LASING);
        public static final RecipeType<VEFluidRecipe> FLUID_ELECTROLYZING = registerType(RecipeConstants.FLUID_ELECTROLYZING);
        public static final RecipeType<VEFluidRecipe> FLUID_MIXING = registerType(RecipeConstants.FLUID_MIXING);
        public static final RecipeType<VEFluidRecipe> HYDROPONIC_INCUBATING = registerType(RecipeConstants.HYDROPONIC_INCUBATING);
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
    public static final RegistryObject<RecipeSerializer<?>> DIMENSIONAL_LASING = registerSerializer(RecipeConstants.DIMENSIONAL_LASING, () -> DimensionalLaserRecipe.SERIALIZER);
    public static final RegistryObject<RecipeSerializer<?>> FLUID_ELECTROLYZER = registerSerializer(RecipeConstants.FLUID_ELECTROLYZING, () -> FluidElectrolyzerRecipe.SERIALIZER);
    public static final RegistryObject<RecipeSerializer<?>> FLUID_MIXING = registerSerializer(RecipeConstants.FLUID_MIXING, () -> FluidMixerRecipe.SERIALIZER);
    public static final RegistryObject<RecipeSerializer<?>> HYDROPONIC_INCUBATING = registerSerializer(RecipeConstants.HYDROPONIC_INCUBATING, () -> HydroponicIncubatorRecipe.SERIALIZER);

    private static RegistryObject<RecipeSerializer<?>> registerSerializer(ResourceLocation name, Supplier<RecipeSerializer<?>> serializer) {
        VoluminousEnergy.LOGGER.info("Registering Serializer for Recipe: " + name.toString());
        return RECIPE_SERIALIZERS.register(name.getPath(), serializer);

        //IRecipeSerializer.register(name.toString(),serializer);
    }

    private static <T extends Recipe<?>> RecipeType<T> registerType(ResourceLocation name){
        VoluminousEnergy.LOGGER.info("Registering Recipe Type: " + name.toString());
        return Registry.register(Registry.RECIPE_TYPE, name, new RecipeType<T>() {
            @Override
            public String toString() {
                return name.toString();
            }
        });
        //Registry.register(Registry.RECIPE_TYPE, name, recipeType);
    }
}