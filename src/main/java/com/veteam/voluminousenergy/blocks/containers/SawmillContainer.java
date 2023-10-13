package com.veteam.voluminousenergy.blocks.containers;

import com.veteam.voluminousenergy.blocks.blocks.VEBlocks;
import com.veteam.voluminousenergy.blocks.inventory.slots.VEBucketSlot;
import com.veteam.voluminousenergy.blocks.inventory.slots.VEInsertSlot;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.items.IItemHandler;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;

import static com.veteam.voluminousenergy.blocks.blocks.VEBlocks.SAWMILL_CONTAINER;

public class SawmillContainer extends VEContainer {

    public static final int NUMBER_OF_SLOTS = 6;

    public SawmillContainer(int id, Level world, BlockPos pos, Inventory inventory, Player player) {
        super(SAWMILL_CONTAINER.get(), id,world,pos,inventory,player,VEBlocks.SAWMILL_BLOCK.get());
    }

    @Override
    protected void addSlotsToGUI(IItemHandler h) {
        addSlot(new VEInsertSlot(h, 0, 44, 32)); // Log input slot
        addSlot(new VEInsertSlot(h, 1, 80, 24)); // Plank Output
        addSlot(new VEInsertSlot(h, 2, 80, 42)); // Secondary Output
        addSlot(new VEBucketSlot(h, 3, 115, 18)); // Bucket Input
        addSlot(new VEBucketSlot(h, 4, 115, 49)); // Bucket Output
        addSlot(new VEInsertSlot(h, 5, 154, -14)); // Upgrade Slot
    }

    @Nonnull
    @Override
    public @NotNull ItemStack quickMoveStack(final @NotNull Player player, final int index) {
        ItemStack returnStack = ItemStack.EMPTY;
        final Slot slot = this.slots.get(index);

        if (slot != null && slot.hasItem()) {
            final ItemStack slotStack = slot.getItem();
            returnStack = slotStack.copy();

            if (handleCoreQuickMoveStackLogicWithUpgradeSlot(index, NUMBER_OF_SLOTS, 5, slotStack) != null)
                return ItemStack.EMPTY;

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