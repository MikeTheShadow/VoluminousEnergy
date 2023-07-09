package com.veteam.voluminousenergy.recipe;

import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.veteam.voluminousenergy.blocks.blocks.VEBlocks;
import com.veteam.voluminousenergy.util.recipe.FluidIngredient;
import com.veteam.voluminousenergy.util.recipe.FluidSerializerHelper;
import com.veteam.voluminousenergy.util.recipe.RecipeUtil;
import com.veteam.voluminousenergy.util.TagUtil;
import com.veteam.voluminousenergy.util.recipe.VEFluidIngredientSerializer;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.common.util.Lazy;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class FluidMixerRecipe extends VEFluidRecipe {
    public static final RecipeType<VEFluidRecipe> RECIPE_TYPE = VERecipes.VERecipeTypes.FLUID_MIXING.get();

    public static final FluidMixerRecipe.Serializer SERIALIZER = new FluidMixerRecipe.Serializer();

    private final ResourceLocation recipeId;
    private int processTime;

    // Second input (Fluid Mixer specific)

    public FluidMixerRecipe(ResourceLocation recipeId) {
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
    public int getProcessTime() {
        return processTime;
    }

    @Override
    public @NotNull ItemStack getToastSymbol() {
        return new ItemStack(VEBlocks.FLUID_MIXER_BLOCK.get());
    }

    public static class Serializer implements RecipeSerializer<FluidMixerRecipe> {
        @Override
        public @NotNull FluidMixerRecipe fromJson(@NotNull ResourceLocation recipeId, JsonObject json) {
            FluidMixerRecipe recipe = new FluidMixerRecipe(recipeId);

            JsonObject ingredientJson = json.get("ingredient").getAsJsonObject();
            int ingredientCount = GsonHelper.getAsInt(ingredientJson, "count", 1);
            recipe.lazyIngredientList.add(Lazy.of(() -> RecipeUtil.modifyIngredientAmounts(Ingredient.fromJson(ingredientJson), ingredientCount)));

            recipe.processTime = GsonHelper.getAsInt(json, "process_time", 200);

            // First Input fluid
            recipe.lazyFluidIngredientList.add(Lazy.of(() -> FluidIngredient.fromJson(json.get("first_input_fluid").getAsJsonObject())));

            // Second input fluid
//            recipe.secondInputAmount = GsonHelper.getAsInt(inputFluid,"amount",0);
            recipe.lazyFluidIngredientList.add(Lazy.of(() -> FluidIngredient.fromJson(json.get("second_input_fluid").getAsJsonObject())));


            ResourceLocation bucketResourceLocation = ResourceLocation.of(GsonHelper.getAsString(json.get("result").getAsJsonObject(), "fluid", "minecraft:empty"), ':');
            int firstOutputFluidAmount = GsonHelper.getAsInt(json.get("result").getAsJsonObject(), "amount", 0);
            FluidStack result = new FluidStack(Objects.requireNonNull(ForgeRegistries.FLUIDS.getValue(bucketResourceLocation)), firstOutputFluidAmount);

            recipe.fluidOutputList.add(result);

            return recipe;
        }

        FluidSerializerHelper<FluidMixerRecipe> helper = new FluidSerializerHelper<>();

        @Nullable
        @Override
        public FluidMixerRecipe fromNetwork(@NotNull ResourceLocation recipeId, FriendlyByteBuf buffer) {
            FluidMixerRecipe recipe = new FluidMixerRecipe((recipeId));
            helper.fromNetwork(recipe, buffer);
            return recipe;
        }

        @Override
        public void toNetwork(FriendlyByteBuf buffer, FluidMixerRecipe recipe) {
           helper.toNetwork(buffer,recipe);
        }
    }
}