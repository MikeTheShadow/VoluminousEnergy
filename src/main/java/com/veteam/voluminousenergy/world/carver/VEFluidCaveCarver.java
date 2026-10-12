package com.veteam.voluminousenergy.world.carver;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.veteam.voluminousenergy.blocks.blocks.VEBlocks;
import com.veteam.voluminousenergy.world.feature.LakeMask;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.SectionPos;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.util.valueproviders.FloatProvider;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.CarvingMask;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.levelgen.Aquifer;
import net.minecraft.world.level.levelgen.carver.CarverConfiguration;
import net.minecraft.world.level.levelgen.carver.CarvingContext;
import net.minecraft.world.level.levelgen.carver.WorldCarver;
import net.minecraft.world.level.levelgen.feature.configurations.OreConfiguration;
import net.minecraft.world.level.levelgen.structure.templatesystem.AlwaysTrueTest;
import net.minecraft.world.level.material.FluidState;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

/**
 * A small cave system filled entirely with one fluid's source blocks and sealed by a shell chosen per
 * position from the configuration's perimeter targets. Every chunk traces the same outline from the
 * start chunk's seed, and the shell is decided from that outline alone, so chunks agree on the fill
 * and the shell without reading each other.
 */
public class VEFluidCaveCarver extends WorldCarver<VEFluidCaveCarver.Configuration> {
    public VEFluidCaveCarver(Codec<Configuration> codec) {
        super(codec);
    }

    @Override
    public int getRange() {
        return 2;
    }

    @Override
    public boolean isStartChunk(Configuration config, RandomSource random) {
        return random.nextFloat() <= config.probability;
    }

    @Override
    public boolean carve(CarvingContext context, Configuration config, ChunkAccess chunk, Function<BlockPos, Holder<Biome>> biomeAccessor,
                         RandomSource random, Aquifer aquifer, ChunkPos startChunk, CarvingMask carvingMask) {
        List<Ellipsoid> outline = traceOutline(context, config, random, startChunk);
        OutlineGrid grid = OutlineGrid.of(outline, chunk.getPos(), context);
        if (grid == null) {
            return false;
        }

        fill(config, chunk, carvingMask, grid);
        seal(config, chunk, random, grid);
        return true;
    }

    private List<Ellipsoid> traceOutline(CarvingContext context, Configuration config, RandomSource random, ChunkPos startChunk) {
        List<Ellipsoid> outline = new ArrayList<>();
        int maxTunnelLength = SectionPos.sectionToBlockCoord(this.getRange() * 2 - 1);
        double startX = startChunk.getBlockX(random.nextInt(16));
        double startY = config.y.sample(random, context);
        double startZ = startChunk.getBlockZ(random.nextInt(16));
        TunnelShape shape = new TunnelShape(
                config.horizontalRadiusMultiplier.sample(random),
                config.verticalRadiusMultiplier.sample(random),
                config.floorLevel.sample(random)
        );

        int tunnelCount = 1;
        if (random.nextInt(4) == 0) {
            double roomYScale = config.yScale.sample(random);
            double roomRadius = 1.5D + (1.0F + random.nextFloat() * 6.0F);
            outline.add(new Ellipsoid(startX + 1.0D, startY, startZ, roomRadius, roomRadius * roomYScale, shape.floorLevel()));
            tunnelCount += random.nextInt(3);
        }

        for (int tunnel = 0; tunnel < tunnelCount; ++tunnel) {
            float yaw = random.nextFloat() * Mth.TWO_PI;
            float pitch = (random.nextFloat() - 0.5F) / 4.0F;
            float thickness = tunnelThickness(random);
            int length = maxTunnelLength - random.nextInt(maxTunnelLength / 4);
            traceTunnel(outline, shape, random.nextLong(), startX, startY, startZ, thickness, yaw, pitch, 0, length);
        }

        return outline;
    }

