package com.veteam.voluminousenergy.items.dusts;

import com.veteam.voluminousenergy.items.VEItem;
import com.veteam.voluminousenergy.setup.VESetup;
import net.minecraft.world.item.Item;

public class NetherrackDust extends VEItem {
    public NetherrackDust(){
        super(new Item.Properties()
                .stacksTo(64)
                .tab(VESetup.itemGroup));
        setRegistryName("netherrack_dust");
    }
}