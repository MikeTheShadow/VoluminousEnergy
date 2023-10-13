package com.veteam.voluminousenergy.blocks.containers;

import com.veteam.voluminousenergy.VoluminousEnergy;
import com.veteam.voluminousenergy.blocks.screens.VEContainerScreen;
import com.veteam.voluminousenergy.blocks.tiles.IVEPoweredTileEntity;
import com.veteam.voluminousenergy.blocks.tiles.VETileEntity;
import com.veteam.voluminousenergy.tools.sidemanager.VESlotManager;
import com.veteam.voluminousenergy.util.RegistryLookups;
import com.veteam.voluminousenergy.util.SlotType;
import com.veteam.voluminousenergy.util.TagUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.SlotItemHandler;
import net.minecraftforge.items.wrapper.InvWrapper;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

public abstract class VEContainer extends AbstractContainerMenu {

    VETileEntity tileEntity;

    Player playerEntity;
    IItemHandler playerInventory;

    VEContainerScreen<?> screen;
    Block block;

    ContainerLevelAccess access;
    Level world;

    protected VEContainer(@Nullable MenuType<?> menuType, int id, Level world, BlockPos pos, Inventory inventory, Player player, Block block) {
        super(menuType, id);
        this.tileEntity = (VETileEntity) world.getBlockEntity(pos);
        this.playerEntity = player;
        this.playerInventory = new InvWrapper(inventory);
        this.block = block;
        this.access = ContainerLevelAccess.create(this.tileEntity.getLevel(),this.tileEntity.getBlockPos());
        this.world = world;
        // we add slots to GUI here
        tileEntity.getCapability(ForgeCapabilities.ITEM_HANDLER).ifPresent(this::addSlotsToGUI);

        // layout player inventory slots here
        layoutPlayerInventorySlots();

        // We assume if it's a powered tile entity that it requires a dataslot for energy
        if(this.tileEntity instanceof IVEPoweredTileEntity) {
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
    }

    protected abstract void addSlotsToGUI(IItemHandler h);

    /**
     * Override this if you wish to move where the inventory is displayed or disable the inventory display entirely
     */
    void layoutPlayerInventorySlots() {
        // Player inventory
        addSlotBox(playerInventory, 9, 8, 84, 9, 18, 3, 18);

        // Hotbar
        int hotBar = 84 + 58;
        addSlotRange(playerInventory, 0, 8, hotBar, 9, 18);
    }

    @Override
    public boolean stillValid(@NotNull Player player) {
        return stillValid(this.access,this.playerEntity, this.block);
    }

    public VETileEntity getTileEntity(){
        return tileEntity;
    }

    protected int addSlotRange(IItemHandler handler, int index, int x, int y, int amount, int dx) {
        for (int i = 0 ; i < amount ; i++) {
            addSlot(new SlotItemHandler(handler, index, x, y));
            x += dx;
            index++;
        }
        return index;
    }

    protected int addSlotBox(IItemHandler handler, int index, int x, int y, int horAmount, int dx, int verAmount, int dy) {
        for (int j = 0 ; j < verAmount ; j++) {
            index = addSlotRange(handler, index, x, y, horAmount, dx);
            y += dy;
        }
        return index;
    }

    public int getEnergy(){
        return tileEntity.getCapability(ForgeCapabilities.ENERGY).map(IEnergyStorage::getEnergyStored).orElse(0);
    }

    public int powerScreen(int px){
        int stored = tileEntity.getCapability(ForgeCapabilities.ENERGY).map(IEnergyStorage::getEnergyStored).orElse(0);
        int max = tileEntity.getCapability(ForgeCapabilities.ENERGY).map(IEnergyStorage::getMaxEnergyStored).orElse(0);
        return (((stored*100/max*100)/100)*px)/100;
    }

    public ItemStack handleCoreQuickMoveStackLogicWithUpgradeSlot(final int index, final int containerSlots, final int upgradeSlotId, ItemStack slotStack){
        if (index < containerSlots) { // Container --> Inventory
            if (!moveItemStackTo(slotStack, containerSlots, this.slots.size(), true)) {
                return ItemStack.EMPTY;
            }
        } else { // Inventory --> Container
            if(/*slotStack.is(VEItems.QUARTZ_MULTIPLIER)*/ TagUtil.isTaggedMachineUpgradeItem(slotStack) && !moveItemStackTo(slotStack, upgradeSlotId, upgradeSlotId+1, false)) {
                return ItemStack.EMPTY;
            }

            if (/*!slotStack.is(VEItems.QUARTZ_MULTIPLIER)*/ !TagUtil.isTaggedMachineUpgradeItem(slotStack) && !moveItemStackTo(slotStack, 0, upgradeSlotId, false)){
                return ItemStack.EMPTY;
            }
        }
        return null;
    }

    public ItemStack handleCoreQuickMoveStackLogic(final int index, final int containerSlots, ItemStack slotStack){
        if (index < containerSlots) { // Container --> Inventory
            if (!moveItemStackTo(slotStack, containerSlots, this.slots.size(), true)) {
                return ItemStack.EMPTY;
            }
        } else if (!moveItemStackTo(slotStack, 0, containerSlots, false)) { // Inventory --> Container
            return ItemStack.EMPTY;
        }
        return null;
    }

    public void setTileEntity(VETileEntity tileEntity){
        this.tileEntity = tileEntity;
    }

    // Unauthorized call to this method can be dangerous. Can't not be public AFAIK. :(
    public void setScreen(VEContainerScreen screen){
        this.screen = screen;
    }

    public void updateDirectionButton(int direction, int slotId){
        screen.updateButtonDirection(direction,slotId);
    }

    public void updateStatusButton(boolean status, int slotId){
        screen.updateBooleanButton(status, slotId);
    }

    public void updateStatusTank(boolean status, int id){
        screen.updateTankStatus(status, id);
    }

    public void updateDirectionTank(int direction, int id){
        screen.updateTankDirection(direction, id);
    }

    public int getUpgradeSlotId(){
        if (tileEntity instanceof IVEPoweredTileEntity){
            return ((IVEPoweredTileEntity) tileEntity).getUpgradeSlotId();
        }
        VoluminousEnergy.LOGGER.error("A container called getUpgradeSlotId when tile doesn't support upgrade slots! Offending tile is: " + RegistryLookups.getBlockEntityTypeKey(tileEntity.getType()));
        return 0;
    }

    @Nonnull
    @Override
    public ItemStack quickMoveStack(final @NotNull Player player, final int index) {
        ItemStack returnStack = ItemStack.EMPTY;
        final Slot slot = this.slots.get(index);

        int numberOfSlots = this.tileEntity.getSlotManagers().size() + (this.tileEntity instanceof IVEPoweredTileEntity ? 1 : 0);

        // TODO why is this a dangling if?
        if(this.tileEntity instanceof IVEPoweredTileEntity)

        if (slot.hasItem()) {
            final ItemStack slotStack = slot.getItem();
            returnStack = slotStack.copy();

            if (handleItemMove(index, numberOfSlots, slotStack,index) != null)
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

    public ItemStack handleItemMove(final int index, final int containerSlots, ItemStack slotStack, int slotId){
        if(index < containerSlots && !super.moveItemStackTo(slotStack, containerSlots, this.slots.size(), true)) {
            this.tileEntity.markRecipeDirty();
            return ItemStack.EMPTY;
        }
        else if(!moveItemStackTo(slotStack,this.slots.size(),slotId)) {
            this.tileEntity.markRecipeDirty();
            return ItemStack.EMPTY;
        }
        return null;
    }

    protected boolean moveItemStackTo(@NotNull ItemStack stackToMove, int endPos, int slotId) {
        boolean flag = false;
        int currentPos = 0;

        List<VESlotManager> slotManagers = this.tileEntity.getSlotManagers();
        ItemStackHandler handler = this.tileEntity.getInventoryHandler();
        int powerId = -1;
        if(tileEntity instanceof IVEPoweredTileEntity ivePoweredTileEntity) powerId = ivePoweredTileEntity.getUpgradeSlotId();
        if (stackToMove.isStackable()) {
            while (!stackToMove.isEmpty()) {
                if (currentPos >= endPos) {
                    break;
                }

                Slot slot = this.slots.get(currentPos);
                ItemStack itemInSlot = slot.getItem();

                boolean isInput;

                if(currentPos < slotManagers.size()) {
                    VESlotManager manager = slotManagers.get(currentPos);
                    isInput = manager.getSlotType() == SlotType.INPUT || manager.getSlotType() == SlotType.FLUID_INPUT;
                    if(handler != null && isInput) {
                        isInput = handler.isItemValid(currentPos,stackToMove.copy());
                    }
                } else if(currentPos == powerId) {
                    isInput = TagUtil.isTaggedMachineUpgradeItem(stackToMove);
                } else {
                    isInput = true;
                }
                if (slotId != currentPos && isInput && !itemInSlot.isEmpty() && ItemStack.isSameItemSameTags(stackToMove, itemInSlot)) {
                    int j = itemInSlot.getCount() + stackToMove.getCount();
                    int maxSize = Math.min(slot.getMaxStackSize(), stackToMove.getMaxStackSize());
                    if (j <= maxSize) {
                        stackToMove.setCount(0);
                        itemInSlot.setCount(j);
                        slot.setChanged();
                        flag = true;
                    } else if (itemInSlot.getCount() < maxSize) {
                        stackToMove.shrink(maxSize - itemInSlot.getCount());
                        itemInSlot.setCount(maxSize);
                        slot.setChanged();
                        flag = true;
                    }
                }
                ++currentPos;
            }
        }
        if (!stackToMove.isEmpty()) {
            currentPos = 0;

            while (true) {
                if (currentPos >= endPos) {
                    break;
                }

                Slot slot1 = this.slots.get(currentPos);
                ItemStack itemstack1 = slot1.getItem();

                boolean isInput;

                if(currentPos < slotManagers.size()) {
                    VESlotManager manager = slotManagers.get(currentPos);
                    isInput = manager.getSlotType() == SlotType.INPUT || manager.getSlotType() == SlotType.FLUID_INPUT;
                    if(handler != null && isInput) {
                        isInput = handler.isItemValid(currentPos,stackToMove.copy());
                    }
                } else if(currentPos == powerId) {
                    isInput = TagUtil.isTaggedMachineUpgradeItem(stackToMove);
                } else {
                    isInput = true;
                }
                if (isInput && itemstack1.isEmpty() && slot1.mayPlace(stackToMove)) {
                    if (stackToMove.getCount() > slot1.getMaxStackSize()) {
                        slot1.setByPlayer(stackToMove.split(slot1.getMaxStackSize()));
                    } else {
                        slot1.setByPlayer(stackToMove.split(stackToMove.getCount()));
                    }

                    slot1.setChanged();
                    flag = true;
                    break;
                }

                ++currentPos;
            }
        }
        return flag;
    }
}
