package com.veteam.voluminousenergy.recipe;

import com.google.gson.JsonObject;
import com.veteam.voluminousenergy.blocks.blocks.VEBlocks;
import com.veteam.voluminousenergy.util.recipe.FluidIngredient;
import com.veteam.voluminousenergy.util.recipe.FluidSerializerHelper;
import com.veteam.voluminousenergy.util.recipe.RecipeUtil;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraftforge.common.util.Lazy;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;

public class HydroponicIncubatorRecipe extends VEFluidRecipe implements IRNGRecipe {
    public static final RecipeType<VEFluidRecipe> RECIPE_TYPE = VERecipes.VERecipeTypes.HYDROPONIC_INCUBATING.get();

    public static final Serializer SERIALIZER = new Serializer();

    private final ResourceLocation recipeId;

    private float[] rng;

    public HydroponicIncubatorRecipe(ResourceLocation recipeId) {
        this.recipeId = recipeId;
    }


    @Override
    public @NotNull ResourceLocation getId() {
        return recipeId;
    }

    @Override
    public @NotNull RecipeSerializer<?> getSerializer() {
        return SERIALIZER;
    }

    @Override
    public @NotNull RecipeType<VEFluidRecipe> getType() {
        return RECIPE_TYPE;
    }


    @Override
    public @NotNull ItemStack getToastSymbol() {
        return new ItemStack(VEBlocks.HYDROPONIC_INCUBATOR_BLOCK.get());
    }

    @Override
    public float[] getRNGOutputs() {
        return rng;
    }

    @Override
    public void setRNGOutputs(float[] rngOutputs) {
        this.rng = rngOutputs;
    }

    public static class Serializer implements RecipeSerializer<HydroponicIncubatorRecipe> {
        @Override
        public @NotNull HydroponicIncubatorRecipe fromJson(@NotNull ResourceLocation recipeId, JsonObject json) {
            HydroponicIncubatorRecipe recipe = new HydroponicIncubatorRecipe(recipeId);

            float rng[] = new float[4];

            JsonObject ingredientJson = json.get("ingredient").getAsJsonObject();

            int ingredientCount = GsonHelper.getAsInt(ingredientJson, "count", 1);
            recipe.lazyIngredientList
                    .add(Lazy.of(() -> RecipeUtil.modifyIngredientAmounts(Ingredient.fromJson(ingredientJson), ingredientCount)));
            recipe.processTime = GsonHelper.getAsInt(json, "process_time", 200);

            JsonObject inputFluid = json.get("input_fluid").getAsJsonObject();
            recipe.lazyFluidIngredientList.add(Lazy.of(() -> FluidIngredient.fromJson(inputFluid)));

            // Main Output Slot
            ResourceLocation itemResourceLocation = ResourceLocation.of(GsonHelper.getAsString(json.get("result").getAsJsonObject(), "item", "minecraft:air"), ':');
            int itemAmount = GsonHelper.getAsInt(json.get("result").getAsJsonObject(), "count", 1);
            ItemStack stack0 = new ItemStack(ForgeRegistries.ITEMS.getValue(itemResourceLocation), itemAmount);
            rng[0] = 1;

            // First RNG Slot, RNG 0
            ResourceLocation rngResourceLocation0 = ResourceLocation.of(GsonHelper.getAsString(json.get("rng_slot_0").getAsJsonObject(), "item", "minecraft:air"), ':');
            int rngAmount0 = GsonHelper.getAsInt(json.get("rng_slot_0").getAsJsonObject(), "count", 0);
            float rngChance0 = GsonHelper.getAsFloat(json.get("rng_slot_0").getAsJsonObject(), "chance", 0); //Enter % as DECIMAL. Ie 50% = 0.5

            ItemStack stack1 = new ItemStack(ForgeRegistries.ITEMS.getValue(rngResourceLocation0), rngAmount0);
            rng[1] = rngChance0;

            //Second RNG Slot, RNG 1
            ResourceLocation rngResourceLocation1 = ResourceLocation.of(GsonHelper.getAsString(json.get("rng_slot_1").getAsJsonObject(), "item", "minecraft:air"), ':');
            int rngAmount1 = GsonHelper.getAsInt(json.get("rng_slot_1").getAsJsonObject(), "count", 0);
            float rngChance1 = GsonHelper.getAsFloat(json.get("rng_slot_1").getAsJsonObject(), "chance", 0); //Enter % as DECIMAL. Ie 50% = 0.5

            ItemStack stack2 = new ItemStack(ForgeRegistries.ITEMS.getValue(rngResourceLocation1), rngAmount1);
            rng[2] = rngChance1;

            //Third RNG Slot, RNG 2
            ResourceLocation rngResourceLocation2 = ResourceLocation.of(GsonHelper.getAsString(json.get("rng_slot_2").getAsJsonObject(), "item", "minecraft:air"), ':');
            int rngAmount2 = GsonHelper.getAsInt(json.get("rng_slot_2").getAsJsonObject(), "count", 0);
            float rngChance2 = GsonHelper.getAsFloat(json.get("rng_slot_2").getAsJsonObject(), "chance", 0); //Enter % as DECIMAL. Ie 50% = 0.5

            ItemStack stack3 = new ItemStack(ForgeRegistries.ITEMS.getValue(rngResourceLocation2), rngAmount2);
            rng[3] = rngChance2;

            recipe.addItemOutput(stack0);
            recipe.addItemOutput(stack1);
            recipe.addItemOutput(stack2);
            recipe.addItemOutput(stack3);
            recipe.setRNGOutputs(rng);

            return recipe;
        }


        private static final FluidSerializerHelper<HydroponicIncubatorRecipe> helper = new FluidSerializerHelper<>();

        @Nullable
        @Override
        public HydroponicIncubatorRecipe fromNetwork(@NotNull ResourceLocation recipeId, FriendlyByteBuf buffer) {
            HydroponicIncubatorRecipe recipe = new HydroponicIncubatorRecipe(recipeId);
            helper.fromNetwork(recipe, buffer);
            return recipe;
        }

        @Override
        public void toNetwork(FriendlyByteBuf buffer, HydroponicIncubatorRecipe recipe) {
            helper.toNetwork(buffer, recipe);
        }
    }
}
