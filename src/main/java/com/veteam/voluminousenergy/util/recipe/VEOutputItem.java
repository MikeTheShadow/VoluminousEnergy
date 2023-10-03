package com.veteam.voluminousenergy.util.recipe;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public record VEOutputItem(Item item, int amount, float rng) {

    public ItemStack getAsItemStack() {
        return new ItemStack(item,amount);
    }

}
