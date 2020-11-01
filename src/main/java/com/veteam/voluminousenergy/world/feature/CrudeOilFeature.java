package com.veteam.voluminousenergy.world.feature;

import com.mojang.serialization.Codec;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ISeedReader;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.feature.BlockStateFeatureConfig;

import java.util.Random;

public class CrudeOilFeature extends VELakesFeature {
    public static CrudeOilFeature INSTANCE = new CrudeOilFeature(BlockStateFeatureConfig.field_236455_a_);

    public CrudeOilFeature(Codec<BlockStateFeatureConfig> p_i231962_1_) {
        super(p_i231962_1_);
    }

    @Override
    public boolean generate(ISeedReader worldIn, ChunkGenerator generator, Random rand, BlockPos pos, BlockStateFeatureConfig conf){

        if (rand.nextDouble() < 0.1) return super.generate(worldIn, generator, rand, pos, conf);

        return super.generate(worldIn, generator, rand, new BlockPos(pos.getX(), rand.nextInt(32) + 16, pos.getZ()), conf); // Should place between 32 and 48
    }
}
