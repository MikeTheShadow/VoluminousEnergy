package com.veteam.voluminousenergy.items.ingots;

import com.veteam.voluminousenergy.items.VEItem;
import com.veteam.voluminousenergy.util.VERegistryHelper;
import net.minecraft.world.item.Item;

public class NighaliteIngot extends VEItem {
    public NighaliteIngot() {
        super(new Item.Properties().setId(VERegistryHelper.currentItemId())
                .stacksTo(64)
        );
        setRegistryName("nighalite_ingot");
    }
}
