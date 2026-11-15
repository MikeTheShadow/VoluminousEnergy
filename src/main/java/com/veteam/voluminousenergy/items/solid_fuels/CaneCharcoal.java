package com.veteam.voluminousenergy.items.solid_fuels;

import com.veteam.voluminousenergy.items.VEItem;
import com.veteam.voluminousenergy.util.VERegistryHelper;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.block.entity.FuelValues;
import org.jetbrains.annotations.Nullable;

public class CaneCharcoal extends VEItem {

    public CaneCharcoal() {
        super(new Item.Properties().setId(VERegistryHelper.currentItemId())
                .stacksTo(64)
        );
        setRegistryName("cane_charcoal");
    }

    @Override
    public int getBurnTime(ItemStack itemStack, @Nullable RecipeType<?> recipeType, FuelValues fuelValues) {
        return 1600;
    }
}
