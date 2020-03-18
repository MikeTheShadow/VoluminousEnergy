package com.veteam.voluminousenergy.world;

import com.veteam.voluminousenergy.VoluminousEnergy;
import com.veteam.voluminousenergy.blocks.VEBlocks;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.pattern.BlockMatcher;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biomes;
import net.minecraft.world.gen.GenerationStage;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.OreFeatureConfig;
import net.minecraft.world.gen.placement.CountRangeConfig;
import net.minecraft.world.gen.placement.Placement;
import net.minecraftforge.common.IExtensibleEnum;
import net.minecraftforge.registries.ForgeRegistries;

import javax.sql.rowset.Predicate;
import java.util.*;
import java.util.stream.Collectors;

import static net.minecraft.world.biome.Biome.LOGGER;

public class VEOreGeneration {

    public static void OreGeneration(){
        //Setup CountRangeConfigs here for each ore
        CountRangeConfig saltpeterOreConf = new CountRangeConfig(175, 55, 0, 256);//"Weight", Minimum Height, topOffset (Adjusts from max value), Maximum height
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
