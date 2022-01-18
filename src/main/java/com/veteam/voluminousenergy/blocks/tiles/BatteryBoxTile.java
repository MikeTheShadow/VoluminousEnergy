package com.veteam.voluminousenergy.blocks.tiles;

import com.veteam.voluminousenergy.blocks.blocks.VEBlocks;
import com.veteam.voluminousenergy.blocks.containers.BatteryBoxContainer;
import com.veteam.voluminousenergy.items.batteries.VEEnergyItem;
import com.veteam.voluminousenergy.tools.Config;
import com.veteam.voluminousenergy.tools.energy.VEEnergyStorage;
import com.veteam.voluminousenergy.tools.networking.VENetwork;
import com.veteam.voluminousenergy.tools.networking.packets.BatteryBoxSendOutPowerPacket;
import com.veteam.voluminousenergy.tools.networking.packets.BatteryBoxSlotPairPacket;
import com.veteam.voluminousenergy.tools.networking.packets.BoolButtonPacket;
import com.veteam.voluminousenergy.tools.networking.packets.DirectionButtonPacket;
import com.veteam.voluminousenergy.tools.sidemanager.VESlotManager;
import com.veteam.voluminousenergy.util.SlotType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.wrapper.RangedWrapper;
import net.minecraftforge.network.PacketDistributor;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.UUID;

public class BatteryBoxTile extends VoluminousTileEntity implements MenuProvider {
    private LazyOptional<VEEnergyStorage> energy = LazyOptional.of(this::createEnergy);

    // Slot Managers
    public VESlotManager topManager = new VESlotManager(0, Direction.UP, true, "slot.voluminousenergy.input_slot", SlotType.INPUT);
    public VESlotManager bottomManager = new VESlotManager(1,Direction.DOWN, true, "slot.voluminousenergy.output_slot",SlotType.OUTPUT);

    private final int POWER_MAX_TX = Config.BATTERY_BOX_TRANSFER.get();
    private final int MAX_POWER = Config.BATTERY_BOX_MAX_POWER.get();

    // Sided Item Handlers
    private LazyOptional<ItemStackHandler> handler = LazyOptional.of(() -> this.inventory);
    private LazyOptional<IItemHandlerModifiable> topHandler = LazyOptional.of(() -> new RangedWrapper(this.inventory, 0, 6));
    private LazyOptional<IItemHandlerModifiable> bottomHandler = LazyOptional.of(() -> new RangedWrapper(this.inventory, 6, 12));

    // Modes and meta stuff for the battery box
    private boolean[] doDischargeInstead = {true,true,true,true,true,true};
    private boolean topIsIngress = true;
    private boolean sendOutPower = false;

    public BatteryBoxTile(BlockPos pos, BlockState state) {
        super(VEBlocks.BATTERY_BOX_TILE, pos, state);
    }

    @Deprecated
    public BatteryBoxTile(BlockEntityType<?> type, BlockPos pos, BlockState state){
        super(VEBlocks.BATTERY_BOX_TILE, pos, state);
    }

    public ItemStackHandler inventory = new ItemStackHandler(12) {
        @Override
        public boolean isItemValid(int slot, @Nonnull ItemStack stack) { //IS ITEM VALID PLEASE DO THIS PER SLOT TO SAVE DEBUG HOURS!!!!
            return stack.getCapability(CapabilityEnergy.ENERGY).isPresent();
        }

        @Nonnull
        @Override
        public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate){ //ALSO DO THIS PER SLOT BASIS TO SAVE DEBUG HOURS!!!
            if(stack.getCapability(CapabilityEnergy.ENERGY).isPresent()) return super.insertItem(slot, stack, simulate);
            return stack;
        }

        @Override
        @Nonnull
        public ItemStack extractItem(int slot, int amount, boolean simulate){
            return super.extractItem(slot,amount,simulate);
        }

