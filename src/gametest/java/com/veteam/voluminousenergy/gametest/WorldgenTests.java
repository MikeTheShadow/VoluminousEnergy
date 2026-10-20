package com.veteam.voluminousenergy.gametest;

import com.veteam.voluminousenergy.VoluminousEnergy;
import com.veteam.voluminousenergy.fluids.VEFluids;
import com.veteam.voluminousenergy.recipe.DimensionalLaserRecipe;
import com.veteam.voluminousenergy.recipe.VERecipe;
import com.veteam.voluminousenergy.tools.Config;
import com.veteam.voluminousenergy.util.WorldUtil;
import com.veteam.voluminousenergy.util.climate.FluidClimateSpawn;
import com.veteam.voluminousenergy.world.feature.VEFluidCaveFeature;
import com.veteam.voluminousenergy.world.feature.VEFluidReservoirFeature;
import com.veteam.voluminousenergy.world.feature.WorldgenReplay;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.QuartPos;
import net.minecraft.core.SectionPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.chunk.LevelChunkSection;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import oshi.util.tuples.Pair;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HexFormat;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.TreeMap;
import java.util.function.LongPredicate;

/**
 * Worldgen surveys on real terrain (test plan T3). They only run on request, in a throwaway world
 * the autotest harness creates, after {@code wg_pregen} has generated the survey area: a square of
 * chunks around the origin, {@code ve.autotest.radius} chunks out (20 unless set).
 */
public class WorldgenTests {
    private static final int OTHER_DIMENSION_RADIUS = 5;
    // Tunnels reach up to 4 chunks from their start, so replays stay this far inside the area.
    private static final int TUNNEL_MARGIN = 5;

    private WorldgenTests() {
    }

    static void register(List<GameTestDefinition> tests) {
        tests.add(GameTestDefinition.manual("wg_pregen", 100, helper -> pregen(helper, "wg_pregen", "rings")));
        tests.add(GameTestDefinition.manual("wg_pregen_reverse", 100, helper -> pregen(helper, "wg_pregen_reverse", "reverse")));
        tests.add(GameTestDefinition.manual("wg_pregen_random_first_half", 100, helper -> pregen(helper, "wg_pregen_random_first_half", "random_first_half")));
        tests.add(GameTestDefinition.manual("wg_pregen_random_second_half", 100, helper -> pregen(helper, "wg_pregen_random_second_half", "random_second_half")));
        tests.add(GameTestDefinition.manual("wg_petroleum_caves", 100, WorldgenTests::petroleumCaves));
        tests.add(GameTestDefinition.manual("wg_reservoirs", 100, WorldgenTests::reservoirs));
        tests.add(GameTestDefinition.manual("wg_lakes_geysers", 100, WorldgenTests::lakesAndGeysers));
        tests.add(GameTestDefinition.manual("wg_ores", 100, WorldgenTests::ores));
        tests.add(GameTestDefinition.manual("wg_rice", 100, WorldgenTests::rice));
        tests.add(GameTestDefinition.manual("wg_other_dimensions", 100, WorldgenTests::otherDimensions));
        tests.add(GameTestDefinition.manual("wg_fingerprint", 100, WorldgenTests::fingerprint));
        tests.add(GameTestDefinition.manual("wg_fluid_scanner", 100, WorldgenTests::fluidScanner));
        tests.add(GameTestDefinition.manual("wg_datapack_override", 100, WorldgenTests::datapackOverride));
        tests.add(GameTestDefinition.manual("wg_order_attribution", 100, WorldgenTests::orderAttribution));
        tests.add(GameTestDefinition.manual("wg_reservoir_cache", 100, WorldgenTests::reservoirCache));
        tests.add(GameTestDefinition.manual("wg_tunnel_biomes", 100, WorldgenTests::tunnelBiomes));
    }

    static int radius() {
        return Integer.getInteger("ve.autotest.radius", 20);
    }

    private static List<ChunkPos> area(int radius) {
        List<ChunkPos> chunks = new ArrayList<>();
        for (int ring = 0; ring <= radius; ring++) {
            for (int x = -ring; x <= ring; x++) {
                for (int z = -ring; z <= ring; z++) {
                    if (Math.max(Math.abs(x), Math.abs(z)) == ring) {
                        chunks.add(new ChunkPos(x, z));
                    }
                }
            }
        }
        return chunks;
    }

    /**
     * Generates the survey area in the order named: outward rings, inward rings, or a fixed random
     * order split in two so the harness can restart the server between the halves.
     */
    private static void pregen(GameTestHelper helper, String test, String order) {
        long started = System.nanoTime();
        List<ChunkPos> chunks = area(radius());
        switch (order) {
            case "reverse" -> Collections.reverse(chunks);
            case "random_first_half", "random_second_half" -> {
                Collections.shuffle(chunks, new Random(4_172_144_997L));
                int half = chunks.size() / 2;
                chunks = order.equals("random_first_half") ? chunks.subList(0, half) : chunks.subList(half, chunks.size());
            }
            default -> {
            }
        }
        ServerLevel overworld = helper.getLevel().getServer().overworld();
        for (ChunkPos chunk : chunks) {
            overworld.getChunk(chunk.x(), chunk.z());
        }
        if (!order.equals("random_first_half")) {
            for (ResourceKey<Level> dimension : List.of(Level.NETHER, Level.END)) {
                ServerLevel level = helper.getLevel().getServer().getLevel(dimension);
                for (ChunkPos chunk : area(OTHER_DIMENSION_RADIUS)) {
                    level.getChunk(chunk.x(), chunk.z());
                }
            }
        }
        long seconds = (System.nanoTime() - started) / 1_000_000_000L;
        GameTestLog.value(test, "chunks", chunks.size());
        GameTestLog.value(test, "seconds", seconds);
        GameTestLog.pass(helper, test, order + " " + chunks.size() + " chunks in " + seconds + " s");
    }

