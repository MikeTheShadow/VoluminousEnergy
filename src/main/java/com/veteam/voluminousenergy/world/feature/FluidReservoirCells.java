package com.veteam.voluminousenergy.world.feature;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.veteam.voluminousenergy.VoluminousEnergy;
import com.veteam.voluminousenergy.tools.Config;
import it.unimi.dsi.fastutil.longs.Long2IntMap;
import it.unimi.dsi.fastutil.longs.Long2IntOpenHashMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.QuartPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ColumnPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.DensityFunction;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.NoiseBasedChunkGenerator;
import net.minecraft.world.level.levelgen.NoiseSettings;
import net.minecraft.world.level.levelgen.PositionalRandomFactory;
import net.minecraft.world.level.levelgen.RandomState;
import net.minecraft.world.level.levelgen.WorldGenerationContext;
import net.minecraft.world.level.levelgen.synth.NormalNoise;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * The aquifer-style grid a fluid reservoir is laid out on. Each cell has a centre jittered by a
 * positional random source, and a position belongs to the nearest centre, with the boundary between
 * two centres bent by the configured noise. Every answer depends only on the seed, the
 * configuration, and the position, so chunks agree without reading each other, and caching an answer
 * never changes it.
 */
class FluidReservoirCells {
    private static final int BURIED_SURFACE_CLEARANCE = 8;
    private static final int SHARED_CELL_LIMIT = 16_384;
    private static final int SHARED_SURFACE_LIMIT = 16_384;
    // The density vanilla's NoiseChunk.computePreliminarySurfaceLevel treats as solid ground.
    private static final double PRELIMINARY_SURFACE_DENSITY = 0.390625D;

    private static final Map<ServerLevel, SharedLevel> SHARED_LEVELS = new ConcurrentHashMap<>();

    private final VEFluidReservoirFeature.Configuration config;
    private final ServerLevel level;
    private final ChunkGenerator generator;
    private final RandomState randomState;
    private final PositionalRandomFactory randomFactory;
    @Nullable
    private final NormalNoise boundaryNoise;
    private final float wobble;
    private final int bandMinY;
    private final int bandMaxY;
    private final int jitterWidth;
    private final int jitterHeight;
    @Nullable
    private final NoiseBasedChunkGenerator fastSurfaceGenerator;
    @Nullable
    private final Cache<Long, Cell> sharedCells;
    @Nullable
    private final Cache<Long, Integer> sharedSurfaces;
    private final Long2ObjectMap<Cell> localCells = new Long2ObjectOpenHashMap<>();
    private final Long2IntMap localSurfaces = new Long2IntOpenHashMap();

    private FluidReservoirCells(VEFluidReservoirFeature.Configuration config, ServerLevel level, ChunkGenerator generator,
                                @Nullable Cache<Long, Cell> sharedCells, @Nullable Cache<Long, Integer> sharedSurfaces) {
        this.config = config;
        this.level = level;
        this.generator = generator;
        this.randomState = level.getChunkSource().randomState();
        this.randomFactory = randomState.getOrCreateRandomFactory(
                ResourceLocation.fromNamespaceAndPath(VoluminousEnergy.MODID, "petroleum_reservoir_" + config.salt()));

        VEFluidReservoirFeature.Boundary boundary = config.boundary().filter(candidate -> candidate.wobble() > 0.0F).orElse(null);
        this.boundaryNoise = boundary == null ? null : randomState.getOrCreateNoise(boundary.noise());
        this.wobble = boundary == null ? 0.0F : boundary.wobble();

        WorldGenerationContext heightContext = new WorldGenerationContext(generator, level);
        this.bandMinY = Math.max(config.minY().resolveY(heightContext), level.getMinBuildHeight());
        this.bandMaxY = Math.min(config.maxY().resolveY(heightContext), level.getMaxBuildHeight() - 1);
        // Vanilla aquifers jitter centres across 10 of 16 blocks horizontally and 9 of 12 vertically.
        this.jitterWidth = config.cellWidth() * 5 / 8;
        this.jitterHeight = config.cellHeight() * 3 / 4;

        boolean fastSurface = Config.FAST_PETROLEUM_RESERVOIR_SURFACE_SAMPLING.get();
        this.fastSurfaceGenerator = fastSurface && generator instanceof NoiseBasedChunkGenerator noiseGenerator ? noiseGenerator : null;
        this.sharedCells = sharedCells;
        this.sharedSurfaces = sharedSurfaces;
    }

