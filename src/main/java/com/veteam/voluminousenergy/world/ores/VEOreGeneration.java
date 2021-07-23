package com.veteam.voluminousenergy.world.ores;

import com.veteam.voluminousenergy.VoluminousEnergy;
import com.veteam.voluminousenergy.blocks.blocks.VEBlocks;
import com.veteam.voluminousenergy.tools.Config;
import com.veteam.voluminousenergy.util.MultiBlockStateMatchRuleTest;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.VerticalAnchor;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.OreConfiguration;
import net.minecraft.world.level.levelgen.heightproviders.UniformHeight;
import net.minecraft.world.level.levelgen.structure.templatesystem.RuleTest;
import net.minecraft.world.level.levelgen.structure.templatesystem.TagMatchTest;
import net.minecraft.world.level.levelgen.placement.FeatureDecorator;
import net.minecraft.world.level.levelgen.feature.configurations.RangeDecoratorConfiguration;
import net.minecraftforge.event.world.BiomeLoadingEvent;

public class VEOreGeneration {

    public static void OreGeneration(BiomeLoadingEvent biome){

        if (biome.getCategory() == Biome.BiomeCategory.NETHER){
            // Nether ores
        } else if (biome.getCategory() == Biome.BiomeCategory.THEEND){
            // End ores
            if(Config.ENABLE_EIGHZO_ORE.get()) {
                ConfiguredFeature<?, ?> eighzoOre = Feature.ORE
                        .configured(new OreConfiguration(replace.END, VEBlocks.EIGHZO_ORE.defaultBlockState(), /*Size*/Config.EIGHZO_SIZE.get()))
                        .decorated(FeatureDecorator.RANGE.configured(new RangeDecoratorConfiguration(UniformHeight.of(VerticalAnchor.absolute(Config.EIGHZO_BOTTOM_ANCHOR.get()), VerticalAnchor.absolute(Config.EIGHZO_TOP_ANCHOR.get())))))
                        //.decorated(FeatureDecorator.RANGE.configured(new RangeDecoratorConfiguration(/*Bottom offset*/ Config.EIGHZO_BOTTOM_OFFSET.get(), /*Top offset*/ Config.EIGHZO_HEIGHT_OFFSET.get(), /*top hard cap*/ Config.EIGHZO_MAXIMUM_HEIGHT.get())))
                        .squared()
                        .count(Config.EIGHZO_COUNT.get());
                biome.getGeneration().addFeature(GenerationStep.Decoration.UNDERGROUND_ORES, eighzoOre);
                oreLog(VEBlocks.EIGHZO_ORE, biome, Config.EIGHZO_SIZE.get(), Config.EIGHZO_BOTTOM_ANCHOR.get(), Config.EIGHZO_TOP_ANCHOR.get(), Config.EIGHZO_COUNT.get());
            }
        } else { // Assuming Overworld, catch all other biomes
            if (biome.getCategory() == Biome.BiomeCategory.DESERT || biome.getName() == Biomes.BADLANDS.getRegistryName() || biome.getName() == Biomes.ERODED_BADLANDS.getRegistryName()){
                // Desert and other non-Beach Sandy Biome Oregen goes here, generally for Sand-based ores

                if (Config.ENABLE_SALTPETER_ORE.get()){
                    ConfiguredFeature<?, ?> saltpeterOre = Feature.ORE
                            .configured(new OreConfiguration(replace.SANDS, VEBlocks.SALTPETER_ORE.defaultBlockState(), Config.SALTPETER_SIZE.get()))
                            .decorated(FeatureDecorator.RANGE.configured(new RangeDecoratorConfiguration(UniformHeight.of(VerticalAnchor.absolute(Config.SALTPETER_BOTTOM_ANCHOR.get()), VerticalAnchor.absolute(Config.SALTPETER_TOP_ANCHOR.get())))))
                            //.decorated(FeatureDecorator.RANGE.configured(new RangeDecoratorConfiguration(Config.SALTPETER_BOTTOM_OFFSET.get(), Config.SALTPETER_BOTTOM_OFFSET.get(), Config.SALTPETER_MAXIMUM_HEIGHT.get())))
                            .squared()
                            .count(Config.SALTPETER_COUNT.get());

                    biome.getGeneration().addFeature(GenerationStep.Decoration.UNDERGROUND_ORES, saltpeterOre);
                    oreLog(VEBlocks.SALTPETER_ORE, biome, Config.SALTPETER_SIZE.get(), Config.SALTPETER_BOTTOM_ANCHOR.get(), Config.SALTPETER_TOP_ANCHOR.get(), Config.BAUXITE_COUNT.get());
                }
            }

            if (Config.ENABLE_BAUXITE_ORE.get()){
                ConfiguredFeature<?, ?> bauxiteOre = Feature.ORE
                        .configured(new OreConfiguration(replace.OVERWORLD, VEBlocks.BAUXITE_ORE.defaultBlockState(), Config.BAUXITE_SIZE.get()))
                        .decorated(FeatureDecorator.RANGE.configured(new RangeDecoratorConfiguration(UniformHeight.of(VerticalAnchor.absolute(Config.BAUXITE_BOTTOM_ANCHOR.get()), VerticalAnchor.absolute(Config.BAUXITE_TOP_ANCHOR.get())))))
                        //.decorated(FeatureDecorator.RANGE.configured(new RangeDecoratorConfiguration(Config.BAUXITE_BOTTOM_OFFSET.get(), Config.BAUXITE_HEIGHT_OFFSET.get(), Config.BAUXITE_MAXIMUM_HEIGHT.get())))
                        .squared()
                        .count(Config.BAUXITE_COUNT.get());

                biome.getGeneration().addFeature(GenerationStep.Decoration.UNDERGROUND_ORES, bauxiteOre);
                oreLog(VEBlocks.BAUXITE_ORE, biome, Config.BAUXITE_SIZE.get(), Config.BAUXITE_BOTTOM_ANCHOR.get(), Config.BAUXITE_TOP_ANCHOR.get(), Config.BAUXITE_COUNT.get());
            }

            if (Config.ENABLE_CINNABAR_ORE.get()){
                ConfiguredFeature<?, ?> cinnabarOre = Feature.ORE
                        .configured(new OreConfiguration(replace.OVERWORLD, VEBlocks.CINNABAR_ORE.defaultBlockState(), Config.CINNABAR_SIZE.get()))
                        .decorated(FeatureDecorator.RANGE.configured(new RangeDecoratorConfiguration(UniformHeight.of(VerticalAnchor.absolute(Config.CINNABAR_BOTTOM_ANCHOR.get()), VerticalAnchor.absolute(Config.CINNABAR_TOP_ANCHOR.get())))))
                        //.decorated(FeatureDecorator.RANGE.configured(new RangeDecoratorConfiguration(Config.CINNABAR_BOTTOM_OFFSET.get(), Config.CINNABAR_HEIGHT_OFFSET.get(), Config.CINNABAR_MAXIMUM_HEIGHT.get())))
                        .squared()
                        .count(Config.CINNABAR_COUNT.get());

                biome.getGeneration().addFeature(GenerationStep.Decoration.UNDERGROUND_ORES, cinnabarOre);
                oreLog(VEBlocks.CINNABAR_ORE, biome, Config.CINNABAR_SIZE.get(), Config.CINNABAR_BOTTOM_ANCHOR.get(), Config.CINNABAR_TOP_ANCHOR.get(), Config.CINNABAR_COUNT.get());
            }

            if (Config.ENABLE_RUTILE_ORE.get()){
                ConfiguredFeature<?, ?> rutileOre = Feature.ORE
                        .configured(new OreConfiguration(replace.OVERWORLD, VEBlocks.RUTILE_ORE.defaultBlockState(), Config.RUTILE_SIZE.get()))
                        .decorated(FeatureDecorator.RANGE.configured(new RangeDecoratorConfiguration(UniformHeight.of(VerticalAnchor.absolute(Config.RUTILE_BOTTOM_ANCHOR.get()), VerticalAnchor.absolute(Config.RUTILE_TOP_ANCHOR.get())))))
                        //.decorated(FeatureDecorator.RANGE.configured(new RangeDecoratorConfiguration(Config.RUTILE_BOTTOM_OFFSET.get(), Config.BAUXITE_HEIGHT_OFFSET.get(), Config.RUTILE_MAXIMUM_HEIGHT.get())))
                        .squared()
                        .count(Config.RUTILE_COUNT.get());

                biome.getGeneration().addFeature(GenerationStep.Decoration.UNDERGROUND_ORES, rutileOre);
                oreLog(VEBlocks.RUTILE_ORE, biome, Config.RUTILE_SIZE.get(), Config.RUTILE_BOTTOM_ANCHOR.get(), Config.RUTILE_TOP_ANCHOR.get(), Config.RUTILE_COUNT.get());
            }

            if (Config.ENABLE_GALENA_ORE.get()){
                ConfiguredFeature<?, ?> galenaOre = Feature.ORE
                        .configured(new OreConfiguration(replace.OVERWORLD, VEBlocks.GALENA_ORE.defaultBlockState(), Config.GALENA_SIZE.get()))
                        .decorated(FeatureDecorator.RANGE.configured(new RangeDecoratorConfiguration(UniformHeight.of(VerticalAnchor.absolute(Config.GALENA_BOTTOM_ANCHOR.get()), VerticalAnchor.absolute(Config.GALENA_TOP_ANCHOR.get())))))
                        //.decorated(FeatureDecorator.RANGE.configured(new RangeDecoratorConfiguration(Config.GALENA_BOTTOM_OFFSET.get(), Config.GALENA_HEIGHT_OFFSET.get(), Config.GALENA_MAXIMUM_HEIGHT.get())))
                        .squared()
                        .count(Config.GALENA_COUNT.get());

                biome.getGeneration().addFeature(GenerationStep.Decoration.UNDERGROUND_ORES, galenaOre);
                oreLog(VEBlocks.GALENA_ORE, biome, Config.GALENA_SIZE.get(), Config.GALENA_BOTTOM_ANCHOR.get(), Config.GALENA_TOP_ANCHOR.get(), Config.GALENA_COUNT.get());
            }

        }

    }

