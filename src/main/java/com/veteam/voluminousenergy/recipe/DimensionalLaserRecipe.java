package com.veteam.voluminousenergy.recipe;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.veteam.voluminousenergy.blocks.blocks.VEBlocks;
import com.veteam.voluminousenergy.util.ServerSideOnly;
import com.veteam.voluminousenergy.util.climate.FluidClimateSpawn;
import com.veteam.voluminousenergy.util.recipe.FluidSerializerHelper;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;
import oshi.util.tuples.Pair;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

import static com.veteam.voluminousenergy.util.recipe.VERecipeCodecs.*;

public class DimensionalLaserRecipe extends VEFluidRecipe {

    public static final RecipeType<VEFluidRecipe> RECIPE_TYPE = VERecipes.VERecipeTypes.DIMENSIONAL_LASING.get();

    @ServerSideOnly
    private ClimateData climateData;

    @ServerSideOnly
    private IntPair minMaxAmounts;

    @ServerSideOnly
    private FluidMinMax fluidMinMax;

    private Fluid regionFluid;
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

    public static final RecipeSerializer<DimensionalLaserRecipe> SERIALIZER = new RecipeSerializer<>() {

        public static final Codec<DimensionalLaserRecipe> VE_RECIPE_CODEC = RecordCodecBuilder.create((instance) -> instance.group(
                VE_MIN_MAX_FLUID_CODEC.fieldOf("region_fluid").forGetter((getter) -> getter.fluidMinMax),
                VE_CLIMATE_CODEC.fieldOf("climate").forGetter((getter) -> getter.climateData)
        ).apply(instance, DimensionalLaserRecipe::new));

        private static final FluidSerializerHelper<DimensionalLaserRecipe> helper = new FluidSerializerHelper<>();

        @Nullable
        @Override
        public DimensionalLaserRecipe fromNetwork(@NotNull FriendlyByteBuf buffer) {
            DimensionalLaserRecipe recipe = new DimensionalLaserRecipe();
            recipe.setMinimumAmount(buffer.readInt());
            recipe.setMaximumAmount(buffer.readInt());

            recipe.setContinentalnessMin(buffer.readFloat());
            recipe.setContinentalnessMax(buffer.readFloat());

            recipe.setErosionMin(buffer.readFloat());
            recipe.setErosionMax(buffer.readFloat());

            recipe.setHumidityMin(buffer.readFloat());
            recipe.setHumidityMax(buffer.readFloat());

            recipe.setTemperatureMin(buffer.readFloat());
            recipe.setTemperatureMax(buffer.readFloat());
            recipe.regionFluid = buffer.readRegistryId();
            return helper.fromNetwork(recipe, buffer);
        }

        @Override
        public @NotNull Codec<DimensionalLaserRecipe> codec() {
            return VE_RECIPE_CODEC;
        }

        @Override
        public void toNetwork(@NotNull FriendlyByteBuf buffer, @NotNull DimensionalLaserRecipe recipe) {
            buffer.writeInt(recipe.getMinimumAmount());
            buffer.writeInt(recipe.getMaximumAmount());

            buffer.writeFloat(recipe.getContinentalnessMin());
            buffer.writeFloat(recipe.getContinentalnessMax());

            buffer.writeFloat(recipe.getErosionMin());
            buffer.writeFloat(recipe.getErosionMax());

            buffer.writeFloat(recipe.getHumidityMin());
            buffer.writeFloat(recipe.getHumidityMax());

            buffer.writeFloat(recipe.getTemperatureMin());
            buffer.writeFloat(recipe.getTemperatureMax());
            buffer.writeRegistryId(ForgeRegistries.FLUIDS, recipe.getRegionFluid());
        }
    };

    public DimensionalLaserRecipe() {

    }

    public DimensionalLaserRecipe(FluidMinMax fluidMinMax, ClimateData climateData) {
        super(new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), 0);
        this.maximumAmount = fluidMinMax.max();
        this.minimumAmount = fluidMinMax.min();
        this.regionFluid = fluidMinMax.fluid();
        this.continentalnessMin = climateData.continentalness().min();
        this.continentalnessMax = climateData.continentalness().max();
        this.erosionMin = climateData.erosion().min();
        this.erosionMax = climateData.erosion().max();
        this.humidityMin = climateData.humidity().min();
        this.humidityMax = climateData.humidity().max();
        this.temperatureMin = climateData.temperature().min();
        this.temperatureMax = climateData.temperature().max();
    }

    private FluidClimateSpawn fluidClimateSpawn = null;

    public FluidClimateSpawn getFluidClimateSpawn() {
        if (this.fluidClimateSpawn == null) {
            this.fluidClimateSpawn = new FluidClimateSpawn(
                    new Pair<>(this.continentalnessMin, this.continentalnessMax),
                    new Pair<>(this.erosionMin, this.erosionMax),
                    new Pair<>(this.humidityMin, this.humidityMax),
                    new Pair<>(this.temperatureMin, this.temperatureMax),
                    this.regionFluid,
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

    public Fluid getRegionFluid() {
        return regionFluid;
    }

    @Override
    public List<FluidStack> getOutputFluids() {
        throw new IllegalStateException("Unexpected call to getOutputFluids from DimensionalLaserRecipe!");
    }


    @Override
    public FluidStack getOutputFluid(int slot) {
        throw new IllegalStateException("Unexpected call to getOutputFluid from DimensionalLaserRecipe!");
    }

    @Override
    public @NotNull ItemStack getToastSymbol() {
        return new ItemStack(VEBlocks.AQUEOULIZER_BLOCK.get());
    }
}
