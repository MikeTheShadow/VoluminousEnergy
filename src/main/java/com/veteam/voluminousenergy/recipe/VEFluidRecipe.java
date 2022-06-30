package com.veteam.voluminousenergy.recipe;

import com.veteam.voluminousenergy.util.RecipeUtil;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.common.util.Lazy;
import net.minecraftforge.fluids.FluidStack;

import java.util.ArrayList;
import java.util.List;

public abstract class VEFluidRecipe extends VERecipe {

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

    @Override
    public boolean canCraftInDimensions(int width, int height){
        return true;
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
