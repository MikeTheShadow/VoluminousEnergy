package com.veteam.voluminousenergy.world.feature;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.veteam.voluminousenergy.blocks.blocks.VEBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.QuartPos;
import net.minecraft.core.SectionPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.core.registries.codec.RegistryCodecs;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.util.valueproviders.FloatProvider;
import net.minecraft.util.valueproviders.FloatProviders;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeResolver;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.LegacyRandomSource;
import net.minecraft.world.level.levelgen.WorldGenerationContext;
import net.minecraft.world.level.levelgen.WorldgenRandom;
import net.minecraft.world.level.levelgen.feature.BlockReplacement;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.heightproviders.HeightProvider;
import net.minecraft.world.level.levelgen.structure.templatesystem.AlwaysTrueTest;
import net.minecraft.world.level.material.FluidState;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Small cave systems filled entirely with one fluid's source blocks and sealed by a shell chosen per
 * position from the perimeter targets. A tunnel starts in a chunk whose biome is in {@code biomes}
 * with the given probability, seeded from the world seed, the salt, and that chunk the way vanilla
 * seeds carvers. Each chunk replays every tunnel that can reach it and writes only its own blocks, so
 * chunks agree on the fill and the shell without reading each other. Placed in the first decoration
 * step, tunnels follow all carving, so no carver opens them. This is a feature rather than a carver
 * because carvers cannot place fluids.
 */
