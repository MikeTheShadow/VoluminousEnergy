package com.veteam.voluminousenergy.recipe.parser;

import com.veteam.voluminousenergy.blocks.tiles.VEItemStackHandler;
import com.veteam.voluminousenergy.blocks.tiles.VETileEntity;
import com.veteam.voluminousenergy.recipe.VERNGRecipe;
import com.veteam.voluminousenergy.recipe.VERecipe;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static net.minecraft.util.Mth.abs;

public class RNGRecipeParser extends RecipeParser {
    public RNGRecipeParser(VERecipe recipe) {
        super(recipe);
    }
    List<SlotAndRecipePos> randomItemResultPositions = new ArrayList<>();

    public static RNGRecipeParser forRecipe(VERecipe recipe) {
        return new RNGRecipeParser(recipe);
    }

    public RNGRecipeParser addChancedItemResult(int tilePos, int recipePos) {
        this.randomItemResultPositions.add(new SlotAndRecipePos(tilePos,recipePos));
        return this;
    }

    @Override
    public boolean canCompleteRecipe(VETileEntity tile) {
        for (SlotAndRecipePos pos : randomItemResultPositions) {
            ItemStack stack = tile.getStackInSlot(pos.tilePos());
            ItemStack result = recipe.getResult(pos.recipePos());
            if(stack.isEmpty()) continue;
            if (!stack.is(result.getItem()) || result.getCount() + stack.getCount() > result.getMaxStackSize())
                return false;
        }
        return super.canCompleteRecipe(tile);
    }

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

        super.completeRecipe(tile);
    }
}