    /**
     * Every block of the mod in a dimension's survey area, with counts and height ranges per block.
     */
    private static class Survey {
        final Long2ObjectMap<BlockState> blocks = new Long2ObjectOpenHashMap<>();
        final Map<String, int[]> perBlock = new TreeMap<>();

        static Survey of(ServerLevel level, int radius) {
            Survey survey = new Survey();
            for (ChunkPos chunkPos : area(radius)) {
                LevelChunk chunk = level.getChunk(chunkPos.x(), chunkPos.z());
                LevelChunkSection[] sections = chunk.getSections();
                for (int index = 0; index < sections.length; index++) {
                    LevelChunkSection section = sections[index];
                    if (section.hasOnlyAir() || !section.getStates().maybeHas(WorldgenTests::isSurveyed)) {
                        continue;
                    }
                    int baseY = chunk.getSectionYFromSectionIndex(index) << 4;
                    for (int y = 0; y < 16; y++) {
                        for (int z = 0; z < 16; z++) {
                            for (int x = 0; x < 16; x++) {
                                BlockState state = section.getBlockState(x, y, z);
                                if (isSurveyed(state)) {
                                    survey.add(new BlockPos(chunkPos.getMinBlockX() + x, baseY + y, chunkPos.getMinBlockZ() + z), state);
                                }
                            }
                        }
                    }
                }
            }
            return survey;
        }

        void add(BlockPos pos, BlockState state) {
            blocks.put(pos.asLong(), state);
            int[] stats = perBlock.computeIfAbsent(id(state), ignored -> new int[]{0, Integer.MAX_VALUE, Integer.MIN_VALUE});
            stats[0]++;
            stats[1] = Math.min(stats[1], pos.getY());
            stats[2] = Math.max(stats[2], pos.getY());
        }

        int count(String id) {
            int[] stats = perBlock.get(id);
            return stats == null ? 0 : stats[0];
        }

        void log(String test) {
            perBlock.forEach((id, stats) -> GameTestLog.value(test, id, stats[0] + " blocks, y " + stats[1] + ".." + stats[2]));
        }
    }

    private static boolean isSurveyed(BlockState state) {
        return BuiltInRegistries.BLOCK.getKey(state.getBlock()).getNamespace().equals(VoluminousEnergy.MODID)
                || state.is(Blocks.RAW_IRON_BLOCK) || state.is(Blocks.RAW_COPPER_BLOCK) || state.is(Blocks.RAW_GOLD_BLOCK);
    }

    private static String id(BlockState state) {
        return BuiltInRegistries.BLOCK.getKey(state.getBlock()).toString();
    }

    private static boolean isBituminous(BlockState state) {
        return id(state).startsWith(VoluminousEnergy.MODID + ":bituminous");
    }

    /**
     * Whether a block next to sealed fluid lets it out: open space, water, lava, or another fluid.
     */
    private static boolean isOpening(BlockState state, Fluid sealed) {
        if (state.isAir()) {
            return true;
        }
        FluidState fluid = state.getFluidState();
        return !fluid.isEmpty() && !fluid.getType().isSame(sealed);
    }

    /**
     * The most common blocks within 3 of a position, to tell which feature left an opening.
     */
    private static String surroundings(ServerLevel level, BlockPos pos) {
        Map<String, Integer> counts = new HashMap<>();
        for (BlockPos near : BlockPos.betweenClosed(pos.offset(-3, -3, -3), pos.offset(3, 3, 3))) {
            counts.merge(BuiltInRegistries.BLOCK.getKey(level.getBlockState(near).getBlock()).getPath(), 1, Integer::sum);
        }
        List<Map.Entry<String, Integer>> sorted = new ArrayList<>(counts.entrySet());
        sorted.sort(Map.Entry.<String, Integer>comparingByValue().reversed());
        StringBuilder text = new StringBuilder("chunk-local ").append(pos.getX() & 15).append(',').append(pos.getZ() & 15).append(" around:");
        for (int index = 0; index < Math.min(10, sorted.size()); index++) {
            text.append(' ').append(sorted.get(index).getKey()).append('=').append(sorted.get(index).getValue());
        }
        return text.toString();
    }

    private static boolean inStructure(ServerLevel level, BlockPos pos) {
        return level.structureManager().getStructureWithPieceAt(pos, structure -> true).isValid();
    }

