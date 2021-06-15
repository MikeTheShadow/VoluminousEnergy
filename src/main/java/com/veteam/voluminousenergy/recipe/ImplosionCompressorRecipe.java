package com.veteam.voluminousenergy.recipe;

import com.google.common.collect.ImmutableMap;
import com.google.gson.JsonObject;
import com.veteam.voluminousenergy.blocks.blocks.VEBlocks;
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
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.ForgeRegistryEntry;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

public class ImplosionCompressorRecipe extends VERecipe {
    public static final IRecipeType<ImplosionCompressorRecipe> RECIPE_TYPE = VERecipes.VERecipeTypes.IMPLOSION_COMPRESSING;

    public static final Serializer SERIALIZER = new ImplosionCompressorRecipe.Serializer();

    public final ResourceLocation recipeId;
    public Ingredient ingredient;
    public int ingredientCount;
    public ItemStack result;
    private int processTime;
    private int outputAmount;

    private final Map<Ingredient, Integer> ingredients = new LinkedHashMap<>();

    public Map<Ingredient, Integer> getIngredientMap() {
        return ImmutableMap.copyOf(ingredients);
    }

    public ImplosionCompressorRecipe(ResourceLocation recipeId){ this.recipeId = recipeId; }

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
        ItemStack stack = inv.getItem(0);
        int count = stack.getCount();
        return ingredient.test(stack) && count >= ingredientCount;
    }

    @Override
    public ItemStack assemble(IInventory inv){
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
    public IRecipeSerializer<?> getSerializer(){
        return SERIALIZER;
    }

    @Override
    public IRecipeType<?> getType(){
        return RECIPE_TYPE;
    }

    public int getOutputAmount(){
        return outputAmount;
    }

    @Override
    public ItemStack getToastSymbol(){
        return new ItemStack(VEBlocks.COMPRESSOR_BLOCK);
    }

    public static class Serializer extends ForgeRegistryEntry<IRecipeSerializer<?>> implements IRecipeSerializer<ImplosionCompressorRecipe>{

        public static ArrayList<Item> ingredientList = new ArrayList<>();

        @Override
        public ImplosionCompressorRecipe fromJson(ResourceLocation recipeId, JsonObject json){

            ImplosionCompressorRecipe recipe = new ImplosionCompressorRecipe(recipeId);

            recipe.ingredient = Ingredient.fromJson(json.get("ingredient"));
            recipe.ingredientCount = JSONUtils.getAsInt(json.get("ingredient").getAsJsonObject(),"count",1);
            recipe.processTime = JSONUtils.getAsInt(json, "process_time", 200);

            for (ItemStack stack : recipe.ingredient.getItems()){
                if (!ingredientList.contains(stack.getItem())){
                    ingredientList.add(stack.getItem());
                }
            }

            ResourceLocation itemResourceLocation = ResourceLocation.of(JSONUtils.getAsString(json.get("result").getAsJsonObject(), "item", "minecraft:air"),':');
            int itemAmount = JSONUtils.getAsInt(json.get("result").getAsJsonObject(), "count", 1);
            recipe.result = new ItemStack(ForgeRegistries.ITEMS.getValue(itemResourceLocation));
            recipe.outputAmount = itemAmount;

            return recipe;
        }

        @Nullable
        @Override
        public ImplosionCompressorRecipe fromNetwork(ResourceLocation recipeId, PacketBuffer buffer){
            ImplosionCompressorRecipe recipe = new ImplosionCompressorRecipe(recipeId);
            recipe.ingredient = Ingredient.fromNetwork(buffer);
            recipe.ingredientCount = buffer.readByte();
            recipe.result = buffer.readItem();
            recipe.processTime = buffer.readInt();
            recipe.outputAmount = buffer.readInt();
            return recipe;
        }

        @Override
        public void toNetwork(PacketBuffer buffer, ImplosionCompressorRecipe recipe){
            recipe.ingredient.toNetwork(buffer);
            buffer.writeByte(recipe.getIngredientCount());
            buffer.writeItem(recipe.getResult());
            buffer.writeInt(recipe.processTime);
            buffer.writeInt(recipe.outputAmount);
        }
    }
}
