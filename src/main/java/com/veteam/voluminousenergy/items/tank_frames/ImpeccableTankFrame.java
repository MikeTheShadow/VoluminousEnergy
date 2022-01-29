package com.veteam.voluminousenergy.items.tank_frames;

import com.veteam.voluminousenergy.setup.VESetup;
import net.minecraft.world.item.Item;

public class ImpeccableTankFrame extends Item {
    public ImpeccableTankFrame() {
        super(new Item.Properties()
                .stacksTo(64)
                .tab(VESetup.itemGroup)
        );
        setRegistryName("impeccable_tank_frame");
    }
}