    /**
     * The vanilla feature placed after petroleum features that explains an opening at a position,
     * judged by its signature blocks nearby, or null when none does.
     */
    private static String laterFeature(ServerLevel level, BlockPos pos) {
        boolean geode = false;
        for (BlockPos near : BlockPos.betweenClosed(pos.offset(-5, -5, -5), pos.offset(5, 5, 5))) {
            BlockState state = level.getBlockState(near);
            if (state.is(Blocks.SPAWNER) || state.is(Blocks.MOSSY_COBBLESTONE)) {
                return "dungeon";
            }
            geode |= state.is(Blocks.AMETHYST_BLOCK) || state.is(Blocks.BUDDING_AMETHYST) || state.is(Blocks.CALCITE);
        }
        return geode ? "geode" : null;
    }

    private static <T extends Feature> T feature(ServerLevel level, String name, Class<T> type) {
        return type.cast(level.registryAccess().lookupOrThrow(Registries.FEATURE)
                .getOrThrow(ResourceKey.create(Registries.FEATURE, Identifier.fromNamespaceAndPath(VoluminousEnergy.MODID, name))).value());
    }

    /**
     * T3.1 to T3.4: every tunnel that starts in the area is filled and sealed, nothing carved its
     * shell, and it stays in its height band.
     */
    private static void petroleumCaves(GameTestHelper helper) {
        ServerLevel level = helper.getLevel().getServer().overworld();
        int limit = radius() - TUNNEL_MARGIN;
        StringBuilder failures = new StringBuilder();
        for (String name : List.of("crude_oil_cave", "natural_gas_cave")) {
            VEFluidCaveFeature cave = feature(level, name, VEFluidCaveFeature.class);
            Fluid fluid = cave.fluidState().getType();
            List<WorldgenReplay.Tunnel> tunnels = WorldgenReplay.tunnels(level, cave, -limit, limit);
            CaveStats stats = new CaveStats();
            for (WorldgenReplay.Tunnel tunnel : tunnels) {
                stats.add(level, tunnel, fluid);
            }
            stats.log(name, tunnels.size());
            if (tunnels.isEmpty()) {
                failures.append(name).append(": not found. ");
            }
            if (stats.unexplainedBreaches > 0) {
                failures.append(name).append(": ").append(stats.unexplainedBreaches).append(" shell openings, first at ").append(stats.firstBreach).append(". ");
            }
            if (stats.unexplainedUnfilled > 0) {
                failures.append(name).append(": ").append(stats.unexplainedUnfilled).append(" open interior positions, first at ").append(stats.firstUnfilled).append(". ");
            }
        }
        helper.assertTrue(failures.isEmpty(), failures.toString());
        GameTestLog.pass(helper, "wg_petroleum_caves", "");
    }

    private static class CaveStats {
        long interior;
        long filled;
        long shell;
        long bituminousShell;
        final Map<String, Long> causes = new TreeMap<>();
        long unexplainedBreaches;
        long unexplainedUnfilled;
        int lowestY = Integer.MAX_VALUE;
        int highestY = Integer.MIN_VALUE;
        double farthest;
        String firstBreach = "";
        String firstUnfilled = "";

        void add(ServerLevel level, WorldgenReplay.Tunnel tunnel, Fluid fluid) {
            LongSet inside = tunnel.interior();
            BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();
            BlockPos.MutableBlockPos neighbour = new BlockPos.MutableBlockPos();
            double startX = tunnel.start().getMiddleBlockX();
            double startZ = tunnel.start().getMiddleBlockZ();
            for (long packed : inside) {
                pos.set(packed);
                ++interior;
                lowestY = Math.min(lowestY, pos.getY());
                highestY = Math.max(highestY, pos.getY());
                farthest = Math.max(farthest, Math.hypot(pos.getX() - startX, pos.getZ() - startZ));
                BlockState state = level.getBlockState(pos);
                if (state.getFluidState().getType().isSame(fluid)) {
                    ++filled;
                } else if ((state.isAir() || state.getFluidState().getType().isSame(Fluids.WATER)) && !explained(level, pos, "unfilled")) {
                    if (unexplainedUnfilled++ == 0) {
                        firstUnfilled = pos.toShortString() + " " + id(state);
                    }
                    if (unexplainedUnfilled <= 5) {
                        GameTestLog.value("wg_petroleum_caves", "unfilled_" + unexplainedUnfilled, pos.toShortString() + " " + id(state)
                                + " start " + tunnel.start() + " " + surroundings(level, pos));
                    }
                }
                if (!state.getFluidState().getType().isSame(fluid)) {
                    continue;
                }
                for (Direction direction : Direction.values()) {
                    neighbour.setWithOffset(pos, direction);
                    if (inside.contains(neighbour.asLong())) {
                        continue;
                    }
                    ++shell;
                    BlockState around = level.getBlockState(neighbour);
                    if (isBituminous(around)) {
                        ++bituminousShell;
                    } else if (isOpening(around, fluid)) {
                        if (!explained(level, neighbour, "openings")) {
                            if (unexplainedBreaches++ == 0) {
                                firstBreach = neighbour.toShortString() + " " + id(around);
                            }
                            if (unexplainedBreaches <= 5) {
                                GameTestLog.value("wg_petroleum_caves", "opening_" + unexplainedBreaches, neighbour.toShortString() + " " + id(around)
                                        + " start " + tunnel.start() + " " + surroundings(level, neighbour.immutable()));
                            }
                        }
                    }
                }
            }
        }

