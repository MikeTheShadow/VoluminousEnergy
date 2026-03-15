package com.veteam.voluminousenergy.blocks.containers.iolisteners;

import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.IItemHandler;

public interface SlotWithIOListener {

    void onRemoved(IItemHandler handler, int slot, int amount, boolean isClientSide);

    void onTake(IItemHandler handler, ItemStack stack, boolean isClientSide);

    /**
     *
     * @param stack The item that is currently **attempting** to be inserted. Note that a failure still results in this function
     *              being called.
     * @param slot The slot number that the item is attempting to be inserted into.
     * @param handler The inventory of the screen
     * @param isClientSide If we're on the client
     */
    void onSet(ItemStack stack, int slot, IItemHandler handler, boolean isClientSide);

    void preQuickMoveStack(IItemHandler h, ItemStack multitool, boolean isClientSide);
}
