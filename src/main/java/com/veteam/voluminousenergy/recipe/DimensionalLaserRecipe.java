package com.veteam.voluminousenergy.recipe;

import com.google.gson.JsonObject;
import com.veteam.voluminousenergy.blocks.blocks.VEBlocks;
import com.veteam.voluminousenergy.util.climate.FluidClimateSpawn;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.common.util.Lazy;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;
import oshi.util.tuples.Pair;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class DimensionalLaserRecipe extends VEFluidRecipe {

    public static final RecipeType<VEFluidRecipe> RECIPE_TYPE = VERecipes.VERecipeTypes.DIMENSIONAL_LASING.get();

    public static final DimensionalLaserRecipe.Serializer SERIALIZER = new DimensionalLaserRecipe.Serializer();

    private final ResourceLocation recipeId;

    private FluidStack result;

    private Fluid fluid;

    private int maximumAmount;
    private int minimumAmount;

    private Pair<Float,Float> continentalnessRange;
    private Pair<Float,Float> erosionRange;
    private Pair<Float,Float> humidityRange;
    private Pair<Float,Float> temperatureRange;

    private FluidClimateSpawn fluidClimateSpawn;

    public DimensionalLaserRecipe() {
        recipeId = null;
    }

    public DimensionalLaserRecipe(ResourceLocation recipeId){
        this.recipeId = recipeId;
    }

    @Override
    @Deprecated
    // use getOutputFluid instead
    public ItemStack getResult() {return new ItemStack(this.fluid.getBucket());}

    public FluidStack getInputFluid(){
        return new FluidStack(this.fluid, 1000);
    }

    @Override
    @Deprecated
    // Use getResult instead
    public ItemStack getResultItem(){return this.getResult();}

    @Override
    public @NotNull ResourceLocation getId(){return recipeId;}

    @Override
    public @NotNull RecipeSerializer<?> getSerializer(){ return SERIALIZER;}

    @Override
    public @NotNull RecipeType<VEFluidRecipe> getType(){return RECIPE_TYPE;}

    @Override
    public ArrayList<Item> getIngredientList() {
        return null;
    }

    @Override
    public List<FluidStack> getInputFluids() {
        return List.of(new FluidStack(this.fluid, 1000));
    }
    @Override
    public List<ItemStack> getOutputItems() {
        return null;
    }

    @Override
    public int getProcessTime() { return 0; }

    public FluidClimateSpawn getFluidClimateSpawn() {
        return fluidClimateSpawn;
    }

    @Override
    public @NotNull ItemStack getToastSymbol(){
        return new ItemStack(VEBlocks.AQUEOULIZER_BLOCK.get());
    }

    public static class Serializer implements RecipeSerializer<DimensionalLaserRecipe>{
        @Override
        public @NotNull DimensionalLaserRecipe fromJson(@NotNull ResourceLocation recipeId, @NotNull JsonObject json) {
            DimensionalLaserRecipe recipe = new DimensionalLaserRecipe(recipeId);

            ResourceLocation fluidResourceLocation = ResourceLocation.of(GsonHelper.getAsString(json,"fluid","minecraft:air"),':');
            recipe.fluid = ForgeRegistries.FLUIDS.getValue(fluidResourceLocation);

            // Second: get amounts:
            JsonObject amountsJson = json.get("amounts").getAsJsonObject();

            recipe.minimumAmount = GsonHelper.getAsInt(amountsJson, "minimum", 1);
            recipe.maximumAmount = GsonHelper.getAsInt(amountsJson, "maximum", 1);

            // Third: get spans
            float min, max;
            JsonObject spansJson = json.get("climate").getAsJsonObject();

            JsonObject continentalness = spansJson.get("continentalness").getAsJsonObject();
            min = GsonHelper.getAsFloat(continentalness, "minimum", 0);
            max = GsonHelper.getAsFloat(continentalness, "maximum", 0);
            recipe.continentalnessRange = new Pair<>(min, max);

            JsonObject erosion = spansJson.get("erosion").getAsJsonObject();
            min = GsonHelper.getAsFloat(erosion, "minimum", 0);
            max = GsonHelper.getAsFloat(erosion, "maximum", 0);
            recipe.erosionRange = new Pair<>(min, max);

            JsonObject humidity = spansJson.get("humidity").getAsJsonObject();
            min = GsonHelper.getAsFloat(humidity, "minimum", 0);
            max = GsonHelper.getAsFloat(humidity, "maximum", 0);
            recipe.humidityRange = new Pair<>(min, max);

            JsonObject temperature = spansJson.get("temperature").getAsJsonObject();
            min = GsonHelper.getAsFloat(temperature, "minimum", 0);
            max = GsonHelper.getAsFloat(temperature, "maximum", 0);
            recipe.temperatureRange = new Pair<>(min, max);

            recipe.fluidClimateSpawn = new FluidClimateSpawn(
                    recipe.continentalnessRange,
                    recipe.erosionRange,
                    recipe.humidityRange,
                    recipe.temperatureRange,
                    recipe.fluid,
                    recipe.minimumAmount,
                    recipe.maximumAmount
            );

            recipe.ingredient = Lazy.of(() -> Ingredient.of(new ItemStack(Items.BUCKET,1)));
            recipe.result = new FluidStack(Objects.requireNonNull(recipe.fluid), 1000);

            recipe.fluidOutputList.add(recipe.result);

            return recipe;
        }

        @Nullable
        @Override
        public DimensionalLaserRecipe fromNetwork(@NotNull ResourceLocation recipeId, FriendlyByteBuf buffer){
            DimensionalLaserRecipe recipe = new DimensionalLaserRecipe(recipeId);
            recipe.fluid = buffer.readFluidStack().getRawFluid();

            recipe.minimumAmount = buffer.readInt();
            recipe.maximumAmount = buffer.readInt();

            float tempA, tempB;

            // Continentalness
            tempA = buffer.readFloat();
            tempB = buffer.readFloat();
            recipe.continentalnessRange = new Pair<>(tempA, tempB);

            // Erosion
            tempA = buffer.readFloat();
            tempB = buffer.readFloat();
            recipe.erosionRange = new Pair<>(tempA, tempB);

            // Humidity
            tempA = buffer.readFloat();
            tempB = buffer.readFloat();
            recipe.humidityRange = new Pair<>(tempA, tempB);

            // Temperature
            tempA = buffer.readFloat();
            tempB = buffer.readFloat();
            recipe.temperatureRange = new Pair<>(tempA, tempB);

            recipe.fluidClimateSpawn = new FluidClimateSpawn(
                    recipe.continentalnessRange,
                    recipe.erosionRange,
                    recipe.humidityRange,
                    recipe.temperatureRange,
                    recipe.fluid,
                    recipe.minimumAmount,
                    recipe.maximumAmount
            );

            recipe.ingredient = Lazy.of(() -> Ingredient.of(new ItemStack(Items.BUCKET,1)));
            recipe.result = new FluidStack(recipe.fluid, 1000);
            recipe.fluidOutputList.add(recipe.result);

            return recipe;
        }

        @Override
        public void toNetwork(FriendlyByteBuf buffer, DimensionalLaserRecipe recipe){
            buffer.writeFluidStack(new FluidStack(recipe.fluid, 1000));
            buffer.writeInt(recipe.minimumAmount);
            buffer.writeInt(recipe.maximumAmount);

            buffer.writeFloat(recipe.continentalnessRange.getA());
            buffer.writeFloat(recipe.continentalnessRange.getB());

            buffer.writeFloat(recipe.erosionRange.getA());
            buffer.writeFloat(recipe.erosionRange.getB());

            buffer.writeFloat(recipe.humidityRange.getA());
            buffer.writeFloat(recipe.humidityRange.getB());

            buffer.writeFloat(recipe.temperatureRange.getA());
            buffer.writeFloat(recipe.temperatureRange.getB());
        }
    }
}