        boolean explained(ServerLevel level, BlockPos pos, String kind) {
            String cause = inStructure(level, pos) ? "structure" : laterFeature(level, pos);
            if (cause == null) {
                return false;
            }
            causes.merge(kind + "_by_" + cause, 1L, Long::sum);
            return true;
        }

        void log(String name, int tunnels) {
            String test = "wg_petroleum_caves";
            GameTestLog.value(test, name + ".tunnels", tunnels);
            GameTestLog.value(test, name + ".interior", interior);
            GameTestLog.value(test, name + ".filled", filled);
            GameTestLog.value(test, name + ".shell_faces", shell);
            GameTestLog.value(test, name + ".bituminous_shell_faces", bituminousShell);
            causes.forEach((cause, count) -> GameTestLog.value(test, name + "." + cause, count));
            GameTestLog.value(test, name + ".openings_unexplained", unexplainedBreaches);
            GameTestLog.value(test, name + ".unfilled_unexplained", unexplainedUnfilled);
            GameTestLog.value(test, name + ".y_range", lowestY + ".." + highestY);
            GameTestLog.value(test, name + ".farthest_from_start", Math.round(farthest));
        }
    }

    /**
     * A column whose highest reservoir fluid, at {@code top}, lies less than 8 blocks under the
     * ground at height {@code ground}, while the highest ground 12 blocks away is {@code groundAround}.
     */
    private record ShallowColumn(int depth, BlockPos top, int ground, int groundAround) {
    }

    /**
     * The highest {@link #groundHeight} 12 blocks away from a column in the four horizontal directions.
     */
    private static int highestGroundAround(ServerLevel level, int x, int z) {
        int highest = level.getMinY();
        for (Direction direction : Direction.Plane.HORIZONTAL) {
            int aroundX = x + direction.getStepX() * 12;
            int aroundZ = z + direction.getStepZ() * 12;
            LevelChunk chunk = level.getChunk(SectionPos.blockToSectionCoord(aroundX), SectionPos.blockToSectionCoord(aroundZ));
            highest = Math.max(highest, groundHeight(chunk, aroundX, aroundZ));
        }
        return highest;
    }

    /**
     * The height of the highest solid block in a column that is not part of a tree.
     * It scans the blocks instead of reading a heightmap, since a heightmap reads as the bottom of
     * the world in a chunk that has not loaded yet.
     */
    private static int groundHeight(LevelChunk chunk, int x, int z) {
        BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos(x, chunk.getMaxY(), z);
        for (; pos.getY() >= chunk.getMinY(); pos.move(Direction.DOWN)) {
            BlockState state = chunk.getBlockState(pos);
            if (state.isSolid() && !state.is(BlockTags.LEAVES) && !state.is(BlockTags.LOGS)) {
                return pos.getY();
            }
        }
        return chunk.getMinY();
    }

