package com.veteam.voluminousenergy.recipe;

import com.veteam.voluminousenergy.util.recipe.RecipeUtil;
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
import org.apache.commons.lang3.NotImplementedException;
import org.jetbrains.annotations.NotNull;

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
    public ArrayList<FluidStack> fluidOutputList = new ArrayList<>();

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
    public boolean matches(Container inv, @NotNull Level worldIn){
        ItemStack stack = inv.getItem(0);
        int count = stack.getCount();
        return ingredient.get().test(stack) && count >= ingredientCount;
    }
    @Override
    public @NotNull ItemStack assemble(@NotNull Container inv, @NotNull RegistryAccess registryAccess){
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
    public @NotNull ItemStack getResultItem(@NotNull RegistryAccess registryAccess){
        return this.getResultItem();
    }

    // NOTE: Legacy impl pre 1.19.4
    public ItemStack getResultItem() { return result; }

    @Override
    public abstract @NotNull ResourceLocation getId();

    @Override
    public  abstract @NotNull RecipeSerializer<?> getSerializer();


    public abstract @NotNull RecipeType<VEFluidRecipe> getType();

    public abstract ArrayList<Item>  getIngredientList();

    public List<FluidStack> getInputFluids() {
        return fluidInputList.get();
    }

    public abstract List<ItemStack> getOutputItems();

    public List<FluidStack> getOutputFluids() {
        return this.fluidOutputList;
    }

    public abstract int getProcessTime();

    public Lazy<ArrayList<Item>> getLazyIngredients() {
        return this.ingredientList;
    }

    public FluidStack getOutputFluid(int slot) {
        return this.fluidOutputList.get(slot);
    }

    public FluidStack getInputFluid(int slot) {
        return getInputFluids().get(slot); // Use getInputFluids because it might need to be overriden in some different entities
    }


    public List<Float> getRNGAmounts() {
        throw new NotImplementedException("This method needs to be impl'd before call in " + this.getClass().getName());
    }
}
