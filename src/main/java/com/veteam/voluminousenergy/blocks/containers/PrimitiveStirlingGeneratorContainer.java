package com.veteam.voluminousenergy.blocks.containers;

import com.veteam.voluminousenergy.blocks.blocks.VEBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;
import org.jetbrains.annotations.NotNull;

import static com.veteam.voluminousenergy.blocks.blocks.VEBlocks.PRIMITIVE_STIRLING_GENERATOR_CONTAINER;

public class PrimitiveStirlingGeneratorContainer extends VEContainer {

    public static final int NUMBER_OF_SLOTS = 1;

    public PrimitiveStirlingGeneratorContainer(int windowID, Level world, BlockPos pos, Inventory playerInventory, Player player) {
        super(PRIMITIVE_STIRLING_GENERATOR_CONTAINER.get(), windowID,world,pos,playerInventory,player,VEBlocks.PRIMITIVE_STIRLING_GENERATOR_BLOCK.get());
    }

    @Override
    protected void addSlotsToGUI(IItemHandler h) {
        addSlot(new SlotItemHandler(h, 0, 80, 35));
    }

    @Override
    public @NotNull ItemStack quickMoveStack(@NotNull Player player, int index) {
        ItemStack returnStack = ItemStack.EMPTY;
        final Slot slot = this.slots.get(index);
        if (slot != null && slot.hasItem()) {
            final ItemStack slotStack = slot.getItem();
            returnStack = slotStack.copy();

            if (handleCoreQuickMoveStackLogic(index, NUMBER_OF_SLOTS, slotStack) != null) return ItemStack.EMPTY;

            if (slotStack.getCount() == 0) {
                slot.set(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }
            if (slotStack.getCount() == returnStack.getCount()) {
                return ItemStack.EMPTY;
            }
            slot.onTake(player, slotStack);
        }
        return returnStack;
    }

}
