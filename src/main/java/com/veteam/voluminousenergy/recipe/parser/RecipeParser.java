package com.veteam.voluminousenergy.recipe.parser;

import com.veteam.voluminousenergy.blocks.tiles.VETileEntity;
import com.veteam.voluminousenergy.blocks.tiles.handlers.VEItemStackHandler;
import com.veteam.voluminousenergy.recipe.VERecipe;
import com.veteam.voluminousenergy.util.recipe.FluidIngredient;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;

import java.util.ArrayList;
import java.util.List;


/**
 * A recipe parser system for taking a recipe and translating it into something more consumable
 */
public class RecipeParser {

    List<SlotAndRecipePos> ingredientPositions = new ArrayList<>();
    List<SlotAndRecipePos> itemResultPositions = new ArrayList<>();
    List<SlotAndRecipePos> fluidIngredientPositions = new ArrayList<>();
    List<SlotAndRecipePos> fluidResultPositions = new ArrayList<>();

    final VERecipe recipe;

    public RecipeParser(VERecipe recipe) {
        this.recipe = recipe;
    }

    public RecipeParser addIngredient(int tilePos, int recipePos) {
        this.ingredientPositions.add(new SlotAndRecipePos(tilePos, recipePos));
        return this;
    }

    public RecipeParser addFluidIngredient(int tilePos, int recipePos) {
        this.fluidIngredientPositions.add(new SlotAndRecipePos(tilePos, recipePos));
        return this;
    }

    public RecipeParser addItemResult(int tilePos, int recipePos) {
        this.itemResultPositions.add(new SlotAndRecipePos(tilePos, recipePos));
        return this;
    }

    public RecipeParser addFluidResult(int tilePos, int recipePos) {
        this.fluidResultPositions.add(new SlotAndRecipePos(tilePos, recipePos));
        return this;
    }

    /**
     * @param tile The tile entity that is being checked
     * @return returns true if the recipe partially matches.
     * This means that AIR and EMPTY for items and fluids respectively are ignored
     */
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

    /**
     * @param tile The tile to check
     * @return returns true if the recipe is completed. You must validate your
     * input amounts here or else risk over-insertion.
     */
    public boolean isCompleteRecipe(VETileEntity tile) {
        for (SlotAndRecipePos pos : ingredientPositions) {
            ItemStack stackInSlot = tile.getStackInSlot(pos.tilePos);
            Ingredient ingredient = recipe.getIngredient(pos.recipePos);
            int amountNeeded = recipe.getIngredientCount(pos.recipePos);
            if (ingredient.isEmpty()) continue;
            if (!ingredient.test(stackInSlot) || stackInSlot.getCount() < amountNeeded)
                return false;
        }

        for (SlotAndRecipePos pos : fluidIngredientPositions) {
            FluidStack stack = tile.getFluidStackFromTank(pos.tilePos);
            FluidIngredient fluidIngredient = recipe.getFluidIngredient(pos.recipePos);
            if (fluidIngredient.isEmpty()) continue;
            int amountNeeded = fluidIngredient.getAmountNeeded();
            if (!fluidIngredient.test(stack) || stack.getAmount() < amountNeeded)
                return false;
        }
        return true;
    }

    /**
     * @param tile The tile to check
     * @return returns true if the tile has enough space in the output
     * to insert the finished product. Note that making this fail
     * for any other reason can cause a 99% deadlock. If your tile
     * is deadlocking at recipe completion this is probably why.
     */
    public boolean canCompleteRecipe(VETileEntity tile) {

        for (SlotAndRecipePos pos : itemResultPositions) {
            ItemStack stack = tile.getStackInSlot(pos.tilePos);
            ItemStack result = recipe.getResult(pos.recipePos);
            if (stack.isEmpty()) continue;
            if (!stack.is(result.getItem()) || result.getCount() + stack.getCount() > result.getMaxStackSize())
                return false;
        }
        for (SlotAndRecipePos pos : fluidResultPositions) {
            FluidStack stack = tile.getFluidStackFromTank(pos.tilePos);
            FluidStack result = recipe.getOutputFluid(pos.recipePos);
            if (stack.isEmpty()) continue;
            if (!stack.isFluidEqual(result) || result.getAmount() + stack.getAmount() > tile.getTankCapacity(pos.tilePos))
                return false;
        }
        return true;
    }

    /**
     * This code will be called when your recipe has been validated using
     * {@link #canCompleteRecipe(VETileEntity)}. Thus insuring that amounts, inputs, outputs, are all valid
     * and ready to be added to / subtracted from. When called it should handle
     * all the processing required.
     *
     * @param tile The tile to complete the recipe for
     */
    public void completeRecipe(VETileEntity tile) {

        VEItemStackHandler handler = tile.getInventory();

        // Subtract the amounts
        for (SlotAndRecipePos pos : ingredientPositions) {
            handler.extractItem(pos.tilePos, recipe.getIngredientCount(pos.recipePos), false);
        }

        for (SlotAndRecipePos pos : fluidIngredientPositions) {
            tile.getTank(pos.tilePos)
                    .getTank().drain(recipe.getFluidIngredientAmount(pos.recipePos), IFluidHandler.FluidAction.EXECUTE);
        }

        // Insert the results
        for (SlotAndRecipePos pos : itemResultPositions) {
            ItemStack result = recipe.getResult(pos.recipePos);
            handler.insertItem(pos.tilePos, result.copy(), false);
        }

        for (SlotAndRecipePos pos : fluidResultPositions) {
            FluidStack result = recipe.getOutputFluid(pos.recipePos);
            tile.getTank(pos.tilePos).fillTank(result.copy());
        }

        // mark fluid IO as dirty
        tile.markFluidInputDirty();
    }

    /**
     * This a copy of the ItemHandlers isItemValid. This will be called to
     * validate item IO for the {@link VEItemStackHandler}. You should also
     * check NBT data like in {@link com.veteam.voluminousenergy.recipe.processor.DimensionalLaserRecipeProcessor}
     *
     * @param slot  the slot position in the tile inventory
     * @param stack The stack to be inserted
     * @return If the item is valid given the Parsers context
     */
    public boolean canInsertItem(int slot, ItemStack stack) {
        for (SlotAndRecipePos pos : ingredientPositions) {
            if (pos.tilePos == slot) {
                Ingredient recipeIngredient = recipe.getIngredient(pos.recipePos);
                return recipeIngredient.test(stack);
            }
        }
        for (SlotAndRecipePos pos : itemResultPositions) {
            if (pos.tilePos == slot) {
                ItemStack itemStack = recipe.getResult(pos.recipePos);
                return itemStack.is(stack.getItem());
            }
        }
        return false;
    }

    record SlotAndRecipePos(int tilePos, int recipePos) {

    }
}
