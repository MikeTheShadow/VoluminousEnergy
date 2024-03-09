package com.veteam.voluminousenergy.recipe.parser;

import com.veteam.voluminousenergy.blocks.tiles.VEItemStackHandler;
import com.veteam.voluminousenergy.blocks.tiles.VETileEntity;
import com.veteam.voluminousenergy.recipe.VERNGRecipe;
import com.veteam.voluminousenergy.recipe.VERecipe;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

import java.util.Random;

import static net.minecraft.util.Mth.abs;

public class HydroponicParser extends RNGRecipeParser {
    public HydroponicParser(VERecipe recipe) {
        super(recipe);
    }

    // We don't subtract the ingredient amounts for the hydroponic incubator
    @Override
    public void completeRecipe(VETileEntity tile) {
        VEItemStackHandler handler = tile.getInventory();

        Random randomInstance = new Random();

        // Insert the rng results
        for (SlotAndRecipePos pos : randomItemResultPositions) {

            VERNGRecipe rngRecipe = (VERNGRecipe) recipe;

            float randomness = rngRecipe.getOutputChance(pos.recipePos());
            if(randomness != 1) {
                float random = abs(0 + randomInstance.nextFloat() * (-1));
                if(random > randomness) continue;
                ItemStack result = rngRecipe.getResult(pos.recipePos());
                handler.insertItem(pos.tilePos(), result.copy(), false);
            }
        }

        for (SlotAndRecipePos pos : fluidIngredientPositions) {
            FluidStack fluidStack = tile.getFluidStackFromTank(pos.tilePos());
            fluidStack.setAmount(fluidStack.getAmount() - recipe.getFluidIngredientAmount(pos.recipePos()));
        }

        // Insert the results
        for (SlotAndRecipePos pos : itemResultPositions) {
            ItemStack result = recipe.getResult(pos.recipePos());
            handler.insertItem(pos.tilePos(), result.copy(), false);
        }

        for (SlotAndRecipePos pos : fluidResultPositions) {
            FluidStack result = recipe.getOutputFluid(pos.recipePos());
            FluidStack tileFluid = tile.getFluidStackFromTank(pos.tilePos());
            tileFluid.setAmount(tileFluid.getAmount() + result.getAmount());
        }

        // mark fluid IO as dirty
        tile.markFluidInputDirty();
    }
}
