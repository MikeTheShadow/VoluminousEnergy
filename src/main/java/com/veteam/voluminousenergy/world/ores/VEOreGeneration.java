package com.veteam.voluminousenergy.world.ores;

import com.veteam.voluminousenergy.blocks.blocks.VEBlocks;
import com.veteam.voluminousenergy.util.MultiBlockStateMatchRuleTest;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.feature.configurations.OreConfiguration;
import net.minecraft.world.level.levelgen.structure.templatesystem.RuleTest;
import net.minecraft.world.level.levelgen.structure.templatesystem.TagMatchTest;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.List;

public class VEOreGeneration {

    public static class OreWithTargetStatesToReplace {
        public static final List<OreConfiguration.TargetBlockState> SALTPETER_ORE_TARGETS = List.of(
                OreConfiguration.target(ReplacementRules.COLORLESS_SAND, VEBlocks.SALTPETER_ORE.get().defaultBlockState()),
                OreConfiguration.target(ReplacementRules.RED_SAND, VEBlocks.RED_SALTPETER_ORE.get().defaultBlockState())
        );

        public static final List<OreConfiguration.TargetBlockState> BAUXITE_ORE_TARGETS = List.of(
                OreConfiguration.target(ReplacementRules.REGULAR_STONE, VEBlocks.BAUXITE_ORE.get().defaultBlockState()),
                OreConfiguration.target(ReplacementRules.DEEPSLATE_STONE, VEBlocks.DEEPSLATE_BAUXITE_ORE.get().defaultBlockState())
        );

        public static final List<OreConfiguration.TargetBlockState> CINNABAR_ORE_TARGETS = List.of(
                OreConfiguration.target(ReplacementRules.REGULAR_STONE, VEBlocks.CINNABAR_ORE.get().defaultBlockState()),
                OreConfiguration.target(ReplacementRules.DEEPSLATE_STONE, VEBlocks.DEEPSLATE_CINNABAR_ORE.get().defaultBlockState())
        );

        public static final List<OreConfiguration.TargetBlockState> GALENA_ORE_TARGETS = List.of(
                OreConfiguration.target(ReplacementRules.REGULAR_STONE, VEBlocks.GALENA_ORE.get().defaultBlockState()),
                OreConfiguration.target(ReplacementRules.DEEPSLATE_STONE, VEBlocks.DEEPSLATE_GALENA_ORE.get().defaultBlockState())
        );

        public static final List<OreConfiguration.TargetBlockState> RUTILE_ORE_TARGETS = List.of(
                OreConfiguration.target(ReplacementRules.REGULAR_STONE, VEBlocks.RUTILE_ORE.get().defaultBlockState()),
                OreConfiguration.target(ReplacementRules.DEEPSLATE_STONE, VEBlocks.DEEPSLATE_RUTILE_ORE.get().defaultBlockState())
        );

        public static final List<OreConfiguration.TargetBlockState> EIGHZO_ORE_TARGETS = List.of(
                OreConfiguration.target(ReplacementRules.END, VEBlocks.EIGHZO_ORE.get().defaultBlockState())
        );

    }

    //public static void oreLog(Block block, BiomeLoadingEvent biome, int size, int bottomAnchor, int topAnchor, int amount){
    //    if (Config.WORLD_GEN_LOGGING.get()) VoluminousEnergy.LOGGER.info(block.getRegistryName() + " registered to generate in: " + biome.getName() + " With Size: " + size + " Bottom Anchor: " + bottomAnchor + " Top Anchor: " + topAnchor + " Count: " + amount);
    //}

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
            TagKey<Block> blockTag = TagKey.create(ForgeRegistries.BLOCKS.getRegistryKey(), new ResourceLocation(blockTagLocation));
            return new TagMatchTest(blockTag);
        }
    }
}