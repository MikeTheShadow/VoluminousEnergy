package com.veteam.voluminousenergy.recipe;

import com.veteam.voluminousenergy.util.RecipeUtil;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.common.util.Lazy;
import net.minecraftforge.fluids.FluidStack;

import java.util.ArrayList;
import java.util.List;

public abstract class VEFluidRecipe implements Recipe<Container> {

    public Lazy<Ingredient> ingredient;
    public int ingredientCount;
    public ItemStack result;

    // Fluids
    public Lazy<ArrayList<Item>> ingredientList = RecipeUtil.getLazyItemsFromIngredient(this);
    public Lazy<ArrayList<FluidStack>> fluidInputList;
    public Lazy<ArrayList<Fluid>> rawFluidInputList;
    public Lazy<Integer> inputArraySize;

    public boolean fluidUsesTagKey;
    public String tagKeyString;

    public VEFluidRecipe() {

    }

    public Ingredient getIngredient() {
        return ingredient.get();
    }

    public int getIngredientCount() {
        return ingredientCount;
    }

    public ItemStack getResult() { return result; }

    @Override
    public boolean matches(Container inv, Level worldIn){
        ItemStack stack = inv.getItem(0);
        int count = stack.getCount();
        return ingredient.get().test(stack) && count >= ingredientCount;
    }
    @Override
    public ItemStack assemble(Container inv, RegistryAccess registryAccess){
        return this.assemble(inv);
    }

    // NOTE: Legacy impl pre 1.19.4
    public ItemStack assemble(Container inv){
        return ItemStack.EMPTY;
    }

    @Override
    public boolean canCraftInDimensions(int width, int height){
        return true;
    }

    @Override
    public ItemStack getResultItem(RegistryAccess registryAccess){
        return this.getResultItem();
    }

    // NOTE: Legacy impl pre 1.19.4
    public ItemStack getResultItem() { return result; }

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
