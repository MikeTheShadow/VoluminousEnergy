package com.veteam.voluminousenergy.recipe;

import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.Container;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraftforge.fluids.FluidStack;

import java.util.ArrayList;
import java.util.List;

public abstract class VEFluidRecipe implements Recipe<Container> {

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
        return null;
    }

    @Override
    public  abstract RecipeSerializer<?> getSerializer();


    public abstract RecipeType<VEFluidRecipe> getType();

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
