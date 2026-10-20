package com.veteam.voluminousenergy.world.feature;

import com.mojang.serialization.MapCodec;
import com.veteam.voluminousenergy.blocks.blocks.VEBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.feature.Feature;

public record RiceFeature(BlockState state) implements Feature {
    public static final MapCodec<RiceFeature> CODEC = BlockState.CODEC.fieldOf("state").xmap(RiceFeature::new, RiceFeature::state);

    @Override
    public MapCodec<RiceFeature> codec() {
        return CODEC;
    }

    @Override
    public boolean place(WorldGenLevel worldIn, ChunkGenerator generator, RandomSource random, BlockPos pos) {
        if (worldIn.isWaterAt(pos.below()) && worldIn.getBlockState(pos).isAir()) {
            generateRice(worldIn, pos);
            return true;
        }

        return false;
    }

    public void generateRice(WorldGenLevel worldIn, BlockPos pos) {

        worldIn.setBlock(pos, VEBlocks.RICE_CROP.get().defaultBlockState(), 2);
        worldIn.setBlock(
                pos.below(),
                VEBlocks.RICE_CROP.get().defaultBlockState()
                        .setValue(BlockStateProperties.WATERLOGGED, true)
                        .setValue(BlockStateProperties.DOUBLE_BLOCK_HALF, DoubleBlockHalf.LOWER),
                2);
    }

}
