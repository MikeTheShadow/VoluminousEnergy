package com.veteam.voluminousenergy.items.tank_frames;

import com.veteam.voluminousenergy.items.VEItem;
import net.minecraft.world.item.Item;

public class RobustTankFrame extends VEItem {
    public RobustTankFrame() {
        super(new Item.Properties().setId(com.veteam.voluminousenergy.util.VERegistryHelper.currentItemId())
                .stacksTo(64)
        );
        setRegistryName("robust_tank_frame");
    }
}