package com.veteam.voluminousenergy.blocks.tiles;


import com.veteam.voluminousenergy.blocks.blocks.VEBlocks;
import com.veteam.voluminousenergy.blocks.containers.PrimitiveBlastFurnaceContainer;
import com.veteam.voluminousenergy.blocks.containers.PrimitiveStirlingGeneratorContainer;
import com.veteam.voluminousenergy.items.VEItems;
import com.veteam.voluminousenergy.tools.Config;
import com.veteam.voluminousenergy.tools.VEEnergyStorage;
import com.veteam.voluminousenergy.tools.networking.VENetwork;
import com.veteam.voluminousenergy.tools.networking.packets.BoolButtonPacket;
import com.veteam.voluminousenergy.tools.networking.packets.DirectionButtonPacket;
import com.veteam.voluminousenergy.tools.sidemanager.VESlotManager;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
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
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.UUID;

public class PrimitiveStirlingGeneratorTile extends VoluminousTileEntity implements ITickableTileEntity, INamedContainerProvider {

    private LazyOptional<IItemHandler> handler = LazyOptional.of(this::createHandler);
    private LazyOptional<IEnergyStorage> energy = LazyOptional.of(this::createEnergy);

    public VESlotManager slotManager = new VESlotManager(0,Direction.UP,true,"slot.voluminousenergy.input_slot");

    private int counter;
    private int length;

    public PrimitiveStirlingGeneratorTile() { super(VEBlocks.PRIMITIVE_STIRLING_GENERATOR_TILE); }

    @Override
    public void tick() {
        updateClients();
        if (counter > 0){
            counter--;
            if (this.getCapability(CapabilityEnergy.ENERGY).map(IEnergyStorage::getEnergyStored).orElse(0) < Config.PRIMITIVE_STIRLING_GENERATOR_MAX_POWER.get()){
                energy.ifPresent(e -> ((VEEnergyStorage)e).addEnergy(Config.PRIMITIVE_STIRLING_GENERATOR_GENERATE.get())); //Amount of energy to add per tick
            }
            markDirty();
        } else {
            handler.ifPresent(h -> {
                ItemStack stack = h.getStackInSlot(0);
                if (stack.getItem() == Items.COAL || stack.getItem() == Items.COAL_BLOCK || stack.getItem() == VEItems.COALCOKE || stack.getItem() == VEItems.PETCOKE || stack.getItem() == Items.CHARCOAL) { //TODO: Change it to allow JSON recipes (tags) instead of static
                    h.extractItem(0, 1, false);
                    //counter = Config.PRIMITIVE_STIRLING_GENERATOR_TICKS.get();
                    if (stack.getItem() == Items.COAL || stack.getItem() == Items.CHARCOAL){
                        counter = 1600;
                    } else if (stack.getItem() == Items.COAL_BLOCK){
                        counter = 16000;
                    } else if (stack.getItem() == VEItems.COALCOKE){
                        counter = 3200;
                    } else if (stack.getItem() == VEItems.PETCOKE){
                        counter = 4000;
                    }
                    length = counter;
                    markDirty();
                }
            });
        }

        sendOutPower();
    }

