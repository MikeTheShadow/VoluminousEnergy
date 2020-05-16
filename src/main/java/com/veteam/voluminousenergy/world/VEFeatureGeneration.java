package com.veteam.voluminousenergy.world;

import com.veteam.voluminousenergy.fluids.CrudeOil;
import com.veteam.voluminousenergy.world.feature.CrudeOilFeature;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.GenerationStage;
import net.minecraft.world.gen.feature.LakesConfig;
import net.minecraft.world.gen.placement.LakeChanceConfig;
import net.minecraft.world.gen.placement.Placement;
import net.minecraftforge.registries.ForgeRegistries;

public class VEFeatureGeneration {
    public static void VEFeatureGenerationSetup(){
        LakeGeneration();
    }

    private static void LakeGeneration(){
        final int chance = 80;
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
