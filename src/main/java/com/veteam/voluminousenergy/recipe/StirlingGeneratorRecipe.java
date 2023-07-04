package com.veteam.voluminousenergy.recipe;

import com.google.common.collect.ImmutableMap;
import com.google.gson.JsonObject;
import com.veteam.voluminousenergy.blocks.blocks.VEBlocks;
import com.veteam.voluminousenergy.tools.Config;
import com.veteam.voluminousenergy.util.recipe.RecipeUtil;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.util.Lazy;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.LinkedHashMap;
import java.util.Map;

public class StirlingGeneratorRecipe extends VERecipe {

    public static final RecipeType<StirlingGeneratorRecipe> RECIPE_TYPE = VERecipes.VERecipeTypes.STIRLING.get();
    public static final Serializer SERIALIZER = new Serializer();

    public final ResourceLocation recipeId;
    public Lazy<Ingredient> ingredient;
    public int ingredientCount;
    public ItemStack result;
    private int energyPerTick;
    private int processTime;

    public StirlingGeneratorRecipe(ResourceLocation recipeId){ this.recipeId = recipeId; }


    private final Map<Ingredient, Integer> ingredients = new LinkedHashMap<>();

    public Map<Ingredient, Integer> getIngredientMap() {
        return ImmutableMap.copyOf(ingredients);
    }

    @Override
    public Ingredient getIngredient() {
        return ingredient.get();
    }

    public int getIngredientCount() {
        return ingredientCount;
    }

    @Override
    public ItemStack getResult() { return result; }

    public int getProcessTime() { return processTime; }

    @Override
    public boolean matches(Container inv, Level worldIn){
        ItemStack stack = inv.getItem(0);
        int count = stack.getCount();
        return ingredient.get().test(stack) && count >= ingredientCount;
    }

    @Override
    public ItemStack getResultItem(){
        return result;
    }

    @Override
    public ResourceLocation getId(){
        return recipeId;
    }

    @Override
    public RecipeSerializer<?> getSerializer(){
        return SERIALIZER;
    }

    @Override
    public RecipeType<?> getType(){
        return RECIPE_TYPE;
    }

    public int getEnergyPerTick(){ return energyPerTick;}

    @Override
    public ItemStack getToastSymbol(){
        return new ItemStack(VEBlocks.STIRLING_GENERATOR_BLOCK.get());
    }

    public static class Serializer implements RecipeSerializer<StirlingGeneratorRecipe> {

        @Override
        public @NotNull StirlingGeneratorRecipe fromJson(@NotNull ResourceLocation recipeId, JsonObject json){

            StirlingGeneratorRecipe recipe = new StirlingGeneratorRecipe(recipeId);

            JsonObject ingredientJson = json.get("ingredient").getAsJsonObject();

            int ingredientCount = GsonHelper.getAsInt(ingredientJson, "count", 1);
            recipe.ingredient = Lazy.of(() -> RecipeUtil.modifyIngredientAmounts(Ingredient.fromJson(ingredientJson), ingredientCount));
            recipe.processTime = GsonHelper.getAsInt(json, "process_time", 200);
            recipe.energyPerTick  = GsonHelper.getAsInt(json, "energy_per_tick", Config.STIRLING_GENERATOR_GENERATE.get());

            recipe.result = new ItemStack(Items.BUCKET); // REQUIRED TO PREVENT JEI OR VANILLA RECIPE BOOK TO RETURN A NULL POINTER
            return recipe;
        }

        /**
         * These buffers are more important when dealing with a dedicated server. Even if you aren't going to use a result,
         * and lets say result is just an arbirary item, YOU MUST include it in the buffers or else the client recipe book,
         * and to extention any mod that checks recipes (hint hint JEI) will also NullPointerException out. The client will not
         * crash, the server will be fine, but the mods that check recipes (hint hint JEI) will be borked.
         **/
        @Nullable
        @Override
        public StirlingGeneratorRecipe fromNetwork(@NotNull ResourceLocation recipeId, FriendlyByteBuf buffer){
            StirlingGeneratorRecipe recipe = new StirlingGeneratorRecipe(recipeId);
            recipe.ingredientCount = buffer.readByte();
            recipe.processTime = buffer.readInt();
            recipe.energyPerTick = buffer.readInt();
            recipe.result = buffer.readItem();

            Ingredient tempIngredient = Ingredient.fromNetwork(buffer);
            recipe.ingredient = Lazy.of(() -> tempIngredient);

            return recipe;
        }

        @Override
        public void toNetwork(FriendlyByteBuf buffer, StirlingGeneratorRecipe recipe){
            buffer.writeByte(recipe.getIngredientCount());
            buffer.writeInt(recipe.processTime);
            buffer.writeInt(recipe.energyPerTick);
            buffer.writeItem(recipe.result);

            recipe.ingredient.get().toNetwork(buffer);

        }
    }
}