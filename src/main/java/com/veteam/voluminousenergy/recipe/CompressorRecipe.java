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
import java.util.Objects;

public class CompressorRecipe extends VERecipe {
    public static final RecipeType<CompressorRecipe> RECIPE_TYPE = VERecipes.VERecipeTypes.COMPRESSING.get();

    public static final Serializer SERIALIZER = new Serializer();


    public CompressorRecipe(ResourceLocation recipeId){ this.recipeId = recipeId; }

    @Override
    public @NotNull ResourceLocation getId(){
        return recipeId;
    }

    @Override
    public @NotNull RecipeSerializer<?> getSerializer(){
        return SERIALIZER;
    }

    @Override
    public @NotNull RecipeType<?> getType(){
        return RECIPE_TYPE;
    }

    @Override
    public @NotNull ItemStack getToastSymbol(){
        return new ItemStack(VEBlocks.COMPRESSOR_BLOCK.get());
    }

    public static class Serializer implements RecipeSerializer<CompressorRecipe>{

        @Override
        public @NotNull CompressorRecipe fromJson(@NotNull ResourceLocation recipeId, JsonObject json){

            CompressorRecipe recipe = new CompressorRecipe(recipeId);

            JsonObject ingredientJson = json.get("ingredient").getAsJsonObject();

            int ingredientCount = GsonHelper.getAsInt(ingredientJson, "count", 1);
            Lazy<Ingredient> ingredientLazy = Lazy.of(() -> RecipeUtil.modifyIngredientAmounts(Ingredient.fromJson(ingredientJson), ingredientCount));
            recipe.addLazyIngredient(ingredientLazy);

            recipe.processTime = GsonHelper.getAsInt(json, "process_time", 200);

            ResourceLocation itemResourceLocation = ResourceLocation.of(GsonHelper.getAsString(json.get("result").getAsJsonObject(), "item", "minecraft:air"),':');
            int itemAmount = GsonHelper.getAsInt(json.get("result").getAsJsonObject(), "count", 1);
            ItemStack result = new ItemStack(Objects.requireNonNull(ForgeRegistries.ITEMS.getValue(itemResourceLocation)),itemAmount);
            recipe.results.add(result);

            return recipe;
        }

        IngredientSerializerHelper<CompressorRecipe> helper = new IngredientSerializerHelper<>();

        @Nullable
        @Override
        public CompressorRecipe fromNetwork(@NotNull ResourceLocation recipeId, @NotNull FriendlyByteBuf buffer){
            CompressorRecipe recipe = new CompressorRecipe(recipeId);
            helper.fromNetwork(recipe,buffer);
            return recipe;
        }

        @Override
        public void toNetwork(@NotNull FriendlyByteBuf buffer, @NotNull CompressorRecipe recipe){
            helper.toNetwork(buffer,recipe);
        }
    }
}
