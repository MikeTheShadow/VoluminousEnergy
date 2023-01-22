package com.veteam.voluminousenergy.items.tank_frames;

import com.veteam.voluminousenergy.items.VEItem;
import com.veteam.voluminousenergy.setup.VESetup;
import net.minecraft.world.item.Item;

public class ImpeccableTankFrame extends VEItem {
    public ImpeccableTankFrame() {
        super(new Item.Properties()
                .stacksTo(64)
        );
        setRegistryName("impeccable_tank_frame");
    }
}