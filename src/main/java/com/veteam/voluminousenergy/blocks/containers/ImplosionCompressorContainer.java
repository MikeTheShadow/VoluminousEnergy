package com.veteam.voluminousenergy.blocks.containers;

import com.veteam.voluminousenergy.blocks.blocks.VEBlocks;
import com.veteam.voluminousenergy.blocks.inventory.slots.VEInsertSlot;
import com.veteam.voluminousenergy.blocks.inventory.slots.VEOutputSlot;
import com.veteam.voluminousenergy.blocks.screens.ImplosionCompressorScreen;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.items.IItemHandler;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;

import static com.veteam.voluminousenergy.blocks.blocks.VEBlocks.IMPLOSION_COMPRESSOR_CONTAINER;

public class ImplosionCompressorContainer extends VEContainer {

    private ImplosionCompressorScreen screen;
    public static final int NUMBER_OF_SLOTS = 4;

    public ImplosionCompressorContainer(int id, Level world, BlockPos pos, Inventory inventory, Player player){
        super(IMPLOSION_COMPRESSOR_CONTAINER.get(),id,world,pos,inventory,player,VEBlocks.IMPLOSION_COMPRESSOR_BLOCK.get());
    }

    @Nonnull
    @Override
    public @NotNull ItemStack quickMoveStack(final @NotNull Player player, final int index) {
        ItemStack returnStack = ItemStack.EMPTY;
        final Slot slot = this.slots.get(index);
        if (slot != null && slot.hasItem()) {
            final ItemStack slotStack = slot.getItem();
            returnStack = slotStack.copy();

            if (handleCoreQuickMoveStackLogicWithUpgradeSlot(index, NUMBER_OF_SLOTS, 3, slotStack) != null) return ItemStack.EMPTY;

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


    // Unauthorized call to this method can be dangerous. Can't not be public AFAIK. :(
    public void setScreen(ImplosionCompressorScreen screen){
        this.screen = screen;
    }

    @Override
    protected void addSlotsToGUI(IItemHandler h) {
        addSlot(new VEInsertSlot(h, 0, 53, 23)); // Main input
        addSlot(new VEInsertSlot(h, 1, 53, 41)); // Gunpowder slot
        addSlot(new VEOutputSlot(h, 2,116,33)); //Main Output
        addSlot(new VEInsertSlot(h, 3,154, -14)); //Upgrade slot
    }

    public void updateDirectionButton(int direction, int slotId){ this.screen.updateButtonDirection(direction,slotId); }

    @Override
public void updateStatusButton(boolean status, int slotId){
        this.screen.updateBooleanButton(status, slotId);
    }
}
