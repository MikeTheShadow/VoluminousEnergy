package com.veteam.voluminousenergy.blocks.containers.iolisteners;

import com.veteam.voluminousenergy.blocks.tiles.inventory.VEItemStackHandler;
import com.veteam.voluminousenergy.util.ExperienceHelper;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.IItemHandler;

public class ExperienceListener implements SlotWithIOListener {

    @Override
    public void onRemoved(IItemHandler handler, int slot, int amount, boolean isClientSide) {
        // Do nothing on removal by automation or simple stack reduction if needed
    }

    @Override
    public void onTake(IItemHandler handler, ItemStack stack, boolean isClientSide, Player player) {
        if (!isClientSide && player instanceof ServerPlayer serverPlayer && handler instanceof VEItemStackHandler veHandler) {
            ExperienceHelper.awardUsedRecipesAndPopExperience(serverPlayer, veHandler.getTileEntity());
        }
    }

    @Override
    public void onSet(ItemStack stack, int slot, IItemHandler handler, boolean isClientSide) {
        // Do nothing
    }

    @Override
    public void preQuickMoveStack(IItemHandler h, ItemStack multitool, boolean isClientSide) {
        // Do nothing
    }
}
