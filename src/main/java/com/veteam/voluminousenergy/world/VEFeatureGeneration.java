package com.veteam.voluminousenergy.world;

import com.veteam.voluminousenergy.fluids.CrudeOil;
import com.veteam.voluminousenergy.tools.Config;
import com.veteam.voluminousenergy.world.feature.CrudeOilFeature;
import com.veteam.voluminousenergy.world.feature.GeyserFeature;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biomes;
import net.minecraft.world.gen.GenerationStage;
import net.minecraft.world.gen.feature.BlockStateFeatureConfig;
import net.minecraft.world.gen.feature.NoFeatureConfig;
import net.minecraft.world.gen.placement.ChanceConfig;
import net.minecraft.world.gen.placement.Placement;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Arrays;

public class VEFeatureGeneration {

    public static void VEFeatureGenerationSetup(){
        if (!Config.ENABLE_VE_FEATURE_GEN.get()) return;
        LakeGeneration();
        GeyserGeneration();
    }

    private static void LakeGeneration() {

        // Oil Lake generation
        if (Config.GENERATE_OIL_LAKES.get()){
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
}
