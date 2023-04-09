package com.veteam.voluminousenergy.items.dusts;

import com.veteam.voluminousenergy.items.VEItem;
import net.minecraft.world.item.Item;

public class EndStoneDust extends VEItem {
    public EndStoneDust(){
        super(new Item.Properties()
                .stacksTo(64)
        );
        setRegistryName("end_stone_dust");
    }
}