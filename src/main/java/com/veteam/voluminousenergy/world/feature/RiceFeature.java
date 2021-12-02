package com.veteam.voluminousenergy.world.feature;

import com.mojang.serialization.Codec;
import com.veteam.voluminousenergy.blocks.blocks.VEBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.BlockStateConfiguration;

public class RiceFeature extends Feature<BlockStateConfiguration>  {
    public static RiceFeature INSTANCE = new RiceFeature(BlockStateConfiguration.CODEC);

    public RiceFeature(Codec<BlockStateConfiguration> codec) {
        super(codec);
    }

    @Override
    public boolean place(FeaturePlaceContext<BlockStateConfiguration> context) {
        BlockPos pos = context.origin();
        WorldGenLevel worldIn = context.level();

        if(!worldIn.canSeeSky(pos)) return false;

        if (worldIn.isWaterAt(pos.below()) && worldIn.isEmptyBlock(pos)){
            generateRice(worldIn, pos);
            return true;
        }

        return false;
    }

    public void generateRice(WorldGenLevel worldIn, BlockPos pos){

        worldIn.setBlock(pos, VEBlocks.RICE_CROP.defaultBlockState(), 2);
        worldIn.setBlock(
                pos.below(),
                VEBlocks.RICE_CROP.defaultBlockState()
                        .setValue(BlockStateProperties.WATERLOGGED, true)
                        .setValue(BlockStateProperties.DOUBLE_BLOCK_HALF, DoubleBlockHalf.LOWER),
                2);
    }

}
