package com.veteam.voluminousenergy.world.feature;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.feature.BlockReplacement;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.material.FluidState;

import java.util.List;

/**
 * A lake-shaped pocket filled completely with fluid source blocks and sealed by a shell chosen per
 * position from the configuration's perimeter targets. It is not placed where the shell would meet
 * air or fluid, so the pocket never spills into a cave or aquifer.
 */
public record VEFluidDepositFeature(FluidState fluidState, List<BlockReplacement> perimeterTargets) implements Feature {
    public static final MapCodec<VEFluidDepositFeature> CODEC = VELakesFeature.CODEC.xmap(
            lake -> new VEFluidDepositFeature(lake.fluidState(), lake.perimeterTargets()),
            deposit -> new VELakesFeature(deposit.fluidState(), deposit.perimeterTargets()));

    @Override
    public MapCodec<VEFluidDepositFeature> codec() {
        return CODEC;
    }

    @Override
    public boolean place(WorldGenLevel level, ChunkGenerator generator, RandomSource random, BlockPos origin) {
        BlockPos corner = origin.below(4);
        LakeMask mask = LakeMask.generate(random);

        if (!canPlace(level, corner, mask)) {
            return false;
        }

        BlockState fluidBlock = fluidState.createLegacyBlock();
        for (int x = 0; x < LakeMask.WIDTH; ++x) {
            for (int z = 0; z < LakeMask.WIDTH; ++z) {
                for (int y = 0; y < LakeMask.HEIGHT; ++y) {
                    BlockPos pos = corner.offset(x, y, z);
                    if (mask.isInside(x, y, z)) {
                        level.setBlock(pos, fluidBlock, 2);
                    } else if (mask.isBorder(x, y, z)) {
                        BlockState shellState = LakeMask.perimeterState(perimeterTargets, level.getBlockState(pos), pos, random);
                        if (shellState != null) {
                            level.setBlock(pos, shellState, 2);
                        }
                    }
                }
            }
        }

        return true;
    }

    private static boolean canPlace(WorldGenLevel level, BlockPos corner, LakeMask mask) {
        for (int x = 0; x < LakeMask.WIDTH; ++x) {
            for (int z = 0; z < LakeMask.WIDTH; ++z) {
                for (int y = 0; y < LakeMask.HEIGHT; ++y) {
                    boolean shell = mask.isBorder(x, y, z);
                    if (!shell && !mask.isInside(x, y, z)) {
                        continue;
                    }

                    BlockState existing = level.getBlockState(corner.offset(x, y, z));
                    if (existing.is(BlockTags.FEATURES_CANNOT_REPLACE)) {
                        return false;
                    }

                    if (shell && (!existing.isSolid() || !existing.getFluidState().isEmpty())) {
                        return false;
                    }
                }
            }
        }
        return true;
    }
}
