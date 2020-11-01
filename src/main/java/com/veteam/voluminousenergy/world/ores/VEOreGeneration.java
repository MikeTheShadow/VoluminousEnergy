package com.veteam.voluminousenergy.world.ores;

import com.veteam.voluminousenergy.VoluminousEnergy;
import com.veteam.voluminousenergy.blocks.blocks.VEBlocks;
import com.veteam.voluminousenergy.tools.Config;
import com.veteam.voluminousenergy.util.MultiBlockStateMatchRuleTest;
import net.minecraft.block.Blocks;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biomes;
import net.minecraft.world.gen.GenerationStage;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.OreFeatureConfig;
import net.minecraft.world.gen.feature.template.BlockStateMatchRuleTest;
import net.minecraft.world.gen.feature.template.RuleTest;
import net.minecraft.world.gen.feature.template.TagMatchRuleTest;
import net.minecraft.world.gen.placement.DepthAverageConfig;
import net.minecraft.world.gen.placement.Placement;
import net.minecraftforge.event.world.BiomeLoadingEvent;

public class VEOreGeneration {

    public static void OreGeneration(BiomeLoadingEvent biome){
        VoluminousEnergy.LOGGER.debug("ORE GEN CALLED!");
        if (biome.getCategory() == Biome.Category.NETHER){
            // Nether ores
        } else if (biome.getCategory() == Biome.Category.THEEND){
            // End ores
        } else { // Assuming Overworld, catch all other biomes
            if (biome.getCategory() == Biome.Category.DESERT || biome.getName() == Biomes.BADLANDS.getRegistryName() || biome.getName() == Biomes.ERODED_BADLANDS.getRegistryName()){
                // Desert and other non-Beach Sandy Biome Oregen goes here, generally for Sand-based ores
                ConfiguredFeature<?, ?> saltpeterOre = Feature.ORE.withConfiguration(new OreFeatureConfig(replace.SANDS, VEBlocks.SALTPETER_ORE.getDefaultState(),
                        Config.SALTPETER_SIZE.get())).withPlacement(Placement.DEPTH_AVERAGE.configure(new DepthAverageConfig(15,16)));
                biome.getGeneration().withFeature(GenerationStage.Decoration.UNDERGROUND_ORES, saltpeterOre);
            }

            if (Config.ENABLE_BAUXITE_ORE.get()){
                ConfiguredFeature<?, ?> bauxiteOre = Feature.ORE.withConfiguration(new OreFeatureConfig(replace.OVERWORLD, VEBlocks.BAUXITE_ORE.getDefaultState(),
                        Config.BAUXITE_SIZE.get())).withPlacement(Placement.DEPTH_AVERAGE.configure(new DepthAverageConfig(15, 16)));
                biome.getGeneration().withFeature(GenerationStage.Decoration.UNDERGROUND_ORES, bauxiteOre);
            }

            if (Config.ENABLE_CINNABAR_ORE.get()){
                ConfiguredFeature<?, ?> cinnabarOre = Feature.ORE.withConfiguration(new OreFeatureConfig(replace.OVERWORLD, VEBlocks.CINNABAR_ORE.getDefaultState(),
                        Config.CINNABAR_SIZE.get())).withPlacement(Placement.DEPTH_AVERAGE.configure(new DepthAverageConfig(15, 16)));
                biome.getGeneration().withFeature(GenerationStage.Decoration.UNDERGROUND_ORES, cinnabarOre);
            }

            if (Config.ENABLE_RUTILE_ORE.get()){
                ConfiguredFeature<?, ?> rutileOre = Feature.ORE.withConfiguration(new OreFeatureConfig(replace.OVERWORLD, VEBlocks.RUTILE_ORE.getDefaultState(),
                        Config.RUTILE_SIZE.get())).withPlacement(Placement.DEPTH_AVERAGE.configure(new DepthAverageConfig(15, 16)));
                biome.getGeneration().withFeature(GenerationStage.Decoration.UNDERGROUND_ORES, rutileOre);
            }

            if (Config.ENABLE_GALENA_ORE.get()){
                ConfiguredFeature<?, ?> galenaOre = Feature.ORE.withConfiguration(new OreFeatureConfig(replace.OVERWORLD, VEBlocks.GALENA_ORE.getDefaultState(),
                        Config.GALENA_SIZE.get())).withPlacement(Placement.DEPTH_AVERAGE.configure(new DepthAverageConfig(15,16)));
                biome.getGeneration().withFeature(GenerationStage.Decoration.UNDERGROUND_ORES, galenaOre);
            }

        }
    }

