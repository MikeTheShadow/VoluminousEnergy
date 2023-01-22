package com.veteam.voluminousenergy.world.feature;

import com.veteam.voluminousenergy.VoluminousEnergy;
import com.veteam.voluminousenergy.world.ores.VEOres;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.BlockStateConfiguration;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class VEFeatures { // TODO: Investigate `BlockTags.FEATURES_CANNOT_REPLACE` as seen in LakeFeature.java
    public static final DeferredRegister<Feature<?>> VE_FEATURE_REGISTRY = DeferredRegister.create(ForgeRegistries.FEATURES, VoluminousEnergy.MODID);
//    public static final DeferredRegister<PlacedFeature> VE_PLACED_FEATURES = DeferredRegister.create(Registry.PLACED_FEATURE_REGISTRY, VoluminousEnergy.MODID);

    /** REG OBJECTS **/

    // "High Level" Features
    public static RegistryObject<VELakesFeature> VE_BSC_LAKE_FEATURE = VE_FEATURE_REGISTRY.register("ve_bsc_lake_feature", () -> new VELakesFeature(VELakesFeature.Configuration.CODEC)); // Lake using BlockStateConfiguration. AKA How MC used to do lakes
    public static RegistryObject<GeyserFeature> VE_GEYSER_FEATURE = VE_FEATURE_REGISTRY.register("ve_geyser_feature", () -> new GeyserFeature(GeyserFeature.Configuration.CODEC)); // Geyser using BlockStateConfiguration
    public static RegistryObject<RiceFeature> VE_RICE_FEATURE = VE_FEATURE_REGISTRY.register("ve_rice_feature", () -> new RiceFeature(BlockStateConfiguration.CODEC)); // Rice crop using BlockStateConfiguration
    public static RegistryObject<VEOreDepositFeature> VE_ORE_DEPOSIT_FEATURE = VE_FEATURE_REGISTRY.register("ve_ore_deposit_feature", () -> new VEOreDepositFeature(VEOreDepositFeature.Configuration.CODEC));
    public static RegistryObject<SurfaceMattersLakesFeature> VE_BSC_LAKE_SURFACE_FEATURE = VE_FEATURE_REGISTRY.register("ve_bsc_surface_lake_feature", () -> new SurfaceMattersLakesFeature(VELakesFeature.Configuration.CODEC, true));
    public static RegistryObject<SurfaceMattersLakesFeature> VE_BSC_LAKE_UNDERGROUND_FEATURE = VE_FEATURE_REGISTRY.register("ve_bsc_underground_lakes_feature", () -> new SurfaceMattersLakesFeature(VELakesFeature.Configuration.CODEC, false));

    protected static VEOres.NoPlacement noPlacement = new VEOres.NoPlacement();

    // PlacedFeatures created by methods below
    // Oil stuff
//    public static RegistryObject<PlacedFeature> VE_SURFACE_OIL_LAKE_PLACED = VE_PLACED_FEATURES.register("surface_oil_lake", () -> createSurfaceOilLake().get());
//    public static RegistryObject<PlacedFeature> VE_UNDERGROUND_OIL_LAKE_PLACED = VE_PLACED_FEATURES.register("underground_oil_lake", () -> createUndergroundOilLake().get());
//    public static RegistryObject<PlacedFeature> VE_OIL_GEYSER_PLACED = VE_PLACED_FEATURES.register("oil_geyser", () -> createOilGeyser().get());
//
//    // Deposits
//    public static RegistryObject<PlacedFeature> VE_COPPER_DEPOSIT_PLACED = VE_PLACED_FEATURES.register("copper_deposit", () -> createCopperDeposit().get());
//    public static RegistryObject<PlacedFeature> VE_IRON_DEPOSIT_PLACED = VE_PLACED_FEATURES.register("iron_deposit", () -> createIronDeposit().get());
//    public static RegistryObject<PlacedFeature> VE_GOLD_DEPOSIT_PLACED = VE_PLACED_FEATURES.register("gold_deposit", () -> createGoldDeposit().get());
//    public static RegistryObject<PlacedFeature> VE_BAUXITE_DEPOSIT_PLACED = VE_PLACED_FEATURES.register("bauxite_deposit", () -> createBauxiteDeposit().get());
//    public static RegistryObject<PlacedFeature> VE_CINNABAR_DEPOSIT_PLACED = VE_PLACED_FEATURES.register("cinnabar_deposit", () -> createCinnabarDeposit().get());
//    public static RegistryObject<PlacedFeature> VE_RUTILE_DEPOSIT_PLACED = VE_PLACED_FEATURES.register("rutile_deposit", () -> createRutileDeposit().get());
//    public static RegistryObject<PlacedFeature> VE_GALENA_DEPOSIT_PLACED = VE_PLACED_FEATURES.register("galena_deposit", () -> createGalenaDeposit().get());
//    public static RegistryObject<PlacedFeature> VE_EIGHZO_DEPOSIT_PLACED = VE_PLACED_FEATURES.register("eighzo_deposit", () -> createEighzoDeposit().get());
//
//    // Misc
//    public static RegistryObject<PlacedFeature> VE_RICE_CROP_PLACED = VE_PLACED_FEATURES.register("rice_crop", () -> createRiceCrop().get());

    /** ACTUAL FEATURE PLACEMENT CODE **/

//    // Oil PlacedFeatures
//    @NotNull
//    public static Holder<PlacedFeature> createSurfaceOilLake(){
//        ConfiguredFeature<?,?> configuredFeature = new ConfiguredFeature<>(
//                VE_BSC_LAKE_SURFACE_FEATURE.get(),
//                new BlockStateConfiguration(
//                        VEFluids.CRUDE_OIL_REG.get().getFlowing().defaultFluidState().createLegacyBlock()
//                ));
//        return registerPlacedFeature("surface_oil_lake", configuredFeature, noPlacement);
//    }
//
//    public static Holder<PlacedFeature> createUndergroundOilLake(){
//        ConfiguredFeature<?,?> configuredFeature = new ConfiguredFeature<>(
//                VE_BSC_LAKE_UNDERGROUND_FEATURE.get(),
//                new BlockStateConfiguration(
//                        VEFluids.CRUDE_OIL_REG.get().getFlowing().defaultFluidState().createLegacyBlock()
//                ));
//        return registerPlacedFeature("underground_oil_lake", configuredFeature, noPlacement);
//    }
//
//    public static Holder<PlacedFeature> createOilGeyser(){
//        ConfiguredFeature<?,?> configuredFeature = new ConfiguredFeature<>(
//                VE_GEYSER_FEATURE.get(),
//                new GeyserFeature.Configuration(
//                        VEFluids.CRUDE_OIL_REG.get().defaultFluidState()
//                ));
//        return registerPlacedFeature("oil_geyser", configuredFeature, noPlacement);
//    }
//
//    // Deposits
//    public static Holder<PlacedFeature> createCopperDeposit(){
//        ConfiguredFeature<?,?> configuredFeature = new ConfiguredFeature<>(VE_ORE_DEPOSIT_FEATURE.get(),
//                new VEOreDepositFeature.Configuration(
//                        BlockStateProvider.simple(Blocks.COPPER_ORE.defaultBlockState()),
//                        BlockStateProvider.simple(Blocks.RAW_COPPER_BLOCK.defaultBlockState())
//                ));
//        return registerPlacedFeature("copper_deposit", configuredFeature, noPlacement);
//    }
//
//    public static Holder<PlacedFeature> createIronDeposit(){
//        ConfiguredFeature<?,?> configuredFeature = new ConfiguredFeature<>(VE_ORE_DEPOSIT_FEATURE.get(),
//                new VEOreDepositFeature.Configuration(
//                        BlockStateProvider.simple(Blocks.IRON_ORE.defaultBlockState()),
//                        BlockStateProvider.simple(Blocks.RAW_IRON_BLOCK.defaultBlockState())
//                ));
//        return registerPlacedFeature("iron_deposit", configuredFeature, noPlacement);
//    }
//
//    public static Holder<PlacedFeature> createGoldDeposit(){
//        ConfiguredFeature<?,?> configuredFeature = new ConfiguredFeature<>(VE_ORE_DEPOSIT_FEATURE.get(),
//                new VEOreDepositFeature.Configuration(
//                        BlockStateProvider.simple(Blocks.GOLD_ORE.defaultBlockState()),
//                        BlockStateProvider.simple(Blocks.RAW_GOLD_BLOCK.defaultBlockState())
//                ));
//        return registerPlacedFeature("gold_deposit", configuredFeature, noPlacement);
//    }
//
//    public static Holder<PlacedFeature> createBauxiteDeposit(){
//        ConfiguredFeature<?,?> configuredFeature = new ConfiguredFeature<>(VE_ORE_DEPOSIT_FEATURE.get(),
//                new VEOreDepositFeature.Configuration(
//                        BlockStateProvider.simple(VEBlocks.BAUXITE_ORE.get().defaultBlockState()),
//                        BlockStateProvider.simple(VEBlocks.RAW_BAUXITE_BLOCK.get().defaultBlockState())
//                ));
//        return registerPlacedFeature("bauxite_deposit", configuredFeature, noPlacement);
//    }
//
//    public static Holder<PlacedFeature> createCinnabarDeposit(){
//        ConfiguredFeature<?,?> configuredFeature = new ConfiguredFeature<>(VE_ORE_DEPOSIT_FEATURE.get(),
//                new VEOreDepositFeature.Configuration(
//                        BlockStateProvider.simple(VEBlocks.CINNABAR_ORE.get().defaultBlockState()),
//                        BlockStateProvider.simple(VEBlocks.RAW_CINNABAR_BLOCK.get().defaultBlockState())
//                ));
//        return registerPlacedFeature("cinnabar_deposit", configuredFeature, noPlacement);
//    }
//
//    public static Holder<PlacedFeature> createRutileDeposit(){
//        ConfiguredFeature<?,?> configuredFeature = new ConfiguredFeature<>(VE_ORE_DEPOSIT_FEATURE.get(),
//                new VEOreDepositFeature.Configuration(
//                        BlockStateProvider.simple(VEBlocks.RUTILE_ORE.get().defaultBlockState()),
//                        BlockStateProvider.simple(VEBlocks.RAW_RUTILE_BLOCK.get().defaultBlockState())
//                ));
//        return registerPlacedFeature("rutile_deposit", configuredFeature, noPlacement);
//    }
//
//    public static Holder<PlacedFeature> createGalenaDeposit(){
//        ConfiguredFeature<?,?> configuredFeature = new ConfiguredFeature<>(VE_ORE_DEPOSIT_FEATURE.get(),
//                new VEOreDepositFeature.Configuration(
//                        BlockStateProvider.simple(VEBlocks.GALENA_ORE.get().defaultBlockState()),
//                        BlockStateProvider.simple(VEBlocks.RAW_GALENA_BLOCK.get().defaultBlockState())
//                ));
//        return registerPlacedFeature("galena_deposit", configuredFeature, noPlacement);
//    }
//
//    public static Holder<PlacedFeature> createEighzoDeposit(){
//        ConfiguredFeature<?,?> configuredFeature = new ConfiguredFeature<>(VE_ORE_DEPOSIT_FEATURE.get(),
//                new VEOreDepositFeature.Configuration(
//                        BlockStateProvider.simple(VEBlocks.EIGHZO_ORE.get().defaultBlockState()),
//                        BlockStateProvider.simple(VEBlocks.RAW_EIGHZO_BLOCK.get().defaultBlockState())
//                ));
//        return registerPlacedFeature("eighzo_deposit", configuredFeature, noPlacement);
//    }
//
//    // Misc
//    public static Holder<PlacedFeature> createRiceCrop(){
//        ConfiguredFeature<?,?> configuredFeature = new ConfiguredFeature<>(VEFeatures.VE_RICE_FEATURE.get(), new BlockStateConfiguration(VEBlocks.RICE_CROP.get().defaultBlockState()));
//        return registerPlacedFeature("rice_crop", configuredFeature, noPlacement);
//    }


//    private static <C extends FeatureConfiguration, F extends Feature<C>> Holder<PlacedFeature> registerPlacedFeature(String registryName, ConfiguredFeature<C, F> feature, PlacementModifier... placementModifiers) {
//        return PlacementUtils.register(registryName, Holder.direct(feature), placementModifiers);
//    }
}
