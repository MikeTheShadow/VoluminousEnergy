package com.veteam.voluminousenergy.blocks.blocks.ores;

import net.minecraft.block.BlockState;
import net.minecraft.block.OreBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;

import java.util.Random;

public class VEOreBlock extends OreBlock {
    public VEOreBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected int getExperience(Random rand) {
        return MathHelper.nextInt(rand, 2, 5);
    }

    @Override
    public int getExpDrop(BlockState state, net.minecraft.world.IWorldReader reader, BlockPos pos, int fortune, int silktouch) {
        return silktouch == 0 ? this.getExperience(RANDOM)*(1+fortune) : 0;
    }
}
