package com.veteam.voluminousenergy.items.tools.multitool.bits;

import net.minecraft.world.item.Item;

public class BitItem extends Item {
    private MultitoolBit multitoolBit;

    public BitItem(MultitoolBit bit, Item.Properties itemProperties) {
        super(itemProperties);
        this.multitoolBit = bit;
    }

    public MultitoolBit getBit(){
        return this.multitoolBit;
    }

}
