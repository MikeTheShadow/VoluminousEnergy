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
import net.minecraft.world.gen.feature.template.RuleTest;
import net.minecraft.world.gen.feature.template.TagMatchRuleTest;
import net.minecraft.world.gen.placement.*;
import net.minecraftforge.event.world.BiomeLoadingEvent;

public class VEOreGeneration {

    public static void OreGeneration(BiomeLoadingEvent biome){

        if (biome.getCategory() == Biome.Category.NETHER){
            // Nether ores
        } else if (biome.getCategory() == Biome.Category.THEEND){
            // End ores
        } else { // Assuming Overworld, catch all other biomes
            if (biome.getCategory() == Biome.Category.DESERT || biome.getName() == Biomes.BADLANDS.getRegistryName() || biome.getName() == Biomes.ERODED_BADLANDS.getRegistryName()){
                // Desert and other non-Beach Sandy Biome Oregen goes here, generally for Sand-based ores

                if (Config.ENABLE_SALTPETER_ORE.get()){
                    ConfiguredFeature<?, ?> saltpeterOre = Feature.ORE
                            .withConfiguration(new OreFeatureConfig(replace.SANDS, VEBlocks.SALTPETER_ORE.getDefaultState(), Config.SALTPETER_SIZE.get()))
                            .withPlacement(Placement.RANGE.configure(new TopSolidRangeConfig(Config.SALTPETER_BOTTOM_OFFSET.get(), Config.SALTPETER_BOTTOM_OFFSET.get(), Config.SALTPETER_MAXIMUM_HEIGHT.get())))
                            .square()
                            .func_242731_b(Config.SALTPETER_COUNT.get());

                    biome.getGeneration().withFeature(GenerationStage.Decoration.UNDERGROUND_ORES, saltpeterOre);
                    VoluminousEnergy.LOGGER.info("Saltpeter Ore registered to generate in: " + biome.getName() + " With Size: " + Config.SALTPETER_SIZE.get() + " Bottom Offset: " + Config.SALTPETER_BOTTOM_OFFSET.get() + " Max Height: " + Config.SALTPETER_MAXIMUM_HEIGHT.get() + " Count: " + Config.SALTPETER_COUNT.get());
                }
            }

            if (Config.ENABLE_BAUXITE_ORE.get()){
                ConfiguredFeature<?, ?> bauxiteOre = Feature.ORE
                        .withConfiguration(new OreFeatureConfig(replace.OVERWORLD, VEBlocks.BAUXITE_ORE.getDefaultState(), Config.BAUXITE_SIZE.get()))
                        .withPlacement(Placement.RANGE.configure(new TopSolidRangeConfig(Config.BAUXITE_BOTTOM_OFFSET.get(), Config.BAUXITE_BOTTOM_OFFSET.get(), Config.BAUXITE_MAXIMUM_HEIGHT.get())))
                        .square()
                        .func_242731_b(Config.BAUXITE_COUNT.get());

                biome.getGeneration().withFeature(GenerationStage.Decoration.UNDERGROUND_ORES, bauxiteOre);
                VoluminousEnergy.LOGGER.info("Bauxite Ore registered to generate in: " + biome.getName() + " With Size: " + Config.BAUXITE_SIZE.get() + " Bottom Offset: " + Config.BAUXITE_BOTTOM_OFFSET.get() + " Max Height: " + Config.BAUXITE_MAXIMUM_HEIGHT.get() + " Count: " + Config.BAUXITE_COUNT.get());
            }

            if (Config.ENABLE_CINNABAR_ORE.get()){
                ConfiguredFeature<?, ?> cinnabarOre = Feature.ORE
                        .withConfiguration(new OreFeatureConfig(replace.OVERWORLD, VEBlocks.CINNABAR_ORE.getDefaultState(), Config.CINNABAR_SIZE.get()))
                        .withPlacement(Placement.RANGE.configure(new TopSolidRangeConfig(Config.CINNABAR_BOTTOM_OFFSET.get(), Config.CINNABAR_BOTTOM_OFFSET.get(), Config.CINNABAR_MAXIMUM_HEIGHT.get())))
                        .square()
                        .func_242731_b(Config.CINNABAR_COUNT.get());

                biome.getGeneration().withFeature(GenerationStage.Decoration.UNDERGROUND_ORES, cinnabarOre);
                VoluminousEnergy.LOGGER.info("Cinnabar Ore registered to generate in: " + biome.getName() + " With Size: " + Config.CINNABAR_SIZE.get() + " Bottom Offset: " + Config.CINNABAR_BOTTOM_OFFSET.get() + " Max Height: " + Config.CINNABAR_MAXIMUM_HEIGHT.get() + " Count: " + Config.CINNABAR_COUNT.get());
            }

            if (Config.ENABLE_RUTILE_ORE.get()){
                ConfiguredFeature<?, ?> rutileOre = Feature.ORE
                        .withConfiguration(new OreFeatureConfig(replace.OVERWORLD, VEBlocks.RUTILE_ORE.getDefaultState(), Config.RUTILE_SIZE.get()))
                        .withPlacement(Placement.RANGE.configure(new TopSolidRangeConfig(Config.RUTILE_BOTTOM_OFFSET.get(), Config.RUTILE_BOTTOM_OFFSET.get(), Config.RUTILE_MAXIMUM_HEIGHT.get())))
                        .square()
                        .func_242731_b(Config.RUTILE_COUNT.get());

                biome.getGeneration().withFeature(GenerationStage.Decoration.UNDERGROUND_ORES, rutileOre);
                VoluminousEnergy.LOGGER.info("Rutile Ore registered to generate in: " + biome.getName() + " With Size: " + Config.RUTILE_SIZE.get() + " Bottom Offset: " + Config.RUTILE_BOTTOM_OFFSET.get() + " Max Height: " + Config.RUTILE_MAXIMUM_HEIGHT.get() + " Count: " + Config.RUTILE_COUNT.get());
            }

            if (Config.ENABLE_GALENA_ORE.get()){
                ConfiguredFeature<?, ?> galenaOre = Feature.ORE
                        .withConfiguration(new OreFeatureConfig(replace.OVERWORLD, VEBlocks.GALENA_ORE.getDefaultState(), Config.GALENA_SIZE.get()))
                        .withPlacement(Placement.RANGE.configure(new TopSolidRangeConfig(Config.GALENA_BOTTOM_OFFSET.get(), Config.GALENA_BOTTOM_OFFSET.get(), Config.GALENA_MAXIMUM_HEIGHT.get())))
                        .square()
                        .func_242731_b(Config.GALENA_COUNT.get());

                biome.getGeneration().withFeature(GenerationStage.Decoration.UNDERGROUND_ORES, galenaOre);
                VoluminousEnergy.LOGGER.info("Galena Ore registered to generate in: " + biome.getName() + " With Size: " + Config.GALENA_SIZE.get() + " Bottom Offset: " + Config.GALENA_BOTTOM_OFFSET.get() + " Max Height: " + Config.GALENA_MAXIMUM_HEIGHT.get() + " Count: " + Config.GALENA_COUNT.get());
            }

        }
    }

