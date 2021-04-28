package com.veteam.voluminousenergy.items;

import com.veteam.voluminousenergy.setup.VESetup;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class CoalCoke extends Item {
    public CoalCoke (){
        super(new Item.Properties()
            .stacksTo(64)
            .tab(VESetup.itemGroup)
        );
        setRegistryName("coalcoke");
    }

    @Override
    public int getBurnTime(ItemStack itemStack) {
        return 3200;
    }


}
