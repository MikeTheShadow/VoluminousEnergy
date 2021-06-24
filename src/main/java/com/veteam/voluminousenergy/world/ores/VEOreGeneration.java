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
import net.minecraft.world.gen.placement.Placement;
import net.minecraft.world.gen.placement.TopSolidRangeConfig;
import net.minecraftforge.event.world.BiomeLoadingEvent;

public class VEOreGeneration {

    public static void OreGeneration(BiomeLoadingEvent biome){

        if (biome.getCategory() == Biome.Category.NETHER){
            // Nether ores
        } else if (biome.getCategory() == Biome.Category.THEEND){
            // End ores
            // TODO: Eighzo ore
            ConfiguredFeature<?, ?> eighzoOre = Feature.ORE
                    .configured(new OreFeatureConfig(replace.END, VEBlocks.EIGHZO_ORE.defaultBlockState(), /*Size*/4))
                    .decorated(Placement.RANGE.configured(new TopSolidRangeConfig(/*Bottom offset*/ 1, /*Top offset*/ 1,/*top hard cap*/ 36)))
                    .squared()
                    .count(1)
                    ;
            biome.getGeneration().addFeature(GenerationStage.Decoration.UNDERGROUND_ORES, eighzoOre);
        } else { // Assuming Overworld, catch all other biomes
            if (biome.getCategory() == Biome.Category.DESERT || biome.getName() == Biomes.BADLANDS.getRegistryName() || biome.getName() == Biomes.ERODED_BADLANDS.getRegistryName()){
                // Desert and other non-Beach Sandy Biome Oregen goes here, generally for Sand-based ores

                if (Config.ENABLE_SALTPETER_ORE.get()){
                    ConfiguredFeature<?, ?> saltpeterOre = Feature.ORE
                            .configured(new OreFeatureConfig(replace.SANDS, VEBlocks.SALTPETER_ORE.defaultBlockState(), Config.SALTPETER_SIZE.get()))
                            .decorated(Placement.RANGE.configured(new TopSolidRangeConfig(Config.SALTPETER_BOTTOM_OFFSET.get(), Config.SALTPETER_BOTTOM_OFFSET.get(), Config.SALTPETER_MAXIMUM_HEIGHT.get())))
                            .squared()
                            .count(Config.SALTPETER_COUNT.get());

                    biome.getGeneration().addFeature(GenerationStage.Decoration.UNDERGROUND_ORES, saltpeterOre);
                    VoluminousEnergy.LOGGER.info("Saltpeter Ore registered to generate in: " + biome.getName() + " With Size: " + Config.SALTPETER_SIZE.get() + " Bottom Offset: " + Config.SALTPETER_BOTTOM_OFFSET.get() + " Max Height: " + Config.SALTPETER_MAXIMUM_HEIGHT.get() + " Count: " + Config.SALTPETER_COUNT.get());
                }
            }

            if (Config.ENABLE_BAUXITE_ORE.get()){
                ConfiguredFeature<?, ?> bauxiteOre = Feature.ORE
                        .configured(new OreFeatureConfig(replace.OVERWORLD, VEBlocks.BAUXITE_ORE.defaultBlockState(), Config.BAUXITE_SIZE.get()))
                        .decorated(Placement.RANGE.configured(new TopSolidRangeConfig(Config.BAUXITE_BOTTOM_OFFSET.get(), Config.BAUXITE_BOTTOM_OFFSET.get(), Config.BAUXITE_MAXIMUM_HEIGHT.get())))
                        .squared()
                        .count(Config.BAUXITE_COUNT.get());

                biome.getGeneration().addFeature(GenerationStage.Decoration.UNDERGROUND_ORES, bauxiteOre);
                VoluminousEnergy.LOGGER.info("Bauxite Ore registered to generate in: " + biome.getName() + " With Size: " + Config.BAUXITE_SIZE.get() + " Bottom Offset: " + Config.BAUXITE_BOTTOM_OFFSET.get() + " Max Height: " + Config.BAUXITE_MAXIMUM_HEIGHT.get() + " Count: " + Config.BAUXITE_COUNT.get());
            }

            if (Config.ENABLE_CINNABAR_ORE.get()){
                ConfiguredFeature<?, ?> cinnabarOre = Feature.ORE
                        .configured(new OreFeatureConfig(replace.OVERWORLD, VEBlocks.CINNABAR_ORE.defaultBlockState(), Config.CINNABAR_SIZE.get()))
                        .decorated(Placement.RANGE.configured(new TopSolidRangeConfig(Config.CINNABAR_BOTTOM_OFFSET.get(), Config.CINNABAR_BOTTOM_OFFSET.get(), Config.CINNABAR_MAXIMUM_HEIGHT.get())))
                        .squared()
                        .count(Config.CINNABAR_COUNT.get());

                biome.getGeneration().addFeature(GenerationStage.Decoration.UNDERGROUND_ORES, cinnabarOre);
                VoluminousEnergy.LOGGER.info("Cinnabar Ore registered to generate in: " + biome.getName() + " With Size: " + Config.CINNABAR_SIZE.get() + " Bottom Offset: " + Config.CINNABAR_BOTTOM_OFFSET.get() + " Max Height: " + Config.CINNABAR_MAXIMUM_HEIGHT.get() + " Count: " + Config.CINNABAR_COUNT.get());
            }

            if (Config.ENABLE_RUTILE_ORE.get()){
                ConfiguredFeature<?, ?> rutileOre = Feature.ORE
                        .configured(new OreFeatureConfig(replace.OVERWORLD, VEBlocks.RUTILE_ORE.defaultBlockState(), Config.RUTILE_SIZE.get()))
                        .decorated(Placement.RANGE.configured(new TopSolidRangeConfig(Config.RUTILE_BOTTOM_OFFSET.get(), Config.RUTILE_BOTTOM_OFFSET.get(), Config.RUTILE_MAXIMUM_HEIGHT.get())))
                        .squared()
                        .count(Config.RUTILE_COUNT.get());

                biome.getGeneration().addFeature(GenerationStage.Decoration.UNDERGROUND_ORES, rutileOre);
                VoluminousEnergy.LOGGER.info("Rutile Ore registered to generate in: " + biome.getName() + " With Size: " + Config.RUTILE_SIZE.get() + " Bottom Offset: " + Config.RUTILE_BOTTOM_OFFSET.get() + " Max Height: " + Config.RUTILE_MAXIMUM_HEIGHT.get() + " Count: " + Config.RUTILE_COUNT.get());
            }

            if (Config.ENABLE_GALENA_ORE.get()){
                ConfiguredFeature<?, ?> galenaOre = Feature.ORE
                        .configured(new OreFeatureConfig(replace.OVERWORLD, VEBlocks.GALENA_ORE.defaultBlockState(), Config.GALENA_SIZE.get()))
                        .decorated(Placement.RANGE.configured(new TopSolidRangeConfig(Config.GALENA_BOTTOM_OFFSET.get(), Config.GALENA_BOTTOM_OFFSET.get(), Config.GALENA_MAXIMUM_HEIGHT.get())))
                        .squared()
                        .count(Config.GALENA_COUNT.get());

                biome.getGeneration().addFeature(GenerationStage.Decoration.UNDERGROUND_ORES, galenaOre);
                VoluminousEnergy.LOGGER.info("Galena Ore registered to generate in: " + biome.getName() + " With Size: " + Config.GALENA_SIZE.get() + " Bottom Offset: " + Config.GALENA_BOTTOM_OFFSET.get() + " Max Height: " + Config.GALENA_MAXIMUM_HEIGHT.get() + " Count: " + Config.GALENA_COUNT.get());
            }

        }
    }

