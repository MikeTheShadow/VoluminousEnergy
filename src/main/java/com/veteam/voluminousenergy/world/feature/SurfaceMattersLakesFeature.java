package com.veteam.voluminousenergy.world.feature;

import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.BlockStateConfiguration;

import java.util.Random;

public class SurfaceMattersLakesFeature extends VELakesFeature {

    private final boolean isForSurface;

    public SurfaceMattersLakesFeature(Codec<BlockStateConfiguration> codec, boolean forSurface) {
        super(codec);
        this.isForSurface = forSurface;
    }

    @Override
    public boolean place(FeaturePlaceContext<BlockStateConfiguration> context) {
        BlockPos pos = context.origin();
        WorldGenLevel worldIn = context.level();
        Random rand = context.random();
        ChunkGenerator generator = context.chunkGenerator();
        BlockStateConfiguration conf = context.config();

        if (worldIn.canSeeSky(pos) && this.isForSurface) return super.place(context);

        return !this.isForSurface && super.place(worldIn, generator, rand, new BlockPos(pos.getX(), rand.nextInt(32) + 16, pos.getZ()), conf); // Should place between 32 and 48
    }
}
