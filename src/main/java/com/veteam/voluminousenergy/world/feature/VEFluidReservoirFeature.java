package com.veteam.voluminousenergy.world.feature;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.veteam.voluminousenergy.blocks.blocks.VEBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistryCodecs;
import net.minecraft.core.SectionPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.status.ChunkStatus;
import net.minecraft.world.level.levelgen.VerticalAnchor;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.OreConfiguration;
import net.minecraft.world.level.levelgen.structure.templatesystem.AlwaysTrueTest;
import net.minecraft.world.level.levelgen.synth.NormalNoise;
import net.minecraft.world.level.material.FluidState;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;

/**
 * Aquifer-sized bodies of one fluid laid out by {@link FluidReservoirCells}: a pool fills open space below
 * a flat level and a cap fills open space above a flat floor. Every opening other than the open face is
 * plugged with the perimeter targets. Each chunk fills only its own blocks and plugs up to one block
 * into its neighbours, which make the same decisions when they generate.
 */
public class VEFluidReservoirFeature extends Feature<VEFluidReservoirFeature.Configuration> {
    public VEFluidReservoirFeature(Codec<VEFluidReservoirFeature.Configuration> codec) {
        super(codec);
    }

    @Override
    public boolean place(FeaturePlaceContext<VEFluidReservoirFeature.Configuration> context) {
        WorldGenLevel level = context.level();
        Configuration config = context.config();
        ChunkPos chunkPos = ChunkPos.containing(context.origin());
        FluidReservoirCells cells = FluidReservoirCells.forPlacement(config, level.getLevel(), context.chunkGenerator());

        List<FluidReservoirCells.Cell> reservoirCells = cells.reservoirCellsNear(chunkPos);
        if (reservoirCells.isEmpty()) {
            return false;
        }

        int fromY = Integer.MAX_VALUE;
        int toY = Integer.MIN_VALUE;
        for (FluidReservoirCells.Cell cell : reservoirCells) {
            fromY = Math.min(fromY, cell.lowerY());
            toY = Math.max(toY, cell.upperY());
        }
        if (fromY >= toY) {
            return false;
        }

        FilledMask filled = fill(level, config, cells, chunkPos, fromY, toY);
        if (filled.isEmpty()) {
            return false;
        }
        seal(level, config, cells, context.random(), filled);
        return true;
    }

    /**
     * Drops the reservoir results shared by the chunks of {@code level}, once it unloads.
     */
    public static void forgetLevel(LevelAccessor level) {
        FluidReservoirCells.forgetLevel(level);
    }

    private static FilledMask fill(WorldGenLevel level, Configuration config, FluidReservoirCells cells, ChunkPos chunkPos, int fromY, int toY) {
        FilledMask filled = new FilledMask(chunkPos, fromY, toY);
        BlockState fluidBlock = config.fluidState().createLegacyBlock();
        BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();

        for (int x = chunkPos.getMinBlockX(); x <= chunkPos.getMaxBlockX(); ++x) {
            for (int z = chunkPos.getMinBlockZ(); z <= chunkPos.getMaxBlockZ(); ++z) {
                for (int y = fromY; y < toY; ++y) {
                    pos.set(x, y, z);
                    if (canFill(level.getBlockState(pos)) && cells.ownerAt(x, y, z).fills(y)) {
                        level.setBlock(pos, fluidBlock, 2);
                        filled.set(x, y, z);
                    }
                }
            }
        }
        return filled;
    }

    private static void seal(WorldGenLevel level, Configuration config, FluidReservoirCells cells, RandomSource random, FilledMask filled) {
        Direction openFace = config.kind() == Kind.POOL ? Direction.UP : Direction.DOWN;
        BlockPos.MutableBlockPos neighbour = new BlockPos.MutableBlockPos();

        filled.forEach((x, y, z) -> {
            for (Direction direction : Direction.values()) {
                if (direction == openFace) {
                    continue;
                }
                neighbour.set(x, y, z).move(direction);
                if (!filled.contains(neighbour) && needsPlug(level, config, cells, filled, neighbour)) {
                    BlockState plug = LakeMask.perimeterState(config.perimeterTargets(), level.getBlockState(neighbour.below()), random);
                    if (plug != null) {
                        level.setBlock(neighbour, plug, 2);
                    }
                }
            }
        });
    }