    public static final class replace { // These are rule tests to see if the block in the world (inside the biome) is valid to be replaced with the generated ore
        public static final RuleTest OVERWORLD = new TagMatchRuleTest(BlockTags.BASE_STONE_OVERWORLD);
        public static final RuleTest SANDS = new MultiBlockStateMatchRuleTest(Blocks.SAND.getDefaultState(), Blocks.RED_SAND.getDefaultState());
        public static final RuleTest NETHER = new MultiBlockStateMatchRuleTest(Blocks.NETHERRACK.getDefaultState(), Blocks.SOUL_SAND.getDefaultState());
        public static final RuleTest TERRACOTTA = new MultiBlockStateMatchRuleTest(Blocks.TERRACOTTA.getDefaultState(), Blocks.WHITE_TERRACOTTA.getDefaultState(), Blocks.ORANGE_TERRACOTTA.getDefaultState(), Blocks.MAGENTA_TERRACOTTA.getDefaultState(), Blocks.LIGHT_BLUE_TERRACOTTA.getDefaultState(), Blocks.YELLOW_TERRACOTTA.getDefaultState(), Blocks.LIME_TERRACOTTA.getDefaultState(), Blocks.PINK_TERRACOTTA.getDefaultState(), Blocks.GRAY_TERRACOTTA.getDefaultState(), Blocks.LIGHT_GRAY_TERRACOTTA.getDefaultState(), Blocks.CYAN_TERRACOTTA.getDefaultState(), Blocks.PURPLE_TERRACOTTA.getDefaultState(), Blocks.BLUE_TERRACOTTA.getDefaultState(), Blocks.BROWN_TERRACOTTA.getDefaultState(), Blocks.GREEN_TERRACOTTA.getDefaultState(), Blocks.RED_TERRACOTTA.getDefaultState(), Blocks.BLACK_TERRACOTTA.getDefaultState());
    }

    /*
    public static void OreGeneration(BiomeLoadingEvent biome){

        //Setup CountRangeConfigs here for each ore
        VEOreConfig saltpeterOreConf = new VEOreConfig(Config.SALTPETER_COUNT.get(), Config.SALTPETER_BOTTOM_OFFSET.get(), Config.SALTPETER_HEIGHT_OFFSET.get(), Config.SALTPETER_MAXIMUM_HEIGHT.get());//"Weight", Minimum Height, topOffset (Adjusts from max value), Maximum height
        VEOreConfig bauxiteOreConf = new VEOreConfig(Config.BAUXITE_COUNT.get(),Config.BAUXITE_BOTTOM_OFFSET.get(),Config.BAUXITE_HEIGHT_OFFSET.get(),Config.BAUXITE_MAXIMUM_HEIGHT.get());
        VEOreConfig cinnabarOreConf = new VEOreConfig(Config.CINNABAR_COUNT.get(),Config.CINNABAR_BOTTOM_OFFSET.get(),Config.CINNABAR_HEIGHT_OFFSET.get(),Config.CINNABAR_MAXIMUM_HEIGHT.get());
        VEOreConfig rutileOreConf = new VEOreConfig(Config.RUTILE_COUNT.get(),Config.RUTILE_BOTTOM_OFFSET.get(),Config.RUTILE_HEIGHT_OFFSET.get(),Config.RUTILE_MAXIMUM_HEIGHT.get());
        VEOreConfig galenaOreConf = new VEOreConfig(Config.GALENA_COUNT.get(), Config.GALENA_BOTTOM_OFFSET.get(),Config.GALENA_HEIGHT_OFFSET.get(),Config.GALENA_MAXIMUM_HEIGHT.get());

        //Lists for blocks to generate ores in
        List<BlockState> sandList = new ArrayList<>();
        sandList.add(Blocks.SAND.getDefaultState());
        sandList.add(Blocks.RED_SAND.getDefaultState());

        List<BlockState> stoneList = new ArrayList<>();
        stoneList.add(Blocks.STONE.getDefaultState());
        stoneList.add(Blocks.GRANITE.getDefaultState());
        stoneList.add(Blocks.DIORITE.getDefaultState());
        stoneList.add(Blocks.ANDESITE.getDefaultState());

        List<BlockState> overWorld = new ArrayList<>();
        overWorld.addAll(stoneList);
        overWorld.addAll(sandList);

        ForgeRegistries.BIOMES;

        //Run through every single BiomeLoadingEvent registered with Forge and add the ores to the Loading Event
        if (biome.getCategory() != Biome.Category.NETHER || biome.getCategory() != Biome.Category.THEEND){
            //If category is Desert or biome is Badlands or Eroded Badlands
            if (biome.getCategory() == Biome.Category.DESERT || biome.getName() == Biomes.BADLANDS.getRegistryName() || biome.getName() == Biomes.ERODED_BADLANDS.getRegistryName()){
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
    private static OreFeatureConfig configBuilder(String name,String sName,List<BlockState> blockType,int size,BlockState state) {
        return new OreFeatureConfig(OreFeatureConfig.FillerBlockType.create(name,sName,(BlockState) -> {
            if (BlockState == null) {
                return false;
            } else {
                //Block block = BlockState.getBlock();
                return blockType.contains(state);
            }
        }), state,size);
    }
     */
}
