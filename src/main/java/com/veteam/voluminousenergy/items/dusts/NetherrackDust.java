package com.veteam.voluminousenergy.items.dusts;

import com.veteam.voluminousenergy.items.VEItem;
import net.minecraft.world.item.Item;
import com.veteam.voluminousenergy.util.VERegistryHelper;

public class NetherrackDust extends VEItem {
    public NetherrackDust() {
        super(new Item.Properties().setId(VERegistryHelper.currentItemId())
                .stacksTo(64)
        );
        setRegistryName("netherrack_dust");
    }
}