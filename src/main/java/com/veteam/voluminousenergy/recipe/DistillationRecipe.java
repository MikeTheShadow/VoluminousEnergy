package com.veteam.voluminousenergy.recipe;

import com.google.gson.JsonObject;
import com.veteam.voluminousenergy.blocks.blocks.VEBlocks;
import com.veteam.voluminousenergy.util.recipe.FluidIngredient;
import com.veteam.voluminousenergy.util.recipe.FluidSerializerHelper;
import com.veteam.voluminousenergy.util.recipe.RecipeUtil;
import com.veteam.voluminousenergy.util.TagUtil;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
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

public class DistillationRecipe extends VEFluidRecipe {
    public static final RecipeType<VEFluidRecipe> RECIPE_TYPE = VERecipes.VERecipeTypes.DISTILLING.get();

    public static final Serializer SERIALIZER = new Serializer();
    private final ResourceLocation recipeId;
    private float thirdChance;

    public DistillationRecipe(ResourceLocation recipeId){
        this.recipeId = recipeId;
    }

    public DistillationRecipe() {
        recipeId = null;
    }

    @Override
    public @NotNull ItemStack getToastSymbol(){
        return new ItemStack(VEBlocks.DISTILLATION_UNIT_BLOCK.get());
    }

    @Override
    public @NotNull ResourceLocation getId(){return recipeId;}

    @Override
    public @NotNull RecipeSerializer<?> getSerializer(){ return SERIALIZER;}

    @Override
    public @NotNull RecipeType<VEFluidRecipe> getType(){return RECIPE_TYPE;}

    public float getThirdChance(){return thirdChance;}


    public static class Serializer implements RecipeSerializer<DistillationRecipe> {

        private static final FluidSerializerHelper<DistillationRecipe> helper = new FluidSerializerHelper<>();

        @Override
        public @NotNull DistillationRecipe fromJson(@NotNull ResourceLocation recipeId, JsonObject json) {
            DistillationRecipe recipe = new DistillationRecipe(recipeId);

            JsonObject ingredientJson = json.get("ingredient").getAsJsonObject();
            int ingredientCount = GsonHelper.getAsInt(ingredientJson, "count", 1);
            recipe.lazyIngredientList.add(Lazy.of(() -> RecipeUtil.modifyIngredientAmounts(Ingredient.fromJson(ingredientJson), ingredientCount)));
            recipe.processTime = GsonHelper.getAsInt(json,"process_time",200);

            JsonObject inputFluid = json.get("input_fluid").getAsJsonObject();
            recipe.lazyFluidIngredientList.add(Lazy.of(() -> FluidIngredient.fromJson(inputFluid)));

            recipe.addFluidOutput(RecipeUtil.pullFluidFromJSON("first_result",json));
            recipe.addFluidOutput(RecipeUtil.pullFluidFromJSON("second_result",json));

            recipe.thirdChance = GsonHelper.getAsFloat(json.get("third_result").getAsJsonObject(),"chance",0);

            recipe.addItemOutput(RecipeUtil.pullItemFromJSON("third_result",json));

            return recipe;
        }

        @Nullable
        @Override
        public DistillationRecipe fromNetwork(@NotNull ResourceLocation recipeId, FriendlyByteBuf buffer){
            DistillationRecipe recipe = new DistillationRecipe((recipeId));
            recipe.thirdChance = buffer.readFloat();
            helper.fromNetwork(recipe,buffer);

            return recipe;
        }

        @Override
        public void toNetwork(FriendlyByteBuf buffer, DistillationRecipe recipe){
            buffer.writeFloat(recipe.thirdChance);
            helper.toNetwork(buffer,recipe);
        }
    }
}
