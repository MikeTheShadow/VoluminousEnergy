package com.veteam.voluminousenergy.util;

import com.veteam.voluminousenergy.persistence.ChunkFluid;
import com.veteam.voluminousenergy.persistence.ChunkFluids;
import com.veteam.voluminousenergy.recipe.DimensionalLaserRecipe;
import com.veteam.voluminousenergy.recipe.VERecipe;
import com.veteam.voluminousenergy.util.climate.DepositNoise;
import com.veteam.voluminousenergy.util.climate.FluidClimateSpawn;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerChunkCache;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Climate;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.levelgen.RandomState;
import net.minecraft.world.level.levelgen.densityfunction.SamplerContext;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.storage.SavedDataStorage;
import oshi.util.tuples.Pair;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import java.util.concurrent.atomic.AtomicReference;

public class WorldUtil {

    /*public static FluidClimateSpawn HOT_CRUDE_OIL_SPAWN = new FluidClimateSpawn(
            new Pair<Float, Float>(0F, 2F),                 // Continentalness
            new Pair<Float, Float>(-0.25F, 0.765F),         // Erosion
            new Pair<Float, Float>(0.05F, 0.415F),          // Humidity
            new Pair<Float, Float>(0.5F, 2F),               // Temperature
            VEFluids.CRUDE_OIL_REG.get(),
            262_144,
            1_048_576
    );*/

    public enum ClimateParameters {
        CONTINENTALNESS,
        EROSION,
        HUMIDITY,
        TEMPERATURE
    }

    public static HashMap<ClimateParameters, Double> sampleClimate(Level level, BlockPos pos) {
        if (level.isClientSide()) {
            return new HashMap<>();
        }

        ServerLevel serverLevel = level.getServer().getLevel(level.dimension());
        ServerChunkCache serverchunkcache = serverLevel.getChunkSource();

        RandomState randomState = serverchunkcache.randomState();

        Climate.Sampler sampler = randomState.createClimateSampler(SamplerContext.EMPTY_UNCACHED);

        double continentalness = sampler.continentalness().sampleValue(pos.getX(), pos.getY(), pos.getZ());
        double erosion = sampler.erosion().sampleValue(pos.getX(), pos.getY(), pos.getZ());
        double humidity = sampler.humidity().sampleValue(pos.getX(), pos.getY(), pos.getZ());
        double temperature = sampler.temperature().sampleValue(pos.getX(), pos.getY(), pos.getZ());

        HashMap<ClimateParameters, Double> climateMap = new HashMap<>();
        climateMap.put(ClimateParameters.CONTINENTALNESS, continentalness);
        climateMap.put(ClimateParameters.EROSION, erosion);
        climateMap.put(ClimateParameters.HUMIDITY, humidity);
        climateMap.put(ClimateParameters.TEMPERATURE, temperature);
        return climateMap;
    }

    public static ChunkFluid getFluidFromPosition(Level level, BlockPos pos) {
        ServerLevel serverLevel = level.getServer().getLevel(level.dimension());
        ArrayList<Pair<Fluid, Integer>> fluidsList = WorldUtil.queryForFluids(level, pos);
        ChunkAccess chunkAccess = level.getChunk(pos);
        ChunkFluids chunkFluids = ChunkFluids.getInstance();

        ChunkFluid chunkFluid = chunkFluids.getOrElse(new ChunkFluid(
                chunkAccess.getPos(),
                fluidsList
        ));

        if (!chunkFluids.hasChunkFluid(chunkFluid)) {
            chunkFluids.add(chunkFluid);
            chunkFluids.setDirty();
            SavedDataStorage storage = serverLevel.getDataStorage();
            storage.set(ChunkFluids.TYPE, chunkFluids);
            storage.scheduleSave();
        }
        return chunkFluid;
    }

    public static ArrayList<Pair<Fluid, Integer>> queryForFluids(Level level, BlockPos pos) {
        AtomicReference<ArrayList<Pair<Fluid, Integer>>> fluidsAtLocation = new AtomicReference<>(new ArrayList<>());

        HashMap<ClimateParameters, Double> sampledClimate = sampleClimate(level, pos);
        if (sampledClimate.isEmpty())
            return fluidsAtLocation.get(); // Return empty as well; Likely client side if this is the case
        VERecipe.getCachedRecipes(DimensionalLaserRecipe.RECIPE_TYPE).parallelStream().forEach(recipe -> {
            if (recipe instanceof DimensionalLaserRecipe dimensionalLaserRecipe) {
                FluidClimateSpawn spawn = dimensionalLaserRecipe.getFluidClimateSpawn();
                if (spawn.checkValidity(sampledClimate)) { // This might be unsafe, would prefer to avoid atomic as this is read-only
                    fluidsAtLocation.get().add(new Pair<>(spawn.getFluid(), spawn.calculateDepositAmount(sampledClimate, pos, level)));
                }
            }
        });


        // Add a fluid to the location if no other fluids exist. Can make this if it's only 1 add a pair
        if (fluidsAtLocation.get().size() == 0 && level instanceof ServerLevel serverLevel) {

            Random random = new Random(randomSeedFromClimate(sampledClimate));

            if (random.nextInt(10) > 4) {

                fluidsAtLocation.get().add(new Pair<>(Fluids.WATER,
                        fallbackFluidAmount(serverLevel, Fluids.WATER, sampledClimate, pos)
                ));
            } else {

                fluidsAtLocation.get().add(new Pair<>(Fluids.LAVA,
                        fallbackFluidAmount(serverLevel, Fluids.LAVA, sampledClimate, pos)
                ));
            }
        }

        return fluidsAtLocation.get();
    }


    public static int randomSeedFromClimate(HashMap<WorldUtil.ClimateParameters, Double> sampledClimate) { // Generated seed for choosing between water and lava when no fluid present
        return (int) (10000 * (sampledClimate.get(WorldUtil.ClimateParameters.CONTINENTALNESS) +
                sampledClimate.get(WorldUtil.ClimateParameters.EROSION) +
                sampledClimate.get(WorldUtil.ClimateParameters.HUMIDITY) +
                sampledClimate.get(WorldUtil.ClimateParameters.TEMPERATURE)));

    }

    private static int fallbackFluidAmount(ServerLevel serverLevel, Fluid fluid, HashMap<ClimateParameters, Double> sampledClimate, BlockPos pos) {

        double noise = DepositNoise.sample(serverLevel.getSeed(), pos.getX(), pos.getZ());

        if (fluid.isSame(Fluids.WATER) || fluid.isSame(Fluids.FLOWING_WATER)) {
            return Mth.abs(Mth.ceil((((float) noise + 1_000)) * sampledClimate.get(ClimateParameters.HUMIDITY)));
        } else if (fluid.isSame(Fluids.LAVA) || fluid.isSame(Fluids.FLOWING_LAVA)) {
            return Mth.abs(Mth.ceil((((float) noise + 1_000)) * sampledClimate.get(ClimateParameters.TEMPERATURE)));
        }

        return 250;
    }

    public static boolean isDirt(BlockState state) {
        return state.is(BlockTags.DIRT);
    }

    public static boolean isStone(BlockState state) {
        return state.is(BlockTags.BASE_STONE_OVERWORLD);
    }

}