    private static boolean needsPlug(WorldGenLevel level, Configuration config, FluidReservoirCells cells, FilledMask filled, BlockPos pos) {
        if (level.isOutsideBuildHeight(pos)) {
            return false;
        }

        BlockState existing = level.getBlockState(pos);
        FluidState existingFluid = existing.getFluidState();
        if (existing.isSolid() && existingFluid.isEmpty()) {
            return false;
        }
        if (existingFluid.getType().isSame(config.fluidState().getType()) || existing.is(BlockTags.FEATURES_CANNOT_REPLACE)) {
            return false;
        }
        return filled.isInChunk(pos) || !willBeFilled(level, cells, pos, existing);
    }

    /**
     * Whether a position in a neighbouring chunk will be filled when that chunk places this
     * reservoir. A neighbour that has already placed its features, including one generated before
     * the reservoir existed, never will.
     */
    private static boolean willBeFilled(WorldGenLevel level, FluidReservoirCells cells, BlockPos pos, BlockState existing) {
        if (!canFill(existing) || !cells.ownerAt(pos.getX(), pos.getY(), pos.getZ()).fills(pos.getY())) {
            return false;
        }
        ChunkStatus neighbourStatus = level.getChunk(SectionPos.blockToSectionCoord(pos.getX()), SectionPos.blockToSectionCoord(pos.getZ())).getPersistedStatus();
        return !neighbourStatus.isOrAfter(ChunkStatus.FEATURES);
    }

    /**
     * Air is tested by block rather than {@code isAir()}, because natural gas counts as air and must not
     * be overwritten.
     */
    private static boolean canFill(BlockState existing) {
        return existing.is(Blocks.AIR) || existing.is(Blocks.CAVE_AIR) || existing.is(Blocks.WATER);
    }

    /**
     * The positions of one chunk that this placement filled, between {@code fromY} and {@code toY}.
     */
    private static class FilledMask {
        private final int minX;
        private final int minZ;
        private final int fromY;
        private final int toY;
        private final boolean[] filled;
        private int count;

        FilledMask(ChunkPos chunkPos, int fromY, int toY) {
            this.minX = chunkPos.getMinBlockX();
            this.minZ = chunkPos.getMinBlockZ();
            this.fromY = fromY;
            this.toY = toY;
            this.filled = new boolean[16 * 16 * (toY - fromY)];
        }

        void set(int x, int y, int z) {
            filled[index(x, y, z)] = true;
            ++count;
        }

        boolean isEmpty() {
            return count == 0;
        }

        boolean isInChunk(BlockPos pos) {
            return pos.getX() >= minX && pos.getX() < minX + 16 && pos.getZ() >= minZ && pos.getZ() < minZ + 16;
        }

        boolean contains(BlockPos pos) {
            return isInChunk(pos) && pos.getY() >= fromY && pos.getY() < toY && filled[index(pos.getX(), pos.getY(), pos.getZ())];
        }

        void forEach(PositionConsumer consumer) {
            for (int x = minX; x < minX + 16; ++x) {
                for (int z = minZ; z < minZ + 16; ++z) {
                    for (int y = fromY; y < toY; ++y) {
                        if (filled[index(x, y, z)]) {
                            consumer.accept(x, y, z);
                        }
                    }
                }
            }
        }

        private int index(int x, int y, int z) {
            return ((y - fromY) * 16 + (z - minZ)) * 16 + (x - minX);
        }

        interface PositionConsumer {
            void accept(int x, int y, int z);
        }
    }

    public enum Kind implements StringRepresentable {
        POOL("pool"),
        CAP("cap");