public record VEFluidCaveFeature(float probability, HeightProvider y, FloatProvider yScale, HolderSet<Block> replaceable,
                                 FloatProvider horizontalRadiusMultiplier, FloatProvider verticalRadiusMultiplier, FloatProvider floorLevel,
                                 FluidState fluidState, List<BlockReplacement> perimeterTargets, HolderSet<Biome> biomes, int salt) implements Feature {
    public static final MapCodec<VEFluidCaveFeature> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Codec.floatRange(0.0F, 1.0F).fieldOf("probability").forGetter(VEFluidCaveFeature::probability),
            HeightProvider.CODEC.fieldOf("y").forGetter(VEFluidCaveFeature::y),
            FloatProviders.CODEC.fieldOf("yScale").forGetter(VEFluidCaveFeature::yScale),
            RegistryCodecs.holderSet(Registries.BLOCK).fieldOf("replaceable").forGetter(VEFluidCaveFeature::replaceable),
            FloatProviders.CODEC.fieldOf("horizontal_radius_multiplier").forGetter(VEFluidCaveFeature::horizontalRadiusMultiplier),
            FloatProviders.CODEC.fieldOf("vertical_radius_multiplier").forGetter(VEFluidCaveFeature::verticalRadiusMultiplier),
            FloatProviders.codec(-1.0F, 1.0F).fieldOf("floor_level").forGetter(VEFluidCaveFeature::floorLevel),
            FluidState.CODEC.fieldOf("fluidstate").forGetter(VEFluidCaveFeature::fluidState),
            BlockReplacement.CODEC.listOf().optionalFieldOf("perimeter_targets").forGetter(cave -> Optional.of(cave.perimeterTargets())),
            RegistryCodecs.holderSet(Registries.BIOME).fieldOf("biomes").forGetter(VEFluidCaveFeature::biomes),
            Codec.INT.fieldOf("salt").forGetter(VEFluidCaveFeature::salt)
    ).apply(instance, VEFluidCaveFeature::create));

    private static final int TUNNEL_RANGE = 2;
    // As vanilla's carver loop does, look twice as far as the range that sets the tunnel length.
    private static final int SOURCE_CHUNK_RADIUS = TUNNEL_RANGE * 2;

    private static VEFluidCaveFeature create(float probability, HeightProvider y, FloatProvider yScale, HolderSet<Block> replaceable,
                                             FloatProvider horizontalRadiusMultiplier, FloatProvider verticalRadiusMultiplier, FloatProvider floorLevel,
                                             FluidState fluidState, Optional<List<BlockReplacement>> perimeterTargets, HolderSet<Biome> biomes, int salt) {
        return new VEFluidCaveFeature(probability, y, yScale, replaceable, horizontalRadiusMultiplier, verticalRadiusMultiplier, floorLevel,
                fluidState,
                perimeterTargets.orElseGet(() -> List.of(BlockReplacement.replace(AlwaysTrueTest.INSTANCE, VEBlocks.BITUMINOUS_GRAVEL.get().defaultBlockState()))),
                biomes, salt);
    }

    @Override
    public MapCodec<VEFluidCaveFeature> codec() {
        return CODEC;
    }

    @Override
    public boolean place(WorldGenLevel level, ChunkGenerator generator, RandomSource random, BlockPos origin) {
        ChunkPos chunkPos = ChunkPos.containing(origin);
        WorldGenerationContext context = new WorldGenerationContext(generator, level);
        BiomeResolver biomeResolver = generator.getBiomeSource().createUncachedResolver(level.getLevel().getChunkSource().randomState());
        WorldgenRandom sourceRandom = new WorldgenRandom(new LegacyRandomSource(0L));
        boolean placed = false;

        for (int offsetX = -SOURCE_CHUNK_RADIUS; offsetX <= SOURCE_CHUNK_RADIUS; ++offsetX) {
            for (int offsetZ = -SOURCE_CHUNK_RADIUS; offsetZ <= SOURCE_CHUNK_RADIUS; ++offsetZ) {
                ChunkPos sourceChunk = new ChunkPos(chunkPos.x() + offsetX, chunkPos.z() + offsetZ);
                sourceRandom.setLargeFeatureSeed(level.getSeed() + salt, sourceChunk.x(), sourceChunk.z());
                if (sourceRandom.nextFloat() > probability || !biomes.contains(sourceBiome(biomeResolver, sourceChunk))) {
                    continue;
                }

                OutlineGrid grid = OutlineGrid.of(traceOutline(context, sourceRandom, sourceChunk), chunkPos, context);
                if (grid != null) {
                    fill(level, chunkPos, grid);
                    seal(level, chunkPos, random, grid);
                    placed = true;
                }
            }
        }
        return placed;
    }

    /**
     * The biome vanilla reads to choose a chunk's carvers.
     */
    private static Holder<Biome> sourceBiome(BiomeResolver biomeResolver, ChunkPos sourceChunk) {
        return biomeResolver.getNoiseBiome(QuartPos.fromBlock(sourceChunk.getMinBlockX()), 0, QuartPos.fromBlock(sourceChunk.getMinBlockZ()));
    }

    private List<Ellipsoid> traceOutline(WorldGenerationContext context, RandomSource random, ChunkPos startChunk) {
        List<Ellipsoid> outline = new ArrayList<>();
        int maxTunnelLength = SectionPos.sectionToBlockCoord(TUNNEL_RANGE * 2 - 1);
        double startX = startChunk.getBlockX(random.nextInt(16));
        double startY = y.sample(random, context);
        double startZ = startChunk.getBlockZ(random.nextInt(16));
        TunnelShape shape = new TunnelShape(
                horizontalRadiusMultiplier.sample(random),
                verticalRadiusMultiplier.sample(random),
                floorLevel.sample(random)
        );

        int tunnelCount = 1;
        if (random.nextInt(4) == 0) {
            double roomYScale = yScale.sample(random);
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

    private void fill(WorldGenLevel level, ChunkPos chunkPos, OutlineGrid grid) {
        BlockState fluidBlock = fluidState.createLegacyBlock();
        BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();

        for (int x = chunkPos.getMinBlockX(); x <= chunkPos.getMaxBlockX(); ++x) {
            for (int z = chunkPos.getMinBlockZ(); z <= chunkPos.getMaxBlockZ(); ++z) {
                for (int y = grid.minY(); y <= grid.maxY(); ++y) {
                    if (!grid.isInside(x, y, z)) {
                        continue;
                    }

                    pos.set(x, y, z);
                    if (canFill(level.getBlockState(pos))) {
                        level.setBlock(pos, fluidBlock, 2);
                    }
                }
            }
        }
    }

    private boolean canFill(BlockState existing) {
        return existing.is(replaceable)
                || existing.is(Blocks.AIR)
                || existing.is(Blocks.CAVE_AIR)
                || existing.getFluidState().is(FluidTags.WATER);
    }

    private void seal(WorldGenLevel level, ChunkPos chunkPos, RandomSource random, OutlineGrid grid) {
        BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();

        for (int x = chunkPos.getMinBlockX(); x <= chunkPos.getMaxBlockX(); ++x) {
            for (int z = chunkPos.getMinBlockZ(); z <= chunkPos.getMaxBlockZ(); ++z) {
                for (int y = grid.minY() - 1; y <= grid.maxY() + 1; ++y) {
                    if (!grid.isShell(x, y, z)) {
                        continue;
                    }

                    pos.set(x, y, z);
                    BlockState existing = level.getBlockState(pos);
                    if (existing.is(BlockTags.FEATURES_CANNOT_REPLACE)) {
                        continue;
                    }

                    BlockState shellState = LakeMask.perimeterState(perimeterTargets, existing, pos, random);
                    if (shellState != null) {
                        level.setBlock(pos, shellState, 2);
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
        static OutlineGrid of(List<Ellipsoid> outline, ChunkPos chunkPos, WorldGenerationContext context) {
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
}