    private static float tunnelThickness(RandomSource random) {
        float thickness = random.nextFloat() * 2.0F + random.nextFloat();
        if (random.nextInt(10) == 0) {
            thickness *= random.nextFloat() * random.nextFloat() * 3.0F + 1.0F;
        }
        return thickness;
    }

    private static void traceTunnel(List<Ellipsoid> outline, TunnelShape shape, long seed, double x, double y, double z,
                                    float thickness, float yaw, float pitch, int firstStep, int length) {
        RandomSource random = RandomSource.create(seed);
        int branchStep = random.nextInt(length / 2) + length / 4;
        boolean steep = random.nextInt(6) == 0;
        float yawChange = 0.0F;
        float pitchChange = 0.0F;

        for (int step = firstStep; step < length; ++step) {
            double radius = 1.5D + Mth.sin(Mth.PI * (float) step / (float) length) * thickness;
            float horizontalPitch = Mth.cos(pitch);
            x += Mth.cos(yaw) * horizontalPitch;
            y += Mth.sin(pitch);
            z += Mth.sin(yaw) * horizontalPitch;
            pitch *= steep ? 0.92F : 0.7F;
            pitch += pitchChange * 0.1F;
            yaw += yawChange * 0.1F;
            pitchChange *= 0.9F;
            yawChange *= 0.75F;
            pitchChange += (random.nextFloat() - random.nextFloat()) * random.nextFloat() * 2.0F;
            yawChange += (random.nextFloat() - random.nextFloat()) * random.nextFloat() * 4.0F;

            if (step == branchStep && thickness > 1.0F) {
                traceTunnel(outline, shape, random.nextLong(), x, y, z, random.nextFloat() * 0.5F + 0.5F, yaw - Mth.HALF_PI, pitch / 3.0F, step, length);
                traceTunnel(outline, shape, random.nextLong(), x, y, z, random.nextFloat() * 0.5F + 0.5F, yaw + Mth.HALF_PI, pitch / 3.0F, step, length);
                return;
            }

            if (random.nextInt(4) != 0) {
                outline.add(new Ellipsoid(x, y, z, radius * shape.horizontalMultiplier(), radius * shape.verticalMultiplier(), shape.floorLevel()));
            }
        }
    }

    private static void fill(Configuration config, ChunkAccess chunk, CarvingMask carvingMask, OutlineGrid grid) {
        BlockState fluidBlock = config.fluidState.createLegacyBlock();
        BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();
        int chunkMinX = chunk.getPos().getMinBlockX();
        int chunkMinZ = chunk.getPos().getMinBlockZ();

        for (int localX = 0; localX < 16; ++localX) {
            for (int localZ = 0; localZ < 16; ++localZ) {
                for (int y = grid.minY(); y <= grid.maxY(); ++y) {
                    if (!grid.isInside(chunkMinX + localX, y, chunkMinZ + localZ)) {
                        continue;
                    }

                    pos.set(chunkMinX + localX, y, chunkMinZ + localZ);
                    if (canFill(config, chunk.getBlockState(pos))) {
                        chunk.setBlockState(pos, fluidBlock, false);
                        carvingMask.set(localX, y, localZ);
                    }
                }
            }
        }
    }

    private static boolean canFill(Configuration config, BlockState existing) {
        return existing.is(config.replaceable)
                || existing.is(Blocks.AIR)
                || existing.is(Blocks.CAVE_AIR)
                || existing.getFluidState().is(FluidTags.WATER);
    }

