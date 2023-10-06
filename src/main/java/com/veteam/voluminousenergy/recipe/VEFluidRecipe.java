package com.veteam.voluminousenergy.recipe;

import com.veteam.voluminousenergy.util.recipe.FluidIngredient;
import com.veteam.voluminousenergy.util.recipe.VERecipeCodecs;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraftforge.fluids.FluidStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public abstract class VEFluidRecipe extends VERecipe {

    private List<FluidIngredient> fluidIngredientList = null;
    public List<VERecipeCodecs.RegistryFluidIngredient> registryFluidIngredients;
    public List<FluidStack> fluidOutputList;

    public VEFluidRecipe() {

    }

    public VEFluidRecipe(List<VERecipeCodecs.RegistryIngredient> i, List<VERecipeCodecs.RegistryFluidIngredient> fi, List<FluidStack> of, List<ItemStack> oi, int processTime) {
        super(i,oi,processTime);
        registryFluidIngredients = fi;
        fluidOutputList = of;
        this.processTime = processTime;
    }

    public List<FluidStack> getOutputFluids() {
        return this.fluidOutputList;
    }

    public FluidStack getOutputFluid(int slot) {
        return this.fluidOutputList.get(slot).copy();
    }


    public List<FluidIngredient> getFluidIngredients() {
        if(fluidIngredientList == null) {
            List<FluidIngredient> fluidIngredients = new ArrayList<>();
            for(VERecipeCodecs.RegistryFluidIngredient ingredient : registryFluidIngredients) {
                fluidIngredients.add(ingredient.getIngredient());
                this.fluidIngredientList = fluidIngredients;
            }
        }
        return fluidIngredientList;
    }

    public FluidIngredient getFluidIngredient(int slot) {
        return getFluidIngredients().get(slot);
    }

    public int getFluidIngredientAmount(int slot) {
        return getFluidIngredients().get(slot).getFluids()[0].getAmount();
    }

    public void setFluidOutputList(List<FluidStack> fluidOutputList) {
        this.fluidOutputList = fluidOutputList;
    }

    public void setFluidIngredientList(List<FluidIngredient> fluidIngredientList) {
        this.fluidIngredientList = fluidIngredientList;
    }
}
