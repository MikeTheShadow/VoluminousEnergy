package com.veteam.voluminousenergy.recipe;

import com.veteam.voluminousenergy.VoluminousEnergy;
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

        public static final RegistryObject<RecipeType<VERecipe>> CRUSHING =
                VE_RECIPE_TYPES_REGISTRY.register("crushing", () -> new VERecipeType<>(RecipeConstants.CRUSHING));

        public static final RegistryObject<RecipeType<VERecipe>> ELECTROLYZING =
                VE_RECIPE_TYPES_REGISTRY.register("electrolyzing", () -> new VERecipeType<>(RecipeConstants.ELECTROLYZING));

        public static final RegistryObject<RecipeType<VERecipe>> CENTRIFUGAL_AGITATING =
                VE_RECIPE_TYPES_REGISTRY.register("centrifugal_agitating", () -> new VERecipeType<>(RecipeConstants.CENTRIFUGAL_AGITATING));

        public static final RegistryObject<RecipeType<VERecipe>> COMPRESSING =
                VE_RECIPE_TYPES_REGISTRY.register("compressing", () -> new VERecipeType<>(RecipeConstants.COMPRESSING));

        public static final RegistryObject<RecipeType<VERecipe>> STIRLING =
                VE_RECIPE_TYPES_REGISTRY.register("stirling", () -> new VERecipeType<>(RecipeConstants.STIRLING));

        public static final RegistryObject<RecipeType<VERecipe>> FUEL_COMBUSTION =
                VE_RECIPE_TYPES_REGISTRY.register("fuel_combustion", () -> new VERecipeType<>(RecipeConstants.FUEL_COMBUSTION));

        public static final RegistryObject<RecipeType<VERecipe>> AQUEOULIZING =
                VE_RECIPE_TYPES_REGISTRY.register("aqueoulizing", () -> new VERecipeType<>(RecipeConstants.AQUEOULIZING));

        public static final RegistryObject<RecipeType<VERecipe>> DISTILLING =
                VE_RECIPE_TYPES_REGISTRY.register("distilling", () -> new VERecipeType<>(RecipeConstants.DISTILLING));

        public static final RegistryObject<RecipeType<VERecipe>> CENTRIFUGAL_SEPARATION =
                VE_RECIPE_TYPES_REGISTRY.register("centrifugal_separation", () -> new VERecipeType<>(RecipeConstants.CENTRIFUGAL_SEPARATION));

        public static final RegistryObject<RecipeType<VERecipe>> IMPLOSION_COMPRESSING =
                VE_RECIPE_TYPES_REGISTRY.register("implosion_compressing", () -> new VERecipeType<>(RecipeConstants.IMPLOSION_COMPRESSING));

        public static final RegistryObject<RecipeType<VERecipe>> INDUSTRIAL_BLASTING =
                VE_RECIPE_TYPES_REGISTRY.register("industrial_blasting", () -> new VERecipeType<>(RecipeConstants.INDUSTRIAL_BLASTING));

        public static final RegistryObject<RecipeType<VERecipe>> TOOLING =
                VE_RECIPE_TYPES_REGISTRY.register("tooling", () -> new VERecipeType<>(RecipeConstants.TOOLING));

        public static final RegistryObject<RecipeType<VERecipe>> SAWMILLING =
                VE_RECIPE_TYPES_REGISTRY.register("sawmilling", () -> new VERecipeType<>(RecipeConstants.SAWMILLING));

        public static final RegistryObject<RecipeType<VERecipe>> DIMENSIONAL_LASING =
                VE_RECIPE_TYPES_REGISTRY.register("dimensional_lasing", () -> new VERecipeType<>(RecipeConstants.DIMENSIONAL_LASING));

        public static final RegistryObject<RecipeType<VERecipe>> FLUID_ELECTROLYZING =
                VE_RECIPE_TYPES_REGISTRY.register("fluid_electrolyzing", () -> new VERecipeType<>(RecipeConstants.FLUID_ELECTROLYZING));

        public static final RegistryObject<RecipeType<VERecipe>> FLUID_MIXING =
                VE_RECIPE_TYPES_REGISTRY.register("fluid_mixing", () -> new VERecipeType<>(RecipeConstants.FLUID_MIXING));

        public static final RegistryObject<RecipeType<VERecipe>> HYDROPONIC_INCUBATING =
                VE_RECIPE_TYPES_REGISTRY.register("hydroponic_incubating", () -> new VERecipeType<>(RecipeConstants.HYDROPONIC_INCUBATING));
    }

    public static final RegistryObject<RecipeSerializer<?>> PRIMITIVE_BLAST_FURNACING =
            VE_RECIPE_SERIALIZERS_REGISTRY.register("primitive_blast_furnacing", () -> new PrimitiveBlastFurnaceRecipe().getSerializer());
    public static final RegistryObject<RecipeSerializer<?>> CRUSHING =
            VE_RECIPE_SERIALIZERS_REGISTRY.register("crushing", () -> new CrusherRecipe().getSerializer());
    public static final RegistryObject<RecipeSerializer<?>> ELECTROLYZING =
            VE_RECIPE_SERIALIZERS_REGISTRY.register("electrolyzing", () -> new ElectrolyzerRecipe().getSerializer());
    public static final RegistryObject<RecipeSerializer<?>> CENTRIFUGAL_AGITATING =
            VE_RECIPE_SERIALIZERS_REGISTRY.register("centrifugal_agitating", () -> new CentrifugalAgitatorRecipe().getSerializer());
    public static final RegistryObject<RecipeSerializer<?>> COMPRESSING =
            VE_RECIPE_SERIALIZERS_REGISTRY.register("compressing", () -> new CompressorRecipe().getSerializer());
    public static final RegistryObject<RecipeSerializer<?>> STIRLING =
            VE_RECIPE_SERIALIZERS_REGISTRY.register("stirling", () -> new StirlingGeneratorRecipe().getSerializer());

    public static final RegistryObject<RecipeSerializer<?>> FUEL_COMBUSTION =
            VE_RECIPE_SERIALIZERS_REGISTRY.register("fuel_combustion", () -> new CombustionGeneratorRecipe().getSerializer());
    public static final RegistryObject<RecipeSerializer<?>> AQUEOULIZING =
            VE_RECIPE_SERIALIZERS_REGISTRY.register("aqueoulizing", () -> new AqueoulizerRecipe().getSerializer());
    public static final RegistryObject<RecipeSerializer<?>> DISTILLING =
            VE_RECIPE_SERIALIZERS_REGISTRY.register("distilling", () -> new DistillationRecipe().getSerializer());
    public static final RegistryObject<RecipeSerializer<?>> CENTRIFUGAL_SEPARATION =
            VE_RECIPE_SERIALIZERS_REGISTRY.register("centrifugal_separation", () -> new CentrifugalSeparatorRecipe().getSerializer());
    public static final RegistryObject<RecipeSerializer<?>> IMPLOSION_COMPRESSING =
            VE_RECIPE_SERIALIZERS_REGISTRY.register("implosion_compressing", () -> new ImplosionCompressorRecipe().getSerializer());
    public static final RegistryObject<RecipeSerializer<?>> INDUSTRIAL_BLASTING =
            VE_RECIPE_SERIALIZERS_REGISTRY.register("industrial_blasting", () -> new IndustrialBlastingRecipe().getSerializer());
    public static final RegistryObject<RecipeSerializer<?>> TOOLING =
            VE_RECIPE_SERIALIZERS_REGISTRY.register("tooling", () -> new ToolingRecipe().getSerializer());
    public static final RegistryObject<RecipeSerializer<?>> SAWMILLING =
            VE_RECIPE_SERIALIZERS_REGISTRY.register("sawmilling", () -> new VEFluidSawmillRecipe().getSerializer());
    public static final RegistryObject<RecipeSerializer<?>> DIMENSIONAL_LASING =
            VE_RECIPE_SERIALIZERS_REGISTRY.register("dimensional_lasing", () -> new DimensionalLaserRecipe().getSerializer());
    public static final RegistryObject<RecipeSerializer<?>> FLUID_ELECTROLYZING =
            VE_RECIPE_SERIALIZERS_REGISTRY.register("fluid_electrolyzing", () -> new FluidElectrolyzerRecipe().getSerializer());
    public static final RegistryObject<RecipeSerializer<?>> FLUID_MIXING =
            VE_RECIPE_SERIALIZERS_REGISTRY.register("fluid_mixing", () -> new FluidMixerRecipe().getSerializer());
    public static final RegistryObject<RecipeSerializer<?>> HYDROPONIC_INCUBATING =
            VE_RECIPE_SERIALIZERS_REGISTRY.register("hydroponic_incubating", () -> new HydroponicIncubatorRecipe().getSerializer());
}