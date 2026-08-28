package com.veteam.voluminousenergy.items.tools.multitool.bits;

import com.veteam.voluminousenergy.items.VEItem;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.component.Tool;
import net.minecraft.world.level.block.Block;

import java.util.List;

public class BitItem extends VEItem {
    private final BitItemData bitItemData;
    private final Tool tool;

    public BitItem(BitItemData bit, String registryName, Item.Properties itemProperties) {
        super(itemProperties.setId(com.veteam.voluminousenergy.util.VERegistryHelper.currentItemId()));
        this.bitItemData = bit;
        // ToolMaterial.createToolProperties(TagKey<Block>) was removed; build the Tool component
        // ourselves the same way ToolMaterial.applyToolProperties(...) does internally.
        HolderGetter<Block> registrationLookup = BuiltInRegistries.acquireBootstrapRegistrationLookup(BuiltInRegistries.BLOCK);
        this.tool = new Tool(
                List.of(Tool.Rule.minesAndDrops(registrationLookup.getOrThrow(bit.getMineableBlocks()), bit.getTier().speed())),
                1.0F,
                1,
                true
        );
        setRegistryName(registryName);
    }

    public BitItemData getBitItemData() {
        return this.bitItemData;
    }

    public Tool getTool() {
        return this.tool;
    }

}
