package com.veteam.voluminousenergy.blocks.containers;

import com.veteam.voluminousenergy.blocks.blocks.VEBlocks;
import com.veteam.voluminousenergy.blocks.inventory.slots.VEBucketSlot;
import com.veteam.voluminousenergy.blocks.inventory.slots.VEInsertSlot;
import com.veteam.voluminousenergy.blocks.tiles.IVEPoweredTileEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.items.IItemHandler;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;

import static com.veteam.voluminousenergy.blocks.blocks.VEBlocks.AIR_COMPRESSOR_CONTAINER;

public class AirCompressorContainer extends VEContainer {

    private static final int NUMBER_OF_SLOTS = 3;

    public AirCompressorContainer(int id, Level world, BlockPos pos, Inventory inventory, Player player){
        super(AIR_COMPRESSOR_CONTAINER.get(),id,world,pos,inventory,player,VEBlocks.AIR_COMPRESSOR_BLOCK.get());
    }

    @Override
    protected void addSlotsToGUI(IItemHandler handler) {
        addSlot(new VEBucketSlot(handler, 0, 70, 18)); // Air Compressor bucket input slot
        addSlot(new VEBucketSlot(handler, 1, 70, 49)); // Air Compressor bucket output slot
        addSlot(new VEInsertSlot(handler, 2, 154, -14)); // Upgrade Slot
    }

    @Nonnull
    @Override
    public @NotNull ItemStack quickMoveStack(final @NotNull Player player, final int index) {
        ItemStack returnStack = ItemStack.EMPTY;
        final Slot slot = this.slots.get(index);
        if (slot != null && slot.hasItem()) {
            final ItemStack slotStack = slot.getItem();
            returnStack = slotStack.copy();

            if (handleCoreQuickMoveStackLogicWithUpgradeSlot(index, NUMBER_OF_SLOTS, ((IVEPoweredTileEntity) this.tileEntity).getUpgradeSlotId(), slotStack) != null)
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

