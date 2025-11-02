package com.veteam.voluminousenergy.items.tools.multitool;

import com.veteam.voluminousenergy.items.tools.multitool.bits.BitItem;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.ItemStackHandler;

//TODO DELETE ME
public class MultiToolItemStackHandler extends ItemStackHandler {

    public MultiToolItemStackHandler() {
        super(4);
    }

    @Override
    public boolean isItemValid(int slot, ItemStack stack) {
        if (!(stack.getItem() instanceof BitItem bitItem)) return false;

        for(ItemStack s : this.stacks) {
            if (s.getItem() instanceof BitItem inventoryBit) {
                if (inventoryBit.getBitItemData().getToolType() == bitItem.getBitItemData().getToolType()) {
                    return false;
                }
            }
        }

        return super.isItemValid(slot, stack);
    }
}
