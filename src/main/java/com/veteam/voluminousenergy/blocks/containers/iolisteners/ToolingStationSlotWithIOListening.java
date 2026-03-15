package com.veteam.voluminousenergy.blocks.containers.iolisteners;

import com.veteam.voluminousenergy.blocks.tiles.inventory.VEItemStackHandler;
import com.veteam.voluminousenergy.items.tools.multitool.Multitool;
import com.veteam.voluminousenergy.items.tools.multitool.bits.BitItem;
import com.veteam.voluminousenergy.util.VEDataComponents;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.IItemHandler;

import java.util.ArrayList;
import java.util.List;

public class ToolingStationSlotWithIOListening implements SlotWithIOListener {

    @Override
    public void onRemoved(IItemHandler h, int slot, int amount, boolean isClientSide) {
        ItemStack multitool = h.getStackInSlot(slot);
        onTake(h, multitool, isClientSide);
    }

    @Override
    public void onTake(IItemHandler h, ItemStack multitool, boolean isClientSide) {
        VEItemStackHandler handler = (VEItemStackHandler) h;

        if (!(multitool.getItem() instanceof Multitool)) {
            return;
        }

        multitool.set(VEDataComponents.TOOL_TYPE, 0);
        multitool.set(VEDataComponents.TOOL_TIER, 0);
        if (isClientSide)
            return;

        List<ItemStack> bits = new ArrayList<>();
        for (int i = 3; i < 7; i++) {
            if (handler.getStackInSlot(i).getItem() instanceof BitItem bitItem) {
                bits.add(new ItemStack(bitItem, 1));
                handler.setStackInSlot(i, ItemStack.EMPTY);
            }
        }
        multitool.set(VEDataComponents.ITEM_STACK_LIST_COMPONENT, bits);
    }

    @Override
    public void onSet(ItemStack stack, int slot, IItemHandler h, boolean isClientSide) {
        if (isClientSide)
            return;
        if (slot != 2 || !(stack.getItem() instanceof Multitool))
            return;
        VEItemStackHandler handler = (VEItemStackHandler) h;
        List<ItemStack> bits = stack.getOrDefault(VEDataComponents.ITEM_STACK_LIST_COMPONENT, new ArrayList<>());
        int bitPosInTile = 3;
        for (ItemStack bit : bits) {
            handler.setStackInSlot(bitPosInTile++, bit.copy());
        }
    }

    @Override
    public void preQuickMoveStack(IItemHandler h, ItemStack multitool, boolean isClientSide) {
        VEItemStackHandler handler = (VEItemStackHandler) h;

        if (!(multitool.getItem() instanceof Multitool)) {
            return;
        }

        multitool.set(VEDataComponents.TOOL_TYPE, 0);
        multitool.set(VEDataComponents.TOOL_TIER, 0);
        if (isClientSide)
            return;

        List<ItemStack> bits = new ArrayList<>();
        for (int i = 3; i < 7; i++) {
            if (handler.getStackInSlot(i).getItem() instanceof BitItem bitItem) {
                bits.add(new ItemStack(bitItem, 1));
            }
        }
        multitool.set(VEDataComponents.ITEM_STACK_LIST_COMPONENT, bits);
    }
}
