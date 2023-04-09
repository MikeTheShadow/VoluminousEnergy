package com.veteam.voluminousenergy.items.ingots;

import com.veteam.voluminousenergy.items.VEItem;
import net.minecraft.world.item.Item;

public class NighaliteIngot  extends VEItem {
    public NighaliteIngot (){
        super(new Item.Properties()
                .stacksTo(64)
        );
        setRegistryName("nighalite_ingot");
    }
}
