package com.veteam.voluminousenergy.recipe;

import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.veteam.voluminousenergy.blocks.blocks.VEBlocks;
import com.veteam.voluminousenergy.util.recipe.FluidIngredient;
import com.veteam.voluminousenergy.util.recipe.FluidSerializerHelper;
import com.veteam.voluminousenergy.util.recipe.RecipeUtil;
import com.veteam.voluminousenergy.util.TagUtil;
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

public class FluidElectrolyzerRecipe extends VEFluidRecipe {
    public static final RecipeType<VEFluidRecipe> RECIPE_TYPE = VERecipes.VERecipeTypes.FLUID_ELECTROLYZING.get();

    public static final FluidElectrolyzerRecipe.Serializer SERIALIZER = new FluidElectrolyzerRecipe.Serializer();

    private final ResourceLocation recipeId;
    private int processTime;

    public FluidElectrolyzerRecipe(ResourceLocation recipeId){
        this.recipeId = recipeId;
    }

    @Override
    public @NotNull ResourceLocation getId(){return recipeId;}

    @Override
    public @NotNull RecipeSerializer<?> getSerializer(){ return SERIALIZER;}

    @Override
    public @NotNull RecipeType<VEFluidRecipe> getType(){return RECIPE_TYPE;}

    @Override
    public int getProcessTime() { return processTime; }

    @Override
    public @NotNull ItemStack getToastSymbol(){
        return new ItemStack(VEBlocks.FLUID_ELECTROLYZER_BLOCK.get());
    }

    public static class Serializer implements RecipeSerializer<FluidElectrolyzerRecipe> {
        @Override
        public @NotNull FluidElectrolyzerRecipe fromJson(@NotNull ResourceLocation recipeId, JsonObject json) {
            FluidElectrolyzerRecipe recipe = new FluidElectrolyzerRecipe(recipeId);

            JsonObject ingredientJson = json.get("ingredient").getAsJsonObject();

            int ingredientCount = GsonHelper.getAsInt(ingredientJson, "count", 1);
            recipe.lazyIngredientList.add(Lazy.of(() -> RecipeUtil.modifyIngredientAmounts(Ingredient.fromJson(ingredientJson), ingredientCount)));

            recipe.processTime = GsonHelper.getAsInt(json,"process_time",200);

            JsonObject inputFluid = json.get("input_fluid").getAsJsonObject();
            recipe.lazyFluidIngredientList.add(Lazy.of(() -> FluidIngredient.fromJson(inputFluid)));

            recipe.addFluidOutput(RecipeUtil.pullFluidFromJSON("first_result",json));
            recipe.addFluidOutput(RecipeUtil.pullFluidFromJSON("second_result",json));

            return recipe;
        }

        FluidSerializerHelper<FluidElectrolyzerRecipe> helper = new FluidSerializerHelper<>();

        @Nullable
        @Override
        public FluidElectrolyzerRecipe fromNetwork(@NotNull ResourceLocation recipeId, @NotNull FriendlyByteBuf buffer){
            FluidElectrolyzerRecipe recipe = new FluidElectrolyzerRecipe(recipeId);
            helper.fromNetwork(recipe,buffer);
            return recipe;
        }

        @Override
        public void toNetwork(@NotNull FriendlyByteBuf buffer, @NotNull FluidElectrolyzerRecipe recipe){
            helper.toNetwork(buffer,recipe);
        }
    }
}