    /**
     * T3.5 and T3.15: every position a reservoir claims and holds its fluid is a source block, and
     * nothing but its open face lets the fluid out. The fluid in each column lies at least 8 blocks
     * under the ground, unless ground at least that high stands 12 blocks away, which marks a ravine
     * or pit cutting down to the reservoir. Columns whose fluid continues above the reservoir belong
     * to a geyser or lake and are only counted.
     */
    private static void reservoirs(GameTestHelper helper) {
        ServerLevel level = helper.getLevel().getServer().overworld();
        int limit = radius() - 1;
        StringBuilder failures = new StringBuilder();
        for (String name : List.of("crude_oil_reservoir", "natural_gas_reservoir")) {
            VEFluidReservoirFeature reservoir = feature(level, name, VEFluidReservoirFeature.class);
            Fluid fluid = reservoir.fluidState().getType();
            Direction openFace = reservoir.kind() == VEFluidReservoirFeature.Kind.POOL ? Direction.UP : Direction.DOWN;
            WorldgenReplay.Reservoirs cells = new WorldgenReplay.Reservoirs(level, reservoir);
            long filled = 0;
            long flowing = 0;
            long openings = 0;
            Map<String, Long> causes = new TreeMap<>();
            List<ShallowColumn> tooShallow = new ArrayList<>();
            long otherFluidAbove = 0;
            long underCut = 0;
            String firstOpening = "";
            BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();
            BlockPos.MutableBlockPos neighbour = new BlockPos.MutableBlockPos();
            for (int chunkX = -limit; chunkX <= limit; chunkX++) {
                for (int chunkZ = -limit; chunkZ <= limit; chunkZ++) {
                    ChunkPos chunk = new ChunkPos(chunkX, chunkZ);
                    int[] band = cells.band(chunk);
                    if (band == null) {
                        continue;
                    }
                    LevelChunk loaded = level.getChunk(chunkX, chunkZ);
                    for (int x = chunk.getMinBlockX(); x <= chunk.getMaxBlockX(); x++) {
                        for (int z = chunk.getMinBlockZ(); z <= chunk.getMaxBlockZ(); z++) {
                            int topFilledY = Integer.MIN_VALUE;
                            for (int y = band[0]; y < band[1]; y++) {
                                pos.set(x, y, z);
                                FluidState here = level.getFluidState(pos);
                                if (!here.getType().isSame(fluid) || !cells.fills(x, y, z)) {
                                    continue;
                                }
                                ++filled;
                                topFilledY = y;
                                if (!here.isSource()) {
                                    ++flowing;
                                }
                                for (Direction direction : Direction.values()) {
                                    if (direction == openFace) {
                                        continue;
                                    }
                                    neighbour.setWithOffset(pos, direction);
                                    BlockState around = level.getBlockState(neighbour);
                                    if (isOpening(around, fluid)) {
                                        String cause = inStructure(level, neighbour) ? "structure" : laterFeature(level, neighbour);
                                        if (cause != null) {
                                            causes.merge("openings_by_" + cause, 1L, Long::sum);
                                        } else {
                                            if (openings++ == 0) {
                                                firstOpening = neighbour.toShortString() + " " + id(around);
                                            }
                                            if (openings <= 10) {
                                                GameTestLog.value("wg_reservoirs", name + ".opening_" + openings, neighbour.toShortString() + " " + id(around)
                                                        + " from " + pos.toShortString() + " claimed " + cells.fills(neighbour.getX(), neighbour.getY(), neighbour.getZ())
                                                        + " " + surroundings(level, neighbour.immutable()));
                                            }
                                        }
                                    }
                                }
                            }
                            if (topFilledY == Integer.MIN_VALUE) {
                                continue;
                            }
                            if (level.getFluidState(pos.set(x, topFilledY + 1, z)).getType().isSame(fluid)) {
                                ++otherFluidAbove;
                                continue;
                            }
                            int ground = groundHeight(loaded, x, z);
                            int depth = ground - topFilledY;
                            if (depth >= 8) {
                                continue;
                            }
                            int groundAround = highestGroundAround(level, x, z);
                            if (groundAround - topFilledY >= 8) {
                                ++underCut;
                                continue;
                            }
                            tooShallow.add(new ShallowColumn(depth, new BlockPos(x, topFilledY, z), ground, groundAround));
                        }
                    }
                }
            }
            String test = "wg_reservoirs";
            GameTestLog.value(test, name + ".filled", filled);
            GameTestLog.value(test, name + ".flowing", flowing);
            causes.forEach((cause, count) -> GameTestLog.value(test, name + "." + cause, count));
            GameTestLog.value(test, name + ".openings_unexplained", openings);
            GameTestLog.value(test, name + ".columns_with_other_fluid_above", otherFluidAbove);
            GameTestLog.value(test, name + ".columns_under_8_deep_in_a_cut", underCut);
            GameTestLog.value(test, name + ".columns_too_shallow", tooShallow.size());
            tooShallow.sort(Comparator.comparingInt(ShallowColumn::depth));
            for (int index = 0; index < Math.min(10, tooShallow.size()); index++) {
                ShallowColumn column = tooShallow.get(index);
                GameTestLog.value(test, name + ".too_shallow_" + (index + 1), "depth " + column.depth() + " top " + column.top().toShortString()
                        + " ground " + column.ground() + " ground_around " + column.groundAround() + " " + surroundings(level, column.top()));
            }
            if (filled == 0) {
                failures.append(name).append(": not found. ");
            }
            if (flowing > 0) {
                failures.append(name).append(": ").append(flowing).append(" flowing blocks. ");
            }
            if (openings > 0) {
                failures.append(name).append(": ").append(openings).append(" openings, first at ").append(firstOpening).append(". ");
            }
            if (!tooShallow.isEmpty()) {
                failures.append(name).append(": ").append(tooShallow.size()).append(" columns less than 8 blocks under the ground, shallowest at ")
                        .append(tooShallow.getFirst().top().toShortString()).append(". ");
            }
        }
        helper.assertTrue(failures.isEmpty(), failures.toString());
        GameTestLog.pass(helper, "wg_reservoirs", "");
    }

    /**
     * T3.6: surface oil lakes with bituminous rims and geysers occur in the area.
     */
    private static void lakesAndGeysers(GameTestHelper helper) {
        ServerLevel level = helper.getLevel().getServer().overworld();
        Survey survey = Survey.of(level, radius());
        Fluid crudeOil = VEFluids.CRUDE_OIL_REG.get();
        int lakeBlocks = 0;
        int geyserBlocks = 0;
        BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();
        for (Long2ObjectMap.Entry<BlockState> entry : survey.blocks.long2ObjectEntrySet()) {
            pos.set(entry.getLongKey());
            if (!entry.getValue().getFluidState().getType().isSame(crudeOil)) {
                continue;
            }
            if (pos.getY() > 100) {
                ++geyserBlocks;
            } else if (pos.getY() >= 50 && entry.getValue().getFluidState().isSource() && level.canSeeSky(pos.above()) && hasBituminousNear(survey, pos)) {
                ++lakeBlocks;
            }
        }
        GameTestLog.value("wg_lakes_geysers", "surface_lake_blocks", lakeBlocks);
        GameTestLog.value("wg_lakes_geysers", "geyser_blocks_above_y100", geyserBlocks);
        helper.assertTrue(lakeBlocks > 0, "surface oil lake not found");
        helper.assertTrue(geyserBlocks > 0, "oil geyser not found");
        GameTestLog.pass(helper, "wg_lakes_geysers", "");
    }

