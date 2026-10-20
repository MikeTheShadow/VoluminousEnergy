package com.veteam.voluminousenergy.world.feature;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.feature.Feature;

/**
 * A {@link VELakesFeature} that is only placed where its origin can see the sky.
 */
public record SurfaceMattersLakesFeature(VELakesFeature lake) implements Feature {
    public static final MapCodec<SurfaceMattersLakesFeature> CODEC = VELakesFeature.CODEC.xmap(SurfaceMattersLakesFeature::new, SurfaceMattersLakesFeature::lake);

    @Override
    public MapCodec<SurfaceMattersLakesFeature> codec() {
        return CODEC;
    }

    @Override
    public boolean place(WorldGenLevel level, ChunkGenerator generator, RandomSource random, BlockPos origin) {
        return level.canSeeSky(origin) && lake.place(level, generator, random, origin);
    }
}