        public static final Codec<Kind> CODEC = StringRepresentable.fromEnum(Kind::values);

        private final String name;

        Kind(String name) {
            this.name = name;
        }

        @Override
        public @NotNull String getSerializedName() {
            return name;
        }
    }

    /**
     * Bends the boundary between two cells by up to {@code wobble} blocks of distance difference,
     * following {@code noise}. A wobble of 0 leaves flat boundaries.
     */
    public record Boundary(ResourceKey<NormalNoise.NoiseParameters> noise, float wobble) {
        public static final Codec<Boundary> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                ResourceKey.codec(Registries.NOISE).fieldOf("noise").forGetter(Boundary::noise),
                Codec.floatRange(0.0F, 16.0F).fieldOf("wobble").forGetter(Boundary::wobble)
        ).apply(instance, Boundary::new));
    }

    /**
     * Configures one kind of reservoir. {@code cellHeight} is unused when the reservoir reaches the
     * surface, since its cells are columns spanning the band. Without {@code perimeter_targets} plugs
     * are Bituminous Gravel.
     */
    public record Configuration(FluidState fluidState, Kind kind, boolean reachesSurface, VerticalAnchor minY, VerticalAnchor maxY,
                                int cellWidth, int cellHeight, float chance, HolderSet<Biome> biomes, int salt,
                                List<OreConfiguration.TargetBlockState> perimeterTargets, Optional<Boundary> boundary) implements FeatureConfiguration {
        public static final Codec<VEFluidReservoirFeature.Configuration> CODEC = RecordCodecBuilder.<Configuration>create(instance -> instance.group(
                FluidState.CODEC.fieldOf("fluidstate").forGetter(Configuration::fluidState),
                Kind.CODEC.fieldOf("kind").forGetter(Configuration::kind),
                Codec.BOOL.optionalFieldOf("reaches_surface", false).forGetter(Configuration::reachesSurface),
                VerticalAnchor.CODEC.fieldOf("min_y").forGetter(Configuration::minY),
                VerticalAnchor.CODEC.fieldOf("max_y").forGetter(Configuration::maxY),
                Codec.intRange(8, 128).fieldOf("cell_width").forGetter(Configuration::cellWidth),
                Codec.intRange(4, 64).optionalFieldOf("cell_height", 16).forGetter(Configuration::cellHeight),
                Codec.floatRange(0.0F, 1.0F).fieldOf("chance").forGetter(Configuration::chance),
                RegistryCodecs.homogeneousList(Registries.BIOME).fieldOf("biomes").forGetter(Configuration::biomes),
                Codec.INT.fieldOf("salt").forGetter(Configuration::salt),
                OreConfiguration.TargetBlockState.CODEC.listOf().optionalFieldOf("perimeter_targets").forGetter(config -> Optional.of(config.perimeterTargets())),
                Boundary.CODEC.optionalFieldOf("boundary").forGetter(Configuration::boundary)
        ).apply(instance, Configuration::create)).validate(Configuration::validate);

        private static Configuration create(FluidState fluidState, Kind kind, boolean reachesSurface, VerticalAnchor minY, VerticalAnchor maxY,
                                            int cellWidth, int cellHeight, float chance, HolderSet<Biome> biomes, int salt,
                                            Optional<List<OreConfiguration.TargetBlockState>> perimeterTargets, Optional<Boundary> boundary) {
            return new Configuration(fluidState, kind, reachesSurface, minY, maxY, cellWidth, cellHeight, chance, biomes, salt,
                    perimeterTargets.orElseGet(() -> List.of(OreConfiguration.target(AlwaysTrueTest.INSTANCE, VEBlocks.BITUMINOUS_GRAVEL.get().defaultBlockState()))),
                    boundary);
        }

        private static DataResult<Configuration> validate(Configuration config) {
            if (config.kind() == Kind.CAP && config.reachesSurface()) {
                return DataResult.error(() -> "a cap reservoir cannot reach the surface");
            }
            return DataResult.success(config);
        }
    }
}
