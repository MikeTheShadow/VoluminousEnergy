package com.veteam.voluminousenergy.recipe;

import com.google.common.collect.ImmutableMap;
import com.google.gson.JsonObject;
import com.veteam.voluminousenergy.tools.Config;
import com.veteam.voluminousenergy.util.RecipeConstants;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.registries.ForgeRegistryEntry;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

public class StirlingGeneratorRecipe extends VERecipe {

    public static final IRecipeType<StirlingGeneratorRecipe> RECIPE_TYPE = new IRecipeType<StirlingGeneratorRecipe>() {
        @Override
        public String toString() {
            return RecipeConstants.STIRLING.toString();
        }
    };
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
    public boolean matches(IInventory inv, World worldIn){
        ItemStack stack = inv.getStackInSlot(0);
        int count = stack.getCount();
        return ingredient.test(stack) && count >= ingredientCount;
    }

    @Override
    public ItemStack getCraftingResult(IInventory inv){
        return ItemStack.EMPTY;
    }

    @Override
    public boolean canFit(int width, int height){
        return true;
    }

    @Override
    public ItemStack getRecipeOutput(){
        return result;
    }

    @Override
    public ResourceLocation getId(){
        return recipeId;
    }

    @Override
    public IRecipeSerializer<?> getSerializer(){
        return SERIALIZER;
    }

    @Override
    public IRecipeType<?> getType(){
        return RECIPE_TYPE;
    }

    public int getEnergyPerTick(){ return energyPerTick;};

    public static class Serializer extends ForgeRegistryEntry<IRecipeSerializer<?>> implements IRecipeSerializer<StirlingGeneratorRecipe>{

        public static ArrayList<Item> ingredientList = new ArrayList<>();

        @Override
        public StirlingGeneratorRecipe read(ResourceLocation recipeId, JsonObject json){

            StirlingGeneratorRecipe recipe = new StirlingGeneratorRecipe(recipeId);

            recipe.ingredient = Ingredient.deserialize(json.get("ingredient"));
            recipe.ingredientCount = JSONUtils.getInt(json.get("ingredient").getAsJsonObject(),"count",1);
            recipe.processTime = JSONUtils.getInt(json, "process_time", 200);
            recipe.energyPerTick  = JSONUtils.getInt(json, "energy_per_tick", Config.STIRLING_GENERATOR_GENERATE.get());

            for (ItemStack stack : recipe.ingredient.getMatchingStacks()){
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
        public StirlingGeneratorRecipe read(ResourceLocation recipeId, PacketBuffer buffer){
            StirlingGeneratorRecipe recipe = new StirlingGeneratorRecipe(recipeId);
            recipe.ingredient = Ingredient.read(buffer);
            recipe.ingredientCount = buffer.readByte();
            recipe.processTime = buffer.readInt();
            recipe.energyPerTick = buffer.readInt();
            recipe.result = buffer.readItemStack();
            return recipe;
        }

        @Override
        public void write(PacketBuffer buffer, StirlingGeneratorRecipe recipe){
            recipe.ingredient.write(buffer);
            buffer.writeByte(recipe.getIngredientCount());
            buffer.writeInt(recipe.processTime);
            buffer.writeInt(recipe.energyPerTick);
            buffer.writeItemStack(recipe.result);
        }
    }
}