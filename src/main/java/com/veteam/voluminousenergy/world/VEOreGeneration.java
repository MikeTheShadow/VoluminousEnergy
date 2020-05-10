package com.veteam.voluminousenergy.world;

import com.veteam.voluminousenergy.blocks.blocks.VEBlocks;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biomes;
import net.minecraft.world.gen.GenerationStage;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.OreFeatureConfig;
import net.minecraft.world.gen.placement.CountRangeConfig;
import net.minecraft.world.gen.placement.Placement;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.*;

public class VEOreGeneration {

    public static void OreGeneration(){
        //Setup CountRangeConfigs here for each ore
        CountRangeConfig saltpeterOreConf = new CountRangeConfig(4, 55, 0, 256);//"Weight", Minimum Height, topOffset (Adjusts from max value), Maximum height
        CountRangeConfig bauxiteOreConf = new CountRangeConfig(16,10,0,60); //Cluster 2, MinY 10, MaxY 60
        CountRangeConfig cinnabarOreConf = new CountRangeConfig(9,1,0,256);//Cinnabar can be found at every level
        CountRangeConfig rutileOreConf = new CountRangeConfig(3,1,0,10);//From Y1 to Y10
        CountRangeConfig galenaOreConf = new CountRangeConfig(3,12,0,32);//From Y12 to Y32

        //Lists for blocks to generate ores in
        List<Block> sandList = new ArrayList<>();
        sandList.add(Blocks.SAND);
        sandList.add(Blocks.RED_SAND);

        List<Block> stoneList = new ArrayList<>();
        stoneList.add(Blocks.STONE);
        stoneList.add(Blocks.GRANITE);
        stoneList.add(Blocks.DIORITE);
        stoneList.add(Blocks.ANDESITE);

        List<Block> overWorld = new ArrayList<>();
        overWorld.addAll(stoneList);
        overWorld.addAll(sandList);

        //Run through every single biome registered with Forge
        for (Biome biome : ForgeRegistries.BIOMES){
            //Places ores here that can spawn in ANY forge registered biome
            biome.addFeature(GenerationStage.Decoration.UNDERGROUND_ORES, Biome.createDecoratedFeature(Feature.ORE, new OreFeatureConfig(OreFeatureConfig.FillerBlockType.NATURAL_STONE,VEBlocks.BAUXITE_ORE.getDefaultState(),12),Placement.COUNT_RANGE,bauxiteOreConf));
            biome.addFeature(GenerationStage.Decoration.UNDERGROUND_ORES, Biome.createDecoratedFeature(Feature.ORE, new OreFeatureConfig(OreFeatureConfig.FillerBlockType.NATURAL_STONE,VEBlocks.CINNABAR_ORE.getDefaultState(),12),Placement.COUNT_RANGE,cinnabarOreConf));
            biome.addFeature(GenerationStage.Decoration.UNDERGROUND_ORES, Biome.createDecoratedFeature(Feature.ORE, new OreFeatureConfig(OreFeatureConfig.FillerBlockType.NATURAL_STONE,VEBlocks.RUTILE_ORE.getDefaultState(),12),Placement.COUNT_RANGE,rutileOreConf));
            biome.addFeature(GenerationStage.Decoration.UNDERGROUND_ORES, Biome.createDecoratedFeature(Feature.ORE, new OreFeatureConfig(OreFeatureConfig.FillerBlockType.NATURAL_STONE,VEBlocks.GALENA_ORE.getDefaultState(),12),Placement.COUNT_RANGE,galenaOreConf));

            //If category is Desert. Desert, Desert Lakes, and Desert Hills included to ENSURE this will work (may be unecessary)
            if (biome.getCategory() == Biome.Category.DESERT || biome == Biomes.DESERT || biome == Biomes.DESERT_LAKES || biome == Biomes.DESERT_HILLS){
                //Generate Saltpeter ore in the sand LOGGER.info("Saltpeter ore has been registered!");
                biome.addFeature(GenerationStage.Decoration.UNDERGROUND_ORES, Biome.createDecoratedFeature(Feature.ORE, configBuilder("SAND_BLOCK","SAND",sandList,33,VEBlocks.SALTPETER_ORE.getDefaultState()), Placement.COUNT_RANGE,saltpeterOreConf));
            }

        }

    }
    private static OreFeatureConfig configBuilder(String name,String sName,List<Block> blockType,int size,BlockState state) {
        return new OreFeatureConfig(OreFeatureConfig.FillerBlockType.create(name,sName,(BlockState) -> {
            if (BlockState == null) {
                return false;
            } else {
                Block block = BlockState.getBlock();
                return blockType.contains(block);
            }
        }), state,size);
    }

}
