package com.veteam.voluminousenergy.recipe;

import com.google.gson.JsonObject;
import com.veteam.voluminousenergy.blocks.blocks.VEBlocks;
import com.veteam.voluminousenergy.util.recipe.IngredientSerializerHelper;
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

public class PrimitiveBlastFurnaceRecipe extends VERecipe {

    public static final RecipeType<PrimitiveBlastFurnaceRecipe> RECIPE_TYPE = VERecipes.VERecipeTypes.PRIMITIVE_BLAST_FURNACING.get();

    public static final Serializer SERIALIZER = new Serializer();

    public PrimitiveBlastFurnaceRecipe(ResourceLocation recipeId) {
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
    public @NotNull RecipeType<?> getType() {
        return RECIPE_TYPE;
    }

    @Override
    public @NotNull ItemStack getToastSymbol() {
        return new ItemStack(VEBlocks.PRIMITIVE_BLAST_FURNACE_BLOCK.get());
    }

    public static class Serializer implements RecipeSerializer<PrimitiveBlastFurnaceRecipe> {

        @Override
        public @NotNull PrimitiveBlastFurnaceRecipe fromJson(@NotNull ResourceLocation recipeId, JsonObject json) {

            PrimitiveBlastFurnaceRecipe recipe = new PrimitiveBlastFurnaceRecipe(recipeId);

            int ingredientCount = GsonHelper.getAsInt(json.get("ingredient").getAsJsonObject(), "count", 1);
            Lazy<Ingredient> ingredient = Lazy.of(() ->
                    RecipeUtil.modifyIngredientAmounts(Ingredient.fromJson(json.get("ingredient")), ingredientCount));
            recipe.addLazyIngredient(ingredient);
            recipe.processTime = GsonHelper.getAsInt(json, "process_time", 200);

            ResourceLocation itemResourceLocation = ResourceLocation.of(GsonHelper.getAsString(json.get("result").getAsJsonObject(), "item", "minecraft:air"), ':');
            int itemAmount = GsonHelper.getAsInt(json.get("result").getAsJsonObject(), "count", 1);
            ItemStack result = new ItemStack(ForgeRegistries.ITEMS.getValue(itemResourceLocation), itemAmount);
            recipe.addResult(result);

            return recipe;
        }

        IngredientSerializerHelper<PrimitiveBlastFurnaceRecipe> helper = new IngredientSerializerHelper<>();

        @Nullable
        @Override
        public PrimitiveBlastFurnaceRecipe fromNetwork(@NotNull ResourceLocation recipeId, @NotNull FriendlyByteBuf buffer) {
            PrimitiveBlastFurnaceRecipe recipe = new PrimitiveBlastFurnaceRecipe(recipeId);
            helper.fromNetwork(recipe, buffer);

            return recipe;
        }

        @Override
        public void toNetwork(@NotNull FriendlyByteBuf buffer, @NotNull PrimitiveBlastFurnaceRecipe recipe) {
            helper.toNetwork(buffer,recipe);

        }
    }

}
