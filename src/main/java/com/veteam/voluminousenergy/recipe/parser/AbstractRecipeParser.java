package com.veteam.voluminousenergy.recipe.parser;

import com.veteam.voluminousenergy.blocks.tiles.VETileEntity;
import com.veteam.voluminousenergy.blocks.tiles.inventory.VEItemStackHandler;
import net.minecraft.world.item.ItemStack;

/**
 * A recipe parser system for taking a recipe and translating it into something more consumable
 */
public interface AbstractRecipeParser {

    /**
     * @param tile The tile entity that is being checked
     * @return returns true if the recipe partially matches.
     * This means that AIR and EMPTY for items and fluids respectively are ignored
     */
    boolean isPartialRecipe(VETileEntity tile);

    /**
     * @param tile The tile to check
     * @return returns true if the recipe is completed. You must validate your
     * input amounts here or else risk over-insertion.
     */
    boolean isCompleteRecipe(VETileEntity tile);

    /**
     * @param tile The tile to check
     * @return returns true if the tile has enough space in the output
     * to insert the finished product. Note that making this fail
     * for any other reason can cause a 99% deadlock. If your tile
     * is deadlocking at recipe completion this is probably why.
     */
    boolean canCompleteRecipe(VETileEntity tile);

    /**
     * This code will be called when your recipe has been validated using
     * {@link #canCompleteRecipe(VETileEntity)}. Thus insuring that amounts, inputs, outputs, are all valid
     * and ready to be added to / subtracted from. When called it should handle
     * all the processing required.
     *
     * @param tile The tile to complete the recipe for
     */
    void completeRecipe(VETileEntity tile);

    /**
     * This a copy of the ItemHandlers isItemValid. This will be called to
     * validate item IO for the {@link VEItemStackHandler}. You should also
     * check NBT data like in {@link com.veteam.voluminousenergy.recipe.processor.DimensionalLaserRecipeProcessor}
     *
     * @param slot  the slot position in the tile inventory
     * @param stack The stack to be inserted
     * @return If the item is valid given the Parsers context
     */
    boolean canInsertItem(int slot, ItemStack stack);

    /**
     * A simple util record to help with readability
     * @param tilePos The position of the item/fluid/etc within the tile (ie tank 0)
     * @param recipePos The position of the item/fluid/etc within the recipe (ie result 0)
     */
    record SlotAndRecipePos(int tilePos, int recipePos) {

    }
}
