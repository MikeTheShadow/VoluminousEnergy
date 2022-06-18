package com.veteam.voluminousenergy.blocks.containers;

import com.veteam.voluminousenergy.blocks.blocks.VEBlocks;
import com.veteam.voluminousenergy.blocks.inventory.slots.TileEntitySlots.CompressorInputSlot;
import com.veteam.voluminousenergy.blocks.inventory.slots.VEInsertSlot;
import com.veteam.voluminousenergy.blocks.inventory.slots.VEOutputSlot;
import com.veteam.voluminousenergy.tools.energy.VEEnergyStorage;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.DataSlot;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.wrapper.InvWrapper;

import javax.annotation.Nonnull;

import static com.veteam.voluminousenergy.blocks.blocks.VEBlocks.COMPRESSOR_CONTAINER;

public class CompressorContainer extends VoluminousContainer {

        private final Player playerEntity;
        private final IItemHandler playerInventory;
        public static final int NUMBER_OF_SLOTS = 3;

        public CompressorContainer(int id, Level world, BlockPos pos, Inventory inventory, Player player){
            super(COMPRESSOR_CONTAINER.get(),id);
            this.tileEntity = world.getBlockEntity(pos);
            this.tileEntity.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY);
            this.playerEntity = player;
            this.playerInventory = new InvWrapper(inventory);

            tileEntity.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).ifPresent(h -> {
                addSlot(new CompressorInputSlot(h, 0, 80, 13, world));
                addSlot(new VEOutputSlot(h, 1,80,58));//Main Output
                addSlot(new VEInsertSlot(h, 2,154, -14));//Upgrade slot
            });
            layoutPlayerInventorySlots(8, 84);

            addDataSlot(new DataSlot() {
                @Override
                public int get() {
                    return getEnergy();
                }

                @Override
                public void set(int value) {
                }
            });
        }

        public int getEnergy(){
            return tileEntity.getCapability(CapabilityEnergy.ENERGY).map(IEnergyStorage::getEnergyStored).orElse(0);
        }

        public int powerScreen(int px){
            int stored = tileEntity.getCapability(CapabilityEnergy.ENERGY).map(IEnergyStorage::getEnergyStored).orElse(0);
            int max = tileEntity.getCapability(CapabilityEnergy.ENERGY).map(IEnergyStorage::getMaxEnergyStored).orElse(0);
            int ret = (((stored*100/max*100)/100)*px)/100;
            return ret;
        }

        @Override
        public boolean stillValid(Player playerIn) {
            return stillValid(ContainerLevelAccess.create(tileEntity.getLevel(),tileEntity.getBlockPos()),playerEntity, VEBlocks.COMPRESSOR_BLOCK.get());
        }

        private void layoutPlayerInventorySlots(int leftCol, int topRow) {
            // Player inventory
            addSlotBox(playerInventory, 9, leftCol, topRow, 9, 18, 3, 18);

            // Hotbar
            topRow += 58;
            addSlotRange(playerInventory, 0, leftCol, topRow, 9, 18);
        }

        @Nonnull
        @Override
        public ItemStack quickMoveStack(final Player player, final int index) {
            ItemStack returnStack = ItemStack.EMPTY;
            final Slot slot = this.slots.get(index);

            if (slot != null && slot.hasItem()) {
                final ItemStack slotStack = slot.getItem();
                returnStack = slotStack.copy();

                if (handleCoreQuickMoveStackLogicWithUpgradeSlot(index, NUMBER_OF_SLOTS, 2, slotStack) != null) return ItemStack.EMPTY;

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
