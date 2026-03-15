package com.veteam.voluminousenergy.items.tools.multitool.bits;

import com.veteam.voluminousenergy.items.VEItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.component.Tool;

public class BitItem extends VEItem {
    private final BitItemData bitItemData;
    private final Tool tool;

    public BitItem(BitItemData bit, String registryName, Item.Properties itemProperties) {
        super(itemProperties);
        this.bitItemData = bit;
        this.tool = bit.getTier().createToolProperties(bit.getMineableBlocks());
        setRegistryName(registryName);
    }

    public BitItemData getBitItemData() {
        return this.bitItemData;
    }

    public Tool getTool() {
        return this.tool;
    }

}
