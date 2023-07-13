package com.veteam.voluminousenergy.recipe;

import com.veteam.voluminousenergy.VoluminousEnergy;
import com.veteam.voluminousenergy.recipe.CombustionGenerator.CombustionGeneratorFuelRecipe;
import com.veteam.voluminousenergy.recipe.CombustionGenerator.CombustionGeneratorOxidizerRecipe;
import com.veteam.voluminousenergy.util.RecipeConstants;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class VERecipes {

    public static final DeferredRegister<RecipeSerializer<?>> VE_RECIPE_SERIALIZERS_REGISTRY = DeferredRegister.create(ForgeRegistries.RECIPE_SERIALIZERS, VoluminousEnergy.MODID);

    public static final class VERecipeTypes{
        public static final DeferredRegister<RecipeType<?>> VE_RECIPE_TYPES_REGISTRY = DeferredRegister.create(ForgeRegistries.RECIPE_TYPES, VoluminousEnergy.MODID);

        public static final RegistryObject<RecipeType<PrimitiveBlastFurnaceRecipe>> PRIMITIVE_BLAST_FURNACING =
                VE_RECIPE_TYPES_REGISTRY.register("primitive_blast_furnacing", () -> new VERecipeType<>(RecipeConstants.PRIMITIVE_BLAST_FURNACING));

        public static final RegistryObject<RecipeType<CrusherRecipe>> CRUSHING =
                VE_RECIPE_TYPES_REGISTRY.register("crushing", () -> new VERecipeType<>(RecipeConstants.CRUSHING));

        public static final RegistryObject<RecipeType<ElectrolyzerRecipe>> ELECTROLYZING =
                VE_RECIPE_TYPES_REGISTRY.register("electrolyzing", () -> new VERecipeType<>(RecipeConstants.ELECTROLYZING));

        public static final RegistryObject<RecipeType<VEFluidRecipe>> CENTRIFUGAL_AGITATING =
                VE_RECIPE_TYPES_REGISTRY.register("centrifugal_agitating", () -> new VERecipeType<>(RecipeConstants.CENTRIFUGAL_AGITATING));

        public static final RegistryObject<RecipeType<CompressorRecipe>> COMPRESSING =
                VE_RECIPE_TYPES_REGISTRY.register("compressing", () -> new VERecipeType<>(RecipeConstants.COMPRESSING));

        public static final RegistryObject<RecipeType<StirlingGeneratorRecipe>> STIRLING =
                VE_RECIPE_TYPES_REGISTRY.register("stirling", () -> new VERecipeType<>(RecipeConstants.STIRLING));

        public static final RegistryObject<RecipeType<VEFluidRecipe>> OXIDIZING =
                VE_RECIPE_TYPES_REGISTRY.register("oxidizer_combustion", () -> new VERecipeType<>(RecipeConstants.OXIDIZING));

        public static final RegistryObject<RecipeType<VEFluidRecipe>> FUEL_COMBUSTION =
                VE_RECIPE_TYPES_REGISTRY.register("fuel_combustion", () -> new VERecipeType<>(RecipeConstants.FUEL_COMBUSTION));

        public static final RegistryObject<RecipeType<VEFluidRecipe>> AQUEOULIZING =
                VE_RECIPE_TYPES_REGISTRY.register("aqueoulizing", () -> new VERecipeType<>(RecipeConstants.AQUEOULIZING));

        public static final RegistryObject<RecipeType<VEFluidRecipe>> DISTILLING =
                VE_RECIPE_TYPES_REGISTRY.register("distilling", () -> new VERecipeType<>(RecipeConstants.DISTILLING));

        public static final RegistryObject<RecipeType<CentrifugalSeparatorRecipe>> CENTRIFUGAL_SEPARATION =
                VE_RECIPE_TYPES_REGISTRY.register("centrifugal_separation", () -> new VERecipeType<>(RecipeConstants.CENTRIFUGAL_SEPARATION));

        public static final RegistryObject<RecipeType<ImplosionCompressorRecipe>> IMPLOSION_COMPRESSING =
                VE_RECIPE_TYPES_REGISTRY.register("implosion_compressing", () -> new VERecipeType<>(RecipeConstants.IMPLOSION_COMPRESSING));

        public static final RegistryObject<RecipeType<IndustrialBlastingRecipe>> INDUSTRIAL_BLASTING =
                VE_RECIPE_TYPES_REGISTRY.register("industrial_blasting", () -> new VERecipeType<>(RecipeConstants.INDUSTRIAL_BLASTING));

        public static final RegistryObject<RecipeType<ToolingRecipe>> TOOLING =
                VE_RECIPE_TYPES_REGISTRY.register("tooling", () -> new VERecipeType<>(RecipeConstants.TOOLING));

        public static final RegistryObject<RecipeType<SawmillingRecipe>> SAWMILLING =
                VE_RECIPE_TYPES_REGISTRY.register("sawmilling", () -> new VERecipeType<>(RecipeConstants.SAWMILLING));

        public static final RegistryObject<RecipeType<VEFluidRecipe>> DIMENSIONAL_LASING =
                VE_RECIPE_TYPES_REGISTRY.register("dimensional_lasing", () -> new VERecipeType<>(RecipeConstants.DIMENSIONAL_LASING));

        public static final RegistryObject<RecipeType<VEFluidRecipe>> FLUID_ELECTROLYZING =
                VE_RECIPE_TYPES_REGISTRY.register("fluid_electrolyzing", () -> new VERecipeType<>(RecipeConstants.FLUID_ELECTROLYZING));

        public static final RegistryObject<RecipeType<VEFluidRecipe>> FLUID_MIXING =
                VE_RECIPE_TYPES_REGISTRY.register("fluid_mixing", () -> new VERecipeType<>(RecipeConstants.FLUID_MIXING));

        public static final RegistryObject<RecipeType<VEFluidRecipe>> HYDROPONIC_INCUBATING =
                VE_RECIPE_TYPES_REGISTRY.register("hydroponic_incubating", () -> new VERecipeType<>(RecipeConstants.HYDROPONIC_INCUBATING));
    }

    public static final RegistryObject<RecipeSerializer<?>> PRIMITIVE_BLAST_FURNACING =
            VE_RECIPE_SERIALIZERS_REGISTRY.register("primitive_blast_furnacing", () -> PrimitiveBlastFurnaceRecipe.SERIALIZER);
    public static final RegistryObject<RecipeSerializer<?>> CRUSHING =
            VE_RECIPE_SERIALIZERS_REGISTRY.register("crushing", () -> CrusherRecipe.SERIALIZER);
    public static final RegistryObject<RecipeSerializer<?>> ELECTROLYZING =
            VE_RECIPE_SERIALIZERS_REGISTRY.register("electrolyzing", () -> ElectrolyzerRecipe.SERIALIZER);
    public static final RegistryObject<RecipeSerializer<?>> CENTRIFUGAL_AGITATING =
            VE_RECIPE_SERIALIZERS_REGISTRY.register("centrifugal_agitating", () -> CentrifugalAgitatorRecipe.SERIALIZER);
    public static final RegistryObject<RecipeSerializer<?>> COMPRESSING =
            VE_RECIPE_SERIALIZERS_REGISTRY.register("compressing", () -> CompressorRecipe.SERIALIZER);
    public static final RegistryObject<RecipeSerializer<?>> STIRLING =
            VE_RECIPE_SERIALIZERS_REGISTRY.register("stirling", () -> StirlingGeneratorRecipe.SERIALIZER);
    public static final RegistryObject<RecipeSerializer<?>> OXIDIZING =
            VE_RECIPE_SERIALIZERS_REGISTRY.register("oxidizer_combustion", () -> CombustionGeneratorOxidizerRecipe.SERIALIZER);
    public static final RegistryObject<RecipeSerializer<?>> FUEL_COMBUSTION =
            VE_RECIPE_SERIALIZERS_REGISTRY.register("fuel_combustion", () -> CombustionGeneratorFuelRecipe.SERIALIZER);
    public static final RegistryObject<RecipeSerializer<?>> AQUEOULIZING =
            VE_RECIPE_SERIALIZERS_REGISTRY.register("aqueoulizing", () -> AqueoulizerRecipe.SERIALIZER);
    public static final RegistryObject<RecipeSerializer<?>> DISTILLING =
            VE_RECIPE_SERIALIZERS_REGISTRY.register("distilling", () -> DistillationRecipe.SERIALIZER);
    public static final RegistryObject<RecipeSerializer<?>> CENTRIFUGAL_SEPARATION =
            VE_RECIPE_SERIALIZERS_REGISTRY.register("centrifugal_separation", () -> CentrifugalSeparatorRecipe.SERIALIZER);
    public static final RegistryObject<RecipeSerializer<?>> IMPLOSION_COMPRESSING =
            VE_RECIPE_SERIALIZERS_REGISTRY.register("implosion_compressing", () -> ImplosionCompressorRecipe.SERIALIZER);
    public static final RegistryObject<RecipeSerializer<?>> INDUSTRIAL_BLASTING =
            VE_RECIPE_SERIALIZERS_REGISTRY.register("industrial_blasting", () -> IndustrialBlastingRecipe.SERIALIZER);
    public static final RegistryObject<RecipeSerializer<?>> TOOLING =
            VE_RECIPE_SERIALIZERS_REGISTRY.register("tooling", () -> ToolingRecipe.SERIALIZER);
    public static final RegistryObject<RecipeSerializer<?>> SAWMILLING =
            VE_RECIPE_SERIALIZERS_REGISTRY.register("sawmilling", () -> SawmillingRecipe.SERIALIZER);
    public static final RegistryObject<RecipeSerializer<?>> DIMENSIONAL_LASING =
            VE_RECIPE_SERIALIZERS_REGISTRY.register("dimensional_lasing", () -> DimensionalLaserRecipe.SERIALIZER);
    public static final RegistryObject<RecipeSerializer<?>> FLUID_ELECTROLYZING =
            VE_RECIPE_SERIALIZERS_REGISTRY.register("fluid_electrolyzing", () -> FluidElectrolyzerRecipe.SERIALIZER);
    public static final RegistryObject<RecipeSerializer<?>> FLUID_MIXING =
            VE_RECIPE_SERIALIZERS_REGISTRY.register("fluid_mixing", () -> FluidMixerRecipe.SERIALIZER);
    public static final RegistryObject<RecipeSerializer<?>> HYDROPONIC_INCUBATING =
            VE_RECIPE_SERIALIZERS_REGISTRY.register("hydroponic_incubating", () -> HydroponicIncubatorRecipe.SERIALIZER);
}