        @Override
        protected void onContentsChanged(final int slot) {
            super.onContentsChanged(slot);
            BatteryBoxTile.this.setChanged();
        }
    };

    @Override
    public void tick(){
        updateClients();
        for(int i = 0; i < 12; i++){
            if(!inventory.getStackInSlot(i).isEmpty()){
                if(inventory.getStackInSlot(i).getCapability(CapabilityEnergy.ENERGY).isPresent()){
                    if(i >= 6){
                        int j = i-6;
                        if(doDischargeInstead[j]){
                            //VoluminousEnergy.LOGGER.debug("Discharging: " + i);
                            dischargeItem(inventory.getStackInSlot(j));
                        } else {
                            //VoluminousEnergy.LOGGER.debug("Charging: " + i);
                            chargeItem(inventory.getStackInSlot(j));
                        }
                    } else {
                        if(doDischargeInstead[i]){
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
        if(sendOutPower){
            sendOutPower();
        }
    }

    private void dischargeItem(ItemStack itemStack) {
        energy.ifPresent(teEnergy -> {
            if(teEnergy.getEnergyStored() < teEnergy.getMaxEnergyStored()){
                itemStack.getCapability(CapabilityEnergy.ENERGY).ifPresent(energyItem ->{
                    if(energyItem.canExtract()){
                        int toExtract;
                        if(itemStack.getItem() instanceof VEEnergyItem) {
                            int maxExtractItem = ((VEEnergyItem) itemStack.getItem()).getMaxTransfer();
                            toExtract = Math.min(energyItem.getEnergyStored(), maxExtractItem);
                            toExtract = Math.min(toExtract, POWER_MAX_TX);
                        } else toExtract = Math.min(energyItem.getEnergyStored(), POWER_MAX_TX);

                        if(energyItem.getEnergyStored() > 0){
                            if(toExtract + teEnergy.getEnergyStored() <= MAX_POWER){
                                energyItem.extractEnergy(toExtract,false);
                                teEnergy.receiveEnergy(toExtract,false);
                            } else if (teEnergy.getEnergyStored() != MAX_POWER){
                                energyItem.extractEnergy(MAX_POWER - teEnergy.getEnergyStored(), false);
                                teEnergy.receiveEnergy(MAX_POWER - teEnergy.getEnergyStored(), false);
                            }
                        }
                    }
                });
            }
        });
    }

    private void chargeItem(ItemStack itemStack){
        energy.ifPresent(teEnergy -> {
            if(teEnergy.getEnergyStored() > 0){
                itemStack.getCapability(CapabilityEnergy.ENERGY).ifPresent(energyItem -> {
                    if(energyItem.canReceive()){
                        int toReceive;
                        if(itemStack.getItem() instanceof VEEnergyItem){
                            int maxReceiveItem = ((VEEnergyItem) itemStack.getItem()).getMaxTransfer();
                            toReceive = Math.min(
                                    (energyItem.getMaxEnergyStored() - energyItem.getEnergyStored()),
                                    maxReceiveItem);
                            toReceive = Math.min(toReceive, POWER_MAX_TX);
                            toReceive = Math.min(toReceive, teEnergy.getEnergyStored());
                        } else toReceive = Math.min(energyItem.getEnergyStored(), POWER_MAX_TX);

                        if(toReceive + teEnergy.getEnergyStored() <= energyItem.getMaxEnergyStored()){
                            teEnergy.extractEnergy(toReceive, false);
                            energyItem.receiveEnergy(toReceive, false);
                        } else if(energyItem.getEnergyStored() != energyItem.getMaxEnergyStored()){ // Actually, this might not be needed
                            teEnergy.extractEnergy(energyItem.getMaxEnergyStored() - energyItem.getEnergyStored(), false);
                            energyItem.receiveEnergy(energyItem.getMaxEnergyStored() - energyItem.getEnergyStored(), false);
                        }
                    }
                });
            }
        });
    }

    private void moveItem(int i){
        if(i <6){
            //VoluminousEnergy.LOGGER.debug("Move Item Called");
            ItemStack itemStack = inventory.getStackInSlot(i).copy();
            if(inventory.getStackInSlot(i+6).isEmpty()){
                //VoluminousEnergy.LOGGER.debug("Empty check passed");
                // Remove stack in the ith, slot and move it to i+6th slot indicating it's discharged
                itemStack.getCapability(CapabilityEnergy.ENERGY).ifPresent(energy -> {
                    //VoluminousEnergy.LOGGER.debug("Item has Energy Capability");
                    if((!doDischargeInstead[i] && energy.getEnergyStored() == energy.getMaxEnergyStored())
                            || (doDischargeInstead[i] && energy.getEnergyStored() == 0)){
                        //VoluminousEnergy.LOGGER.debug("Energy stored is Max energy, or item is drained");
                        inventory.extractItem(i,1,false);
                        inventory.insertItem(i+6,itemStack.copy(),false);
                    }
                });
            }
        } else if (inventory.getStackInSlot(i-6).isEmpty()) {
            ItemStack itemStack = inventory.getStackInSlot(i).copy();
            itemStack.getCapability(CapabilityEnergy.ENERGY).ifPresent(energy -> {
                if((doDischargeInstead[i-6] && energy.getEnergyStored() == energy.getMaxEnergyStored())
                        || (!doDischargeInstead[i-6] && energy.getEnergyStored() == 0)){
                    inventory.extractItem(i,1,false);
                    inventory.insertItem(i-6, itemStack.copy(),false);
                }
            });
        }
    }

    public static int receiveEnergy(BlockEntity tileEntity, Direction from, int maxReceive){
        return tileEntity.getCapability(CapabilityEnergy.ENERGY, from).map(handler ->
                handler.receiveEnergy(maxReceive, false)).orElse(0);
    }

    private void sendOutPower() {
        energy.ifPresent(energy -> {
            for (Direction dir : Direction.values()){
                BlockEntity tileEntity = level.getBlockEntity(getBlockPos().relative(dir));
                Direction opposite = dir.getOpposite();
                if(tileEntity != null){
                    // If less energy stored then max transfer send the all the energy stored rather than the max transfer amount
                    int smallest = Math.min(Config.BATTERY_BOX_TRANSFER.get(), energy.getEnergyStored());
                    int received = receiveEnergy(tileEntity, opposite, smallest);
                    energy.consumeEnergy(received);
                    if (energy.getEnergyStored() <=0){
                        break;
                    }
                }
            }
        });
    }

    /*
        Read and Write on World save
     */

    @Override
    public void load(CompoundTag tag){
        CompoundTag inv = tag.getCompound("inv");
        this.inventory.deserializeNBT(inv);
        //createHandler().deserializeNBT(inv);
        energy.ifPresent(h -> h.deserializeNBT(tag));

        doDischargeInstead[0] = tag.getBoolean("slot_pair_mode_0");
        doDischargeInstead[1] = tag.getBoolean("slot_pair_mode_1");
        doDischargeInstead[2] = tag.getBoolean("slot_pair_mode_2");
        doDischargeInstead[3] = tag.getBoolean("slot_pair_mode_3");
        doDischargeInstead[4] = tag.getBoolean("slot_pair_mode_4");
        doDischargeInstead[5] = tag.getBoolean("slot_pair_mode_5");

        sendOutPower = tag.getBoolean("send_out_power");

        super.load(tag);
    }

    @Override
    public void saveAdditional(CompoundTag tag) {
        tag.put("inv", this.inventory.serializeNBT());
        energy.ifPresent(h -> h.serializeNBT(tag));

        tag.putBoolean("slot_pair_mode_0", doDischargeInstead[0]);
        tag.putBoolean("slot_pair_mode_1", doDischargeInstead[1]);
        tag.putBoolean("slot_pair_mode_2", doDischargeInstead[2]);
        tag.putBoolean("slot_pair_mode_3", doDischargeInstead[3]);
        tag.putBoolean("slot_pair_mode_4", doDischargeInstead[4]);
        tag.putBoolean("slot_pair_mode_5", doDischargeInstead[5]);

        tag.putBoolean("send_out_power", sendOutPower);
    }

    private VEEnergyStorage createEnergy(){
        return new VEEnergyStorage(Config.BATTERY_BOX_MAX_POWER.get(),Config.BATTERY_BOX_TRANSFER.get()); // Max Power Storage, Max transfer
    }

    @Override
    public CompoundTag getUpdateTag() {
        CompoundTag compoundTag = new CompoundTag();
        this.saveAdditional(compoundTag);
        return compoundTag;
    }

    @Nullable
    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public void onDataPacket(final Connection net, final ClientboundBlockEntityDataPacket pkt){
        energy.ifPresent(e -> e.setEnergy(pkt.getTag().getInt("energy")));
        this.load(pkt.getTag());
        super.onDataPacket(net, pkt);
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
        if(cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
            if (side == null) {
                return handler.cast();
            } else {
                // VoluminousEnergy.LOGGER.debug("GET CAPABILITY: " + inputSlotProp.getDirection() + " " + inputSlotProp.getStatus() + " " + outputSlotProp.getDirection() + " " + outputSlotProp.getStatus() + " " + rngSlotProp.getDirection() + " " + rngSlotProp.getStatus());
                // 1 = top, 0 = bottom, 2 = north, 3 = south, 4 = west, 5 = east
                if (side.get3DDataValue() == topManager.getDirection().get3DDataValue() && topManager.getStatus()){
                    return topIsIngress ? topHandler.cast() : bottomHandler.cast();
                }
                if (side.get3DDataValue() == bottomManager.getDirection().get3DDataValue() && bottomManager.getStatus()){
                    return topIsIngress ? bottomHandler.cast() : topHandler.cast();
                }
            }
        }
        if (cap == CapabilityEnergy.ENERGY){
            return energy.cast();
        }
        return super.getCapability(cap, side);
    }

    @Override
    public Component getDisplayName(){
        return new TextComponent(getType().getRegistryName().getPath());
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int i, @Nonnull Inventory playerInventory, @Nonnull Player playerEntity) {
        return new BatteryBoxContainer(i,level,worldPosition,playerInventory,playerEntity);
    }

    public void updateSlotPair(boolean mode, int pairId){
        if(pairId <6) doDischargeInstead[pairId] = mode;
    }

    public void updateSendOutPower(boolean sendOutPower){this.sendOutPower = sendOutPower;};

    @Override
public void updatePacketFromGui(boolean status, int slotId){
        if(slotId == 0) topManager.setStatus(status); // ingress
        else if (slotId == 1) bottomManager.setStatus(status); // egress
    }

    public void updatePacketFromGui(int direction, int slotId){
        if(slotId == 0) topManager.setDirection(direction); // ingress
        else if (slotId == 1) bottomManager.setDirection(direction); // egress
    }

    @Override
    public void sendPacketToClient(){
        if(level == null || getLevel() == null) return;
        if(getLevel().getServer() != null) {
            this.playerUuid.forEach(u -> {
                level.getServer().getPlayerList().getPlayers().forEach(s -> {
                    if (s.getUUID().equals(u)){
                        // Boolean Buttons
                        VENetwork.channel.send(PacketDistributor.PLAYER.with(() -> s), new BoolButtonPacket(topManager.getStatus(), topManager.getSlotNum()));
                        VENetwork.channel.send(PacketDistributor.PLAYER.with(() -> s), new BoolButtonPacket(bottomManager.getStatus(), bottomManager.getSlotNum()));

                        // Direction Buttons
                        VENetwork.channel.send(PacketDistributor.PLAYER.with(() -> s), new DirectionButtonPacket(topManager.getDirection().get3DDataValue(), topManager.getSlotNum()));
                        VENetwork.channel.send(PacketDistributor.PLAYER.with(() -> s), new DirectionButtonPacket(bottomManager.getDirection().get3DDataValue(), bottomManager.getSlotNum()));

                        // Slot status
                        for (int i = 0; i < 6; i++) {
                            VENetwork.channel.send(PacketDistributor.PLAYER.with(() -> s), new BatteryBoxSlotPairPacket(doDischargeInstead[i], i));
                        }

                        // Send Out Power
                        VENetwork.channel.send(PacketDistributor.PLAYER.with(() -> s), new BatteryBoxSendOutPowerPacket(this.sendOutPower));
                    }
                });
            });
        }
    }

    @Override
    protected void uuidCleanup(){
        if(playerUuid.isEmpty() || level == null) return;
        if(level.getServer() == null) return;

        if(cleanupTick == 20){
            ArrayList<UUID> toRemove = new ArrayList<>();
            level.getServer().getPlayerList().getPlayers().forEach(player ->{
                if(player.containerMenu != null){
                    if(!(player.containerMenu instanceof BatteryBoxContainer)){
                        toRemove.add(player.getUUID());
                    }
                } else if (player.containerMenu == null){
                    toRemove.add(player.getUUID());
                }
            });
            toRemove.forEach(uuid -> playerUuid.remove(uuid));
        }
        super.uuidCleanup();
    }
}
