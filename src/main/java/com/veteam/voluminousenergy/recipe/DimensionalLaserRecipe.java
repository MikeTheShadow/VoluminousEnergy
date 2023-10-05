package com.veteam.voluminousenergy.recipe;

import com.veteam.voluminousenergy.blocks.blocks.VEBlocks;
import com.veteam.voluminousenergy.util.climate.FluidClimateSpawn;
import com.veteam.voluminousenergy.util.recipe.FluidIngredient;
import com.veteam.voluminousenergy.util.recipe.serializers.VEDimensionalLaserRecipeSerializer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraftforge.fluids.FluidStack;
import org.jetbrains.annotations.NotNull;
import oshi.util.tuples.Pair;

import java.util.List;

public class DimensionalLaserRecipe extends VEFluidRecipe {

    public static final RecipeType<VEFluidRecipe> RECIPE_TYPE = VERecipes.VERecipeTypes.DIMENSIONAL_LASING.get();

    public static final VEDimensionalLaserRecipeSerializer SERIALIZER = new VEDimensionalLaserRecipeSerializer();

    private int maximumAmount;
    private int minimumAmount;

    private float continentalnessMin;
    private float continentalnessMax;
    private float erosionMin;
    private float erosionMax;
    private float humidityMin;
    private float humidityMax;
    private float temperatureMin;
    private float temperatureMax;

    public DimensionalLaserRecipe() {

    }

    public DimensionalLaserRecipe(List<Ingredient> i, List<FluidIngredient> fi, List<FluidStack> of, List<ItemStack> oi,
                                  int processTime, int maximumAmount, int minimumAmount, float continentalnessMin,
                                  float continentalnessMax, float erosionMin, float erosionMax, float humidityMin,
                                  float humidityMax, float temperatureMin, float temperatureMax) {
        super(i, fi, of, oi, processTime);
        this.maximumAmount = maximumAmount;
        this.minimumAmount = minimumAmount;
        this.continentalnessMin = continentalnessMin;
        this.continentalnessMax = continentalnessMax;
        this.erosionMin = erosionMin;
        this.erosionMax = erosionMax;
        this.humidityMin = humidityMin;
        this.humidityMax = humidityMax;
        this.temperatureMin = temperatureMin;
        this.temperatureMax = temperatureMax;
    }

    private FluidClimateSpawn fluidClimateSpawn = null;

    public FluidClimateSpawn getFluidClimateSpawn() {
        if (this.fluidClimateSpawn == null) {
            this.fluidClimateSpawn = new FluidClimateSpawn(
                    new Pair<>(this.continentalnessMin, this.continentalnessMax),
                    new Pair<>(this.erosionMin, this.erosionMax),
                    new Pair<>(this.humidityMin, this.humidityMax),
                    new Pair<>(this.temperatureMin, this.temperatureMax),
                    this.getOutputFluid(0).getFluid(),
                    this.minimumAmount,
                    this.maximumAmount
            );
        }
        return this.fluidClimateSpawn;
    }

    @Override
    public @NotNull RecipeSerializer<? extends VERecipe> getSerializer() {
        return SERIALIZER;
    }

    @Override
    public @NotNull RecipeType<VEFluidRecipe> getType() {
        return RECIPE_TYPE;
    }

    public int getMaximumAmount() {
        return maximumAmount;
    }

    public int getMinimumAmount() {
        return minimumAmount;
    }

    public float getContinentalnessMin() {
        return continentalnessMin;
    }

    public float getContinentalnessMax() {
        return continentalnessMax;
    }

    public float getErosionMin() {
        return erosionMin;
    }

    public float getErosionMax() {
        return erosionMax;
    }

    public float getHumidityMin() {
        return humidityMin;
    }

    public float getHumidityMax() {
        return humidityMax;
    }

    public float getTemperatureMin() {
        return temperatureMin;
    }

    public float getTemperatureMax() {
        return temperatureMax;
    }

    public void setMaximumAmount(int maximumAmount) {
        this.maximumAmount = maximumAmount;
    }

    public void setMinimumAmount(int minimumAmount) {
        this.minimumAmount = minimumAmount;
    }

    public void setContinentalnessMin(float continentalnessMin) {
        this.continentalnessMin = continentalnessMin;
    }

    public void setContinentalnessMax(float continentalnessMax) {
        this.continentalnessMax = continentalnessMax;
    }

    public void setErosionMin(float erosionMin) {
        this.erosionMin = erosionMin;
    }

    public void setErosionMax(float erosionMax) {
        this.erosionMax = erosionMax;
    }

    public void setHumidityMin(float humidityMin) {
        this.humidityMin = humidityMin;
    }

    public void setHumidityMax(float humidityMax) {
        this.humidityMax = humidityMax;
    }

    public void setTemperatureMin(float temperatureMin) {
        this.temperatureMin = temperatureMin;
    }

    public void setTemperatureMax(float temperatureMax) {
        this.temperatureMax = temperatureMax;
    }

    @Override
    public @NotNull ItemStack getToastSymbol() {
        return new ItemStack(VEBlocks.AQUEOULIZER_BLOCK.get());
    }
}
