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
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.fml.network.PacketDistributor;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.wrapper.RangedWrapper;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.UUID;

public class BatteryBoxTile extends VoluminousTileEntity implements ITickableTileEntity, INamedContainerProvider {
    private LazyOptional<IEnergyStorage> energy = LazyOptional.of(this::createEnergy);

    // Slot Managers
    public VESlotManager topManager = new VESlotManager(0, Direction.UP, true, "slot.voluminousenergy.input_slot");
    public VESlotManager bottomManager = new VESlotManager(1,Direction.DOWN, true, "slot.voluminousenergy.output_slot");

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


    public BatteryBoxTile(){
        super(VEBlocks.BATTERY_BOX_TILE);
        /*
        slotPairList.add(new int[]{0,6});
        slotPairList.add(new int[]{1,7});
        slotPairList.add(new int[]{2,8});
        slotPairList.add(new int[]{3,9});
        slotPairList.add(new int[]{4,10});
        slotPairList.add(new int[]{5,11});
         */
    }

    public final ItemStackHandler inventory = new ItemStackHandler(12) {
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
            BatteryBoxTile.this.markDirty();
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
                            int maxExtractItem = ((VEEnergyItem) itemStack.getItem()).getMaxExtract();
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
                            int maxReceiveItem = ((VEEnergyItem) itemStack.getItem()).getMaxReceive();
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

    public static int receiveEnergy(TileEntity tileEntity, Direction from, int maxReceive){
        return tileEntity.getCapability(CapabilityEnergy.ENERGY, from).map(handler ->
                handler.receiveEnergy(maxReceive, false)).orElse(0);
    }

    private void sendOutPower() {
        energy.ifPresent(energy -> {
            for (Direction dir : Direction.values()){
                TileEntity tileEntity = world.getTileEntity(getPos().offset(dir));
                Direction opposite = dir.getOpposite();
                if(tileEntity != null){
                    // If less energy stored then max transfer send the all the energy stored rather than the max transfer amount
                    int smallest = Math.min(Config.BATTERY_BOX_TRANSFER.get(), energy.getEnergyStored());
                    int received = receiveEnergy(tileEntity, opposite, smallest);
                    ((VEEnergyStorage) energy).consumeEnergy(received);
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
    public void read(BlockState state, CompoundNBT tag){
        CompoundNBT inv = tag.getCompound("inv");
        this.inventory.deserializeNBT(inv);
        //createHandler().deserializeNBT(inv);
        CompoundNBT energyTag = tag.getCompound("energy");
        energy.ifPresent(h -> ((INBTSerializable<CompoundNBT>)h).deserializeNBT(energyTag));

        doDischargeInstead[0] = tag.getBoolean("slot_pair_mode_0");
        doDischargeInstead[1] = tag.getBoolean("slot_pair_mode_1");
        doDischargeInstead[2] = tag.getBoolean("slot_pair_mode_2");
        doDischargeInstead[3] = tag.getBoolean("slot_pair_mode_3");
        doDischargeInstead[4] = tag.getBoolean("slot_pair_mode_4");
        doDischargeInstead[5] = tag.getBoolean("slot_pair_mode_5");

        sendOutPower = tag.getBoolean("send_out_power");

        super.read(state, tag);
    }

    @Override
    public CompoundNBT write(CompoundNBT tag) {
        tag.put("inv", this.inventory.serializeNBT());
        energy.ifPresent(h -> {
            CompoundNBT compound = ((INBTSerializable<CompoundNBT>)h).serializeNBT();
            tag.put("energy",compound);
        });

        tag.putBoolean("slot_pair_mode_0", doDischargeInstead[0]);
        tag.putBoolean("slot_pair_mode_1", doDischargeInstead[1]);
        tag.putBoolean("slot_pair_mode_2", doDischargeInstead[2]);
        tag.putBoolean("slot_pair_mode_3", doDischargeInstead[3]);
        tag.putBoolean("slot_pair_mode_4", doDischargeInstead[4]);
        tag.putBoolean("slot_pair_mode_5", doDischargeInstead[5]);

        tag.putBoolean("send_out_power", sendOutPower);

        return super.write(tag);
    }

    private IEnergyStorage createEnergy(){
        return new VEEnergyStorage(Config.BATTERY_BOX_MAX_POWER.get(),Config.BATTERY_BOX_TRANSFER.get()); // Max Power Storage, Max transfer
    }

    @Override
    public void onDataPacket(final NetworkManager net, final SUpdateTileEntityPacket pkt){
        energy.ifPresent(e -> ((VEEnergyStorage)e).setEnergy(pkt.getNbtCompound().getInt("energy")));
        this.read(this.getBlockState(), pkt.getNbtCompound());
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
                if (side.getIndex() == topManager.getDirection().getIndex() && topManager.getStatus()){
                    return topIsIngress ? topHandler.cast() : bottomHandler.cast();
                }
                if (side.getIndex() == bottomManager.getDirection().getIndex() && bottomManager.getStatus()){
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
    public ITextComponent getDisplayName(){
        return new StringTextComponent(getType().getRegistryName().getPath());
    }

    @Nullable
    @Override
    public Container createMenu(int i, @Nonnull PlayerInventory playerInventory, @Nonnull PlayerEntity playerEntity) {
        return new BatteryBoxContainer(i,world,pos,playerInventory,playerEntity);
    }

    public void updateSlotPair(boolean mode, int pairId){
        if(pairId <6) doDischargeInstead[pairId] = mode;
    }

    public void updateSendOutPower(boolean sendOutPower){this.sendOutPower = sendOutPower;};

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
        if(world == null || getWorld() == null) return;
        if(getWorld().getServer() != null) {
            this.playerUuid.forEach(u -> {
                world.getServer().getPlayerList().getPlayers().forEach(s -> {
                    if (s.getUniqueID().equals(u)){
                        // Boolean Buttons
                        VENetwork.channel.send(PacketDistributor.PLAYER.with(() -> s), new BoolButtonPacket(topManager.getStatus(), topManager.getSlotNum()));
                        VENetwork.channel.send(PacketDistributor.PLAYER.with(() -> s), new BoolButtonPacket(bottomManager.getStatus(), bottomManager.getSlotNum()));

                        // Direction Buttons
                        VENetwork.channel.send(PacketDistributor.PLAYER.with(() -> s), new DirectionButtonPacket(topManager.getDirection().getIndex(), topManager.getSlotNum()));
                        VENetwork.channel.send(PacketDistributor.PLAYER.with(() -> s), new DirectionButtonPacket(bottomManager.getDirection().getIndex(), bottomManager.getSlotNum()));

                        // Slot status
                        for (int i = 0; i < 6; i++) {
                            VENetwork.channel.send(PacketDistributor.PLAYER.with(() -> s), new BatteryBoxSlotPairPacket(doDischargeInstead[i], i));
                        }

                        // Send Out Power
                        VENetwork.channel.send(PacketDistributor.PLAYER.with(() -> s), new BatteryBoxSendOutPowerPacket(this.sendOutPower));
                    }
                });
            });
        } else if (!playerUuid.isEmpty()){ // Legacy solution
            double x = this.getPos().getX();
            double y = this.getPos().getY();
            double z = this.getPos().getZ();
            final double radius = 16;
            RegistryKey<World> worldRegistryKey = this.getWorld().getDimensionKey();
            PacketDistributor.TargetPoint targetPoint = new PacketDistributor.TargetPoint(x,y,z,radius,worldRegistryKey);

            // Boolean Buttons
            VENetwork.channel.send(PacketDistributor.NEAR.with(() -> targetPoint), new BoolButtonPacket(topManager.getStatus(), topManager.getSlotNum()));
            VENetwork.channel.send(PacketDistributor.NEAR.with(() -> targetPoint), new BoolButtonPacket(bottomManager.getStatus(), bottomManager.getSlotNum()));

            // Direction Buttons
            VENetwork.channel.send(PacketDistributor.NEAR.with(() -> targetPoint), new DirectionButtonPacket(topManager.getDirection().getIndex(), topManager.getSlotNum()));
            VENetwork.channel.send(PacketDistributor.NEAR.with(() -> targetPoint), new DirectionButtonPacket(bottomManager.getDirection().getIndex(), bottomManager.getSlotNum()));

            // Slot status
            for (int i = 0; i < 6; i++) {
                VENetwork.channel.send(PacketDistributor.NEAR.with(() -> targetPoint), new BatteryBoxSlotPairPacket(doDischargeInstead[i], i));
            }

            // Send out Power
            VENetwork.channel.send(PacketDistributor.NEAR.with(() -> targetPoint), new BatteryBoxSendOutPowerPacket(this.sendOutPower));
        }
    }

    @Override
    protected void uuidCleanup(){
        if(playerUuid.isEmpty() || world == null) return;
        if(world.getServer() == null) return;

        if(cleanupTick == 20){
            ArrayList<UUID> toRemove = new ArrayList<>();
            world.getServer().getPlayerList().getPlayers().forEach(player ->{
                if(player.openContainer != null){
                    if(!(player.openContainer instanceof BatteryBoxContainer)){
                        toRemove.add(player.getUniqueID());
                    }
                } else if (player.openContainer == null){
                    toRemove.add(player.getUniqueID());
                }
            });
            toRemove.forEach(uuid -> playerUuid.remove(uuid));
        }
        super.uuidCleanup();
    }
}
