package com.veteam.voluminousenergy.recipe;

import com.google.common.collect.ImmutableMap;
import com.google.gson.JsonObject;
import com.veteam.voluminousenergy.blocks.blocks.VEBlocks;
import com.veteam.voluminousenergy.tools.Config;
import net.minecraft.world.Container;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.util.GsonHelper;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraftforge.registries.ForgeRegistryEntry;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

public class StirlingGeneratorRecipe extends VERecipe {

    public static final RecipeType<StirlingGeneratorRecipe> RECIPE_TYPE = VERecipes.VERecipeTypes.STIRLING;
    public static final Serializer SERIALIZER = new Serializer();

    public final ResourceLocation recipeId;
    public Ingredient ingredient;
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
        return ingredient;
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
        return ingredient.test(stack) && count >= ingredientCount;
    }

    @Override
    public ItemStack assemble(Container inv){
        return ItemStack.EMPTY;
    }

    @Override
    public boolean canCraftInDimensions(int width, int height){
        return true;
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

    public int getEnergyPerTick(){ return energyPerTick;};

    @Override
    public ItemStack getToastSymbol(){
        return new ItemStack(VEBlocks.STIRLING_GENERATOR_BLOCK);
    }

    public static class Serializer extends ForgeRegistryEntry<RecipeSerializer<?>> implements RecipeSerializer<StirlingGeneratorRecipe>{

        public static ArrayList<Item> ingredientList = new ArrayList<>();

        @Override
        public StirlingGeneratorRecipe fromJson(ResourceLocation recipeId, JsonObject json){

            StirlingGeneratorRecipe recipe = new StirlingGeneratorRecipe(recipeId);

            recipe.ingredient = Ingredient.fromJson(json.get("ingredient"));
            recipe.ingredientCount = GsonHelper.getAsInt(json.get("ingredient").getAsJsonObject(),"count",1);
            recipe.processTime = GsonHelper.getAsInt(json, "process_time", 200);
            recipe.energyPerTick  = GsonHelper.getAsInt(json, "energy_per_tick", Config.STIRLING_GENERATOR_GENERATE.get());

            for (ItemStack stack : recipe.ingredient.getItems()){
                if (!ingredientList.contains(stack.getItem())){
                    ingredientList.add(stack.getItem());
                }
            }

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
        public StirlingGeneratorRecipe fromNetwork(ResourceLocation recipeId, FriendlyByteBuf buffer){
            StirlingGeneratorRecipe recipe = new StirlingGeneratorRecipe(recipeId);
            recipe.ingredient = Ingredient.fromNetwork(buffer);
            recipe.ingredientCount = buffer.readByte();
            recipe.processTime = buffer.readInt();
            recipe.energyPerTick = buffer.readInt();
            recipe.result = buffer.readItem();
            return recipe;
        }

        @Override
        public void toNetwork(FriendlyByteBuf buffer, StirlingGeneratorRecipe recipe){
            recipe.ingredient.toNetwork(buffer);
            buffer.writeByte(recipe.getIngredientCount());
            buffer.writeInt(recipe.processTime);
            buffer.writeInt(recipe.energyPerTick);
            buffer.writeItem(recipe.result);
        }
    }
}