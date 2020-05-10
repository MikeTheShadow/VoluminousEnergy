package com.veteam.voluminousenergy.recipe;

import com.google.gson.JsonObject;
import com.veteam.voluminousenergy.items.VEItems;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.Style;
import net.minecraft.world.World;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.ForgeRegistryEntry;

import javax.annotation.Nullable;
import javax.xml.transform.Result;
import java.util.ArrayList;

public class PrimitiveBlastFurnaceRecipe extends VERecipe {

    public static final IRecipeType<PrimitiveBlastFurnaceRecipe> recipeType = IRecipeType.register("primitive_blast_furnacing");
    public static final Serializer serializer = new Serializer();

    public final ResourceLocation recipeId;
    public Ingredient ingredient;
    public int ingredientCount;
    public ItemStack result;
    private int processTime;
    private int outputAmount;

    public PrimitiveBlastFurnaceRecipe(ResourceLocation recipeId){ this.recipeId = recipeId; }


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

    public int getOutputAmount(){
        return outputAmount;
    }

    public static class Serializer extends ForgeRegistryEntry<IRecipeSerializer<?>> implements IRecipeSerializer<PrimitiveBlastFurnaceRecipe>{

        public static ArrayList<Item> ingredientList = new ArrayList<>();
        public static ItemStack Result;

        @Override
        public PrimitiveBlastFurnaceRecipe read(ResourceLocation recipeId, JsonObject json){

            PrimitiveBlastFurnaceRecipe recipe = new PrimitiveBlastFurnaceRecipe(recipeId);

            recipe.ingredient = Ingredient.deserialize(json.get("ingredient"));
            recipe.ingredientCount = JSONUtils.getInt(json.get("ingredient").getAsJsonObject(),"count",1);
            recipe.processTime = JSONUtils.getInt(json, "processTime", 200);

            for (ItemStack stack : recipe.ingredient.getMatchingStacks()){
                if (!ingredientList.contains(stack.getItem())){
                    ingredientList.add(stack.getItem());
                }
            }

            ResourceLocation itemResourceLocation = ResourceLocation.create(JSONUtils.getString(json.get("result").getAsJsonObject(), "item", "minecraft:air"),':');
            int itemAmount = JSONUtils.getInt(json.get("result").getAsJsonObject(), "count", 1);
            //recipe.result = new FluidStack(ForgeRegistries.FLUIDS.getValue(fluidResourceLocation),fluidAmount);
            //recipe.result = new ItemStack(VEItems.COALCOKE);
            recipe.result = new ItemStack(ForgeRegistries.ITEMS.getValue(itemResourceLocation));
            recipe.outputAmount = itemAmount;
            Result = recipe.result;



            return recipe;
        }

        @Nullable
        @Override
        public PrimitiveBlastFurnaceRecipe read(ResourceLocation recipeId, PacketBuffer buffer){
            PrimitiveBlastFurnaceRecipe recipe = new PrimitiveBlastFurnaceRecipe(recipeId);
            recipe.ingredient = Ingredient.read(buffer);
            recipe.ingredientCount = buffer.readByte();
            recipe.result = buffer.readItemStack();
            recipe.processTime = buffer.readInt();
            recipe.outputAmount = buffer.readInt();
            return recipe;
        }

        @Override
        public void write(PacketBuffer buffer, PrimitiveBlastFurnaceRecipe recipe){
            recipe.ingredient.write(buffer);
            buffer.writeByte(recipe.getIngredientCount());
            buffer.writeItemStack(recipe.getResult());
            buffer.writeInt(recipe.processTime);
            buffer.writeInt(recipe.outputAmount);
        }
    }

}
