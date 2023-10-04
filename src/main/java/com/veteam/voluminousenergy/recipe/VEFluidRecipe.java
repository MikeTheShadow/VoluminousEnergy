package com.veteam.voluminousenergy.recipe;

import com.veteam.voluminousenergy.util.recipe.FluidIngredient;
import com.veteam.voluminousenergy.util.recipe.serializers.VEFluidRecipeSerializer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraftforge.fluids.FluidStack;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class VEFluidRecipe extends VERecipe {

    public List<FluidIngredient> fluidIngredientList;
    public List<FluidStack> fluidOutputList;

    private static final VEFluidRecipeSerializer SERIALIZER = new VEFluidRecipeSerializer();

    public VEFluidRecipe() {

    }

    public VEFluidRecipe(List<Ingredient> i, List<FluidIngredient> fi, List<FluidStack> of, List<ItemStack> oi,int processTime) {
        super(i,oi,processTime);
        fluidIngredientList = fi;
        fluidOutputList = of;
        this.processTime = processTime;
    }

    @Override
    public @NotNull RecipeSerializer<? extends VERecipe> getSerializer() {
        return SERIALIZER;
    }

    public List<FluidStack> getOutputFluids() {
        return this.fluidOutputList;
    }

    public FluidStack getOutputFluid(int slot) {
        return this.fluidOutputList.get(slot).copy();
    }


    public List<FluidIngredient> getFluidIngredients() {
        return this.fluidIngredientList;
    }

    public FluidIngredient getFluidIngredient(int slot) {
        return getFluidIngredients().get(slot);
    }

    public int getFluidIngredientAmount(int slot) {
        return getFluidIngredients().get(slot).getFluids()[0].getAmount();
    }

    public List<FluidIngredient> getFluidIngredientList() {
        return fluidIngredientList;
    }

    public void setFluidOutputList(List<FluidStack> fluidOutputList) {
        this.fluidOutputList = fluidOutputList;
    }

    public void setFluidIngredientList(List<FluidIngredient> fluidIngredientList) {
        this.fluidIngredientList = fluidIngredientList;
    }
}
