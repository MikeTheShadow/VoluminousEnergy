package com.veteam.voluminousenergy.recipe;

import com.google.gson.JsonObject;
import com.mojang.realmsclient.util.JsonUtils;
import com.veteam.voluminousenergy.util.RecipeConstants;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.BucketItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.ForgeRegistryEntry;

import javax.annotation.Nullable;
import javax.annotation.Resource;
import java.util.ArrayList;

public class DistillationRecipe extends VEFluidRecipe {
    public static final IRecipeType<VEFluidRecipe> RECIPE_TYPE = new IRecipeType<VEFluidRecipe>() {
        @Override
        public String toString() {
            return RecipeConstants.DISTILLING.toString();
        }
    };

    public static final Serializer SERIALIZER = new Serializer();

    public static ArrayList<Item> ingredientList = new ArrayList<>();

    private final ResourceLocation recipeId;
    private int processTime;

    private ItemStack inputFluid;
    private ItemStack result;
    private ItemStack secondResult;
    private ItemStack thirdResult;
    private int inputAmount;
    private int outputAmount;
    private int secondAmount;
    private int thirdAmount;

    public DistillationRecipe(ResourceLocation recipeId){
        this.recipeId = recipeId;
    }

    public DistillationRecipe() {
        recipeId = null;
    }

    @Override
    public Ingredient getIngredient(){ return ingredient;}

    @Override
    public int getIngredientCount(){ return ingredientCount;}

    @Override
    public ItemStack getResult() {return result;}

    @Override
    public ItemStack getSecondResult(){return secondResult;}

    @Override
    public int getInputAmount(){ return inputAmount; }

    public FluidStack getSecondFluid(){
        if (secondResult.getItem() instanceof BucketItem){
            return new FluidStack(((BucketItem) secondResult.getItem()).getFluid(), secondAmount);
        }
        return FluidStack.EMPTY;
    }

    public FluidStack getOutputFluid(){
        if (result.getItem() instanceof BucketItem){
            return new FluidStack(((BucketItem) result.getItem()).getFluid(), outputAmount);
        }
        return FluidStack.EMPTY;
    }

    public FluidStack getInputFluid(){
        if (inputFluid.getItem() instanceof BucketItem){
            return new FluidStack(((BucketItem) inputFluid.getItem()).getFluid(), inputAmount);
        }
        return FluidStack.EMPTY;
    }


    public ItemStack getThirdResult(){
        return thirdResult;
    }

    public int getThirdAmount(){
        return thirdAmount;
    }

    @Override
    public boolean matches(IInventory inv, World worldIn){
        ItemStack stack = inv.getStackInSlot(0);
        int count = stack.getCount();
        return ingredient.test(stack) && count >= ingredientCount;
    }

    @Override
    public ItemStack getCraftingResult(IInventory inv){return ItemStack.EMPTY;}

    @Override
    public boolean canFit(int width, int height){return true;}

    @Override
    public ItemStack getRecipeOutput(){return result;}

    @Override
    public ResourceLocation getId(){return recipeId;}

    @Override
    public IRecipeSerializer<?> getSerializer(){ return SERIALIZER;}

    @Override
    public IRecipeType<VEFluidRecipe> getType(){return RECIPE_TYPE;}

    @Override
    public int getOutputAmount() {return outputAmount;}

    @Override
    public int getSecondAmount(){return secondAmount;}

    @Override
    public int getProcessTime() { return processTime; }


    public static class Serializer extends ForgeRegistryEntry<IRecipeSerializer<?>> implements IRecipeSerializer<DistillationRecipe> {
        @Override
        public DistillationRecipe read(ResourceLocation recipeId, JsonObject json) {
            DistillationRecipe recipe = new DistillationRecipe(recipeId);

            recipe.ingredient = Ingredient.deserialize(json.get("ingredient"));
            recipe.ingredientCount = JSONUtils.getInt(json.get("ingredient").getAsJsonObject(), "count", 1);
            recipe.inputAmount = JSONUtils.getInt(json.get("ingredient").getAsJsonObject(), "amount", 0);
            recipe.processTime = JSONUtils.getInt(json,"process_time",200);

            for (ItemStack stack : recipe.ingredient.getMatchingStacks()){
                if(!ingredientList.contains(stack.getItem())){
                    ingredientList.add(stack.getItem());
                }
            }

            ResourceLocation bucketResourceLocation = ResourceLocation.create(JSONUtils.getString(json.get("first_result").getAsJsonObject(),"item","minecraft:empty"),':');
            int itemAmount = JSONUtils.getInt(json.get("first_result").getAsJsonObject(),"amount",0);
            recipe.result = new ItemStack(ForgeRegistries.ITEMS.getValue(bucketResourceLocation));
            recipe.outputAmount = itemAmount;

            ResourceLocation secondBucketResourceLocation = ResourceLocation.create(JSONUtils.getString(json.get("second_result").getAsJsonObject(),"item","minecraft:empty"),':');
            int secondFluidAmount = JSONUtils.getInt(json.get("second_result").getAsJsonObject(),"amount",0);
            recipe.secondResult = new ItemStack(ForgeRegistries.ITEMS.getValue(secondBucketResourceLocation));
            recipe.secondAmount = secondFluidAmount;

            ResourceLocation itemResourceLocation = ResourceLocation.create(JSONUtils.getString(json.get("third_result").getAsJsonObject(),"item","minecraft:empty"),':');
            int thirdItemAmount = JSONUtils.getInt(json.get("third_result").getAsJsonObject(),"count",1);
            recipe.thirdResult = new ItemStack(ForgeRegistries.ITEMS.getValue(itemResourceLocation));
            recipe.thirdAmount = thirdItemAmount;

            return recipe;
        }

        @Nullable
        @Override
        public DistillationRecipe read(ResourceLocation recipeId, PacketBuffer buffer){
            DistillationRecipe recipe = new DistillationRecipe((recipeId));
            recipe.ingredient = Ingredient.read(buffer);
            recipe.ingredientCount = buffer.readByte();
            recipe.result = buffer.readItemStack();
            recipe.inputAmount = buffer.readInt();
            recipe.processTime = buffer.readInt();
            recipe.outputAmount = buffer.readInt();
            recipe.secondResult = buffer.readItemStack();
            recipe.secondAmount = buffer.readInt();
            return recipe;
        }

        @Override
        public void write(PacketBuffer buffer, DistillationRecipe recipe){
            recipe.ingredient.write(buffer);
            buffer.writeByte(recipe.getIngredientCount());
            buffer.writeItemStack(recipe.getResult());
            buffer.writeInt(recipe.inputAmount);
            buffer.writeInt(recipe.processTime);
            buffer.writeInt(recipe.outputAmount);
            buffer.writeItemStack(recipe.secondResult);
            buffer.writeInt(recipe.secondAmount);
        }
    }
}
