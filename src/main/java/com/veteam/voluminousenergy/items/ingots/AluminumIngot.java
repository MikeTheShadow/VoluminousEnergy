package com.veteam.voluminousenergy.items.ingots;

import com.veteam.voluminousenergy.items.VEItem;
import com.veteam.voluminousenergy.util.VERegistryHelper;
import net.minecraft.world.item.Item;

public class AluminumIngot extends VEItem {
    public AluminumIngot() {
        super(new Item.Properties().setId(VERegistryHelper.currentItemId())
                .stacksTo(64)
        );
        setRegistryName("aluminum_ingot");
    }
}