    /**
     * Returns the cells for one placement of {@code config}. Each placement keeps its own map in
     * front of the level's shared caches, because ownership reads a dozen cells for every open
     * position and the shared caches are slower to read than a plain map.
     */
    static FluidReservoirCells forPlacement(VEFluidReservoirFeature.Configuration config, ServerLevel level, ChunkGenerator generator) {
        if (!Config.CACHE_PETROLEUM_RESERVOIR_CELLS.get()) {
            return new FluidReservoirCells(config, level, generator, null, null);
        }
        SharedLevel shared = SHARED_LEVELS.computeIfAbsent(level, ignored -> new SharedLevel());
        Cache<Long, Cell> sharedCells = shared.cells.computeIfAbsent(config, ignored -> CacheBuilder.newBuilder().maximumSize(SHARED_CELL_LIMIT).build());
        return new FluidReservoirCells(config, level, generator, sharedCells, shared.surfaces);
    }

    static void forgetLevel(LevelAccessor level) {
        SHARED_LEVELS.remove(level);
    }

    /**
     * Returns the reservoir cells that can own a position of the chunk within the band.
     */
    List<Cell> reservoirCellsNear(ChunkPos chunkPos) {
        List<Cell> reservoirCells = new ArrayList<>();
        if (bandMinY > bandMaxY) {
            return reservoirCells;
        }

        int fromGridX = baseGridColumn(chunkPos.getMinBlockX());
        int toGridX = baseGridColumn(chunkPos.getMaxBlockX()) + 1;
        int fromGridZ = baseGridColumn(chunkPos.getMinBlockZ());
        int toGridZ = baseGridColumn(chunkPos.getMaxBlockZ()) + 1;
        int fromGridY = config.reachesSurface() ? 0 : Math.floorDiv(bandMinY, config.cellHeight()) - 1;
        int toGridY = config.reachesSurface() ? 0 : Math.floorDiv(bandMaxY, config.cellHeight()) + 1;

        for (int gridX = fromGridX; gridX <= toGridX; ++gridX) {
            for (int gridZ = fromGridZ; gridZ <= toGridZ; ++gridZ) {
                for (int gridY = fromGridY; gridY <= toGridY; ++gridY) {
                    Cell cell = cell(gridX, gridY, gridZ);
                    if (cell.reservoir()) {
                        reservoirCells.add(cell);
                    }
                }
            }
        }
        return reservoirCells;
    }

    Cell ownerAt(int x, int y, int z) {
        int fromGridX = baseGridColumn(x);
        int fromGridZ = baseGridColumn(z);
        int fromGridY = config.reachesSurface() ? 0 : Math.floorDiv(y, config.cellHeight()) - 1;
        int toGridY = config.reachesSurface() ? 0 : fromGridY + 2;

        Cell nearest = null;
        Cell second = null;
        long nearestDistance = Long.MAX_VALUE;
        long secondDistance = Long.MAX_VALUE;
        for (int gridX = fromGridX; gridX <= fromGridX + 1; ++gridX) {
            for (int gridZ = fromGridZ; gridZ <= fromGridZ + 1; ++gridZ) {
                for (int gridY = fromGridY; gridY <= toGridY; ++gridY) {
                    Cell cell = cell(gridX, gridY, gridZ);
                    long distance = distanceSquared(cell, x, y, z);
                    if (distance < nearestDistance) {
                        second = nearest;
                        secondDistance = nearestDistance;
                        nearest = cell;
                        nearestDistance = distance;
                    } else if (distance < secondDistance) {
                        second = cell;
                        secondDistance = distance;
                    }
                }
            }
        }

        if (boundaryNoise == null || (!nearest.reservoir() && !second.reservoir())) {
            return nearest;
        }

        double nearestReach = Math.sqrt(nearestDistance);
        double secondReach = Math.sqrt(secondDistance);
        if (secondReach - nearestReach > wobble) {
            return nearest;
        }

        // The pair is ordered by grid index, not distance, so the noise can push the boundary either way.
        boolean nearestIsLower = nearest.compareGrid(second) < 0;
        double lowerReach = nearestIsLower ? nearestReach : secondReach;
        double higherReach = nearestIsLower ? secondReach : nearestReach;
        double bend = wobble * Mth.clamp(boundaryNoise.getValue(x, y, z), -1.0D, 1.0D);
        boolean lowerOwns = higherReach - lowerReach + bend > 0.0D;
        return lowerOwns == nearestIsLower ? nearest : second;
    }

