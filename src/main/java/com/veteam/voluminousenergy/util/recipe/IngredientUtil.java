package com.veteam.voluminousenergy.util.recipe;

import net.minecraft.core.Holder;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import org.jetbrains.annotations.Nullable;

public class IngredientUtil {

    private IngredientUtil() {
    }

    /**
     * Returns one single-item stack per item the ingredient matches, or an empty array for a null
     * ingredient.
     */
    public static ItemStack[] getItems(@Nullable Ingredient ingredient) {
        if (ingredient == null) {
            return new ItemStack[0];
        }
        return ingredient.items().map(ItemStack::new).toArray(ItemStack[]::new);
    }
}
