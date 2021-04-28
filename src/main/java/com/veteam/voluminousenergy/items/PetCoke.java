package com.veteam.voluminousenergy.items;

import com.veteam.voluminousenergy.setup.VESetup;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class PetCoke extends Item
{
    public PetCoke()
    {
        super(new Item.Properties()
        .stacksTo(64)
        .tab(VESetup.itemGroup));
        setRegistryName("petcoke");
    }

    @Override
    public int getBurnTime(ItemStack itemStack) {
        return 4000;
    }
}
