package com.veteam.voluminousenergy.recipe.parser;

import com.veteam.voluminousenergy.blocks.tiles.VETileEntity;
import com.veteam.voluminousenergy.recipe.VERNGExperienceRecipe;
import com.veteam.voluminousenergy.recipe.VERecipe;
import net.minecraft.world.item.crafting.Recipe;

public class ExperienceParser extends RNGBasicParser {
    public ExperienceParser(VERecipe recipe) {
        super(recipe);
    }

    @Override
    public void completeRecipe(VETileEntity tile) {
        super.completeRecipe(tile);
        if (recipe instanceof VERNGExperienceRecipe experienceRecipe) {
            tile.recordRecipeUsed(experienceRecipe);
        }
    }
}
