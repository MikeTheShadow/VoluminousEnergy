package com.veteam.voluminousenergy.items.solid_fuels.tiny;

import com.veteam.voluminousenergy.setup.VESetup;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraftforge.common.extensions.IForgeItem;

import javax.annotation.Nullable;

public class TinyCoalCoke extends Item implements IForgeItem {

    public TinyCoalCoke() {
        super(new Item.Properties()
                .stacksTo(64)
                .tab(VESetup.itemGroup)
        );
        setRegistryName("tiny_coal_coke");
    }

    @Override
    public int getBurnTime(ItemStack itemStack, @Nullable RecipeType<?> recipeType) {
        return 200;
    }


}