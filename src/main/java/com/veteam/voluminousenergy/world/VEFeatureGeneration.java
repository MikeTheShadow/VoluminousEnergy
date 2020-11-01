package com.veteam.voluminousenergy.world;

import com.veteam.voluminousenergy.VoluminousEnergy;
import com.veteam.voluminousenergy.fluids.CrudeOil;
import com.veteam.voluminousenergy.tools.Config;
import com.veteam.voluminousenergy.world.feature.CrudeOilFeature;
import com.veteam.voluminousenergy.world.feature.GeyserFeature;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.WorldGenRegistries;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biomes;
import net.minecraft.world.gen.GenerationStage;
import net.minecraft.world.gen.feature.BlockStateFeatureConfig;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.NoFeatureConfig;
import net.minecraft.world.gen.placement.ChanceConfig;
import net.minecraft.world.gen.placement.Placement;
import net.minecraftforge.common.util.Lazy;
import net.minecraftforge.event.world.BiomeLoadingEvent;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Arrays;

public class VEFeatureGeneration {
    private static final Lazy<ConfiguredFeature<?, ?>> OIL_LAKES = Lazy.of(VEFeatureGeneration::createOilLakeFeature);
    private static final Lazy<ConfiguredFeature<?, ?>> OIL_GEYSER = Lazy.of(VEFeatureGeneration::createOilGeyserFeature);

    public static void VEFeatureGenerationSetup(BiomeLoadingEvent biome){
        if (biome.getCategory() != Biome.Category.NETHER && biome.getCategory() != Biome.Category.THEEND){
            registerConfiguredFeature("oil_lake", OIL_LAKES.get());
            registerConfiguredFeature("oil_geyser", OIL_GEYSER.get());
        }
    }

    private static void registerConfiguredFeature(String name, ConfiguredFeature<?, ?> configuredFeature) {
        Registry.register(WorldGenRegistries.CONFIGURED_FEATURE, VoluminousEnergy.MODID + ":" + name, configuredFeature);
    }

    private static ConfiguredFeature<?, ?> createOilLakeFeature(){
        if (Config.GENERATE_OIL_LAKES.get()){
            final int chance = Config.OIL_LAKE_CHANCE.get();
            return CrudeOilFeature.INSTANCE
                    .withConfiguration(new BlockStateFeatureConfig(CrudeOil.CRUDE_OIL.getDefaultState().getBlockState()))
                    .withPlacement(Placement.LAVA_LAKE.configure(new ChanceConfig(chance)));
        }
        return Feature.NO_OP.withConfiguration(new NoFeatureConfig());
    }

    private static ConfiguredFeature<?, ?> createOilGeyserFeature(){
        if (Config.GENERATE_OIL_GEYSER.get()){
            final int chance = Config.OIL_GEYSER_CHANCE.get();
            return GeyserFeature.INSTANCE
                    .withConfiguration(new BlockStateFeatureConfig(CrudeOil.CRUDE_OIL.getDefaultState().getBlockState()))
                    .withPlacement(Placement.LAVA_LAKE.configure(new ChanceConfig(chance)));
        }
        return Feature.NO_OP.withConfiguration(new NoFeatureConfig());
    }

    /*
    private static void LakeGeneration() {

        // Oil Lake generation
        final int chance = Config.OIL_LAKE_CHANCE.get();
        for (Biome biome : ForgeRegistries.BIOMES) {
            if (!(biome.getCategory().equals(Biome.Category.NETHER) || biome.getCategory().equals(Biome.Category.THEEND))){
                biome.addFeature(GenerationStage.Decoration.LOCAL_MODIFICATIONS, CrudeOilFeature.INSTANCE
                        .withConfiguration(new BlockStateFeatureConfig(CrudeOil.CRUDE_OIL.getDefaultState().getBlockState()))
                        .withPlacement(Placement.LAVA_LAKE.configure(new ChanceConfig(chance)))
                );
            }
        }

    }

    private static void GeyserGeneration(){
        final int chance = Config.OIL_GEYSER_CHANCE.get();
        for (Biome biome : ForgeRegistries.BIOMES){
            if (!(biome.getCategory().equals(Biome.Category.NETHER) || biome.getCategory().equals(Biome.Category.THEEND))){
                biome.addFeature(GenerationStage.Decoration.LOCAL_MODIFICATIONS, GeyserFeature.INSTANCE
                        .withConfiguration(new NoFeatureConfig())
                        .withPlacement(Placement.LAVA_LAKE.configure(new ChanceConfig(chance)))
                );
            }
        }
    }
    */
}
