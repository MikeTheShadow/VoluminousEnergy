package com.veteam.voluminousenergy.world;

import com.veteam.voluminousenergy.fluids.CrudeOil;
import com.veteam.voluminousenergy.tools.Config;
import com.veteam.voluminousenergy.world.feature.CrudeOilFeature;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.GenerationStage;
import net.minecraft.world.gen.feature.LakesConfig;
import net.minecraft.world.gen.placement.LakeChanceConfig;
import net.minecraft.world.gen.placement.Placement;
import net.minecraftforge.registries.ForgeRegistries;

public class VEFeatureGeneration {
    public static void VEFeatureGenerationSetup(){
        if (!Config.ENABLE_VE_FEATURE_GEN.get()) return;
        LakeGeneration();
    }

    private static void LakeGeneration(){

        // Oil Lake generation
        if (Config.GENERATE_OIL_LAKES.get()){
            final int chance = Config.OIL_LAKE_CHANCE.get();
            for (Biome biome : ForgeRegistries.BIOMES) {
                biome.addFeature(GenerationStage.Decoration.LOCAL_MODIFICATIONS, Biome.createDecoratedFeature(
                        CrudeOilFeature.INSTANCE,
                        new LakesConfig(CrudeOil.CRUDE_OIL.getDefaultState().getBlockState()),
                        Placement.LAVA_LAKE,
                        new LakeChanceConfig(chance)
                ));
            }
        }

    }
}
