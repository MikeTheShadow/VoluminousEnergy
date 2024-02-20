package com.veteam.voluminousenergy.recipe.validator;

import com.veteam.voluminousenergy.blocks.tiles.VETileEntity;
import com.veteam.voluminousenergy.recipe.RecipeCache;
import com.veteam.voluminousenergy.recipe.VEFluidRecipe;
import com.veteam.voluminousenergy.util.SlotType;
import com.veteam.voluminousenergy.util.TankType;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.items.ItemStackHandler;

import java.util.ArrayList;
import java.util.List;

public class FluidItemValidator implements RecipeValidator {

    @Override
    public void validateRecipe(VETileEntity tile) {
        if (!tile.isRecipeDirty()) {
            return;
        }
        tile.setRecipeDirty(false);

        tile.setPotentialRecipes(RecipeCache.getFluidRecipesFromCache(tile, true));
        if (tile.getPotentialRecipes().size() == 1) {
            List<FluidStack> inputFluids = tile.getRelationalTanks().stream()
                    .filter(tank -> tank.getTankType() == TankType.INPUT)
                    .map(tank -> tank.getTank().getFluid()).toList();

            ItemStackHandler handler = tile.getInventoryHandler();
            List<ItemStack> inputItems = new ArrayList<>();
            if (handler != null) {
                inputItems = tile.getSlotManagers().stream()
                        .filter(manager -> manager.getSlotType() == SlotType.INPUT)
                        .map(manager -> manager.getItem(handler)).toList();
            }
            VEFluidRecipe newRecipe = RecipeCache.getFluidRecipeFromCache(null, tile.getRecipeType(), inputFluids, inputItems);

            if (newRecipe == null) {
                tile.setData("counter",0);
                tile.setData("length",0);
                tile.setSelectedRecipe(null);
                return;
            }

            int newLength;

            if (tile.getEnergy() != null && handler != null) {
                newLength = tile.calculateCounter(newRecipe.getProcessTime(),
                        handler.getStackInSlot(tile.getEnergy().getUpgradeSlotId()).copy());
            } else {
                newLength = tile.calculateCounter(newRecipe.getProcessTime(), ItemStack.EMPTY);
            }


            double ratio = (double) tile.getData("length") / (double) newLength;
            tile.setData("length",newLength);
            tile.setData("counter", (int) (tile.getData("counter") / ratio));

            if (tile.getSelectedRecipe() != newRecipe) {
                tile.setSelectedRecipe(newRecipe);
                tile.setData("counter",newLength);
            }
        } else {
            tile.setData("counter",0);
            tile.setData("length",0);
            tile.setSelectedRecipe(null);
        }
    }
}
