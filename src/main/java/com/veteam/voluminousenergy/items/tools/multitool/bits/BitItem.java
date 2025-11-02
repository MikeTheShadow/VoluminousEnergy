package com.veteam.voluminousenergy.items.tools.multitool.bits;

import com.veteam.voluminousenergy.items.VEItem;
import net.minecraft.world.item.Item;

public class BitItem extends VEItem {
    private BitItemData bitItemData;

    public BitItem(BitItemData bit, String registryName, Item.Properties itemProperties) {
        super(itemProperties);
        this.bitItemData = bit;
        setRegistryName(registryName);
    }

    public BitItemData getBitItemData() {
        return this.bitItemData;
    }

}
