package com.veteam.voluminousenergy.recipe.parser;

import com.veteam.voluminousenergy.blocks.tiles.VEItemStackHandler;
import com.veteam.voluminousenergy.blocks.tiles.VETileEntity;
import com.veteam.voluminousenergy.recipe.VERecipe;
import com.veteam.voluminousenergy.util.recipe.FluidIngredient;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.fluids.FluidStack;

import java.util.ArrayList;
import java.util.List;

public class RecipeParser {

    List<SlotAndRecipePos> ingredientPositions = new ArrayList<>();
    List<SlotAndRecipePos> itemResultPositions = new ArrayList<>();
    List<SlotAndRecipePos> fluidIngredientPositions = new ArrayList<>();
    List<SlotAndRecipePos> fluidResultPositions = new ArrayList<>();

    final VERecipe recipe;

    public RecipeParser(VERecipe recipe) {
        this.recipe = recipe;
    }

    public static RecipeParser forRecipe(VERecipe recipe) {
        return new RecipeParser(recipe);
    }

    public RecipeParser addIngredient(int tilePos,int recipePos) {
        this.ingredientPositions.add(new SlotAndRecipePos(tilePos,recipePos));
        return this;
    }

    public RecipeParser addFluidIngredient(int tilePos,int recipePos) {
        this.fluidIngredientPositions.add(new SlotAndRecipePos(tilePos,recipePos));
        return this;
    }

    public RecipeParser addItemResult(int tilePos,int recipePos) {
        this.itemResultPositions.add(new SlotAndRecipePos(tilePos,recipePos));
        return this;
    }

    public RecipeParser addFluidResult(int tilePos,int recipePos) {
        this.fluidResultPositions.add(new SlotAndRecipePos(tilePos,recipePos));
        return this;
    }

    public boolean isPartialRecipe(VETileEntity tile) {
        for (SlotAndRecipePos pos : ingredientPositions) {
            ItemStack stackInSlot = tile.getStackInSlot(pos.tilePos);
            Ingredient ingredient = recipe.getIngredient(pos.recipePos);
            if (stackInSlot.isEmpty()) continue;
            if (ingredient.test(stackInSlot)) continue;
            return false;
        }

        for (SlotAndRecipePos pos : fluidIngredientPositions) {
            FluidStack stack = tile.getFluidStackFromTank(pos.tilePos);
            FluidIngredient fluidIngredient = recipe.getFluidIngredient(pos.recipePos);
            if (stack.isEmpty()) continue;
            if (fluidIngredient.test(stack)) continue;
            return false;
        }
        return true;
    }

    public boolean isCompleteRecipe(VETileEntity tile) {
        for (SlotAndRecipePos pos : ingredientPositions) {
            ItemStack stackInSlot = tile.getStackInSlot(pos.tilePos);
            Ingredient ingredient = recipe.getIngredient(pos.recipePos);
            int amountNeeded = recipe.getIngredientCount(pos.recipePos);
            if(ingredient.isEmpty()) continue;
            if (!ingredient.test(stackInSlot) || stackInSlot.getCount() < amountNeeded)
                return false;
        }

        for (SlotAndRecipePos pos : fluidIngredientPositions) {
            FluidStack stack = tile.getFluidStackFromTank(pos.tilePos);
            FluidIngredient fluidIngredient = recipe.getFluidIngredient(pos.recipePos);
            if(fluidIngredient.isEmpty()) continue;
            int amountNeeded = fluidIngredient.getAmountNeeded();
            if (!fluidIngredient.test(stack) || stack.getAmount() < amountNeeded)
                return false;
        }
        return true;
    }

    public boolean canCompleteRecipe(VETileEntity tile) {

        for (SlotAndRecipePos pos : itemResultPositions) {
            ItemStack stack = tile.getStackInSlot(pos.tilePos);
            ItemStack result = recipe.getResult(pos.recipePos);
            if(stack.isEmpty()) continue;
            if (!stack.is(result.getItem()) || result.getCount() + stack.getCount() > result.getMaxStackSize())
                return false;
        }
        for (SlotAndRecipePos pos : fluidResultPositions) {
            FluidStack stack = tile.getFluidStackFromTank(pos.tilePos);
            FluidStack result = recipe.getOutputFluid(pos.recipePos);
            if(stack.isEmpty()) continue;
            if (!stack.isFluidEqual(result) || result.getAmount() + stack.getAmount() > tile.getTankCapacity(pos.tilePos))
                return false;
        }
        return true;
    }

    public void completeRecipe(VETileEntity tile) {

        VEItemStackHandler handler = tile.getInventory();

        // Subtract the amounts
        for (SlotAndRecipePos pos : ingredientPositions) {
            handler.extractItem(pos.tilePos, recipe.getIngredientCount(pos.recipePos), false);
        }

        for (SlotAndRecipePos pos : fluidIngredientPositions) {
            FluidStack fluidStack = tile.getFluidStackFromTank(pos.tilePos);
            fluidStack.setAmount(fluidStack.getAmount() - recipe.getFluidIngredientAmount(pos.recipePos));
        }

        // Insert the results
        for (SlotAndRecipePos pos : itemResultPositions) {
            ItemStack result = recipe.getResult(pos.recipePos);
            handler.insertItem(pos.tilePos, result.copy(), false);
        }

        for (SlotAndRecipePos pos : fluidResultPositions) {
            FluidStack result = recipe.getOutputFluid(pos.recipePos);
            FluidStack tileFluid = tile.getFluidStackFromTank(pos.tilePos);
            tileFluid.setAmount(tileFluid.getAmount() + result.getAmount());
        }

        // mark fluid IO as dirty
        tile.markFluidInputDirty();
    }

    public boolean canInsertItem(int slot, ItemStack stack) {
        for (SlotAndRecipePos pos : ingredientPositions) {
            if (pos.tilePos == slot) {
                Ingredient recipeIngredient = recipe.getIngredient(pos.recipePos);
                return recipeIngredient.test(stack);
            }
        }
        return false;
    }

    record SlotAndRecipePos(int tilePos, int recipePos) {

    }
}
