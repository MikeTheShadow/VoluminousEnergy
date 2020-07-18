package com.veteam.voluminousenergy.world.feature;

import com.mojang.datafixers.Dynamic;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.GenerationSettings;
import net.minecraft.world.gen.feature.BlockStateFeatureConfig;

import java.util.Random;
import java.util.function.Function;

public class CrudeOilFeature extends VELakesFeature {
    public static CrudeOilFeature INSTANCE = new CrudeOilFeature(BlockStateFeatureConfig::deserialize);

    public CrudeOilFeature(Function<Dynamic<?>, ? extends BlockStateFeatureConfig> p_i51485_1_) {
        super(p_i51485_1_);
    }

    @Override
    public boolean place(IWorld worldIn, ChunkGenerator<? extends GenerationSettings> generator, Random rand, BlockPos pos, BlockStateFeatureConfig conf){

        int r = rand.nextInt(rand.nextInt(generator.getMaxHeight() - 8) + 8);
        if(r >= 12){
            return super.place(worldIn, generator, rand, pos, conf);
        }

        return super.place(worldIn, generator, rand, new BlockPos(pos.getX(), rand.nextInt(32) + 16, pos.getZ()), conf); // Should place between 32 and 48
    }
}
