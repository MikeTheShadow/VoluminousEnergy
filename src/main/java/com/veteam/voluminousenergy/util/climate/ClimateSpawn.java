package com.veteam.voluminousenergy.util.climate;

import com.veteam.voluminousenergy.tools.Config;
import com.veteam.voluminousenergy.util.WorldUtil;
import oshi.util.tuples.Pair;

import java.util.HashMap;
import java.util.Random;

public class ClimateSpawn {
    final Pair<Float, Float> CONTINENTALNESS_PARAMETER;
    final Pair<Float, Float> EROSION_PARAMETER;
    final Pair<Float, Float> HUMIDITY_PARAMETER;
    final Pair<Float, Float> TEMPERATURE_PARAMATER;

    public ClimateSpawn(Pair<Float, Float> continentalness,
                        Pair<Float, Float> erosion,
                        Pair<Float, Float> humidity,
                        Pair<Float, Float> temperature) {
        this.CONTINENTALNESS_PARAMETER = continentalness;
        this.EROSION_PARAMETER = erosion;
        this.HUMIDITY_PARAMETER = humidity;
        this.TEMPERATURE_PARAMATER = temperature;
    }

    public Pair<Float, Float> getContinentalnessClimateParameter() {
        return this.CONTINENTALNESS_PARAMETER;
    }

    public Pair<Float, Float> getErosionClimateParameter() {
        return this.EROSION_PARAMETER;
    }

    public Pair<Float, Float> getHumidityClimateParameter() {
        return this.HUMIDITY_PARAMETER;
    }

    public Pair<Float, Float> getTemperatureClimateParameter() {
        return this.TEMPERATURE_PARAMATER;
    }

    public boolean isWithinContinentalnessRange(double value) {
        return value >= this.CONTINENTALNESS_PARAMETER.getA() && value <= this.CONTINENTALNESS_PARAMETER.getB();
    }

    public boolean isWithinErosionRange(double value) {
        return value >= this.EROSION_PARAMETER.getA() && value <= this.EROSION_PARAMETER.getB();
    }

    public boolean isWithinHumidityRange(double value) {
        return value >= this.HUMIDITY_PARAMETER.getA() && value <= this.HUMIDITY_PARAMETER.getB();
    }

    public boolean isWithinTemperatureRange(double value) {
        return value >= this.TEMPERATURE_PARAMATER.getA() && value <= this.TEMPERATURE_PARAMATER.getB();
    }

    public boolean checkValidity(HashMap<WorldUtil.ClimateParameters, Double> sampledClimate) {
        return this.checkValidity(
                sampledClimate.get(WorldUtil.ClimateParameters.CONTINENTALNESS),
                sampledClimate.get(WorldUtil.ClimateParameters.EROSION),
                sampledClimate.get(WorldUtil.ClimateParameters.HUMIDITY),
                sampledClimate.get(WorldUtil.ClimateParameters.TEMPERATURE)
        );
    }

    public boolean checkValidity(double continentalness, double erosion, double humidity, double temperature) {

        if (Config.PUNCH_HOLES_IN_CLIMATE_SPAWNS.get()){
            double cumulativeClimateValue = continentalness + erosion + humidity + temperature;

            Random random = new Random((int) (cumulativeClimateValue * Config.CLIMATE_SPAWNS_HOLE_PUNCH_MULTIPLIER.get()));

            //Add a 20% randomness on top of it using the values as a seed to produce non-random random numbers
            if (random.nextInt(Config.CLIMATE_SPAWNS_HOLE_PUNCH_BOUNDING.get()) > Config.CLIMATE_SPAWNS_HOLE_PUNCH_RNG_MUST_BE_LARGER.get())
                return false;
        }

        return this.isWithinContinentalnessRange(continentalness)
                && this.isWithinErosionRange(erosion)
                && this.isWithinHumidityRange(humidity)
                && this.isWithinTemperatureRange(temperature);
    }

}