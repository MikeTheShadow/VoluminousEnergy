package com.veteam.voluminousenergy.blocks.blocks.ores;

import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.extensions.IForgeBlock;

public class VEOreBlock extends Block implements IForgeBlock {
    public VEOreBlock(BlockBehaviour.Properties properties) {
        super(properties);
    }

    protected int xpOnDrop(RandomSource randomSource) {
        return Mth.nextInt(randomSource, 2, 5);
    }

    @Override
    public int getExpDrop(BlockState state, net.minecraft.world.level.LevelReader reader, RandomSource randomSource, BlockPos pos, int fortune, int silktouch) {
        return silktouch == 0 ? this.xpOnDrop(randomSource)*(1+fortune) : 0;
    }
}
