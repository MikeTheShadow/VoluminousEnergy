package com.veteam.voluminousenergy.world;

import com.veteam.voluminousenergy.blocks.blocks.VEBlocks;
import com.veteam.voluminousenergy.tools.Config;
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

import java.util.ArrayList;
import java.util.List;

public class VEOreGeneration {

    public static void OreGeneration(){
        if (!Config.ENABLE_VE_FEATURE_GEN.get()) return;
        //Setup CountRangeConfigs here for each ore
        CountRangeConfig saltpeterOreConf = new CountRangeConfig(Config.SALTPETER_COUNT.get(), Config.SALTPETER_BOTTOM_OFFSET.get(), Config.SALTPETER_HEIGHT_OFFSET.get(), Config.SALTPETER_MAXIMUM_HEIGHT.get());//"Weight", Minimum Height, topOffset (Adjusts from max value), Maximum height
        CountRangeConfig bauxiteOreConf = new CountRangeConfig(Config.BAUXITE_COUNT.get(),Config.BAUXITE_BOTTOM_OFFSET.get(),Config.BAUXITE_HEIGHT_OFFSET.get(),Config.BAUXITE_MAXIMUM_HEIGHT.get());
        CountRangeConfig cinnabarOreConf = new CountRangeConfig(Config.CINNABAR_COUNT.get(),Config.CINNABAR_BOTTOM_OFFSET.get(),Config.CINNABAR_HEIGHT_OFFSET.get(),Config.CINNABAR_MAXIMUM_HEIGHT.get());
        CountRangeConfig rutileOreConf = new CountRangeConfig(Config.RUTILE_COUNT.get(),Config.RUTILE_BOTTOM_OFFSET.get(),Config.RUTILE_HEIGHT_OFFSET.get(),Config.RUTILE_MAXIMUM_HEIGHT.get());
        CountRangeConfig galenaOreConf = new CountRangeConfig(Config.GALENA_COUNT.get(), Config.GALENA_BOTTOM_OFFSET.get(),Config.GALENA_HEIGHT_OFFSET.get(),Config.GALENA_MAXIMUM_HEIGHT.get());

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
            if (biome.getCategory() != Biome.Category.NETHER || biome.getCategory() != Biome.Category.THEEND){
                //If category is Desert or biome is Badlands or Eroded Badlands
                if (biome.getCategory() == Biome.Category.DESERT || biome == Biomes.BADLANDS || biome == Biomes.ERODED_BADLANDS){
                    //Generate Saltpeter ore in the sand LOGGER.info("Saltpeter ore has been registered!");
                    if (Config.ENABLE_SALTPETER_ORE.get()) biome.addFeature(GenerationStage.Decoration.UNDERGROUND_ORES, Feature.ORE.withConfiguration(configBuilder("SAND_BLOCK","SAND",sandList,Config.SALTPETER_SIZE.get(),VEBlocks.SALTPETER_ORE.getDefaultState())).withPlacement(Placement.COUNT_RANGE.configure(saltpeterOreConf)));
                }
                //Places ores here that can spawn in ANY forge registered biome that is in the overworld
                if (Config.ENABLE_BAUXITE_ORE.get()) biome.addFeature(GenerationStage.Decoration.UNDERGROUND_ORES, Feature.ORE.withConfiguration(new OreFeatureConfig(OreFeatureConfig.FillerBlockType.NATURAL_STONE,VEBlocks.BAUXITE_ORE.getDefaultState(),Config.BAUXITE_SIZE.get())).withPlacement(Placement.COUNT_RANGE.configure(bauxiteOreConf)));
                if (Config.ENABLE_CINNABAR_ORE.get()) biome.addFeature(GenerationStage.Decoration.UNDERGROUND_ORES, Feature.ORE.withConfiguration(new OreFeatureConfig(OreFeatureConfig.FillerBlockType.NATURAL_STONE,VEBlocks.CINNABAR_ORE.getDefaultState(),Config.CINNABAR_SIZE.get())).withPlacement(Placement.COUNT_RANGE.configure(cinnabarOreConf)));
                if (Config.ENABLE_RUTILE_ORE.get()) biome.addFeature(GenerationStage.Decoration.UNDERGROUND_ORES, Feature.ORE.withConfiguration(new OreFeatureConfig(OreFeatureConfig.FillerBlockType.NATURAL_STONE,VEBlocks.RUTILE_ORE.getDefaultState(),Config.RUTILE_SIZE.get())).withPlacement(Placement.COUNT_RANGE.configure(rutileOreConf)));
                if (Config.ENABLE_GALENA_ORE.get()) biome.addFeature(GenerationStage.Decoration.UNDERGROUND_ORES, Feature.ORE.withConfiguration(new OreFeatureConfig(OreFeatureConfig.FillerBlockType.NATURAL_STONE,VEBlocks.GALENA_ORE.getDefaultState(),Config.GALENA_SIZE.get())).withPlacement(Placement.COUNT_RANGE.configure(galenaOreConf)));
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
