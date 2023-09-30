package com.veteam.voluminousenergy.util.recipe;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

/**
 * Due to potential modification of already-existing objects
 * this class exists to provide an extracted check for
 * stack size and item type for recipe checks in tile entities
 */
public class RecipeItem {

    Item item = null;
    int amount = -1;

    // Create an empty instance of the class
    public RecipeItem() {

    }

    public RecipeItem(Item fluid, int amount) {
        this.item = fluid;
        this.amount = amount;
    }

    /**
     * This method provides the benefit of updating the object as well to provide cleaner
     * frontend code
     *
     * @param itemStack The new ItemStack
     * @return returns a value based on if the stack size or material has changed (true) or
     * has remained unchanged (false)
     */
    public boolean isDifferent(ItemStack itemStack) {

        if(this.item == null) {
            if(itemStack == null) return false;
            this.item = itemStack.getItem();
            this.amount = itemStack.getCount();
            return true;
        }

        if (amount != itemStack.getCount() || !itemStack.is(item)) {
            amount = itemStack.getCount();
            item = itemStack.getItem();
            return true;
        } else {
            return false;
        }
    }
}