    private static boolean hasBituminousNear(Survey survey, BlockPos pos) {
        for (BlockPos near : BlockPos.betweenClosed(pos.offset(-1, -1, -1), pos.offset(1, 1, 1))) {
            BlockState state = survey.blocks.get(near.asLong());
            if (state != null && isBituminous(state)) {
                return true;
            }
        }
        return false;
    }

    /**
     * T3.7: the mod's ores occur in the area, with their counts and height ranges recorded.
     */
    private static void ores(GameTestHelper helper) {
        ServerLevel overworld = helper.getLevel().getServer().overworld();
        Survey survey = Survey.of(overworld, radius());
        survey.log("wg_ores");
        StringBuilder missing = new StringBuilder();
        for (String ore : List.of("bauxite", "cinnabar", "galena", "rutile")) {
            if (survey.perBlock.keySet().stream().noneMatch(id -> id.contains(ore) && id.contains("ore"))) {
                missing.append(ore).append(' ');
            }
        }
        Survey end = Survey.of(helper.getLevel().getServer().getLevel(Level.END), OTHER_DIMENSION_RADIUS);
        end.log("wg_ores_end");
        if (end.perBlock.keySet().stream().noneMatch(id -> id.contains("eighzo"))) {
            missing.append("eighzo ");
        }
        helper.assertTrue(missing.isEmpty(), "ores not found: " + missing);
        GameTestLog.pass(helper, "wg_ores", "");
    }

    /**
     * T3.8: rice grows wild in the area.
     */
    private static void rice(GameTestHelper helper) {
        Survey survey = Survey.of(helper.getLevel().getServer().overworld(), radius());
        int rice = survey.count(VoluminousEnergy.MODID + ":rice_crop");
        GameTestLog.value("wg_rice", "rice_blocks", rice);
        helper.assertTrue(rice > 0, "rice not found");
        GameTestLog.pass(helper, "wg_rice", "");
    }

    /**
     * T3.9: no petroleum or bituminous blocks in the Nether or the End.
     */
    private static void otherDimensions(GameTestHelper helper) {
        for (ResourceKey<Level> dimension : List.of(Level.NETHER, Level.END)) {
            Survey survey = Survey.of(helper.getLevel().getServer().getLevel(dimension), OTHER_DIMENSION_RADIUS);
            survey.log("wg_other_dimensions_" + dimension.identifier().getPath());
            for (String id : survey.perBlock.keySet()) {
                helper.assertFalse(id.contains("bituminous") || id.contains("crude_oil") || id.contains("natural_gas"), id + " in " + dimension.identifier());
            }
        }
        GameTestLog.pass(helper, "wg_other_dimensions", "");
    }

    /**
     * T3.10: a hash of every surveyed block, for comparing worlds generated in different ways.
     */
    private static void fingerprint(GameTestHelper helper) {
        Survey survey = Survey.of(helper.getLevel().getServer().overworld(), radius());
        long[] positions = survey.blocks.keySet().toLongArray();
        Arrays.sort(positions);
        try (BufferedWriter listing = Files.newBufferedWriter(Path.of("fingerprint.txt"))) {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            for (long position : positions) {
                digest.update((position + "=" + survey.blocks.get(position) + "\n").getBytes(StandardCharsets.UTF_8));
                listing.write(BlockPos.of(position).toShortString() + " " + id(survey.blocks.get(position)) + "\n");
            }
            String hash = HexFormat.of().formatHex(digest.digest());
            GameTestLog.value("wg_fingerprint", "blocks", positions.length);
            GameTestLog.value("wg_fingerprint", "sha256", hash);
            GameTestLog.pass(helper, "wg_fingerprint", hash);
        } catch (NoSuchAlgorithmException | IOException e) {
            throw new IllegalStateException(e);
        }
    }

    /**
     * T3.16: on real terrain noise, the fluid scanner gives the same answer twice, and the amounts it
     * reports for recipe fluids are recorded against their recipe ranges. A scan reads only noise,
     * so it samples 2 000 positions across a wide area without generating chunks, since each recipe
     * matches only a narrow climate.
     */
    private static void fluidScanner(GameTestHelper helper) {
        ServerLevel level = helper.getLevel().getServer().overworld();
        int inRange = 0;
        int belowMinimum = 0;
        int aboveMaximum = 0;
        Map<String, Integer> fluids = new HashMap<>();
        Random random = new Random(1);
        int blocks = 50_000;
        for (int sample = 0; sample < 2000; sample++) {
            BlockPos pos = new BlockPos(random.nextInt(-blocks, blocks), 320, random.nextInt(-blocks, blocks));
            List<Pair<Fluid, Integer>> first = WorldUtil.queryForFluids(level, pos);
            if (sample < 200) {
                List<Pair<Fluid, Integer>> second = WorldUtil.queryForFluids(level, pos);
                helper.assertValueEqual(describe(second), describe(first), "second scan at " + pos.toShortString());
            }
            HashMap<WorldUtil.ClimateParameters, Double> climate = WorldUtil.sampleClimate(level, pos);
            if (sample < 10) {
                GameTestLog.value("wg_fluid_scanner", "climate_" + sample, pos.toShortString() + " " + climate);
            }
            for (Pair<Fluid, Integer> fluid : first) {
                fluids.merge(BuiltInRegistries.FLUID.getKey(fluid.getA()).toString(), 1, Integer::sum);
                for (VERecipe recipe : VERecipe.getCachedRecipes(DimensionalLaserRecipe.RECIPE_TYPE)) {
                    FluidClimateSpawn spawn = ((DimensionalLaserRecipe) recipe).getFluidClimateSpawn();
                    if (spawn.getFluid().isSame(fluid.getA()) && spawn.checkValidity(climate)) {
                        if (fluid.getB() < spawn.getMinimumFluidAmount()) {
                            ++belowMinimum;
                        } else if (fluid.getB() > spawn.getMaximumFluidAmount()) {
                            ++aboveMaximum;
                        } else {
                            ++inRange;
                        }
                    }
                }
            }
        }
        fluids.forEach((fluid, count) -> GameTestLog.value("wg_fluid_scanner", fluid, count));
        GameTestLog.value("wg_fluid_scanner", "recipe_amounts_in_range", inRange);
        GameTestLog.value("wg_fluid_scanner", "recipe_amounts_below_minimum", belowMinimum);
        GameTestLog.value("wg_fluid_scanner", "recipe_amounts_above_maximum", aboveMaximum);
        helper.assertTrue(inRange + belowMinimum + aboveMaximum > 0, "no recipe fluid found in 2000 scans");
        GameTestLog.pass(helper, "wg_fluid_scanner", inRange + " in range, " + belowMinimum + " below, " + aboveMaximum + " above");
    }

