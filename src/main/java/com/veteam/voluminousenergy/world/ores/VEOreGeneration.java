package com.veteam.voluminousenergy.world.ores;

import com.veteam.voluminousenergy.VoluminousEnergy;
import com.veteam.voluminousenergy.blocks.blocks.VEBlocks;
import com.veteam.voluminousenergy.tools.Config;
import com.veteam.voluminousenergy.util.MultiBlockStateMatchRuleTest;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.feature.configurations.OreConfiguration;
import net.minecraft.world.level.levelgen.structure.templatesystem.RuleTest;
import net.minecraft.world.level.levelgen.structure.templatesystem.TagMatchTest;
import net.minecraftforge.event.world.BiomeLoadingEvent;

import java.util.List;

public class VEOreGeneration {

    public static void OreGeneration(BiomeLoadingEvent biome){
        if (biome.getCategory() == Biome.BiomeCategory.NETHER){
            // Nether ores
        } else if (biome.getCategory() == Biome.BiomeCategory.THEEND){
            // End ores
            if(Config.ENABLE_EIGHZO_ORE_BLOBS.get()) {
                biome.getGeneration().addFeature(GenerationStep.Decoration.UNDERGROUND_ORES, Holder.direct(VEOres.EIGHZO_ORE_BLOB_PLACEMENT));
                oreLog(VEBlocks.EIGHZO_ORE, biome, Config.EIGHZO_ORE_BLOBS_SIZE.get(), Config.EIGHZO_ORE_BLOBS_BOTTOM_ANCHOR.get(), Config.EIGHZO_ORE_BLOBS_TOP_ANCHOR.get(), Config.EIGHZO_ORE_BLOBS_COUNT.get());
            }
        } else { // Assuming Overworld, catch all other biomes

            if (biome.getCategory() == Biome.BiomeCategory.DESERT || biome.getCategory() == Biome.BiomeCategory.MESA){
                // Desert and other non-Beach Sandy Biome Oregen goes here, generally for Sand-based ores
                if (Config.ENABLE_SALTPETER_ORE_BLOBS.get()){
                    biome.getGeneration().addFeature(GenerationStep.Decoration.UNDERGROUND_ORES, Holder.direct(VEOres.SALTPETER_ORE_BLOB_PLACEMENT));
                    oreLog(VEBlocks.SALTPETER_ORE, biome, Config.SALTPETER_ORE_BLOBS_SIZE.get(), Config.SALTPETER_ORE_BLOBS_BOTTOM_ANCHOR.get(), Config.SALTPETER_ORE_BLOBS_TOP_ANCHOR.get(), Config.BAUXITE_ORE_BLOBS_COUNT.get());
                }
            }

            if (Config.ENABLE_BAUXITE_ORE_BLOBS.get()){
                biome.getGeneration().addFeature(GenerationStep.Decoration.UNDERGROUND_ORES, Holder.direct(VEOres.BAUXITE_ORE_BLOB_PLACEMENT));
                oreLog(VEBlocks.BAUXITE_ORE, biome, Config.BAUXITE_ORE_BLOBS_SIZE.get(), Config.BAUXITE_ORE_BLOBS_BOTTOM_ANCHOR.get(), Config.BAUXITE_ORE_BLOBS_TOP_ANCHOR.get(), Config.BAUXITE_ORE_BLOBS_COUNT.get());
            }

            if (Config.ENABLE_CINNABAR_ORE_BLOBS.get()){
                biome.getGeneration().addFeature(GenerationStep.Decoration.UNDERGROUND_ORES, Holder.direct(VEOres.CINNABAR_ORE_BLOB_PLACEMENT));
                oreLog(VEBlocks.CINNABAR_ORE, biome, Config.CINNABAR_ORE_BLOBS_SIZE.get(), Config.CINNABAR_ORE_BLOBS_BOTTOM_ANCHOR.get(), Config.CINNABAR_ORE_BLOBS_TOP_ANCHOR.get(), Config.CINNABAR_ORE_BLOBS_COUNT.get());
            }

            if (Config.ENABLE_RUTILE_ORE_BLOBS.get()){
                biome.getGeneration().addFeature(GenerationStep.Decoration.UNDERGROUND_ORES, Holder.direct(VEOres.RUTILE_ORE_BLOB_PLACEMENT));
                oreLog(VEBlocks.RUTILE_ORE, biome, Config.RUTILE_ORE_BLOBS_SIZE.get(), Config.RUTILE_ORE_BLOBS_BOTTOM_ANCHOR.get(), Config.RUTILE_ORE_BLOBS_TOP_ANCHOR.get(), Config.RUTILE_ORE_BLOBS_COUNT.get());
            }

            if (Config.ENABLE_GALENA_ORE_BLOBS.get()){
                biome.getGeneration().addFeature(GenerationStep.Decoration.UNDERGROUND_ORES, Holder.direct(VEOres.GALENA_ORE_BLOB_PLACEMENT));
                oreLog(VEBlocks.GALENA_ORE, biome, Config.GALENA_ORE_BLOBS_SIZE.get(), Config.GALENA_ORE_BLOBS_BOTTOM_ANCHOR.get(), Config.GALENA_ORE_BLOBS_TOP_ANCHOR.get(), Config.GALENA_ORE_BLOBS_COUNT.get());
            }

        }

    }

