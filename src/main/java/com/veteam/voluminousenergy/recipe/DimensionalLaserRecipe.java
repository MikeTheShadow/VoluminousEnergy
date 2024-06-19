package com.veteam.voluminousenergy.recipe;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.veteam.voluminousenergy.blocks.blocks.VEBlocks;
import com.veteam.voluminousenergy.recipe.parser.BasicParser;
import com.veteam.voluminousenergy.recipe.serializer.FluidSerializerHelper;
import com.veteam.voluminousenergy.util.ServerSideOnly;
import com.veteam.voluminousenergy.util.climate.FluidClimateSpawn;
import com.veteam.voluminousenergy.util.recipe.VERecipeCodecs;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.fluids.FluidStack;
import org.jetbrains.annotations.NotNull;
import oshi.util.tuples.Pair;

import java.util.ArrayList;
import java.util.List;

import static com.veteam.voluminousenergy.util.recipe.VERecipeCodecs.*;

public class DimensionalLaserRecipe extends VERecipe {

    public static final RecipeType<VERecipe> RECIPE_TYPE = VERecipes.VERecipeTypes.DIMENSIONAL_LASING.get();

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
                VERecipeCodecs.VE_MIN_MAX_FLUID_CODEC.fieldOf("region_fluid").forGetter((getter) -> getter.fluidMinMax),
                VERecipeCodecs.VE_CLIMATE_CODEC.fieldOf("climate").forGetter((getter) -> getter.climateData)
        ).apply(instance, DimensionalLaserRecipe::new));

        private static final FluidSerializerHelper<DimensionalLaserRecipe> helper = new FluidSerializerHelper<>();

        @Override
        public @NotNull MapCodec<DimensionalLaserRecipe> codec() {
            return MapCodec.assumeMapUnsafe(VE_RECIPE_CODEC);
        }

        @Override
        @NotNull
        public StreamCodec<RegistryFriendlyByteBuf, DimensionalLaserRecipe> streamCodec() {
            return new StreamCodec<>() {
                @Override
                public void encode(@NotNull RegistryFriendlyByteBuf buf, @NotNull DimensionalLaserRecipe recipe) {
                    buf.writeInt(recipe.getMinimumAmount());
                    buf.writeInt(recipe.getMaximumAmount());

                    buf.writeFloat(recipe.getContinentalnessMin());
                    buf.writeFloat(recipe.getContinentalnessMax());

                    buf.writeFloat(recipe.getErosionMin());
                    buf.writeFloat(recipe.getErosionMax());

                    buf.writeFloat(recipe.getHumidityMin());
                    buf.writeFloat(recipe.getHumidityMax());

                    buf.writeFloat(recipe.getTemperatureMin());
                    buf.writeFloat(recipe.getTemperatureMax());

                    FluidStack.STREAM_CODEC.encode(buf,new FluidStack(recipe.getRegionFluid(),1));
                    helper.toNetwork(buf, recipe);
                }

                @Override
                @NotNull
                public DimensionalLaserRecipe decode(@NotNull RegistryFriendlyByteBuf buffer) {
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
                    recipe.setRegionFluid(FluidStack.STREAM_CODEC.decode(buffer).getFluid());
                    return helper.fromNetwork(recipe, buffer);
                }
            };
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
    public @NotNull RecipeType<VERecipe> getType() {
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

    public void setRegionFluid(Fluid fluid) {
        this.regionFluid = fluid;
    }

    @Override
    public List<FluidStack> getOutputFluids() {
        return new ArrayList<>();
    }


    @Override
    public FluidStack getOutputFluid(int slot) {
        return FluidStack.EMPTY;
    }

    /*
        This is null because we want it to explode if someone tries to pull a parser from here.
     */
    @Override
    public BasicParser getParser() {
        return null;
    }

    @Override
    public @NotNull ItemStack getToastSymbol() {
        return new ItemStack(VEBlocks.DIMENSIONAL_LASER.block().get());
    }
}
