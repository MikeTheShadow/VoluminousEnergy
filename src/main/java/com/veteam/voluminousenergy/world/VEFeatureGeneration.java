package com.veteam.voluminousenergy.world;

import com.veteam.voluminousenergy.VoluminousEnergy;
import com.veteam.voluminousenergy.fluids.CrudeOil;
import com.veteam.voluminousenergy.tools.Config;
import com.veteam.voluminousenergy.world.feature.CrudeOilFeature;
import com.veteam.voluminousenergy.world.feature.GeyserFeature;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.GenerationStage;
import net.minecraft.world.gen.feature.BlockStateFeatureConfig;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.NoFeatureConfig;
import net.minecraft.world.gen.placement.ChanceConfig;
import net.minecraft.world.gen.placement.Placement;
import net.minecraftforge.event.world.BiomeLoadingEvent;

public class VEFeatureGeneration {


    public static void addFeaturesToBiomes(BiomeLoadingEvent event){
        if(event.getCategory() != Biome.Category.NETHER && event.getCategory() != Biome.Category.THEEND){
            VoluminousEnergy.LOGGER.debug(event.getName().toString() + " HIT!!! GOING TO REGISTER OIL!");
             ConfiguredFeature<?, ?> crudeOilLakeFeature = CrudeOilFeature.INSTANCE
                     .withConfiguration(new BlockStateFeatureConfig(CrudeOil.CRUDE_OIL.getDefaultState().getBlockState()))
                     .withPlacement(Placement.LAVA_LAKE.configure(new ChanceConfig(Config.OIL_LAKE_CHANCE.get())));

            ConfiguredFeature<?, ?> crudeOilGeyser = GeyserFeature.INSTANCE
                    .withConfiguration(new BlockStateFeatureConfig(CrudeOil.CRUDE_OIL.getDefaultState().getBlockState()))
                    .withPlacement(Placement.LAVA_LAKE.configure(new ChanceConfig(Config.OIL_GEYSER_CHANCE.get())));

            event.getGeneration().withFeature(GenerationStage.Decoration.LAKES, crudeOilLakeFeature);
            event.getGeneration().withFeature(GenerationStage.Decoration.LAKES, crudeOilGeyser);
        }
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

    public static ConfiguredFeature<?, ?> createOilGeyserFeature(){
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
