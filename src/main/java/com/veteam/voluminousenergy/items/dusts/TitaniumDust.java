package com.veteam.voluminousenergy.items.dusts;

import com.veteam.voluminousenergy.items.VEItem;
import net.minecraft.world.item.Item;

public class TitaniumDust extends VEItem {
    public TitaniumDust (){
        super(new Item.Properties()
                .stacksTo(64)
        );
        setRegistryName("titanium_dust");
    }
}