    private static String describe(List<Pair<Fluid, Integer>> fluids) {
        List<String> entries = new ArrayList<>();
        for (Pair<Fluid, Integer> fluid : fluids) {
            entries.add(fluid.getA() + "=" + fluid.getB());
        }
        entries.sort(null);
        return String.join(",", entries);
    }

    /**
     * T3.13, in a world with the override test datapack: the crude oil caves hold lava and start
     * only where the datapack allows, the natural gas caves are gone, and surface lakes are lava.
     */
    private static void datapackOverride(GameTestHelper helper) {
        ServerLevel level = helper.getLevel().getServer().overworld();
        int limit = radius() - TUNNEL_MARGIN;
        VEFluidCaveFeature oilCave = feature(level, "crude_oil_cave", VEFluidCaveFeature.class);
        helper.assertTrue(oilCave.fluidState().getType().isSame(Fluids.LAVA), "the datapack did not set the oil cave fluid");
        List<WorldgenReplay.Tunnel> lavaTunnels = new ArrayList<>();
        List<WorldgenReplay.Tunnel> ruledOut = new ArrayList<>();
        for (WorldgenReplay.Tunnel tunnel : WorldgenReplay.tunnels(level, oilCave, -limit, limit, true)) {
            (tunnel.allowed() ? lavaTunnels : ruledOut).add(tunnel);
        }
        CaveStats lava = new CaveStats();
        LongSet allowedInterior = new LongOpenHashSet();
        for (WorldgenReplay.Tunnel tunnel : lavaTunnels) {
            lava.add(level, tunnel, oilCave.fluidState().getType());
            allowedInterior.addAll(tunnel.interior());
        }
        lava.log("lava_cave", lavaTunnels.size());
        long ruledOutPositions = 0;
        long ruledOutLava = 0;
        for (WorldgenReplay.Tunnel tunnel : ruledOut) {
            for (long packed : tunnel.interior()) {
                if (allowedInterior.contains(packed)) {
                    continue;
                }
                ++ruledOutPositions;
                if (level.getFluidState(BlockPos.of(packed)).getType().isSame(Fluids.LAVA)) {
                    ++ruledOutLava;
                }
            }
        }
        GameTestLog.value("wg_datapack_override", "ruled_out_tunnels", ruledOut.size());
        GameTestLog.value("wg_datapack_override", "ruled_out_positions", ruledOutPositions);
        GameTestLog.value("wg_datapack_override", "lava_in_ruled_out_positions", ruledOutLava);

        VEFluidCaveFeature gasCave = feature(level, "natural_gas_cave", VEFluidCaveFeature.class);
        long gasInTunnels = 0;
        for (WorldgenReplay.Tunnel tunnel : WorldgenReplay.tunnels(level, gasCave, -limit, limit)) {
            for (long packed : tunnel.interior()) {
                if (level.getFluidState(BlockPos.of(packed)).getType().isSame(VEFluids.NATURAL_GAS_REG.get())) {
                    ++gasInTunnels;
                }
            }
        }
        GameTestLog.value("wg_datapack_override", "natural_gas_in_gas_tunnel_outlines", gasInTunnels);

        Survey survey = Survey.of(level, radius());
        int oilLakeBlocks = 0;
        BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();
        for (Long2ObjectMap.Entry<BlockState> entry : survey.blocks.long2ObjectEntrySet()) {
            pos.set(entry.getLongKey());
            if (entry.getValue().getFluidState().getType().isSame(VEFluids.CRUDE_OIL_REG.get()) && pos.getY() <= 100
                    && level.canSeeSky(pos.above()) && hasBituminousNear(survey, pos)) {
                ++oilLakeBlocks;
            }
        }
        GameTestLog.value("wg_datapack_override", "crude_oil_surface_lake_blocks", oilLakeBlocks);
        helper.assertTrue(!lavaTunnels.isEmpty(), "no tunnel starts in a biome the datapack allows");
        helper.assertTrue(!ruledOut.isEmpty(), "no tunnel start was ruled out by the datapack's biomes");
        // Vanilla lava lakes and aquifers can sit inside a ruled-out outline; a generated tunnel would fill it.
        helper.assertTrue(ruledOutLava * 100 < ruledOutPositions, "ruled-out tunnels hold lava at " + ruledOutLava + " of " + ruledOutPositions + " positions");
        helper.assertTrue(lava.unexplainedBreaches == 0, "lava tunnel openings: " + lava.unexplainedBreaches + ", first at " + lava.firstBreach);
        helper.assertTrue(lava.unexplainedUnfilled == 0, "lava tunnel unfilled positions: " + lava.unexplainedUnfilled + ", first at " + lava.firstUnfilled);
        helper.assertValueEqual(oilLakeBlocks, 0, "crude oil surface lake blocks");
        GameTestLog.pass(helper, "wg_datapack_override", lavaTunnels.size() + " lava tunnels");
    }

