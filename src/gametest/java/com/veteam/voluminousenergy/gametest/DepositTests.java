package com.veteam.voluminousenergy.gametest;

import com.veteam.voluminousenergy.fluids.VEFluids;
import com.veteam.voluminousenergy.persistence.ChunkFluid;
import com.veteam.voluminousenergy.persistence.SingleChunkFluid;
import com.veteam.voluminousenergy.recipe.DimensionalLaserRecipe;
import com.veteam.voluminousenergy.recipe.VERecipe;
import com.veteam.voluminousenergy.util.WorldUtil;
import com.veteam.voluminousenergy.util.climate.FluidClimateSpawn;
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import oshi.util.tuples.Pair;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Fluid deposits the scanner reports for the Dimensional Laser (spec fluids/climate-deposits,
 * test plan T2.36 to T2.39).
 */
public class DepositTests {
    private static final int POSITIONS = 50;

    private DepositTests() {
    }

    static void register(List<GameTestDefinition> tests) {
        tests.add(GameTestDefinition.of("deposit_repeatable", 20, DepositTests::repeatable));
        tests.add(GameTestDefinition.of("deposit_amounts_in_range", 20, DepositTests::amountsInRange));
        tests.add(GameTestDefinition.of("deposit_water_or_lava_fallback", 20, DepositTests::waterOrLavaFallback));
        tests.add(GameTestDefinition.of("deposit_saved_data_round_trip", 20, DepositTests::savedDataRoundTrip));
    }

    private static BlockPos position(int index) {
        return new BlockPos(index * 1_237 - 30_000, 320, index * 2_903 - 70_000);
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
     * T2.36: scanning the same position twice gives the same result.
     */
    private static void repeatable(GameTestHelper helper) {
        for (int index = 0; index < POSITIONS; index++) {
            BlockPos pos = position(index);
            String first = describe(WorldUtil.queryForFluids(helper.getLevel(), pos));
            String second = describe(WorldUtil.queryForFluids(helper.getLevel(), pos));
            helper.assertValueEqual(second, first, "second scan at " + pos.toShortString());
        }
        GameTestLog.pass(helper, "deposit_repeatable", POSITIONS + " positions");
    }

    /**
     * T2.37: every recipe fluid a scan reports lies within that recipe's minimum and maximum.
     */
    private static void amountsInRange(GameTestHelper helper) {
        int checked = 0;
        for (int index = 0; index < POSITIONS; index++) {
            BlockPos pos = position(index);
            HashMap<WorldUtil.ClimateParameters, Double> climate = WorldUtil.sampleClimate(helper.getLevel(), pos);
            for (Pair<Fluid, Integer> fluid : WorldUtil.queryForFluids(helper.getLevel(), pos)) {
                for (VERecipe recipe : VERecipe.getCachedRecipes(DimensionalLaserRecipe.RECIPE_TYPE)) {
                    FluidClimateSpawn spawn = ((DimensionalLaserRecipe) recipe).getFluidClimateSpawn();
                    if (spawn.getFluid().isSame(fluid.getA()) && spawn.checkValidity(climate)) {
                        helper.assertValueInBetween(spawn.getMinimumFluidAmount(), fluid.getB(), spawn.getMaximumFluidAmount(),
                                fluid.getA() + " at " + pos.toShortString());
                        ++checked;
                    }
                }
            }
        }
        GameTestLog.pass(helper, "deposit_amounts_in_range", checked + " recipe fluids");
    }

    /**
     * T2.38: a position whose climate matches no recipe reports exactly one of water or lava.
     */
    private static void waterOrLavaFallback(GameTestHelper helper) {
        int fallbacks = 0;
        for (int index = 0; index < POSITIONS; index++) {
            BlockPos pos = position(index);
            HashMap<WorldUtil.ClimateParameters, Double> climate = WorldUtil.sampleClimate(helper.getLevel(), pos);
            boolean anyRecipe = VERecipe.getCachedRecipes(DimensionalLaserRecipe.RECIPE_TYPE).stream()
                    .anyMatch(recipe -> ((DimensionalLaserRecipe) recipe).getFluidClimateSpawn().checkValidity(climate));
            if (anyRecipe) {
                continue;
            }
            List<Pair<Fluid, Integer>> fluids = WorldUtil.queryForFluids(helper.getLevel(), pos);
            helper.assertValueEqual(fluids.size(), 1, "fluids at " + pos.toShortString());
            Fluid fluid = fluids.getFirst().getA();
            helper.assertTrue(fluid.isSame(Fluids.WATER) || fluid.isSame(Fluids.LAVA), "fallback at " + pos.toShortString() + " is " + fluid);
            ++fallbacks;
        }
        helper.assertTrue(fallbacks > 0, "no sampled position fell back to water or lava");
        GameTestLog.pass(helper, "deposit_water_or_lava_fallback", fallbacks + " positions");
    }

    /**
     * T2.39: a scanned chunk's fluids survive the saved-data codec unchanged.
     */
    private static void savedDataRoundTrip(GameTestHelper helper) {
        ChunkFluid saved = new ChunkFluid(new ChunkPos(12, -34), List.of(new SingleChunkFluid(VEFluids.CRUDE_OIL_REG.get(), 50_000)));
        Tag tag = ChunkFluid.CODEC.encodeStart(NbtOps.INSTANCE, saved).getOrThrow();
        ChunkFluid loaded = ChunkFluid.CODEC.parse(NbtOps.INSTANCE, tag).getOrThrow();
        helper.assertValueEqual(loaded.getChunkPos(), saved.getChunkPos(), "chunk position");
        helper.assertValueEqual(loaded.getFluids().size(), 1, "fluid count");
        helper.assertTrue(loaded.getFluids().getFirst().getFluid().isSame(VEFluids.CRUDE_OIL_REG.get()), "fluid");
        helper.assertValueEqual(loaded.getFluids().getFirst().getAmount(), 50_000, "amount");
        GameTestLog.pass(helper, "deposit_saved_data_round_trip", "");
    }
}
