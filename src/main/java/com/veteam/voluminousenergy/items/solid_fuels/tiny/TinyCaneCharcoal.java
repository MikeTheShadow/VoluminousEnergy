package com.veteam.voluminousenergy.items.solid_fuels.tiny;

import com.veteam.voluminousenergy.items.VEItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeType;
import org.jetbrains.annotations.Nullable;

public class TinyCaneCharcoal extends VEItem {

    public TinyCaneCharcoal() {
        super(new Item.Properties()
                .stacksTo(64)
        );
        setRegistryName("tiny_cane_charcoal");
    }

    @Override
    public int getBurnTime(ItemStack itemStack, @Nullable RecipeType<?> recipeType) {
        return 200;
    }
}
