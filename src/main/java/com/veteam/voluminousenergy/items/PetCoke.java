package com.veteam.voluminousenergy.items;

import com.veteam.voluminousenergy.setup.VESetup;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class PetCoke extends Item
{
    public PetCoke()
    {
        super(new Item.Properties()
        .stacksTo(64)
        .tab(VESetup.itemGroup));
        setRegistryName("petcoke");
    }

    // TODO: Deal with Burn time in furnace
    /*@Override
    public int getBurnTime(ItemStack itemStack) {
        return 4000;
    }*/
}