    public static final class replace { // These are rule tests to see if the block in the world (inside the biome) is valid to be replaced with the generated ore
        public static final RuleTest OVERWORLD = new TagMatchRuleTest(BlockTags.BASE_STONE_OVERWORLD);
        public static final RuleTest SANDS = new MultiBlockStateMatchRuleTest(Blocks.SAND.getDefaultState(), Blocks.RED_SAND.getDefaultState());
        public static final RuleTest NETHER = new MultiBlockStateMatchRuleTest(Blocks.NETHERRACK.getDefaultState(), Blocks.SOUL_SAND.getDefaultState());
        public static final RuleTest TERRACOTTA = new MultiBlockStateMatchRuleTest(Blocks.TERRACOTTA.getDefaultState(), Blocks.WHITE_TERRACOTTA.getDefaultState(), Blocks.ORANGE_TERRACOTTA.getDefaultState(), Blocks.MAGENTA_TERRACOTTA.getDefaultState(), Blocks.LIGHT_BLUE_TERRACOTTA.getDefaultState(), Blocks.YELLOW_TERRACOTTA.getDefaultState(), Blocks.LIME_TERRACOTTA.getDefaultState(), Blocks.PINK_TERRACOTTA.getDefaultState(), Blocks.GRAY_TERRACOTTA.getDefaultState(), Blocks.LIGHT_GRAY_TERRACOTTA.getDefaultState(), Blocks.CYAN_TERRACOTTA.getDefaultState(), Blocks.PURPLE_TERRACOTTA.getDefaultState(), Blocks.BLUE_TERRACOTTA.getDefaultState(), Blocks.BROWN_TERRACOTTA.getDefaultState(), Blocks.GREEN_TERRACOTTA.getDefaultState(), Blocks.RED_TERRACOTTA.getDefaultState(), Blocks.BLACK_TERRACOTTA.getDefaultState());
    }
}
