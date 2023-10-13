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

import static com.veteam.voluminousenergy.blocks.blocks.VEBlocks.DISTILLATION_UNIT_CONTAINER;

public class DistillationUnitContainer extends VEContainer {

    private static final int NUMBER_OF_SLOTS = 8;

    public DistillationUnitContainer(int id, Level world, BlockPos pos, Inventory inventory, Player player){
        super(DISTILLATION_UNIT_CONTAINER.get(),id,world,pos,inventory,player,VEBlocks.DISTILLATION_UNIT_BLOCK.get());
    }

    @Override
    protected void addSlotsToGUI(IItemHandler h) {
        addSlot(new VEBucketSlot(h, 0, 38, 18)); // Fluid input slot
        addSlot(new VEBucketSlot(h, 1,38,49)); // Extract fluid from input
        addSlot(new VEBucketSlot(h, 2, 96,11)); // Top slot for first output
        addSlot(new VEBucketSlot(h, 3, 96,42)); // Bottom slot for first output
        addSlot(new VEBucketSlot(h, 4, 137,11)); // Top slot for from second output
        addSlot(new VEBucketSlot(h, 5, 137,42)); // Bottom slot for second output
        addSlot(new VEInsertSlot(h, 6, 122,64)); // Item Output Slot
        addSlot(new VEInsertSlot(h, 7,130,-14)); // Upgrade slot
    }

    @Nonnull
    @Override
    public @NotNull ItemStack quickMoveStack(final @NotNull Player player, final int index) {
        ItemStack returnStack = ItemStack.EMPTY;
        final Slot slot = this.slots.get(index);
        if (slot != null && slot.hasItem()) {
            final ItemStack slotStack = slot.getItem();
            returnStack = slotStack.copy();

            if (handleCoreQuickMoveStackLogicWithUpgradeSlot(index, NUMBER_OF_SLOTS, 7, slotStack) != null) return ItemStack.EMPTY;

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
