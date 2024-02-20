package com.veteam.voluminousenergy.blocks.tiles;

import com.veteam.voluminousenergy.blocks.blocks.VEBlocks;
import com.veteam.voluminousenergy.blocks.containers.VEContainers;
import com.veteam.voluminousenergy.items.batteries.VEEnergyItem;
import com.veteam.voluminousenergy.tools.Config;
import com.veteam.voluminousenergy.tools.buttons.VEPowerIOManager;
import com.veteam.voluminousenergy.tools.buttons.batteryBox.VEBatterySwitchManager;
import com.veteam.voluminousenergy.tools.sidemanager.VESlotManager;
import com.veteam.voluminousenergy.util.SlotType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public class BatteryBoxTile extends VETileEntity  {

    private final int POWER_MAX_TX = Config.BATTERY_BOX_TRANSFER.get();
    private final int MAX_POWER = Config.BATTERY_BOX_MAX_POWER.get();

    List<VESlotManager> slotManagers = new ArrayList<>() {
        {
            add(new VESlotManager(0, Direction.UP, true, SlotType.INPUT));
            add(new VESlotManager(1, Direction.UP, true, SlotType.INPUT));
            add(new VESlotManager(2, Direction.UP, true, SlotType.INPUT));
            add(new VESlotManager(3, Direction.UP, true, SlotType.INPUT));
            add(new VESlotManager(4, Direction.UP, true, SlotType.INPUT));
            add(new VESlotManager(5, Direction.UP, true, SlotType.INPUT));
            add(new VESlotManager(6, Direction.DOWN, true, SlotType.OUTPUT));
            add(new VESlotManager(7, Direction.DOWN, true, SlotType.OUTPUT));
            add(new VESlotManager(8, Direction.DOWN, true, SlotType.OUTPUT));
            add(new VESlotManager(9, Direction.DOWN, true, SlotType.OUTPUT));
            add(new VESlotManager(10, Direction.DOWN, true, SlotType.OUTPUT));
            add(new VESlotManager(11, Direction.DOWN, true, SlotType.OUTPUT));
        }
    };

    @Override
    public void updatePacketFromGui(boolean status, int slotId) {
        if (slotId < 6) {
            for (int i = 0; i < 6; i++) {
                slotManagers.get(i).setStatus(status);
            }
        } else if (slotId < 12) {
            for (int i = 6; i < 12; i++) {
                slotManagers.get(i).setStatus(status);
            }
        }
    }

    @Override
    public void updatePacketFromGui(int direction, int slotId) {
        if (slotId < 6) {
            for (int i = 0; i < 6; i++) {
                this.capabilityMap.moveSlotManagerPos(slotManagers.get(i), direction);
            }
        } else if (slotId < 12) {
            for (int i = 6; i < 12; i++) {
                this.capabilityMap.moveSlotManagerPos(slotManagers.get(i), direction);
            }
        }
    }

    // Modes and meta stuff for the battery box
    private final VEBatterySwitchManager[] switchManagers = {
            new VEBatterySwitchManager(0, true),
            new VEBatterySwitchManager(1, true),
            new VEBatterySwitchManager(2, true),
            new VEBatterySwitchManager(3, true),
            new VEBatterySwitchManager(4, true),
            new VEBatterySwitchManager(5, true),
    };


    private boolean topIsIngress = true;

    private final VEPowerIOManager powerIOManager = new VEPowerIOManager(true);

    public BatteryBoxTile(BlockPos pos, BlockState state) {
        super(VEBlocks.BATTERY_BOX_TILE.get(), pos, state, null);
    }

    public ItemStackHandler inventory = new ItemStackHandler(12) {
        @Override
        public boolean isItemValid(int slot, @Nonnull ItemStack stack) { //IS ITEM VALID PLEASE DO THIS PER SLOT TO SAVE DEBUG HOURS!!!!
            return stack.getCapability(ForgeCapabilities.ENERGY).isPresent();
        }

        @Nonnull
        @Override
        public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) { //ALSO DO THIS PER SLOT BASIS TO SAVE DEBUG HOURS!!!
            if (stack.getCapability(ForgeCapabilities.ENERGY).isPresent())
                return super.insertItem(slot, stack, simulate);
            return stack;
        }

        @Override
        @Nonnull
        public ItemStack extractItem(int slot, int amount, boolean simulate) {
            return super.extractItem(slot, amount, simulate);
        }

        @Override
        protected void onContentsChanged(final int slot) {
            super.onContentsChanged(slot);
            BatteryBoxTile.this.setChanged();
        }
    };

    @Override
    public void tick() {
        updateClients();
        for (int i = 0; i < 12; i++) {
            if (!inventory.getStackInSlot(i).isEmpty()) {
                if (inventory.getStackInSlot(i).getCapability(ForgeCapabilities.ENERGY).isPresent()) {
                    if (i >= 6) {
                        int j = i - 6;
                        if (switchManagers[j].isFlipped()) {
                            //VoluminousEnergy.LOGGER.debug("Discharging: " + i);
                            dischargeItem(inventory.getStackInSlot(j));
                        } else {
                            //VoluminousEnergy.LOGGER.debug("Charging: " + i);
                            chargeItem(inventory.getStackInSlot(j));
                        }
                    } else {
                        if (switchManagers[i].isFlipped()) {
                            //VoluminousEnergy.LOGGER.debug("Discharging: " + i);
                            dischargeItem(inventory.getStackInSlot(i));
                        } else {
                            //VoluminousEnergy.LOGGER.debug("Charging: " + i);
                            chargeItem(inventory.getStackInSlot(i));
                        }
                    }
                    moveItem(i);
                }
            }
        }
        if (powerIOManager.isFlipped()) {
            sendOutPower();
        }
    }

    private void dischargeItem(ItemStack itemStack) {
        if (energy.getEnergyStored() < energy.getMaxEnergyStored()) {
            itemStack.getCapability(ForgeCapabilities.ENERGY).ifPresent(energyItem -> {
                if (energyItem.canExtract()) {
                    int toExtract;
                    if (itemStack.getItem() instanceof VEEnergyItem) {
                        int maxExtractItem = ((VEEnergyItem) itemStack.getItem()).getMaxTransfer();
                        toExtract = Math.min(energyItem.getEnergyStored(), maxExtractItem);
                        toExtract = Math.min(toExtract, POWER_MAX_TX);
                    } else toExtract = Math.min(energyItem.getEnergyStored(), POWER_MAX_TX);

                    if (energyItem.getEnergyStored() > 0) {
                        if (toExtract + energy.getEnergyStored() <= MAX_POWER) {
                            energyItem.extractEnergy(toExtract, false);
                            energy.receiveEnergy(toExtract, false);
                        } else if (energy.getEnergyStored() != MAX_POWER) {
                            energyItem.extractEnergy(MAX_POWER - energy.getEnergyStored(), false);
                            energy.receiveEnergy(MAX_POWER - energy.getEnergyStored(), false);
                        }
                    }
                }
            });
        }
    }

    private void chargeItem(ItemStack itemStack) {
        if (energy.getEnergyStored() > 0) {
            itemStack.getCapability(ForgeCapabilities.ENERGY).ifPresent(energyItem -> {
                if (energyItem.canReceive()) {
                    int toReceive;
                    if (itemStack.getItem() instanceof VEEnergyItem) {
                        int maxReceiveItem = ((VEEnergyItem) itemStack.getItem()).getMaxTransfer();
                        toReceive = Math.min(
                                (energyItem.getMaxEnergyStored() - energyItem.getEnergyStored()),
                                maxReceiveItem);
                        toReceive = Math.min(toReceive, POWER_MAX_TX);
                        toReceive = Math.min(toReceive, energy.getEnergyStored());
                    } else
                        toReceive = Math.min((energyItem.getMaxEnergyStored() - energyItem.getEnergyStored()), POWER_MAX_TX);

                    if (toReceive + energy.getEnergyStored() <= energyItem.getMaxEnergyStored()) {
                        energy.extractEnergy(toReceive, false);
                        energyItem.receiveEnergy(toReceive, false);
                    } else if (energyItem.getEnergyStored() != energyItem.getMaxEnergyStored()) { // Actually, this might not be needed
                        energy.extractEnergy(energyItem.getMaxEnergyStored() - energyItem.getEnergyStored(), false);
                        energyItem.receiveEnergy(energyItem.getMaxEnergyStored() - energyItem.getEnergyStored(), false);
                    }
                }
            });
        }
    }

    private void moveItem(int i) {
        if (i < 6) {
            //VoluminousEnergy.LOGGER.debug("Move Item Called");
            ItemStack itemStack = inventory.getStackInSlot(i).copy();
            if (inventory.getStackInSlot(i + 6).isEmpty()) {
                //VoluminousEnergy.LOGGER.debug("Empty check passed");
                // Remove item in the ith, slot and move it to i+6th slot indicating it's discharged
                itemStack.getCapability(ForgeCapabilities.ENERGY).ifPresent(energy -> {
                    //VoluminousEnergy.LOGGER.debug("Item has Energy Capability");
                    if ((!switchManagers[i].isFlipped() && energy.getEnergyStored() == energy.getMaxEnergyStored())
                            || (switchManagers[i].isFlipped() && energy.getEnergyStored() == 0)) {
                        //VoluminousEnergy.LOGGER.debug("Energy stored is Max energy, or item is drained");
                        inventory.extractItem(i, 1, false);
                        inventory.insertItem(i + 6, itemStack.copy(), false);
                    }
                });
            }
        } else if (inventory.getStackInSlot(i - 6).isEmpty()) {
            ItemStack itemStack = inventory.getStackInSlot(i).copy();
            itemStack.getCapability(ForgeCapabilities.ENERGY).ifPresent(energy -> {
                if ((switchManagers[i - 6].isFlipped() && energy.getEnergyStored() == energy.getMaxEnergyStored())
                        || (!switchManagers[i - 6].isFlipped() && energy.getEnergyStored() == 0)) {
                    inventory.extractItem(i, 1, false);
                    inventory.insertItem(i - 6, itemStack.copy(), false);
                }
            });
        }
    }

    public static int receiveEnergy(BlockEntity tileEntity, Direction from, int maxReceive) {
        return tileEntity.getCapability(ForgeCapabilities.ENERGY, from).map(handler ->
                handler.receiveEnergy(maxReceive, false)).orElse(0);
    }

    void sendOutPower() {
        for (Direction dir : Direction.values()) {
            BlockEntity tileEntity = level.getBlockEntity(getBlockPos().relative(dir));
            Direction opposite = dir.getOpposite();
            if (tileEntity != null) {
                // If less energy stored then max transfer send the all the energy stored rather than the max transfer amount
                int smallest = Math.min(this.energy.getMaxTransfer(), energy.getEnergyStored());
                int received = receiveEnergy(tileEntity, opposite, smallest);
                energy.consumeEnergy(received);
                if (energy.getEnergyStored() <= 0) {
                    break;
                }
            }
        }
    }

    /*
        Read and Write on World save
     */

    @Override
    public void load(CompoundTag tag) {
        switchManagers[0].setFlipped(tag.getBoolean("slot_pair_mode_0"));
        switchManagers[1].setFlipped(tag.getBoolean("slot_pair_mode_1"));
        switchManagers[2].setFlipped(tag.getBoolean("slot_pair_mode_2"));
        switchManagers[3].setFlipped(tag.getBoolean("slot_pair_mode_3"));
        switchManagers[4].setFlipped(tag.getBoolean("slot_pair_mode_4"));
        switchManagers[5].setFlipped(tag.getBoolean("slot_pair_mode_5"));

        powerIOManager.setFlipped(tag.getBoolean("send_out_power"));
        super.load(tag);
    }

    @Override
    public void saveAdditional(@NotNull CompoundTag tag) {

        tag.putBoolean("slot_pair_mode_0", switchManagers[0].isFlipped());
        tag.putBoolean("slot_pair_mode_1", switchManagers[1].isFlipped());
        tag.putBoolean("slot_pair_mode_2", switchManagers[2].isFlipped());
        tag.putBoolean("slot_pair_mode_3", switchManagers[3].isFlipped());
        tag.putBoolean("slot_pair_mode_4", switchManagers[4].isFlipped());
        tag.putBoolean("slot_pair_mode_5", switchManagers[5].isFlipped());

        tag.putBoolean("send_out_power", powerIOManager.isFlipped());
        super.saveAdditional(tag);
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int i, @Nonnull Inventory playerInventory, @Nonnull Player playerEntity) {
        return VEContainers.BATTERY_BOX_FACTORY.create(i, level, worldPosition, playerInventory, playerEntity);
    }

    @Override
    public @Nonnull ItemStackHandler getInventoryHandler() {
        return inventory;
    }

    @Override
    public @Nonnull List<VESlotManager> getSlotManagers() {
        return slotManagers;
    }

    public void updateSlotPair(boolean mode, int pairId) {
        switchManagers[pairId].setFlipped(mode);
    }

    public void updateSendOutPower(boolean sendOutPower) {
        this.powerIOManager.setFlipped(sendOutPower);
        if (sendOutPower) {
            energy.setMaxReceive(0);
        } else {
            energy.setMaxReceive(this.energy.getMaxTransfer());
        }
    }

    public VEBatterySwitchManager[] getSwitchManagers() {
        return switchManagers;
    }

    public VEPowerIOManager getPowerIOManager() {
        return powerIOManager;
    }
}
