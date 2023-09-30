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
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.Objects;

public class AqueoulizerRecipe extends VEFluidRecipe {
    public static final RecipeType<VEFluidRecipe> RECIPE_TYPE = VERecipes.VERecipeTypes.AQUEOULIZING.get();

    public static final Serializer SERIALIZER = new Serializer();

    private final ResourceLocation recipeId;

    public AqueoulizerRecipe(ResourceLocation recipeId) {
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
        return new ItemStack(VEBlocks.AQUEOULIZER_BLOCK.get());
    }

    public static class Serializer implements RecipeSerializer<AqueoulizerRecipe> {
        @Override
        public @NotNull AqueoulizerRecipe fromJson(@NotNull ResourceLocation recipeId, JsonObject json) {
            AqueoulizerRecipe recipe = new AqueoulizerRecipe(recipeId);

            JsonObject ingredientJson = json.get("ingredient").getAsJsonObject();
            int ingredientCount = GsonHelper.getAsInt(ingredientJson, "count", 1);
            recipe.lazyIngredientList.add(Lazy.of(() -> RecipeUtil.modifyIngredientAmounts(Ingredient.fromJson(ingredientJson), ingredientCount)));
            recipe.processTime = GsonHelper.getAsInt(json, "process_time", 200);

            JsonObject inputFluid = json.get("input_fluid").getAsJsonObject();

            recipe.lazyFluidIngredientList.add(Lazy.of(() -> FluidIngredient.fromJson(inputFluid)));

            ResourceLocation secondBucketResourceLocation = ResourceLocation.of(GsonHelper.getAsString(json.get("result").getAsJsonObject(), "fluid", "minecraft:empty"), ':');
            int outputAmount = GsonHelper.getAsInt(json.get("result").getAsJsonObject(), "amount", 0);
            FluidStack result = new FluidStack(Objects.requireNonNull(ForgeRegistries.FLUIDS.getValue(secondBucketResourceLocation)), outputAmount);
            recipe.fluidOutputList.add(result);

            return recipe;
        }

        FluidSerializerHelper<AqueoulizerRecipe> helper = new FluidSerializerHelper<>();

        @Nullable
        @Override
        public AqueoulizerRecipe fromNetwork(@NotNull ResourceLocation recipeId, @NotNull FriendlyByteBuf buffer) {
            AqueoulizerRecipe recipe = new AqueoulizerRecipe((recipeId));
            helper.fromNetwork(recipe,buffer);
            return recipe;
        }

        @Override
        public void toNetwork(@NotNull FriendlyByteBuf buffer, @NotNull AqueoulizerRecipe recipe) {
            helper.toNetwork(buffer,recipe);
        }
    }
}
