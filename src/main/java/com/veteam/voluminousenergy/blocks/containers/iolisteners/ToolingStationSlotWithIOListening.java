package com.veteam.voluminousenergy.blocks.containers.iolisteners;

import com.veteam.voluminousenergy.blocks.tiles.inventory.VEItemStackHandler;
import com.veteam.voluminousenergy.items.tools.multitool.Multitool;
import com.veteam.voluminousenergy.items.tools.multitool.bits.BitItem;
import com.veteam.voluminousenergy.util.VEDataComponents;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class ToolingStationSlotWithIOListening implements SlotWithIOListener {

    @Override
    public void onRemoved(VEItemStackHandler handler, int slot, int amount, boolean isClientSide) {
        ItemStack multitool = handler.getStack(slot);
        if (!(multitool.getItem() instanceof Multitool)) {
            return;
        }
        onTake(handler, multitool, isClientSide, null);
        handler.setStack(slot, multitool);
    }

    @Override
    public void onTake(VEItemStackHandler handler, ItemStack multitool, boolean isClientSide, Player player) {
        if (!(multitool.getItem() instanceof Multitool)) {
            return;
        }

        multitool.set(VEDataComponents.TOOL_TYPE, 0);
        multitool.set(VEDataComponents.TOOL_TIER, 0);
        if (isClientSide)
            return;

        List<ItemStack> bits = new ArrayList<>();
        for (int i = 3; i < 7; i++) {
            if (handler.getResource(i).getItem() instanceof BitItem bitItem) {
                bits.add(new ItemStack(bitItem, 1));
                handler.setStack(i, ItemStack.EMPTY);
            }
        }
        multitool.set(VEDataComponents.ITEM_STACK_LIST_COMPONENT, bits);
    }

    @Override
    public void onSet(ItemStack stack, int slot, VEItemStackHandler handler, boolean isClientSide) {
        if (isClientSide)
            return;
        if (slot != 2 || !(stack.getItem() instanceof Multitool))
            return;
        List<ItemStack> bits = stack.getOrDefault(VEDataComponents.ITEM_STACK_LIST_COMPONENT, new ArrayList<>());
        int bitPosInTile = 3;
        for (ItemStack bit : bits) {
            handler.setStack(bitPosInTile++, bit.copy());
        }
    }

    @Override
    public void preQuickMoveStack(VEItemStackHandler handler, ItemStack multitool, boolean isClientSide) {
        if (!(multitool.getItem() instanceof Multitool)) {
            return;
        }

        multitool.set(VEDataComponents.TOOL_TYPE, 0);
        multitool.set(VEDataComponents.TOOL_TIER, 0);
        if (isClientSide)
            return;

        List<ItemStack> bits = new ArrayList<>();
        for (int i = 3; i < 7; i++) {
            if (handler.getResource(i).getItem() instanceof BitItem bitItem) {
                bits.add(new ItemStack(bitItem, 1));
            }
        }
        multitool.set(VEDataComponents.ITEM_STACK_LIST_COMPONENT, bits);
    }
}