    public static void oreLog(Block block, BiomeLoadingEvent biome, int size, int bottomAnchor, int topAnchor, int count){
        if (Config.WORLD_GEN_LOGGING.get()) VoluminousEnergy.LOGGER.info(block.getRegistryName() + " registered to generate in: " + biome.getName() + " With Size: " + size + " Bottom Anchor: " + bottomAnchor + " Top Anchor: " + topAnchor + " Count: " + count);
    }

    public static final class replace { // These are rule tests to see if the block in the world (inside the biome) is valid to be replaced with the generated ore
        public static final RuleTest OVERWORLD = new TagMatchTest(BlockTags.BASE_STONE_OVERWORLD);
        public static final RuleTest SANDS = new MultiBlockStateMatchRuleTest(Blocks.SAND.defaultBlockState(), Blocks.RED_SAND.defaultBlockState());
        public static final RuleTest NETHER = new MultiBlockStateMatchRuleTest(Blocks.NETHERRACK.defaultBlockState(), Blocks.SOUL_SAND.defaultBlockState());
        public static final RuleTest TERRACOTTA = new MultiBlockStateMatchRuleTest(Blocks.TERRACOTTA.defaultBlockState(), Blocks.WHITE_TERRACOTTA.defaultBlockState(), Blocks.ORANGE_TERRACOTTA.defaultBlockState(), Blocks.MAGENTA_TERRACOTTA.defaultBlockState(), Blocks.LIGHT_BLUE_TERRACOTTA.defaultBlockState(), Blocks.YELLOW_TERRACOTTA.defaultBlockState(), Blocks.LIME_TERRACOTTA.defaultBlockState(), Blocks.PINK_TERRACOTTA.defaultBlockState(), Blocks.GRAY_TERRACOTTA.defaultBlockState(), Blocks.LIGHT_GRAY_TERRACOTTA.defaultBlockState(), Blocks.CYAN_TERRACOTTA.defaultBlockState(), Blocks.PURPLE_TERRACOTTA.defaultBlockState(), Blocks.BLUE_TERRACOTTA.defaultBlockState(), Blocks.BROWN_TERRACOTTA.defaultBlockState(), Blocks.GREEN_TERRACOTTA.defaultBlockState(), Blocks.RED_TERRACOTTA.defaultBlockState(), Blocks.BLACK_TERRACOTTA.defaultBlockState());
        public static final RuleTest END = new MultiBlockStateMatchRuleTest(Blocks.END_STONE.defaultBlockState());
    }
}