    public static int recieveEnergy(TileEntity tileEntity, Direction from, int maxReceive){
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
                    int smallest = Math.min(Config.PRIMITIVE_STIRLING_GENERATOR_SEND.get(), energy.getEnergyStored());
                    int received = recieveEnergy(tileEntity, opposite, smallest);
                    ((VEEnergyStorage) energy).consumeEnergy(received);
                    if (energy.getEnergyStored() <=0){
                        break;
                    }
                }
            }
        });
    }

    @Override
    public void read(BlockState state, CompoundNBT tag) {
        CompoundNBT invTag = tag.getCompound("inv");
        handler.ifPresent(h -> ((INBTSerializable<CompoundNBT>)h).deserializeNBT(invTag));
        CompoundNBT energyTag = tag.getCompound("energy");
        energy.ifPresent(h -> ((INBTSerializable<CompoundNBT>)h).deserializeNBT(energyTag));
        slotManager.read(tag, "slot_manager");
        super.read(state, tag);
    }

    @Override
    public CompoundNBT write(CompoundNBT tag) {
        handler.ifPresent(h -> {
            CompoundNBT compound = ((INBTSerializable<CompoundNBT>)h).serializeNBT();
            tag.put("inv",compound);
        });
        energy.ifPresent(h -> {
            CompoundNBT compound = ((INBTSerializable<CompoundNBT>)h).serializeNBT();
            tag.put("energy",compound);
        });
        slotManager.write(tag, "slot_manager");
        return super.write(tag);
    }

    private ItemStackHandler createHandler() {
        return new ItemStackHandler(1) {
            @Override
            protected void onContentsChanged(int slot){
                markDirty();
            }

            @Override
            public boolean isItemValid(int slot, @Nonnull ItemStack stack) {
                if (stack.getItem() == Items.COAL || stack.getItem() == Items.COAL_BLOCK || stack.getItem() == Items.CHARCOAL || stack.getItem() == VEItems.COALCOKE || stack.getItem() == VEItems.PETCOKE){
                    return true;
                } else {
                    return false;
                }
            }

            @Nonnull
            @Override
            public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate)
            {
                if (stack.getItem() == Items.COAL){
                    return super.insertItem(slot, stack, simulate);
                } else if (stack.getItem() == Items.COAL_BLOCK){
                    return super.insertItem(slot, stack, simulate);
                } else if (stack.getItem() == VEItems.COALCOKE){
                    return super.insertItem(slot, stack, simulate);
                } else if (stack.getItem() == VEItems.PETCOKE){
                    return super.insertItem(slot, stack, simulate);
                } else if (stack.getItem() == Items.CHARCOAL){
                    return super.insertItem(slot, stack, simulate);
                } else {
                    return stack;
                }
            }
        };
    }

    private IEnergyStorage createEnergy(){
        return new VEEnergyStorage(Config.PRIMITIVE_STIRLING_GENERATOR_MAX_POWER.get(),Config.PRIMITIVE_STIRLING_GENERATOR_MAX_POWER.get());
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
        if(cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
            if(side == null) return handler.cast();
            if(slotManager.getStatus() && slotManager.getDirection().getIndex() == side.getIndex())
                return handler.cast();
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
    public Container createMenu(int i, PlayerInventory playerInventory, PlayerEntity playerEntity){
        return new PrimitiveStirlingGeneratorContainer(i, world, pos, playerInventory, playerEntity);
    }


    public int progressCounterPX(int px){
        if (counter == 0){
            return 0;
        } else {
            return (px*(((counter*100)/length)))/100;
        }
    }

    public int progressCounterPercent(){
        if (length != 0){
            return (int)(100-(((float)counter/(float)length)*100));
        } else {
            return 0;
        }
    }


    public int ticksLeft(){
        return counter;
    }

    public int getEnergyRate(){
        return 40;
    }

    public void updatePacketFromGui(boolean status, int slotId){
        if(slotId == slotManager.getSlotNum()) slotManager.setStatus(status);
    }

    public void updatePacketFromGui(int direction, int slotId){
        if(slotId == slotManager.getSlotNum()) slotManager.setDirection(direction);
    }

    @Override
    public void sendPacketToClient(){
        if(world == null || getWorld() == null) return;
        if(getWorld().getServer() != null) {
            this.playerUuid.forEach(u -> {
                world.getServer().getPlayerList().getPlayers().forEach(s -> {
                    if (s.getUniqueID().equals(u)){
                        // Boolean Buttons
                        VENetwork.channel.send(PacketDistributor.PLAYER.with(() -> s), new BoolButtonPacket(slotManager.getStatus(), slotManager.getSlotNum()));


                        // Direction Buttons
                        VENetwork.channel.send(PacketDistributor.PLAYER.with(() -> s), new DirectionButtonPacket(slotManager.getDirection().getIndex(),slotManager.getSlotNum()));
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
            VENetwork.channel.send(PacketDistributor.NEAR.with(() -> targetPoint), new BoolButtonPacket(slotManager.getStatus(), slotManager.getSlotNum()));

            // Direction Buttons
            VENetwork.channel.send(PacketDistributor.NEAR.with(() -> targetPoint), new DirectionButtonPacket(slotManager.getDirection().getIndex(),slotManager.getSlotNum()));
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
                    if(!(player.openContainer instanceof PrimitiveBlastFurnaceContainer)){
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