package com.veteam.voluminousenergy.gametest;

import com.veteam.voluminousenergy.recipe.VERecipe;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeType;

import java.util.List;

/**
 * Picks real recipes for machine tests. A machine runs only when exactly one recipe matches its
 * inputs, so tests use a recipe whose first input no other recipe of the type accepts.
 */
public class RecipeKit {

    private RecipeKit() {
    }

    public static VERecipe withUniqueInput(RecipeType<?> type) {
        List<VERecipe> recipes = VERecipe.getCachedRecipes(type);
        for (VERecipe recipe : recipes) {
            Ingredient ingredient = recipe.getIngredient(0);
            if (ingredient == null || ingredient.isEmpty()) {
                continue;
            }
            ItemStack input = firstInput(recipe, 0);
            long matching = recipes.stream().filter(other -> other.getIngredient(0) != null && other.getIngredient(0).test(input)).count();
            if (matching == 1) {
                return recipe;
            }
        }
        throw new IllegalStateException("No recipe of " + type + " has an input that only it accepts");
    }

    public static ItemStack firstInput(VERecipe recipe, int ingredient) {
        return recipe.getIngredient(ingredient).items().findFirst().orElseThrow().value().getDefaultInstance()
                .copyWithCount(recipe.getIngredientCount(ingredient));
    }
}
