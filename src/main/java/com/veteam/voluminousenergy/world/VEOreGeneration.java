package com.veteam.voluminousenergy.world;

import com.veteam.voluminousenergy.blocks.VEBlocks;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biomes;
import net.minecraft.world.gen.GenerationStage;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.OreFeatureConfig;
import net.minecraft.world.gen.placement.CountRangeConfig;
import net.minecraft.world.gen.placement.Placement;
import net.minecraftforge.registries.ForgeRegistries;

public class VEOreGeneration {

    public static void OreGeneration(){
        //Setup CountRangeConfigs here for each ore
        CountRangeConfig saltpeterOreConf = new CountRangeConfig(175, 55, 0, 256);//"Weight", Minimum Height, topOffset (Adjusts from max value), Maximum height

        //Run through every single biome registered with Forge
        for (Biome biome : ForgeRegistries.BIOMES){

            //If category is Desert. Desert, Desert Lakes, and Desert Hills included to ENSURE this will work (may be unecessary)
            if (biome.getCategory() == Biome.Category.DESERT || biome == Biomes.DESERT || biome == Biomes.DESERT_LAKES || biome == Biomes.DESERT_HILLS){
                //Generate Saltpeter ore in the sand
                //TODO: Make Saltpeter generate in sand, not stone
                biome.addFeature(GenerationStage.Decoration.UNDERGROUND_ORES, Biome.createDecoratedFeature(Feature.ORE, new OreFeatureConfig(OreFeatureConfig.FillerBlockType.NATURAL_STONE, VEBlocks.SALTPETER_ORE.getDefaultState(),33), Placement.COUNT_RANGE,saltpeterOreConf));
            }

        }

    }
}
