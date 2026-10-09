package com.veteam.voluminousenergy.items.ores;

import com.veteam.voluminousenergy.items.VEItem;
import com.veteam.voluminousenergy.util.VERegistryHelper;
import net.minecraft.world.item.Item;

public class RawBauxite extends VEItem {
    public RawBauxite() {
        super(new Item.Properties().setId(VERegistryHelper.currentItemId())
                .stacksTo(64)
        );
        setRegistryName("raw_bauxite");
    }
}
