package com.veteam.voluminousenergy.blocks.tiles;


import com.veteam.voluminousenergy.blocks.blocks.VEBlocks;
import com.veteam.voluminousenergy.blocks.containers.PrimitiveStirlingGeneratorContainer;
import com.veteam.voluminousenergy.items.VEItems;
import com.veteam.voluminousenergy.tools.Config;
import com.veteam.voluminousenergy.tools.VEEnergyStorage;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.Lazy;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.EnergyStorage;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.concurrent.atomic.AtomicInteger;

public class PrimitiveStirlingGeneratorTile extends TileEntity implements ITickableTileEntity, INamedContainerProvider {

    private LazyOptional<IItemHandler> handler = LazyOptional.of(this::createHandler);
    private LazyOptional<IEnergyStorage> energy = LazyOptional.of(this::createEnergy);

    private int counter;

    public PrimitiveStirlingGeneratorTile() {
        super(VEBlocks.PRIMITIVE_STIRLING_GENERATOR_TILE);
    }

    @Override
    public void tick() {
        if (counter > 0){
            counter--;
            if (counter >= 0){
                energy.ifPresent(e -> ((VEEnergyStorage)e).addEnergy(Config.PRIMITIVE_STIRLING_GENERATOR_GENERATE.get())); //Amount of energy to add per tick
            }
            markDirty();
        } else {
            handler.ifPresent(h -> {
                ItemStack stack = h.getStackInSlot(0);
                if (stack.getItem() == Items.COAL || stack.getItem() == Items.COAL_BLOCK || stack.getItem() == VEItems.COALCOKE || stack.getItem() == VEItems.PETCOKE) { //TODO: Change it to allow JSON recipes (tags) instead of static
                    h.extractItem(0, 1, false);
                    //counter = Config.PRIMITIVE_STIRLING_GENERATOR_TICKS.get();
                    if (stack.getItem() == Items.COAL){
                        counter = 1600;
                    } else if (stack.getItem() == Items.COAL_BLOCK){
                        counter = 16000;
                    } else if (stack.getItem() == VEItems.COALCOKE){
                        counter = 3200;
                    } else if (stack.getItem() == VEItems.PETCOKE){
                        counter = 4000;
                    }
                    markDirty();
                }
            });
        }

        sendOutPower();
    }

    private void sendOutPower() {
        energy.ifPresent(energy -> {
            AtomicInteger capacity = new AtomicInteger(energy.getEnergyStored());
            if (capacity.get() > 0){ //If we don't have energy we can't send any out
                for (Direction direction : Direction.values()){
                    TileEntity te = world.getTileEntity(pos.offset(direction));
                    if (te != null){
                        boolean doContinue = te.getCapability(CapabilityEnergy.ENERGY, direction).map(handler -> {
                                    if (handler.canReceive()){
                                        int recieved = handler.receiveEnergy(Math.min(capacity.get(),Config.PRIMITIVE_STIRLING_GENERATOR_SEND.get()),false);
                                        capacity.addAndGet(-recieved);
                                        ((VEEnergyStorage) energy).consumeEnergy(recieved);
                                        markDirty();
                                        return capacity.get() > 0;
                                    } else {
                                        return true;
                                    }
                                }
                        ).orElse(true);
                        if (!doContinue){
                            return;
                        }
                    }
                }
            }
        });
    }

    @Override
    public void read(CompoundNBT tag) {
        CompoundNBT invTag = tag.getCompound("inv");
        handler.ifPresent(h -> ((INBTSerializable<CompoundNBT>)h).deserializeNBT(invTag));
        CompoundNBT energyTag = tag.getCompound("energy");
        energy.ifPresent(h -> ((INBTSerializable<CompoundNBT>)h).deserializeNBT(energyTag));
        super.read(tag);
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
        energy.ifPresent(h -> tag.putInt("energy", h.getEnergyStored()));
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
                return stack.getItem() == Items.COAL;
            }

            @Nonnull
            @Override
            public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate)
            {
                if(stack.getItem() != Items.COAL) {
                    return stack;
                }
                return super.insertItem(slot, stack, simulate);
            }
        };
    }

    private IEnergyStorage createEnergy(){
        return new VEEnergyStorage(Config.PRIMITIVE_STIRLING_GENERATOR_MAX_POWER.get(),0);

    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
        if(cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
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
}