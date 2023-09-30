package com.veteam.voluminousenergy.recipe;

import com.veteam.voluminousenergy.VoluminousEnergy;
import com.veteam.voluminousenergy.util.recipe.FluidIngredient;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.util.Lazy;
import net.minecraftforge.fluids.FluidStack;
import org.apache.commons.lang3.NotImplementedException;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public abstract class VEFluidRecipe implements Recipe<Container> {


    List<Ingredient> ingredientList = null;
    List<Lazy<Ingredient>> lazyIngredientList = new ArrayList<>();
    List<FluidIngredient> fluidIngredientList = null;
    List<Lazy<FluidIngredient>> lazyFluidIngredientList = new ArrayList<>();

    List<FluidStack> fluidOutputList = new ArrayList<>();
    List<ItemStack> itemOutputList = new ArrayList<>();
    int processTime;

    public VEFluidRecipe() {

    }

    @Override
    public boolean matches(Container inv, @NotNull Level worldIn) {
        throw new NotImplementedException();
    }

    @Override
    public @NotNull ItemStack assemble(@NotNull Container inv, @NotNull RegistryAccess registryAccess) {
        return this.assemble(inv);
    }

    // NOTE: Legacy impl pre 1.19.4
    public ItemStack assemble(Container inv) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return true;
    }

    @Override
    public abstract @NotNull ResourceLocation getId();

    @Override
    public abstract @NotNull RecipeSerializer<?> getSerializer();


    public abstract @NotNull RecipeType<VEFluidRecipe> getType();

    public List<ItemStack> getOutputItems() {
        return this.itemOutputList;
    }

    public List<FluidStack> getOutputFluids() {
        return this.fluidOutputList;
    }

    public FluidStack getOutputFluid(int slot) {
        return this.fluidOutputList.get(slot).copy();
    }


    public List<FluidIngredient> getFluidIngredients() {
        if (this.fluidIngredientList == null) {
            fluidIngredientList = new ArrayList<>();
            lazyFluidIngredientList.forEach(fluidIngredientLazy -> fluidIngredientList.add(fluidIngredientLazy.get()));
        }
        return this.fluidIngredientList;
    }

    public FluidIngredient getFluidIngredient(int slot) {
        return getFluidIngredients().get(slot);
    }

    public int getFluidIngredientAmount(int slot) {
        return getFluidIngredients().get(slot).getFluids()[0].getAmount();
    }

    @Override
    public @NotNull ItemStack getResultItem(@NotNull RegistryAccess registryAccess) {
        VoluminousEnergy.LOGGER.warn("Suspicious call to getResultItem in " + this.getClass().getName() + ".");
        return new ItemStack(Items.BUCKET,1);
    }

    public List<Ingredient> getItemIngredients() {
        if (this.ingredientList == null) {
            ingredientList = new ArrayList<>();
            lazyIngredientList.forEach(fluidIngredientLazy -> ingredientList.add(fluidIngredientLazy.get()));
        }
        return this.ingredientList;
    }

    public Ingredient getItemIngredient(int slot) {
        return getItemIngredients().get(slot);
    }

    public List<Float> getRNGAmounts() {
        throw new NotImplementedException("This method needs to be impl'd before call in " + this.getClass().getName());
    }

    public int getProcessTime() {
        return this.processTime;
    }

    public void setProcessTime(int time) {
        this.processTime = time;
    }

    public List<Lazy<Ingredient>> getLazyIngredientList() {
        return lazyIngredientList;
    }

    public List<FluidIngredient> getFluidIngredientList() {
        return fluidIngredientList;
    }

    public List<Lazy<FluidIngredient>> getLazyFluidIngredientList() {
        return lazyFluidIngredientList;
    }

    public List<FluidStack> getFluidOutputList() {
        return fluidOutputList;
    }

    public void addFluidOutput(FluidStack stack) {
        this.fluidOutputList.add(stack);
    }

    public List<ItemStack> getItemOutputList() {
        return itemOutputList;
    }

    public void setIngredientList(List<Ingredient> ingredientList) {
        this.ingredientList = ingredientList;
    }



    public void setLazyIngredientList(List<Lazy<Ingredient>> lazyIngredientList) {
        this.lazyIngredientList = lazyIngredientList;
    }

    public void setFluidIngredientList(List<FluidIngredient> fluidIngredientList) {
        this.fluidIngredientList = fluidIngredientList;
    }

    public void setLazyFluidIngredientList(List<Lazy<FluidIngredient>> lazyFluidIngredientList) {
        this.lazyFluidIngredientList = lazyFluidIngredientList;
    }

    public void setFluidOutputList(List<FluidStack> fluidOutputList) {
        this.fluidOutputList = fluidOutputList;
    }

    public void setItemOutputList(List<ItemStack> itemOutputList) {
        this.itemOutputList = itemOutputList;
    }

    public void addItemOutput(ItemStack stack) {
        this.itemOutputList.add(stack);
    }

    public ItemStack getOutputItem(int slot) {
        return this.itemOutputList.get(slot);
    }
}