    public static class OreWithTargetStatesToReplace {
        public static final List<OreConfiguration.TargetBlockState> SALTPETER_ORE_TARGETS = List.of(
                OreConfiguration.target(ReplacementRules.COLORLESS_SAND, VEBlocks.SALTPETER_ORE.defaultBlockState()),
                OreConfiguration.target(ReplacementRules.RED_SAND, VEBlocks.RED_SALTPETER_ORE.defaultBlockState())
        );

        public static final List<OreConfiguration.TargetBlockState> BAUXITE_ORE_TARGETS = List.of(
                OreConfiguration.target(ReplacementRules.REGULAR_STONE, VEBlocks.BAUXITE_ORE.defaultBlockState()),
                OreConfiguration.target(ReplacementRules.DEEPSLATE_STONE, VEBlocks.DEEPSLATE_BAUXITE_ORE.defaultBlockState())
        );

        public static final List<OreConfiguration.TargetBlockState> CINNABAR_ORE_TARGETS = List.of(
                OreConfiguration.target(ReplacementRules.REGULAR_STONE, VEBlocks.CINNABAR_ORE.defaultBlockState()),
                OreConfiguration.target(ReplacementRules.DEEPSLATE_STONE, VEBlocks.DEEPSLATE_CINNABAR_ORE.defaultBlockState())
        );

        public static final List<OreConfiguration.TargetBlockState> GALENA_ORE_TARGETS = List.of(
                OreConfiguration.target(ReplacementRules.REGULAR_STONE, VEBlocks.GALENA_ORE.defaultBlockState()),
                OreConfiguration.target(ReplacementRules.DEEPSLATE_STONE, VEBlocks.DEEPSLATE_GALENA_ORE.defaultBlockState())
        );

        public static final List<OreConfiguration.TargetBlockState> RUTILE_ORE_TARGETS = List.of(
                OreConfiguration.target(ReplacementRules.REGULAR_STONE, VEBlocks.RUTILE_ORE.defaultBlockState()),
                OreConfiguration.target(ReplacementRules.DEEPSLATE_STONE, VEBlocks.DEEPSLATE_RUTILE_ORE.defaultBlockState())
        );

        public static final List<OreConfiguration.TargetBlockState> EIGHZO_ORE_TARGETS = List.of(
                OreConfiguration.target(ReplacementRules.END, VEBlocks.EIGHZO_ORE.defaultBlockState())
        );

    }

    public static void oreLog(Block block, BiomeLoadingEvent biome, int size, int bottomAnchor, int topAnchor, int count){
        if (Config.WORLD_GEN_LOGGING.get()) VoluminousEnergy.LOGGER.info(block.getRegistryName() + " registered to generate in: " + biome.getName() + " With Size: " + size + " Bottom Anchor: " + bottomAnchor + " Top Anchor: " + topAnchor + " Count: " + count);
    }

    public static final class ReplacementRules { // These are rule tests to see if the block in the world (inside the biome) is valid to be replaced with the generated ore
        public static final RuleTest REGULAR_STONE = new TagMatchTest(BlockTags.STONE_ORE_REPLACEABLES);
        public static final RuleTest DEEPSLATE_STONE = new TagMatchTest(BlockTags.DEEPSLATE_ORE_REPLACEABLES);

        public static final RuleTest COLORLESS_SAND = createRuleFromTag("forge:ore_bearing_ground/sand/colorless_sand");
        public static final RuleTest RED_SAND = createRuleFromTag("forge:ore_bearing_ground/sand/red_sand");
        public static final RuleTest NETHER = createRuleFromTag("forge:ore_bearing_ground/netherrack");
        public static final RuleTest END = createRuleFromTag("forge:ore_bearing_ground/end_stone");

        // WARN: Not tag based
        public static final RuleTest TERRACOTTA = new MultiBlockStateMatchRuleTest(Blocks.TERRACOTTA.defaultBlockState(), Blocks.WHITE_TERRACOTTA.defaultBlockState(), Blocks.ORANGE_TERRACOTTA.defaultBlockState(), Blocks.MAGENTA_TERRACOTTA.defaultBlockState(), Blocks.LIGHT_BLUE_TERRACOTTA.defaultBlockState(), Blocks.YELLOW_TERRACOTTA.defaultBlockState(), Blocks.LIME_TERRACOTTA.defaultBlockState(), Blocks.PINK_TERRACOTTA.defaultBlockState(), Blocks.GRAY_TERRACOTTA.defaultBlockState(), Blocks.LIGHT_GRAY_TERRACOTTA.defaultBlockState(), Blocks.CYAN_TERRACOTTA.defaultBlockState(), Blocks.PURPLE_TERRACOTTA.defaultBlockState(), Blocks.BLUE_TERRACOTTA.defaultBlockState(), Blocks.BROWN_TERRACOTTA.defaultBlockState(), Blocks.GREEN_TERRACOTTA.defaultBlockState(), Blocks.RED_TERRACOTTA.defaultBlockState(), Blocks.BLACK_TERRACOTTA.defaultBlockState());

        public static RuleTest createRuleFromTag(String blockTagLocation){
            TagKey<Block> blockTag = TagKey.create(Registry.BLOCK_REGISTRY, new ResourceLocation(blockTagLocation));
            return new TagMatchTest(blockTag);
        }
    }
}