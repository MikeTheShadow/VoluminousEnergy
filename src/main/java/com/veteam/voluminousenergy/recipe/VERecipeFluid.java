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

public abstract class VERecipeFluid implements IRecipe<IInventory> {

    public Ingredient ingredient;
    public int ingredientCount;
    public ItemStack result;

    public VERecipeFluid() {

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
        return null;
    }

    @Override
    public IRecipeSerializer<?> getSerializer(){
        return null;
    }

    public IRecipeType<VERecipeFluid> getType(){
        return null;
    }

    public ArrayList<Item>  getIngredientList() {
        return null;
    }

    public List<FluidStack> getOutputFluids() {
        return null;
    }

    public int getInputAmount() {
        return -1;
    }

    public int getOutputAmount() {
        return -1;
    }

    public FluidStack getOutputFluid() {
        return null;
    }

    public int getProcessTime() {
        return -1;
    }
}
