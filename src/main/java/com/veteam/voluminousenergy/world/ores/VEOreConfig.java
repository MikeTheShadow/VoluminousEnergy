package com.veteam.voluminousenergy.world.ores;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.gen.feature.OreFeatureConfig;
import net.minecraft.world.gen.feature.template.BlockStateMatchRuleTest;
import net.minecraft.world.gen.feature.template.RuleTest;
import net.minecraft.world.gen.feature.template.TagMatchRuleTest;
import net.minecraftforge.common.Tags;

public class VEOreConfig extends OreFeatureConfig {

    public VEOreConfig(RuleTest p_i241989_1_, BlockState state, int size) {
        super(p_i241989_1_, state, size);
    }

    public static class fillerBlockType {
        public static RuleTest test = new TagMatchRuleTest(BlockTags.BASE_STONE_OVERWORLD);
        public static RuleTest SAND_TEST = new BlockStateMatchRuleTest(Blocks.SAND.getDefaultState());
    }
}

/*
public class VEOreConfig {
    private final int COUNT;
    private final int BOTTOM_OFFSET;
    private final int HEIGHT_OFFSET;
    private final int MAXIMUM_HEIGHT;

    public VEOreConfig(int count, int bottomOffset, int heightOffset, int maximumHeight) {
        this.COUNT = count;
        this.BOTTOM_OFFSET = bottomOffset;
        this.HEIGHT_OFFSET = heightOffset;
        this.MAXIMUM_HEIGHT = maximumHeight;
    }

    public int getCount() {
        return COUNT;
    }

    public int getBottomOffset() {
        return BOTTOM_OFFSET;
    }

    public int getHeightOffset() {
        return HEIGHT_OFFSET;
    }

    public int getMaximumHeight() {
        return MAXIMUM_HEIGHT;
    }
}
 */
