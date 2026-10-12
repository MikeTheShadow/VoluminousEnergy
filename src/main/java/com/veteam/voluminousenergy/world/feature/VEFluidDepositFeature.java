package com.veteam.voluminousenergy.world.feature;

import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;

/**
 * A lake-shaped pocket filled completely with fluid source blocks and sealed by a shell chosen per
 * position from the configuration's perimeter targets. It is not placed where the shell would meet
 * air or fluid, so the pocket never spills into a cave or aquifer.
 */
public class VEFluidDepositFeature extends Feature<VELakesFeature.Configuration> {
    public VEFluidDepositFeature(Codec<VELakesFeature.Configuration> codec) {
        super(codec);
    }

    @Override
    public boolean place(FeaturePlaceContext<VELakesFeature.Configuration> context) {
        WorldGenLevel level = context.level();
        RandomSource random = context.random();
        VELakesFeature.Configuration config = context.config();
        BlockPos corner = context.origin().below(4);
        LakeMask mask = LakeMask.generate(random);

        if (!canPlace(level, corner, mask)) {
            return false;
        }

        BlockState fluidBlock = config.fluidState().createLegacyBlock();
        for (int x = 0; x < LakeMask.WIDTH; ++x) {
            for (int z = 0; z < LakeMask.WIDTH; ++z) {
                for (int y = 0; y < LakeMask.HEIGHT; ++y) {
                    BlockPos pos = corner.offset(x, y, z);
                    if (mask.isInside(x, y, z)) {
                        level.setBlock(pos, fluidBlock, 2);
                    } else if (mask.isBorder(x, y, z)) {
                        BlockState shellState = LakeMask.perimeterState(config.perimeterTargets(), level.getBlockState(pos), random);
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