    public static final class replace { // These are rule tests to see if the block in the world (inside the biome) is valid to be replaced with the generated ore
        public static final RuleTest OVERWORLD = new TagMatchRuleTest(BlockTags.BASE_STONE_OVERWORLD);
        public static final RuleTest SANDS = new MultiBlockStateMatchRuleTest(Blocks.SAND.defaultBlockState(), Blocks.RED_SAND.defaultBlockState());
        public static final RuleTest NETHER = new MultiBlockStateMatchRuleTest(Blocks.NETHERRACK.defaultBlockState(), Blocks.SOUL_SAND.defaultBlockState());
        public static final RuleTest TERRACOTTA = new MultiBlockStateMatchRuleTest(Blocks.TERRACOTTA.defaultBlockState(), Blocks.WHITE_TERRACOTTA.defaultBlockState(), Blocks.ORANGE_TERRACOTTA.defaultBlockState(), Blocks.MAGENTA_TERRACOTTA.defaultBlockState(), Blocks.LIGHT_BLUE_TERRACOTTA.defaultBlockState(), Blocks.YELLOW_TERRACOTTA.defaultBlockState(), Blocks.LIME_TERRACOTTA.defaultBlockState(), Blocks.PINK_TERRACOTTA.defaultBlockState(), Blocks.GRAY_TERRACOTTA.defaultBlockState(), Blocks.LIGHT_GRAY_TERRACOTTA.defaultBlockState(), Blocks.CYAN_TERRACOTTA.defaultBlockState(), Blocks.PURPLE_TERRACOTTA.defaultBlockState(), Blocks.BLUE_TERRACOTTA.defaultBlockState(), Blocks.BROWN_TERRACOTTA.defaultBlockState(), Blocks.GREEN_TERRACOTTA.defaultBlockState(), Blocks.RED_TERRACOTTA.defaultBlockState(), Blocks.BLACK_TERRACOTTA.defaultBlockState());
        public static final RuleTest END = new MultiBlockStateMatchRuleTest(Blocks.END_STONE.defaultBlockState());
    }
}
