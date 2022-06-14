package com.veteam.voluminousenergy.items.dusts;

import com.veteam.voluminousenergy.items.VEItem;
import com.veteam.voluminousenergy.setup.VESetup;
import net.minecraft.world.item.Item;

public class EighzoDust extends VEItem {
    public EighzoDust (){
        super(new Item.Properties()
                .stacksTo(64)
                .tab(VESetup.itemGroup));
        setRegistryName("eighzo_dust");
    }
}