    private int baseGridColumn(int block) {
        return Math.floorDiv(block - jitterWidth / 2, config.cellWidth());
    }

    private long distanceSquared(Cell cell, int x, int y, int z) {
        long offsetX = cell.centreX() - x;
        long offsetY = config.reachesSurface() ? 0 : cell.centreY() - y;
        long offsetZ = cell.centreZ() - z;
        return offsetX * offsetX + offsetY * offsetY + offsetZ * offsetZ;
    }

    private Cell cell(int gridX, int gridY, int gridZ) {
        long key = BlockPos.asLong(gridX, gridY, gridZ);
        Cell cell = localCells.get(key);
        if (cell == null) {
            cell = sharedCells == null
                    ? createCell(gridX, gridY, gridZ)
                    : sharedCells.asMap().computeIfAbsent(key, ignored -> createCell(gridX, gridY, gridZ));
            localCells.put(key, cell);
        }
        return cell;
    }

    private Cell createCell(int gridX, int gridY, int gridZ) {
        RandomSource random = randomFactory.at(gridX, gridY, gridZ);
        int cellWidth = config.cellWidth();
        int cellHeight = config.cellHeight();
        int centreX = gridX * cellWidth + random.nextInt(jitterWidth);
        int centreY = gridY * cellHeight + random.nextInt(jitterHeight);
        int centreZ = gridZ * cellWidth + random.nextInt(jitterWidth);

        if (random.nextFloat() >= config.chance()) {
            return Cell.dry(gridX, gridY, gridZ, centreX, centreY, centreZ);
        }

        int centreSurface = fastSurfaceGenerator != null ? cornerSurface(gridX, gridZ) : exactSurfaceAt(centreX, centreZ);
        int biomeY = config.reachesSurface() ? centreSurface : centreY;
        if (!config.biomes().contains(biomeAt(centreX, biomeY, centreZ))) {
            return Cell.dry(gridX, gridY, gridZ, centreX, centreY, centreZ);
        }

        int minSurface = fastSurfaceGenerator != null ? centreSurface : exactMinSurface(centreX, centreZ, centreSurface);

        int cellBottom = gridY * cellHeight;
        int lowerY;
        int upperY;
        if (config.reachesSurface()) {
            lowerY = bandMinY;
            upperY = Math.min(minSurface, bandMaxY + 1);
        } else if (config.kind() == VEFluidReservoirFeature.Kind.POOL) {
            int quarter = cellHeight / 4;
            int fluidLevel = cellBottom + quarter + random.nextInt(cellHeight - quarter + 1);
            lowerY = bandMinY;
            upperY = Math.min(Math.min(fluidLevel, minSurface - BURIED_SURFACE_CLEARANCE), bandMaxY + 1);
        } else {
            int floor = cellBottom + random.nextInt(cellHeight * 3 / 4 + 1);
            lowerY = Math.max(floor, bandMinY);
            upperY = Math.min(minSurface - BURIED_SURFACE_CLEARANCE, bandMaxY + 1);
        }
        return new Cell(gridX, gridY, gridZ, centreX, centreY, centreZ, true, lowerY, upperY);
    }

