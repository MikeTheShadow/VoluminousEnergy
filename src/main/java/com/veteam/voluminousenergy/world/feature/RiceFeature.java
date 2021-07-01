package com.veteam.voluminousenergy.world.feature;

import com.mojang.serialization.Codec;
import com.veteam.voluminousenergy.blocks.blocks.VEBlocks;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.state.properties.DoubleBlockHalf;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ISeedReader;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.feature.BlockStateFeatureConfig;
import net.minecraft.world.gen.feature.Feature;

import java.util.Random;

public class RiceFeature extends Feature<BlockStateFeatureConfig>  {
    public static RiceFeature INSTANCE = new RiceFeature(BlockStateFeatureConfig.CODEC);

    public RiceFeature(Codec<BlockStateFeatureConfig> codec) {
        super(codec);
    }

    @Override
    public boolean place(ISeedReader worldIn, ChunkGenerator generator, Random rand, BlockPos pos, BlockStateFeatureConfig conf) {
        if(!worldIn.canSeeSky(pos)) return false;

        if (worldIn.isWaterAt(pos.below()) && worldIn.isEmptyBlock(pos)){
            generateRice(worldIn, pos);
            return true;
        }

        return false;
    }

    public void generateRice(ISeedReader worldIn, BlockPos pos){

        worldIn.setBlock(pos, VEBlocks.RICE_CROP.defaultBlockState(), 2);
        worldIn.setBlock(
                pos.below(),
                VEBlocks.RICE_CROP.defaultBlockState()
                        .setValue(BlockStateProperties.WATERLOGGED, true)
                        .setValue(BlockStateProperties.DOUBLE_BLOCK_HALF, DoubleBlockHalf.LOWER),
                2);
    }

}