    private static void seal(Configuration config, ChunkAccess chunk, RandomSource random, OutlineGrid grid) {
        BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();
        int chunkMinX = chunk.getPos().getMinBlockX();
        int chunkMinZ = chunk.getPos().getMinBlockZ();

        for (int x = chunkMinX; x < chunkMinX + 16; ++x) {
            for (int z = chunkMinZ; z < chunkMinZ + 16; ++z) {
                for (int y = grid.minY() - 1; y <= grid.maxY() + 1; ++y) {
                    if (!grid.isShell(x, y, z)) {
                        continue;
                    }

                    pos.set(x, y, z);
                    BlockState existing = chunk.getBlockState(pos);
                    if (existing.is(BlockTags.FEATURES_CANNOT_REPLACE)) {
                        continue;
                    }

                    BlockState shellState = LakeMask.perimeterState(config.perimeterTargets, existing, random);
                    if (shellState != null) {
                        chunk.setBlockState(pos, shellState, false);
                    }
                }
            }
        }
    }

    private record TunnelShape(double horizontalMultiplier, double verticalMultiplier, double floorLevel) {
    }

    /**
     * One step of a tunnel, using vanilla's cave ellipsoid test: block centres on the horizontal axes,
     * the block's lower half on the vertical axis, and nothing below the floor level.
     */
    private record Ellipsoid(double centreX, double centreY, double centreZ, double horizontalRadius, double verticalRadius, double floorLevel) {
        boolean contains(int x, int y, int z) {
            double normalX = (x + 0.5D - centreX) / horizontalRadius;
            double normalY = (y - 0.5D - centreY) / verticalRadius;
            double normalZ = (z + 0.5D - centreZ) / horizontalRadius;
            return normalY > floorLevel && normalX * normalX + normalY * normalY + normalZ * normalZ < 1.0D;
        }

        int minX() {
            return Mth.floor(centreX - horizontalRadius) - 1;
        }

        int maxX() {
            return Mth.floor(centreX + horizontalRadius) + 1;
        }

        int minY() {
            return Mth.floor(centreY - verticalRadius) - 1;
        }

        int maxY() {
            return Mth.floor(centreY + verticalRadius) + 1;
        }

        int minZ() {
            return Mth.floor(centreZ - horizontalRadius) - 1;
        }

        int maxZ() {
            return Mth.floor(centreZ + horizontalRadius) + 1;
        }
    }

    /**
     * Which positions of one chunk, plus a one-block ring around it, lie inside the outline. The ring
     * lets the shell test see interior positions across the chunk border.
     */
    private static class OutlineGrid {
        private static final int WIDTH = 18;

        private final int originX;
        private final int originZ;
        private final int minY;
        private final int maxY;
        private final boolean[] inside;

        private OutlineGrid(int originX, int originZ, int minY, int maxY) {
            this.originX = originX;
            this.originZ = originZ;
            this.minY = minY;
            this.maxY = maxY;
            this.inside = new boolean[WIDTH * WIDTH * (maxY - minY + 1)];
        }

        @Nullable
        static OutlineGrid of(List<Ellipsoid> outline, ChunkPos chunkPos, CarvingContext context) {
            int originX = chunkPos.getMinBlockX() - 1;
            int originZ = chunkPos.getMinBlockZ() - 1;
            int lowestY = context.getMinGenY() + 2;
            int highestY = context.getMinGenY() + context.getGenDepth() - 8;

            List<Ellipsoid> nearby = new ArrayList<>();
            int minY = Integer.MAX_VALUE;
            int maxY = Integer.MIN_VALUE;
            for (Ellipsoid ellipsoid : outline) {
                if (ellipsoid.maxX() < originX || ellipsoid.minX() >= originX + WIDTH
                        || ellipsoid.maxZ() < originZ || ellipsoid.minZ() >= originZ + WIDTH) {
                    continue;
                }
                nearby.add(ellipsoid);
                minY = Math.min(minY, Math.max(ellipsoid.minY(), lowestY));
                maxY = Math.max(maxY, Math.min(ellipsoid.maxY(), highestY));
            }

            if (nearby.isEmpty() || minY > maxY) {
                return null;
            }

            OutlineGrid grid = new OutlineGrid(originX, originZ, minY, maxY);
            for (Ellipsoid ellipsoid : nearby) {
                grid.mark(ellipsoid);
            }
            return grid;
        }

