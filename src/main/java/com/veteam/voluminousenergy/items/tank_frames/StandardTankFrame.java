package com.veteam.voluminousenergy.items.tank_frames;

import com.veteam.voluminousenergy.items.VEItem;
import com.veteam.voluminousenergy.setup.VESetup;
import net.minecraft.world.item.Item;

public class StandardTankFrame extends VEItem {
    public StandardTankFrame() {
        super(new Item.Properties()
                .stacksTo(64)
        );
        setRegistryName("standard_tank_frame");
    }
}
