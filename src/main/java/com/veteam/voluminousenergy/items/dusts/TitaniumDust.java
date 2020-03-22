package com.veteam.voluminousenergy.items.dusts;

import com.veteam.voluminousenergy.setup.VESetup;
import net.minecraft.item.Item;

public class TitaniumDust extends Item {
    public TitaniumDust (){
        super(new Item.Properties()
                .maxStackSize(64)
                .group(VESetup.itemGroup));
        setRegistryName("titanium_dust");
    }
}
