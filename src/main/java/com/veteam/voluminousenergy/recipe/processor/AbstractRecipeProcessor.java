package com.veteam.voluminousenergy.recipe.processor;

import com.veteam.voluminousenergy.blocks.tiles.VETileEntity;

public interface AbstractRecipeProcessor {

    void tick(VETileEntity tile);

    AbstractRecipeProcessor copy();
}
