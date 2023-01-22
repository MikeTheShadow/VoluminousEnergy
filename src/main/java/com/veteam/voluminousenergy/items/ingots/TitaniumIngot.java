package com.veteam.voluminousenergy.items.ingots;

import com.veteam.voluminousenergy.items.VEItem;
import net.minecraft.world.item.Item;

public class TitaniumIngot extends VEItem {
    public TitaniumIngot (){
        super(new Item.Properties()
                .stacksTo(64)
        );
        setRegistryName("titanium_ingot");
    }
}
