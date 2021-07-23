package com.veteam.voluminousenergy.blocks.blocks.ores;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.OreBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;

import java.util.Random;

import net.minecraft.world.level.block.state.BlockBehaviour.Properties;

public class VEOreBlock extends OreBlock {
    public VEOreBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected int xpOnDrop(Random rand) {
        return Mth.nextInt(rand, 2, 5);
    }

    @Override
    public int getExpDrop(BlockState state, net.minecraft.world.level.LevelReader reader, BlockPos pos, int fortune, int silktouch) {
        return silktouch == 0 ? this.xpOnDrop(RANDOM)*(1+fortune) : 0;
    }
}
