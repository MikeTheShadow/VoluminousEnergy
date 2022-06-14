package com.veteam.voluminousenergy.items.tools.multitool.bits;

import com.veteam.voluminousenergy.items.VEItem;
import net.minecraft.world.item.Item;

public class BitItem extends VEItem {
    private MultitoolBit multitoolBit;

    public BitItem(MultitoolBit bit, String registryName, Item.Properties itemProperties) {
        super(itemProperties);
        this.multitoolBit = bit;
        setRegistryName(registryName);
    }

    public MultitoolBit getBit(){
        return this.multitoolBit;
    }

}