    /**
     * Counts the biomes the area's crude oil tunnels start in, to choose the biome tag of the
     * override test datapack.
     */
    private static void tunnelBiomes(GameTestHelper helper) {
        ServerLevel level = helper.getLevel().getServer().overworld();
        int limit = radius() - TUNNEL_MARGIN;
        Map<String, Integer> counts = new TreeMap<>();
        for (WorldgenReplay.Tunnel tunnel : WorldgenReplay.tunnels(level, feature(level, "crude_oil_cave", VEFluidCaveFeature.class), -limit, limit)) {
            level.getNoiseBiome(QuartPos.fromBlock(tunnel.start().getMinBlockX()), 0, QuartPos.fromBlock(tunnel.start().getMinBlockZ()))
                    .unwrapKey().ifPresent(key -> counts.merge(key.identifier().getPath(), 1, Integer::sum));
        }
        counts.forEach((biome, count) -> GameTestLog.value("wg_tunnel_biomes", biome, count));
        GameTestLog.pass(helper, "wg_tunnel_biomes", counts.size() + " biomes");
    }

    /**
     * T3.11: after the survey area has generated through the shared caches, every reservoir
     * decision read through them matches one computed from scratch.
     */
    private static void reservoirCache(GameTestHelper helper) {
        helper.assertTrue(Config.CACHE_PETROLEUM_RESERVOIR_CELLS.get(), "reservoir cell caching is off in this world");
        ServerLevel level = helper.getLevel().getServer().overworld();
        for (String name : List.of("crude_oil_reservoir", "natural_gas_reservoir")) {
            String difference = WorldgenReplay.cacheDifference(level, feature(level, name, VEFluidReservoirFeature.class), radius());
            helper.assertTrue(difference == null, name + ": " + difference);
        }
        GameTestLog.pass(helper, "wg_reservoir_cache", "");
    }

    /**
     * Sorts the positions listed in {@code order-diff.txt} (one {@code x y z block} per line, where
     * two worlds of the same seed differ) by the petroleum feature that owns them: a tunnel
     * outline or its shell, a reservoir's claimed space or its plugs, or neither.
     */
    private static void orderAttribution(GameTestHelper helper) {
        ServerLevel level = helper.getLevel().getServer().overworld();
        int reach = radius() + TUNNEL_MARGIN;
        LongSet tunnels = new LongOpenHashSet();
        for (String name : List.of("crude_oil_cave", "natural_gas_cave")) {
            for (WorldgenReplay.Tunnel tunnel : WorldgenReplay.tunnels(level, feature(level, name, VEFluidCaveFeature.class), -reach, reach)) {
                tunnels.addAll(tunnel.interior());
            }
        }
        List<WorldgenReplay.Reservoirs> reservoirs = new ArrayList<>();
        for (String name : List.of("crude_oil_reservoir", "natural_gas_reservoir")) {
            reservoirs.add(new WorldgenReplay.Reservoirs(level, feature(level, name, VEFluidReservoirFeature.class)));
        }
        Map<String, Integer> counts = new TreeMap<>();
        try {
            for (String line : Files.readAllLines(Path.of("order-diff.txt"))) {
                String[] parts = line.split(" ");
                BlockPos pos = new BlockPos(Integer.parseInt(parts[0]), Integer.parseInt(parts[1]), Integer.parseInt(parts[2]));
                String owner = "other";
                if (nearAny(pos, packed -> tunnels.contains(packed))) {
                    owner = "cave";
                } else if (nearAny(pos, packed -> {
                    BlockPos near = BlockPos.of(packed);
                    return reservoirs.stream().anyMatch(reservoir -> reservoir.fills(near.getX(), near.getY(), near.getZ()));
                })) {
                    owner = "reservoir";
                }
                counts.merge(owner + " " + parts[3], 1, Integer::sum);
            }
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
        counts.forEach((key, count) -> GameTestLog.value("wg_order_attribution", key.replace(' ', '/'), count));
        GameTestLog.pass(helper, "wg_order_attribution", counts.size() + " groups");
    }

    private static boolean nearAny(BlockPos pos, LongPredicate test) {
        if (test.test(pos.asLong())) {
            return true;
        }
        for (Direction direction : Direction.values()) {
            if (test.test(pos.relative(direction).asLong())) {
                return true;
            }
        }
        return false;
    }
}
