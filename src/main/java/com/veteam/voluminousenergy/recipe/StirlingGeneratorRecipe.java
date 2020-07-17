package com.veteam.voluminousenergy.recipe;

import com.google.gson.JsonObject;
import com.veteam.voluminousenergy.tools.Config;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
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

public class StirlingGeneratorRecipe extends VERecipe {
    public static final IRecipeType<StirlingGeneratorRecipe> recipeType = IRecipeType.register("stirling");
    public static final Serializer SERIALIZER = new Serializer();

    public final ResourceLocation recipeId;
    public Ingredient ingredient;
    public int ingredientCount;
    public ItemStack result;
    private int energyPerTick;
    private int processTime;

    public StirlingGeneratorRecipe(ResourceLocation recipeId){ this.recipeId = recipeId; }


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
        return serializer;
    }

    @Override
    public IRecipeType<?> getType(){
        return recipeType;
    }

    public int getEnergyPerTick(){ return energyPerTick;};

    public static class Serializer extends ForgeRegistryEntry<IRecipeSerializer<?>> implements IRecipeSerializer<StirlingGeneratorRecipe>{

        public static ArrayList<Item> ingredientList = new ArrayList<>();

        @Override
        public StirlingGeneratorRecipe read(ResourceLocation recipeId, JsonObject json){

            StirlingGeneratorRecipe recipe = new StirlingGeneratorRecipe(recipeId);

            recipe.ingredient = Ingredient.deserialize(json.get("ingredient"));
            recipe.ingredientCount = JSONUtils.getInt(json.get("ingredient").getAsJsonObject(),"count",1);
            recipe.processTime = JSONUtils.getInt(json, "processTime", 200);
            recipe.energyPerTick  = JSONUtils.getInt(json, "energyPerTick", Config.STIRLING_GENERATOR_GENERATE.get());

            for (ItemStack stack : recipe.ingredient.getMatchingStacks()){
                if (!ingredientList.contains(stack.getItem())){
                    ingredientList.add(stack.getItem());
                }
            }

            return recipe;
        }

        @Nullable
        @Override
        public StirlingGeneratorRecipe read(ResourceLocation recipeId, PacketBuffer buffer){
            StirlingGeneratorRecipe recipe = new StirlingGeneratorRecipe(recipeId);
            recipe.ingredient = Ingredient.read(buffer);
            recipe.ingredientCount = buffer.readByte();
            recipe.processTime = buffer.readInt();
            recipe.energyPerTick = buffer.readInt();
            return recipe;
        }

        @Override
        public void write(PacketBuffer buffer, StirlingGeneratorRecipe recipe){
            recipe.ingredient.write(buffer);
            buffer.writeByte(recipe.getIngredientCount());
            buffer.writeInt(recipe.processTime);
            buffer.writeInt(recipe.energyPerTick);
        }
    }
}