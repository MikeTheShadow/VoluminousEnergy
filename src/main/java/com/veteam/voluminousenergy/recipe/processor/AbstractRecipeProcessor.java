package com.veteam.voluminousenergy.recipe.processor;

import com.veteam.voluminousenergy.blocks.tiles.VETileEntity;

public interface AbstractRecipeProcessor {

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
     */
    void processRecipe(VETileEntity tile);

    /**
     * Only called when the recipe has been marked as "dirty"
     * use processRecipe for extra checks if this tile requires
     * external checks that won't mark the tile as "dirty"
     *
     * @param tile The tile to validate the recipe for
     */
    void validateRecipe(VETileEntity tile);

}
