package com.veteam.voluminousenergy.recipe;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.veteam.voluminousenergy.blocks.blocks.VEBlocks;
import com.veteam.voluminousenergy.util.climate.FluidClimateSpawn;
import com.veteam.voluminousenergy.util.recipe.FluidIngredient;
import com.veteam.voluminousenergy.util.recipe.FluidSerializerHelper;
import com.veteam.voluminousenergy.util.recipe.VERecipeCodecs;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingRecipeCodecs;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraftforge.fluids.FluidStack;
import org.jetbrains.annotations.NotNull;
import oshi.util.tuples.Pair;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class DimensionalLaserRecipe extends VEFluidRecipe {

    public static final RecipeType<VEFluidRecipe> RECIPE_TYPE = VERecipes.VERecipeTypes.DIMENSIONAL_LASING.get();

    public static final RecipeSerializer<DimensionalLaserRecipe> SERIALIZER = new RecipeSerializer<>() {

        public static final Codec<DimensionalLaserRecipe> VE_RECIPE_CODEC = RecordCodecBuilder.create((instance) -> instance.group(
                VERecipeCodecs.VE_FLUID_INGREDIENT_CODEC.listOf().fieldOf("fluid_ingredients").forGetter((getter) -> getter.registryFluidIngredients),
                Codec.INT.fieldOf("process_time").forGetter((getter) -> getter.processTime),
                Codec.INT.fieldOf("min_amount").forGetter(DimensionalLaserRecipe::getMinimumAmount),
                Codec.INT.fieldOf("max_amount").forGetter(DimensionalLaserRecipe::getMaximumAmount),
                Codec.FLOAT.fieldOf("continentalness_min").forGetter(DimensionalLaserRecipe::getContinentalnessMin),
                Codec.FLOAT.fieldOf("continentalness_max").forGetter(DimensionalLaserRecipe::getContinentalnessMax),

                Codec.FLOAT.fieldOf("erosion_min").forGetter(DimensionalLaserRecipe::getErosionMin),
                Codec.FLOAT.fieldOf("erosion_max").forGetter(DimensionalLaserRecipe::getErosionMax),

                Codec.FLOAT.fieldOf("humidity_min").forGetter(DimensionalLaserRecipe::getHumidityMin),
                Codec.FLOAT.fieldOf("humidity_max").forGetter(DimensionalLaserRecipe::getHumidityMax),

                Codec.FLOAT.fieldOf("temp_min").forGetter(DimensionalLaserRecipe::getTemperatureMin),
                Codec.FLOAT.fieldOf("temp_max").forGetter(DimensionalLaserRecipe::getTemperatureMax)
        ).apply(instance, DimensionalLaserRecipe::new));

        private static final FluidSerializerHelper<DimensionalLaserRecipe> helper = new FluidSerializerHelper<>();

        @Nullable
        @Override
        public DimensionalLaserRecipe fromNetwork(@NotNull FriendlyByteBuf buffer) {
            DimensionalLaserRecipe recipe = new DimensionalLaserRecipe();

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
            return helper.fromNetwork(recipe, buffer);
        }

        @Override
        public @NotNull Codec<DimensionalLaserRecipe> codec() {
            return VE_RECIPE_CODEC;
        }

        @Override
        public void toNetwork(@NotNull FriendlyByteBuf buffer, @NotNull DimensionalLaserRecipe recipe) {
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
            helper.toNetwork(buffer, recipe);
        }
    };

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

    public DimensionalLaserRecipe(List<VERecipeCodecs.RegistryFluidIngredient> fi,
                                  int processTime, int maximumAmount, int minimumAmount, float continentalnessMin,
                                  float continentalnessMax, float erosionMin, float erosionMax, float humidityMin,
                                  float humidityMax, float temperatureMin, float temperatureMax) {
        super(new ArrayList<>(), fi, new ArrayList<>(), new ArrayList<>(), processTime);
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
