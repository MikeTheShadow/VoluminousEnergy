package com.veteam.voluminousenergy.world.feature;

import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.configurations.OreConfiguration;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * The blob of overlapping ellipsoids that pre-1.18 vanilla lakes were carved from, in a box of
 * {@link #WIDTH} by {@link #HEIGHT} by {@link #WIDTH} relative to the feature's corner. A border
 * position is one outside the blob that shares a face with it.
 */
public class LakeMask {
    public static final int WIDTH = 16;
    public static final int HEIGHT = 8;

    private final boolean[] inside = new boolean[WIDTH * WIDTH * HEIGHT];

    private LakeMask() {
    }

    public static LakeMask generate(RandomSource random) {
        LakeMask mask = new LakeMask();
        int ellipsoidCount = random.nextInt(4) + 4;

        for (int ellipsoid = 0; ellipsoid < ellipsoidCount; ++ellipsoid) {
            double sizeX = random.nextDouble() * 6.0D + 3.0D;
            double sizeY = random.nextDouble() * 4.0D + 2.0D;
            double sizeZ = random.nextDouble() * 6.0D + 3.0D;
            double centreX = random.nextDouble() * (16.0D - sizeX - 2.0D) + 1.0D + sizeX / 2.0D;
            double centreY = random.nextDouble() * (8.0D - sizeY - 4.0D) + 2.0D + sizeY / 2.0D;
            double centreZ = random.nextDouble() * (16.0D - sizeZ - 2.0D) + 1.0D + sizeZ / 2.0D;

            for (int x = 1; x < WIDTH - 1; ++x) {
                for (int z = 1; z < WIDTH - 1; ++z) {
                    for (int y = 1; y < HEIGHT - 1; ++y) {
                        double normalX = (x - centreX) / (sizeX / 2.0D);
                        double normalY = (y - centreY) / (sizeY / 2.0D);
                        double normalZ = (z - centreZ) / (sizeZ / 2.0D);
                        if (normalX * normalX + normalY * normalY + normalZ * normalZ < 1.0D) {
                            mask.inside[index(x, y, z)] = true;
                        }
                    }
                }
            }
        }

        return mask;
    }

    public boolean isInside(int x, int y, int z) {
        if (x < 0 || x >= WIDTH || y < 0 || y >= HEIGHT || z < 0 || z >= WIDTH) {
            return false;
        }
        return inside[index(x, y, z)];
    }

    public boolean isBorder(int x, int y, int z) {
        return !isInside(x, y, z)
                && (isInside(x + 1, y, z) || isInside(x - 1, y, z)
                || isInside(x, y + 1, z) || isInside(x, y - 1, z)
                || isInside(x, y, z + 1) || isInside(x, y, z - 1));
    }

    /**
     * Returns the state of the first target whose rule matches {@code existing}, or null when none
     * does and the existing block should be kept.
     */
    @Nullable
    public static BlockState perimeterState(List<OreConfiguration.TargetBlockState> targets, BlockState existing, RandomSource random) {
        for (OreConfiguration.TargetBlockState target : targets) {
            if (target.target.test(existing, random)) {
                return target.state;
            }
        }
        return null;
    }

    private static int index(int x, int y, int z) {
        return (x * WIDTH + z) * HEIGHT + y;
    }
}
