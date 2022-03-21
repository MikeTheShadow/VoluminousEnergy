package com.veteam.voluminousenergy.recipe;

import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.veteam.voluminousenergy.blocks.blocks.VEBlocks;
import com.veteam.voluminousenergy.util.climate.FluidClimateSpawn;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.Container;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.common.util.Lazy;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.ForgeRegistryEntry;
import oshi.util.tuples.Pair;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class DimensionalLaserRecipe extends VEFluidRecipe {
    public static final RecipeType<VEFluidRecipe> RECIPE_TYPE = VERecipes.VERecipeTypes.DIMENSIONAL_LASING;

    public static final Serializer SERIALIZER = new DimensionalLaserRecipe.Serializer();

    private final ResourceLocation recipeId;

    private Fluid fluid;

    private int maximumAmount;
    private int minimumAmount;

    private Pair<Float,Float> continentalnessRange;
    private Pair<Float,Float> erosionRange;
    private Pair<Float,Float> humidityRange;
    private Pair<Float,Float> temperatureRange;

    private FluidClimateSpawn fluidClimateSpawn;

    public DimensionalLaserRecipe(ResourceLocation recipeId){
        this.recipeId = recipeId;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return SERIALIZER;
    }

    @Override
    public RecipeType<VEFluidRecipe> getType() {
        return RECIPE_TYPE;
    }

    @Override
    public ArrayList<Item> getIngredientList() {
        return null;
    }

    @Override
    public Ingredient getIngredient(){ return ingredient.get();}

    @Override
    public int getIngredientCount(){ return ingredientCount;}

    @Override
    @Deprecated
    // use getOutputFluid instead
    public ItemStack getResult() {return new ItemStack(this.fluid.getBucket());}


    public FluidStack getInputFluid(){
        return new FluidStack(this.fluid, 1000);
    }

    @Override
    public boolean matches(Container inv, Level worldIn){
        ItemStack stack = inv.getItem(0);
        int count = stack.getCount();
        return ingredient.get().test(stack) && count >= ingredientCount;
    }

    @Override
    public ItemStack assemble(Container inv){return ItemStack.EMPTY;}

    @Override
    public boolean canCraftInDimensions(int width, int height){return true;}

    @Override
    public List<FluidStack> getFluids() {
        return List.of(new FluidStack(this.fluid, 1000));
    }

    @Override
    public List<Fluid> getRawFluids() {
        return List.of(this.fluid);
    }

    @Override
    public List<ItemStack> getResults() {
        return null;
    }

    @Override
    public int getInputAmount() {
        return 0;
    }

    @Override
    public int getOutputAmount() {
        return 0;
    }

    @Override
    public FluidStack getOutputFluid() {
        return new FluidStack(this.fluid, 1000);
    }

    @Override
    public List<Integer> getAmounts() {
        return List.of(this.minimumAmount, this.maximumAmount);
    }

    @Override
    public int getProcessTime() {
        return 0;
    }

    public FluidClimateSpawn getFluidClimateSpawn() {
        return fluidClimateSpawn;
    }

    @Override
    public ItemStack getToastSymbol(){
        return new ItemStack(VEBlocks.DIMENSIONAL_LASER_BLOCK);
    }

    public static class Serializer extends ForgeRegistryEntry<RecipeSerializer<?>> implements RecipeSerializer<DimensionalLaserRecipe> {
        @Override
        public DimensionalLaserRecipe fromJson(ResourceLocation recipeId, JsonObject json) {
            DimensionalLaserRecipe recipe = new DimensionalLaserRecipe(recipeId);

            /*
            if(json.has("tag") && !json.has("fluid")){
                // A tag is used instead of a manually defined fluid
                throw new JsonSyntaxException("Dimesional Laser Recipe: Usage of tags is not permitted for this recipe type! Offending tag in recipe: " + json.getAsJsonObject("tag").getAsString());
                //ResourceLocation fluidTagLocation = ResourceLocation.of(GsonHelper.getAsString(chunkFluidJson,"tag","minecraft:air"),':');
                //RecipeUtil.setupFluidLazyArrayInputsUsingTags(recipe, fluidTagLocation, 1000);
            } else if (json.has("fluid") && !json.has("tag")){
                // In here, a manually defined fluid is used instead of a tag

            } else {
                throw new JsonSyntaxException("Bad syntax for the Dimensional Laser recipe, no fluid found in JSON");
            }*/
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
            recipe.result = new ItemStack(Objects.requireNonNull(Items.BUCKET), 1);

            return recipe;
        }

        @Nullable
        @Override
        public DimensionalLaserRecipe fromNetwork(ResourceLocation recipeId, FriendlyByteBuf buffer){
            DimensionalLaserRecipe recipe = new DimensionalLaserRecipe(recipeId);
            System.out.println("From Network called");
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
            recipe.result = new ItemStack(Objects.requireNonNull(Items.BUCKET), 1);

            System.out.println("Finished serializing Dimensional Lasing recipe");
            return recipe;
        }

        @Override
        public void toNetwork(FriendlyByteBuf buffer, DimensionalLaserRecipe recipe){
            buffer.writeFluidStack(new FluidStack(recipe.fluid, 1000));
            System.out.println("To network called");
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
