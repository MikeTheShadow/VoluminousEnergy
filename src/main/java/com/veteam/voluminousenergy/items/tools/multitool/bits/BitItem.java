package com.veteam.voluminousenergy.items.tools.multitool.bits;

import com.veteam.voluminousenergy.items.VEItem;
import net.minecraft.world.item.Item;

public class BitItem extends VEItem {
    private MultitoolBitData multitoolBitData;

    public BitItem(MultitoolBitData bit, String registryName, Item.Properties itemProperties) {
        super(itemProperties);
        this.multitoolBitData = bit;
        setRegistryName(registryName);
    }

    public MultitoolBitData getBit() {
        return this.multitoolBitData;
    }

}