        private void mark(Ellipsoid ellipsoid) {
            int fromX = Math.max(ellipsoid.minX(), originX);
            int toX = Math.min(ellipsoid.maxX(), originX + WIDTH - 1);
            int fromZ = Math.max(ellipsoid.minZ(), originZ);
            int toZ = Math.min(ellipsoid.maxZ(), originZ + WIDTH - 1);
            int fromY = Math.max(ellipsoid.minY(), minY);
            int toY = Math.min(ellipsoid.maxY(), maxY);

            for (int x = fromX; x <= toX; ++x) {
                for (int z = fromZ; z <= toZ; ++z) {
                    for (int y = fromY; y <= toY; ++y) {
                        if (ellipsoid.contains(x, y, z)) {
                            inside[index(x, y, z)] = true;
                        }
                    }
                }
            }
        }

        int minY() {
            return minY;
        }

        int maxY() {
            return maxY;
        }

        boolean isInside(int x, int y, int z) {
            if (x < originX || x >= originX + WIDTH || z < originZ || z >= originZ + WIDTH || y < minY || y > maxY) {
                return false;
            }
            return inside[index(x, y, z)];
        }

        boolean isShell(int x, int y, int z) {
            return !isInside(x, y, z)
                    && (isInside(x + 1, y, z) || isInside(x - 1, y, z)
                    || isInside(x, y + 1, z) || isInside(x, y - 1, z)
                    || isInside(x, y, z + 1) || isInside(x, y, z - 1));
        }

        private int index(int x, int y, int z) {
            return ((y - minY) * WIDTH + (z - originZ)) * WIDTH + (x - originX);
        }
    }

    /**
     * Vanilla's cave carver settings plus the fluid and the shell. The inherited lava level is decoded
     * but unused, since the fluid fills the tunnel at every height.
     */
    public static class Configuration extends CarverConfiguration {
        public static final Codec<Configuration> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                CarverConfiguration.CODEC.forGetter(config -> config),
                FloatProvider.CODEC.fieldOf("horizontal_radius_multiplier").forGetter(config -> config.horizontalRadiusMultiplier),
                FloatProvider.CODEC.fieldOf("vertical_radius_multiplier").forGetter(config -> config.verticalRadiusMultiplier),
                FloatProvider.codec(-1.0F, 1.0F).fieldOf("floor_level").forGetter(config -> config.floorLevel),
                FluidState.CODEC.fieldOf("fluidstate").forGetter(config -> config.fluidState),
                OreConfiguration.TargetBlockState.CODEC.listOf().optionalFieldOf("perimeter_targets").forGetter(config -> Optional.of(config.perimeterTargets))
        ).apply(instance, Configuration::new));

        private final FloatProvider horizontalRadiusMultiplier;
        private final FloatProvider verticalRadiusMultiplier;
        private final FloatProvider floorLevel;
        private final FluidState fluidState;
        private final List<OreConfiguration.TargetBlockState> perimeterTargets;

        public Configuration(CarverConfiguration base, FloatProvider horizontalRadiusMultiplier, FloatProvider verticalRadiusMultiplier,
                             FloatProvider floorLevel, FluidState fluidState, Optional<List<OreConfiguration.TargetBlockState>> perimeterTargets) {
            super(base.probability, base.y, base.yScale, base.lavaLevel, base.debugSettings, base.replaceable);
            this.horizontalRadiusMultiplier = horizontalRadiusMultiplier;
            this.verticalRadiusMultiplier = verticalRadiusMultiplier;
            this.floorLevel = floorLevel;
            this.fluidState = fluidState;
            this.perimeterTargets = perimeterTargets.orElseGet(() ->
                    List.of(OreConfiguration.target(AlwaysTrueTest.INSTANCE, VEBlocks.BITUMINOUS_GRAVEL.get().defaultBlockState())));
        }
    }
}
