package com.veteam.voluminousenergy.recipe.validator;

import com.veteam.voluminousenergy.VoluminousEnergy;
import com.veteam.voluminousenergy.blocks.tiles.VETileEntity;
import com.veteam.voluminousenergy.recipe.RecipeCache;
import com.veteam.voluminousenergy.recipe.VEFluidRecipe;
import com.veteam.voluminousenergy.util.SlotType;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.ItemStackHandler;

import java.util.ArrayList;
import java.util.List;

public class GeneratorItemValidator implements RecipeValidator {

    @Override
    public void validateRecipe(VETileEntity tile) {
        if (!tile.isRecipeDirty()) {
            return;
        }
        tile.setRecipeDirty(false);
        tile.setPotentialRecipes(RecipeCache.getFluidRecipesFromCache(tile, true));
        if (tile.getPotentialRecipes().size() == 1) {
            ItemStackHandler handler = tile.getInventoryHandler();
            List<ItemStack> inputItems = new ArrayList<>();
            if (handler != null) {
                inputItems = tile.getSlotManagers().stream()
                        .filter(manager -> manager.getSlotType() == SlotType.INPUT)
                        .map(manager -> manager.getItem(handler)).toList();
            }
            VEFluidRecipe newRecipe = RecipeCache.getFluidRecipeFromCache(null, tile.getRecipeType(), new ArrayList<>(), inputItems);
            tile.setSelectedRecipe(newRecipe);
        }
    }

}
