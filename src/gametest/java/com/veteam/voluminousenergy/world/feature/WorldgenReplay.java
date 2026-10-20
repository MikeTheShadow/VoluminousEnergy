package com.veteam.voluminousenergy.world.feature;

import com.google.common.cache.Cache;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;
import net.minecraft.core.BlockPos;
import net.minecraft.core.QuartPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.biome.BiomeResolver;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.LegacyRandomSource;
import net.minecraft.world.level.levelgen.WorldGenerationContext;
import net.minecraft.world.level.levelgen.WorldgenRandom;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.RecordComponent;
import java.util.ArrayList;
import java.util.List;

/**
 * Recomputes where the petroleum features put their fluid, so worldgen tests can compare a
 * generated world with what the features decided. It lives in the features' package to reach
 * {@link FluidReservoirCells}, and reads the tunnel outline by reflection.
 */
public class WorldgenReplay {

    private WorldgenReplay() {
    }

    /**
     * One tunnel system: the chunk it starts in, every position inside its outline, and whether
     * the biome at its start allows it.
     */
    public record Tunnel(ChunkPos start, LongSet interior, boolean allowed) {
    }

    /**
     * Every tunnel of {@code cave} that starts in a chunk between {@code minChunk} and
     * {@code maxChunk} on both axes.
     */
    public static List<Tunnel> tunnels(ServerLevel level, VEFluidCaveFeature cave, int minChunk, int maxChunk) {
        return tunnels(level, cave, minChunk, maxChunk, false);
    }

    /**
     * Like {@link #tunnels(ServerLevel, VEFluidCaveFeature, int, int)}, and with
     * {@code ignoreBiomes} also the tunnels the start biome rules out.
     */
    public static List<Tunnel> tunnels(ServerLevel level, VEFluidCaveFeature cave, int minChunk, int maxChunk, boolean ignoreBiomes) {
        ChunkGenerator generator = level.getChunkSource().getGenerator();
        WorldGenerationContext context = new WorldGenerationContext(generator, level);
        BiomeResolver biomes = generator.getBiomeSource().createUncachedResolver(level.getChunkSource().randomState());
        int lowestY = context.getMinGenY() + 2;
        int highestY = context.getMinGenY() + context.getGenDepth() - 8;
        WorldgenRandom random = new WorldgenRandom(new LegacyRandomSource(0L));
        List<Tunnel> tunnels = new ArrayList<>();
        for (int chunkX = minChunk; chunkX <= maxChunk; chunkX++) {
            for (int chunkZ = minChunk; chunkZ <= maxChunk; chunkZ++) {
                ChunkPos start = new ChunkPos(chunkX, chunkZ);
                random.setLargeFeatureSeed(level.getSeed() + cave.salt(), chunkX, chunkZ);
                if (random.nextFloat() > cave.probability()) {
                    continue;
                }
                boolean allowed = cave.biomes().contains(biomes.getNoiseBiome(QuartPos.fromBlock(start.getMinBlockX()), 0, QuartPos.fromBlock(start.getMinBlockZ())));
                if (!allowed && !ignoreBiomes) {
                    continue;
                }
                LongSet interior = new LongOpenHashSet();
                for (Object ellipsoid : traceOutline(cave, context, random, start)) {
                    mark(ellipsoid, interior, lowestY, highestY);
                }
                tunnels.add(new Tunnel(start, interior, allowed));
            }
        }
        return tunnels;
    }

    private static List<?> traceOutline(VEFluidCaveFeature cave, WorldGenerationContext context, RandomSource random, ChunkPos start) {
        try {
            Method method = VEFluidCaveFeature.class.getDeclaredMethod("traceOutline", WorldGenerationContext.class, RandomSource.class, ChunkPos.class);
            method.setAccessible(true);
            return (List<?>) method.invoke(cave, context, random, start);
        } catch (ReflectiveOperationException e) {
            throw new IllegalStateException(e);
        }
    }

