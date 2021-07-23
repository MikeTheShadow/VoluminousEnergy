package com.veteam.voluminousenergy.items;

import com.veteam.voluminousenergy.setup.VESetup;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class CoalCoke extends Item {
    public CoalCoke (){
        super(new Item.Properties()
            .stacksTo(64)
            .tab(VESetup.itemGroup)
        );
        setRegistryName("coalcoke");
    }

    // TODO: Deal with Burn Time with furnace
    /*@Override
    public int getBurnTime(ItemStack itemStack) {
        return 3200;
    }*/


}
