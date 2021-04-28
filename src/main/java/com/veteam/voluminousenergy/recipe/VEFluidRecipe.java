package com.veteam.voluminousenergy.recipe;

import net.minecraft.fluid.Fluid;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidStack;

import java.util.ArrayList;
import java.util.List;

public abstract class VEFluidRecipe implements IRecipe<IInventory> {

    public Ingredient ingredient;
    public int ingredientCount;
    public ItemStack result;

    public VEFluidRecipe() {

    }

    public Ingredient getIngredient() {
        return ingredient;
    }

    public int getIngredientCount() {
        return ingredientCount;
    }

    public ItemStack getResult() { return result; }

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
        return null;
    }

    @Override
    public  abstract IRecipeSerializer<?> getSerializer();


    public abstract IRecipeType<VEFluidRecipe> getType();

    public abstract ArrayList<Item>  getIngredientList();

    public abstract List<FluidStack> getFluids();

    public abstract List<Fluid> getRawFluids();

    public abstract List<ItemStack> getResults();

    public abstract int getInputAmount();

    public abstract int getOutputAmount();

    public abstract FluidStack getOutputFluid();

    public abstract List<Integer> getAmounts();

    public abstract int getProcessTime();
}
