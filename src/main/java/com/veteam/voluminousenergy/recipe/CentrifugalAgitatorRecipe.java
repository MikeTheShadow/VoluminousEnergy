package com.veteam.voluminousenergy.recipe;

import com.google.gson.JsonObject;
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
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.ForgeRegistryEntry;

import javax.annotation.Nullable;
import java.util.ArrayList;

public class CentrifugalAgitatorRecipe extends VERecipe {
    // TODO: Add support for second output fluid

    public static final IRecipeType<CentrifugalAgitatorRecipe> recipeType = IRecipeType.register("centrifugal_agitating");
    public static final CentrifugalAgitatorRecipe.Serializer serializer = new CentrifugalAgitatorRecipe.Serializer();

    public final ResourceLocation recipeId;
    public FluidStack inputFluid;
    public FluidStack result;
    public FluidStack secondResult;
    public int inputAmount;
    private int processTime;
    private int outputAmount;
    public int secondAmount;

    public CentrifugalAgitatorRecipe(ResourceLocation recipeId){ this.recipeId = recipeId; }


    @Override
    public Ingredient getIngredient() {
        return null;
    }

    @Override
    public ItemStack getResult() { return null; }

    public FluidStack getFluidResult(){return result;}

    public int getProcessTime() { return processTime; }

    @Override
    public boolean matches(IInventory inv, World worldIn){
        return false;
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
        return null;
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

    public static class Serializer extends ForgeRegistryEntry<IRecipeSerializer<?>> implements IRecipeSerializer<CentrifugalAgitatorRecipe>{

        public static ArrayList<Item> ingredientList = new ArrayList<>();
        public static ItemStack Result;

        @Override
        public CentrifugalAgitatorRecipe read(ResourceLocation recipeId, JsonObject json){

            CentrifugalAgitatorRecipe recipe = new CentrifugalAgitatorRecipe(recipeId);

            recipe.processTime = JSONUtils.getInt(json, "processTime", 200);

            ResourceLocation fluidInputResourceLocation = ResourceLocation.create(JSONUtils.getString(json.get("input").getAsJsonObject(),"fluid","minecraft:empty"), ':');
            int inputFluidAmount = JSONUtils.getInt(json.get("input").getAsJsonObject(),"amount",0);
            recipe.inputFluid = new FluidStack(ForgeRegistries.FLUIDS.getValue(fluidInputResourceLocation), inputFluidAmount);
            recipe.inputAmount = inputFluidAmount;

            ResourceLocation fluidOutputResourceLocation = ResourceLocation.create(JSONUtils.getString(json.get("first_result").getAsJsonObject(), "fluid", "minecraft:empty"),':');
            int outputFluidAmount = JSONUtils.getInt(json.get("first_result").getAsJsonObject(), "amount", 0);
            recipe.result = new FluidStack(ForgeRegistries.FLUIDS.getValue(fluidOutputResourceLocation),outputFluidAmount);
            recipe.outputAmount = outputFluidAmount;

            ResourceLocation secondFluidOutputResourceLocation = ResourceLocation.create(JSONUtils.getString(json.get("second_result").getAsJsonObject(), "fluid", "minecraft:empty"),':');
            int secondOutputFluidAmount = JSONUtils.getInt(json.get("second_result").getAsJsonObject(), "amount", 0);
            recipe.secondResult = new FluidStack(ForgeRegistries.FLUIDS.getValue(secondFluidOutputResourceLocation),secondOutputFluidAmount);
            recipe.secondAmount = secondOutputFluidAmount;

            return recipe;
        }

        @Nullable
        @Override
        public CentrifugalAgitatorRecipe read(ResourceLocation recipeId, PacketBuffer buffer){
            CentrifugalAgitatorRecipe recipe = new CentrifugalAgitatorRecipe(recipeId);
            recipe.processTime = buffer.readInt();
            recipe.inputFluid = buffer.readFluidStack();
            recipe.inputAmount = buffer.readInt();
            recipe.result = buffer.readFluidStack();
            //Process time old position
            recipe.outputAmount = buffer.readInt();
            recipe.secondResult = buffer.readFluidStack();
            recipe.secondAmount = buffer.readInt();
            return recipe;
        }

        @Override
        public void write(PacketBuffer buffer, CentrifugalAgitatorRecipe recipe){
            buffer.writeInt(recipe.processTime);
            buffer.writeFluidStack(recipe.inputFluid);
            buffer.writeInt(recipe.inputAmount);
            buffer.writeFluidStack(recipe.result);
            // process time old position
            buffer.writeInt(recipe.outputAmount);
            buffer.writeFluidStack(recipe.secondResult);
            buffer.writeInt(recipe.secondAmount);
        }
    }


}
