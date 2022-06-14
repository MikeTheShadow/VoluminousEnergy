package com.veteam.voluminousenergy.items.tank_frames;

import com.veteam.voluminousenergy.items.VEItem;
import com.veteam.voluminousenergy.setup.VESetup;
import net.minecraft.world.item.Item;

public class RobustTankFrame extends VEItem {
    public RobustTankFrame() {
        super(new Item.Properties()
                .stacksTo(64)
                .tab(VESetup.itemGroup)
        );
        setRegistryName("robust_tank_frame");
    }
}