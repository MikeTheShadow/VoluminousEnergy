package com.veteam.voluminousenergy.recipe;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraftforge.fluids.FluidStack;

import javax.annotation.Nullable;
import java.util.List;

public class RecipeCache {

    @Nullable
    public static VERecipe getFluidRecipeFromCache(Level level, RecipeType<? extends Recipe<?>> type, List<FluidStack> fluids, List<ItemStack> items) {

        var recipes = VERecipe.getCachedRecipes(type);
        for (VERecipe recipe : recipes) {
            boolean isValid = true;

            for (int i = 0; i < fluids.size(); i++) {
                if (recipe.getFluidIngredient(i).isEmpty()) continue;
                if (!recipe.getFluidIngredient(i).test(fluids.get(i))
                        || fluids.get(i).getAmount() < recipe.getFluidIngredientAmount(i)) {
                    isValid = false;
                    break;
                }
            }

            for (int i = 0; i < items.size(); i++) {
                if (!recipe.getIngredient(i).test(items.get(i))
                        || items.get(i).getCount() < recipe.getIngredientCount(i)) {
                    isValid = false;
                    break;
                }
            }
            if (isValid) return recipe;
        }
        return null;
    }

}
