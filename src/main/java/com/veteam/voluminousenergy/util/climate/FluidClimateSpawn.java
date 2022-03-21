package com.veteam.voluminousenergy.util.climate;

import com.veteam.voluminousenergy.VoluminousEnergy;
import com.veteam.voluminousenergy.util.WorldUtil;
import net.minecraft.util.Mth;
import net.minecraft.world.level.biome.Climate;
import net.minecraft.world.level.material.Fluid;
import oshi.util.tuples.Pair;


import java.util.HashMap;

public class FluidClimateSpawn extends ClimateSpawn {
    final Fluid fluid;
    final int maxAmount;
    final int minAmount;

    public FluidClimateSpawn(Pair<Float, Float> continentalness,
                             Pair<Float, Float> erosion,
                             Pair<Float, Float> humidity,
                             Pair<Float, Float> temperature,
                             Fluid fluid,
                             int minimumDepositFluidAmount,
                             int maximumDepositFluidAmount) {
        super(continentalness, erosion, humidity, temperature);
        this.fluid = fluid;
        this.minAmount = minimumDepositFluidAmount;
        this.maxAmount = maximumDepositFluidAmount;
    }

    public Fluid getFluid() {
        return this.fluid;
    }

    public int getMaximumFluidAmount() {
        return this.maxAmount;
    }

    public int getMinimumFluidAmount() {
        return this.minAmount;
    }

    public int calculateDepositAmount(HashMap<WorldUtil.ClimateParameters,Double> sampledClimate) {
        return this.calculateDepositAmount(
                sampledClimate.get(WorldUtil.ClimateParameters.CONTINENTALNESS),
                sampledClimate.get(WorldUtil.ClimateParameters.EROSION),
                sampledClimate.get(WorldUtil.ClimateParameters.HUMIDITY),
                sampledClimate.get(WorldUtil.ClimateParameters.TEMPERATURE)
        );
    }

    // Main number cruncher
    public int calculateDepositAmount(double continentalness, double erosion, double humidity, double temperature){
        if (!this.checkValidity(continentalness, erosion, humidity, temperature)) return 0;

        // TODO: Tune this algorithm
        double cDeltaMax = this.getContinentalnessClimateParameter().getB() - continentalness;
        double cDeltaMin = continentalness - this.getContinentalnessClimateParameter().getA();

        double eDeltaMax = this.getErosionClimateParameter().getB() - erosion;
        double eDeltaMin = erosion - this.getErosionClimateParameter().getA();

        double hDeltaMax = this.getHumidityClimateParameter().getB() - humidity;
        double hDeltaMin = humidity - this.getHumidityClimateParameter().getA();

        double tDeltaMax = this.getTemperatureClimateParameter().getB() - temperature;
        double tDeltaMin = temperature - this.getTemperatureClimateParameter().getA();

        System.out.println("Printing Values in order of Max/Min, C/E/H/T:\n cMAX: "
                + cDeltaMax + " cMIN: " + cDeltaMin + " eMAX: " + eDeltaMax + " eMIN: " + eDeltaMin + " hMAX: " + hDeltaMax + " hMIN: " + hDeltaMin
                + " tMAX: " + tDeltaMax + " tMIN: " + tDeltaMin);

        VoluminousEnergy.LOGGER.debug("Printing Values in order of Max/Min, C/E/H/T:\n cMAX: "
                + cDeltaMax + " cMIN: " + cDeltaMin + " eMAX: " + eDeltaMax + " eMIN: " + eDeltaMin + " hMAX: " + hDeltaMax + " hMIN: " + hDeltaMin
                + " tMAX: " + tDeltaMax + " tMIN: " + tDeltaMin
        );

        double semiFinalAmount;
        semiFinalAmount = cDeltaMax- Mth.abs((float) cDeltaMin) * eDeltaMax-Mth.abs((float) eDeltaMin) * hDeltaMax-Mth.abs((float) hDeltaMin)
                * tDeltaMax-Mth.abs((float) tDeltaMin);

        if (semiFinalAmount < 0){
            System.out.println(" AMOUNT TO RETURN IS: " + semiFinalAmount + "! WILL ZERO NOW!");
            VoluminousEnergy.LOGGER.debug(" AMOUNT TO RETURN IS: " + semiFinalAmount + "! WILL ZERO NOW!");
            semiFinalAmount = 0;
        }

        int amount2Return = Mth.ceil(semiFinalAmount);
        System.out.println("amount2Return before checking with min and max amounts: " + amount2Return);
        VoluminousEnergy.LOGGER.debug("amount2Return before checking with min and max amounts: " + amount2Return);
        amount2Return = amount2Return > this.maxAmount ? this.maxAmount : (Math.max(amount2Return, this.minAmount));

        return amount2Return;
    }

}