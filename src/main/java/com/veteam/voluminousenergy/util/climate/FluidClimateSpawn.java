package com.veteam.voluminousenergy.util.climate;

import com.veteam.voluminousenergy.util.WorldUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.XoroshiroRandomSource;
import net.minecraft.world.level.levelgen.synth.PerlinSimplexNoise;
import net.minecraft.world.level.material.Fluid;
import oshi.util.tuples.Pair;

import java.util.HashMap;
import java.util.List;

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

    public int calculateDepositAmount(HashMap<WorldUtil.ClimateParameters,Double> sampledClimate, BlockPos blockPos, Level level) {
        return this.calculateDepositAmount(
                sampledClimate.get(WorldUtil.ClimateParameters.CONTINENTALNESS),
                sampledClimate.get(WorldUtil.ClimateParameters.EROSION),
                sampledClimate.get(WorldUtil.ClimateParameters.HUMIDITY),
                sampledClimate.get(WorldUtil.ClimateParameters.TEMPERATURE),
                blockPos,
                level
        );
    }

    // Main number cruncher
    public int calculateDepositAmount(double continentalness, double erosion, double humidity, double temperature, BlockPos blockPos, Level level){
        if (!this.checkValidity(continentalness, erosion, humidity, temperature) || level.isClientSide()) return 0;

        int amount2Return = 0;

        if (level instanceof ServerLevel serverLevel) {
            PerlinSimplexNoise noise = new PerlinSimplexNoise(
                    new XoroshiroRandomSource(serverLevel.getSeed()),
                    List.of(0, -1, -2, -3, -4, -5, -6, -7)
            );

            double amount = noise.getValue(blockPos.getX(), blockPos.getZ(), true);

            amount = (this.getMinimumFluidAmount() + (amount * (this.getMaximumFluidAmount() - this.getMinimumFluidAmount())));

            amount2Return = Mth.ceil(amount);
        }

        return amount2Return;
    }

}