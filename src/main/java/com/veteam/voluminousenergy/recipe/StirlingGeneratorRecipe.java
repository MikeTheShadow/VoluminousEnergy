package com.veteam.voluminousenergy.recipe;

import com.google.gson.JsonObject;
import com.veteam.voluminousenergy.blocks.blocks.VEBlocks;
import com.veteam.voluminousenergy.tools.Config;
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
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;

public class StirlingGeneratorRecipe extends VERecipe {

    public static final RecipeType<StirlingGeneratorRecipe> RECIPE_TYPE = VERecipes.VERecipeTypes.STIRLING.get();
    public static final Serializer SERIALIZER = new Serializer();
    private int energyPerTick;

    public StirlingGeneratorRecipe(ResourceLocation recipeId){ this.recipeId = recipeId; }

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

    public int getEnergyPerTick(){ return energyPerTick;}

    @Override
    public @NotNull ItemStack getToastSymbol(){
        return new ItemStack(VEBlocks.STIRLING_GENERATOR_BLOCK.get());
    }

    public static class Serializer implements RecipeSerializer<StirlingGeneratorRecipe> {

        @Override
        public @NotNull StirlingGeneratorRecipe fromJson(@NotNull ResourceLocation recipeId, JsonObject json){

            StirlingGeneratorRecipe recipe = new StirlingGeneratorRecipe(recipeId);

            JsonObject ingredientJson = json.get("ingredient").getAsJsonObject();

            int ingredientCount = GsonHelper.getAsInt(ingredientJson, "count", 1);
            Lazy<Ingredient> ingredientLazy = Lazy.of(() -> RecipeUtil.modifyIngredientAmounts(Ingredient.fromJson(ingredientJson), ingredientCount));
            recipe.addLazyIngredient(ingredientLazy);

            recipe.processTime = GsonHelper.getAsInt(json, "process_time", 200);
            recipe.energyPerTick  = GsonHelper.getAsInt(json, "energy_per_tick", Config.STIRLING_GENERATOR_GENERATE.get());

            return recipe;
        }

        IngredientSerializerHelper<StirlingGeneratorRecipe> helper = new IngredientSerializerHelper<>();

        @Nullable
        @Override
        public StirlingGeneratorRecipe fromNetwork(@NotNull ResourceLocation recipeId, @NotNull FriendlyByteBuf buffer){
            StirlingGeneratorRecipe recipe = new StirlingGeneratorRecipe(recipeId);
            helper.fromNetwork(recipe, buffer);
            recipe.energyPerTick = buffer.readInt();
            return recipe;
        }

        @Override
        public void toNetwork(@NotNull FriendlyByteBuf buffer, @NotNull StirlingGeneratorRecipe recipe){
            helper.toNetwork(buffer,recipe);
            buffer.writeInt(recipe.energyPerTick);
        }
    }
}