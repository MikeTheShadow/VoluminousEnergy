package com.veteam.voluminousenergy.util;

import com.veteam.voluminousenergy.recipe.DimensionalLaserRecipe;
import com.veteam.voluminousenergy.util.climate.FluidClimateSpawn;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerChunkCache;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Climate;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.DensityFunction;
import net.minecraft.world.level.levelgen.NoiseBasedChunkGenerator;
import net.minecraft.world.level.levelgen.NoiseRouter;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import oshi.util.tuples.Pair;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicReference;
import java.util.Random;

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

    public static HashMap<ClimateParameters,Double> sampleClimate(Level level, BlockPos pos){
        if (level.isClientSide) new HashMap<>();
        ServerLevel serverLevel = level.getServer().getLevel(level.dimension());
        ServerChunkCache serverchunkcache = serverLevel.getChunkSource();
        ChunkGenerator chunkgenerator = serverchunkcache.getGenerator();
        Climate.Sampler climateSampler = chunkgenerator.climateSampler();
        Climate.TargetPoint targetPoint = climateSampler.sample(pos.getX(), pos.getY(), pos.getZ());

        double continentalness = Climate.unquantizeCoord(targetPoint.continentalness());
        double erosion = Climate.unquantizeCoord(targetPoint.erosion());
        double humidity = Climate.unquantizeCoord(targetPoint.humidity());
        double temperature = Climate.unquantizeCoord(targetPoint.temperature());

        /*
        BiomeSource biomeSource = chunkgenerator.getBiomeSource();
        OverworldBiomeBuilder overworldBiomeBuilder = new OverworldBiomeBuilder();
        String c = overworldBiomeBuilder.getDebugStringForContinentalness(continentalness);
        String e = overworldBiomeBuilder.getDebugStringForErosion(erosion);
        String h = overworldBiomeBuilder.getDebugStringForHumidity(humidity);
        String t = overworldBiomeBuilder.getDebugStringForTemperature(temperature);
        /*
         */

        if (chunkgenerator instanceof NoiseBasedChunkGenerator noiseBasedChunkGenerator){
            NoiseRouter router = noiseBasedChunkGenerator.router();
            DensityFunction.SinglePointContext context = new DensityFunction.SinglePointContext(pos.getX(), pos.getY(), pos.getZ());
            continentalness = router.continents().compute(context);
            erosion = router.erosion().compute(context);
            humidity = router.humidity().compute(context);
            temperature = router.temperature().compute(context);
        }

        HashMap<ClimateParameters,Double> climateMap = new HashMap<>();
        climateMap.put(ClimateParameters.CONTINENTALNESS,continentalness);
        climateMap.put(ClimateParameters.EROSION,erosion);
        climateMap.put(ClimateParameters.HUMIDITY,humidity);
        climateMap.put(ClimateParameters.TEMPERATURE,temperature);
        return climateMap;
    }

    public static ArrayList<Pair<Fluid,Integer>> queryForFluids(Level level, BlockPos pos){
        AtomicReference<ArrayList<Pair<Fluid,Integer>>> fluidsAtLocation = new AtomicReference<>(new ArrayList<>());

        HashMap<ClimateParameters,Double> sampledClimate = sampleClimate(level, pos);
        if (sampledClimate.isEmpty()) return fluidsAtLocation.get(); // Return empty as well; Likely client side if this is the case

        level.getRecipeManager().getRecipes().parallelStream().forEach(recipe -> {
            if (recipe instanceof DimensionalLaserRecipe dimensionalLaserRecipe){
                FluidClimateSpawn spawn = dimensionalLaserRecipe.getFluidClimateSpawn();
                if (spawn.checkValidity(sampledClimate)){ // This might be unsafe, would prefer to avoid atomic as this is read-only
                    fluidsAtLocation.get().add(new Pair<>(spawn.getFluid(), spawn.calculateDepositAmount(sampledClimate)));
                }
            }
        });


        // Add a fluid to the location if no other fluids exist. Can make this if it's only 1 add a pair
        if(fluidsAtLocation.get().size() == 0) {

            Random random = new Random(randomSeedFromClimate(sampledClimate));

            if(random.nextInt(10) > 4) {
                fluidsAtLocation.get().add(new Pair<>(Fluids.WATER,2000)); // create the modify thingy later
            }
            else  {
                fluidsAtLocation.get().add(new Pair<>(Fluids.LAVA,2000)); // create the modify thingy later
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

}
