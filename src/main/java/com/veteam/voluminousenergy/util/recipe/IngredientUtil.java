package com.veteam.voluminousenergy.util.recipe;

import net.minecraft.core.Holder;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;

public class IngredientUtil {

    private IngredientUtil() {
    }

    /**
     * Ingredient#getItems() (returning ItemStack[], one stack per matching item) was removed;
     * Ingredient#items() now only exposes the matching item holders. This rebuilds the old
     * ItemStack[] shape (each stack defaulted to count 1) for call sites that relied on it.
     */
    public static ItemStack[] getItems(Ingredient ingredient) {
        return ingredient.items().map(ItemStack::new).toArray(ItemStack[]::new);
    }
}