    private static void mark(Object ellipsoid, LongSet interior, int lowestY, int highestY) {
        double[] values = new double[6];
        RecordComponent[] components = ellipsoid.getClass().getRecordComponents();
        try {
            for (int index = 0; index < 6; index++) {
                Method accessor = components[index].getAccessor();
                accessor.setAccessible(true);
                values[index] = (double) accessor.invoke(ellipsoid);
            }
        } catch (ReflectiveOperationException e) {
            throw new IllegalStateException(e);
        }
        double centreX = values[0];
        double centreY = values[1];
        double centreZ = values[2];
        double horizontal = values[3];
        double vertical = values[4];
        double floor = values[5];
        for (int x = Mth.floor(centreX - horizontal) - 1; x <= Mth.floor(centreX + horizontal) + 1; x++) {
            for (int z = Mth.floor(centreZ - horizontal) - 1; z <= Mth.floor(centreZ + horizontal) + 1; z++) {
                for (int y = Math.max(Mth.floor(centreY - vertical) - 1, lowestY); y <= Math.min(Mth.floor(centreY + vertical) + 1, highestY); y++) {
                    double normalX = (x + 0.5D - centreX) / horizontal;
                    double normalY = (y - 0.5D - centreY) / vertical;
                    double normalZ = (z + 0.5D - centreZ) / horizontal;
                    if (normalY > floor && normalX * normalX + normalY * normalY + normalZ * normalZ < 1.0D) {
                        interior.add(BlockPos.asLong(x, y, z));
                    }
                }
            }
        }
    }

    /**
     * Compares the cells a placement reads through the level's shared caches with cells computed
     * from scratch, over every chunk within {@code radius} of the origin and every other column of
     * each. Returns the first difference, or null when there is none.
     */
    public static String cacheDifference(ServerLevel level, VEFluidReservoirFeature reservoir, int radius) {
        ChunkGenerator generator = level.getChunkSource().getGenerator();
        FluidReservoirCells cached = FluidReservoirCells.forPlacement(reservoir, level, generator);
        FluidReservoirCells uncached;
        try {
            Constructor<FluidReservoirCells> constructor = FluidReservoirCells.class.getDeclaredConstructor(VEFluidReservoirFeature.class,
                    ServerLevel.class, ChunkGenerator.class, Cache.class, Cache.class);
            constructor.setAccessible(true);
            uncached = constructor.newInstance(reservoir, level, generator, null, null);
        } catch (ReflectiveOperationException e) {
            throw new IllegalStateException(e);
        }
        for (int chunkX = -radius; chunkX <= radius; chunkX++) {
            for (int chunkZ = -radius; chunkZ <= radius; chunkZ++) {
                ChunkPos chunk = new ChunkPos(chunkX, chunkZ);
                List<FluidReservoirCells.Cell> near = uncached.reservoirCellsNear(chunk);
                if (!near.equals(cached.reservoirCellsNear(chunk))) {
                    return "cells near chunk " + chunk + " differ";
                }
                for (FluidReservoirCells.Cell cell : near) {
                    for (int x = chunk.getMinBlockX(); x <= chunk.getMaxBlockX(); x += 2) {
                        for (int z = chunk.getMinBlockZ(); z <= chunk.getMaxBlockZ(); z += 2) {
                            for (int y = cell.lowerY(); y < cell.upperY(); y++) {
                                if (!uncached.ownerAt(x, y, z).equals(cached.ownerAt(x, y, z))) {
                                    return "owner of " + x + ", " + y + ", " + z + " differs";
                                }
                            }
                        }
                    }
                }
            }
        }
        return null;
    }

    /**
     * Answers which positions a reservoir feature claims, the same way its placements decide.
     */
    public static class Reservoirs {
        private final FluidReservoirCells cells;

        public Reservoirs(ServerLevel level, VEFluidReservoirFeature reservoir) {
            this.cells = FluidReservoirCells.forPlacement(reservoir, level, level.getChunkSource().getGenerator());
        }

        /**
         * The lowest and highest Y any reservoir cell near the chunk can fill, or null when none can.
         */
        public int[] band(ChunkPos chunk) {
            List<FluidReservoirCells.Cell> near = cells.reservoirCellsNear(chunk);
            if (near.isEmpty()) {
                return null;
            }
            int fromY = Integer.MAX_VALUE;
            int toY = Integer.MIN_VALUE;
            for (FluidReservoirCells.Cell cell : near) {
                fromY = Math.min(fromY, cell.lowerY());
                toY = Math.max(toY, cell.upperY());
            }
            return fromY < toY ? new int[]{fromY, toY} : null;
        }

        public boolean fills(int x, int y, int z) {
            return cells.ownerAt(x, y, z).fills(y);
        }
    }
}
