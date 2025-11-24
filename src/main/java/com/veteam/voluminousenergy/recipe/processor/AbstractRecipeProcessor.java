package com.veteam.voluminousenergy.recipe.processor;

import com.veteam.voluminousenergy.blocks.tiles.VETileEntity;

public interface AbstractRecipeProcessor {

    /**
     * Only called when the recipe has been marked as "dirty"
     * use processRecipe for extra checks if this tile requires
     * external checks that won't mark the tile as "dirty"
     *
     * The idea behind this was for this recipe to validate the recipe as "good" then set VETileEntity#selectedRecipe.
     * For recipes that don't require a recipe/configuration this can be left empty.
     *
     * @param tile The tile to validate the recipe for
     */
    void validateRecipe(VETileEntity tile);

    /**
     * Handles the main processing for this tile.
     * For tiles without recipes you can just use this without
     * using validateRecipe as this will *always* be called
     * regardless of the recipe validation state. You
     * must check that {@link VETileEntity#getSelectedRecipe()}
     * is not null in order to continue processing (or some other self-set
     * method of validating state).
     *
     * @param tile The tile to process the recipe for
     * @return if true then the tile will be marked as changed
     */
    boolean processRecipe(VETileEntity tile);



}