    private int exactMinSurface(int centreX, int centreZ, int centreSurface) {
        int halfWidth = config.cellWidth() / 2;
        return Math.min(centreSurface, Math.min(
                Math.min(exactSurfaceAt(centreX - halfWidth, centreZ), exactSurfaceAt(centreX + halfWidth, centreZ)),
                Math.min(exactSurfaceAt(centreX, centreZ - halfWidth), exactSurfaceAt(centreX, centreZ + halfWidth))));
    }

    private int exactSurfaceAt(int x, int z) {
        return generator.getBaseHeight(x, z, Heightmap.Types.OCEAN_FLOOR_WG, level, randomState);
    }

    /**
     * The lowest preliminary surface at the four corners of a grid column, which every cell and layer
     * in the column shares.
     */
    private int cornerSurface(int gridX, int gridZ) {
        int westX = gridX * config.cellWidth();
        int eastX = westX + config.cellWidth();
        int northZ = gridZ * config.cellWidth();
        int southZ = northZ + config.cellWidth();
        return Math.min(Math.min(preliminarySurfaceAt(westX, northZ), preliminarySurfaceAt(eastX, northZ)),
                Math.min(preliminarySurfaceAt(westX, southZ), preliminarySurfaceAt(eastX, southZ)));
    }

    private int preliminarySurfaceAt(int x, int z) {
        long key = ColumnPos.asLong(x, z);
        if (localSurfaces.containsKey(key)) {
            return localSurfaces.get(key);
        }
        int surface = sharedSurfaces == null
                ? computePreliminarySurface(x, z)
                : sharedSurfaces.asMap().computeIfAbsent(key, ignored -> computePreliminarySurface(x, z));
        localSurfaces.put(key, surface);
        return surface;
    }

    /**
     * The search vanilla's {@code NoiseChunk} uses to keep aquifers below the surface: step down by
     * the noise cell height until the density without jaggedness counts as solid. It is private to
     * {@code NoiseChunk}, so it is repeated here without building one.
     */
    private int computePreliminarySurface(int x, int z) {
        NoiseSettings noiseSettings = fastSurfaceGenerator.generatorSettings().value().noiseSettings().clampToHeightAccessor(level);
        DensityFunction density = randomState.router().initialDensityWithoutJaggedness();
        int minY = noiseSettings.minY();
        for (int y = minY + noiseSettings.height(); y >= minY; y -= noiseSettings.getCellHeight()) {
            if (density.compute(new DensityFunction.SinglePointContext(x, y, z)) > PRELIMINARY_SURFACE_DENSITY) {
                return y;
            }
        }
        return Integer.MAX_VALUE;
    }

    private Holder<Biome> biomeAt(int x, int y, int z) {
        return generator.getBiomeSource().getNoiseBiome(QuartPos.fromBlock(x), QuartPos.fromBlock(y), QuartPos.fromBlock(z), randomState.sampler());
    }

    /**
     * Everything shared by the reservoirs of one level: the cells of each configuration, and the
     * preliminary surface heights, which depend only on the level and so serve every configuration.
     */
    private static class SharedLevel {
        private final Map<VEFluidReservoirFeature.Configuration, Cache<Long, Cell>> cells = new ConcurrentHashMap<>();
        private final Cache<Long, Integer> surfaces = CacheBuilder.newBuilder().maximumSize(SHARED_SURFACE_LIMIT).build();
    }

    /**
     * One grid cell. A reservoir cell fills the positions it owns from {@code lowerY} up to, but not
     * including, {@code upperY}.
     */
    record Cell(int gridX, int gridY, int gridZ, int centreX, int centreY, int centreZ, boolean reservoir, int lowerY, int upperY) {
        static Cell dry(int gridX, int gridY, int gridZ, int centreX, int centreY, int centreZ) {
            return new Cell(gridX, gridY, gridZ, centreX, centreY, centreZ, false, 0, 0);
        }

        boolean fills(int y) {
            return reservoir && y >= lowerY && y < upperY;
        }

        int compareGrid(Cell other) {
            if (gridX != other.gridX) {
                return Integer.compare(gridX, other.gridX);
            }
            if (gridY != other.gridY) {
                return Integer.compare(gridY, other.gridY);
            }
            return Integer.compare(gridZ, other.gridZ);
        }
    }
}
