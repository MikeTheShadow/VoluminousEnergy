package com.veteam.voluminousenergy.blocks.tiles;


import com.veteam.voluminousenergy.blocks.blocks.VEBlocks;
import com.veteam.voluminousenergy.blocks.containers.SolarPanelContainer;
import com.veteam.voluminousenergy.tools.Config;
import com.veteam.voluminousenergy.tools.energy.VEEnergyStorage;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.UUID;

public class SolarPanelTile extends VESolarTile implements ITickableTileEntity, INamedContainerProvider {

    private LazyOptional<VEEnergyStorage> energy = LazyOptional.of(this::createEnergy);
    private int generation;

    public SolarPanelTile() { super(VEBlocks.SOLAR_PANEL_TILE); }

    @Override
    public void tick() {
        updateClients();
        if (this.level != null){
            if (level.dimensionType().hasSkyLight() && isClear()) {
                this.generation = (int)(Config.SOLAR_PANEL_GENERATE.get()*this.solarIntensity());
                generateEnergy(this.generation);
                setChanged();
            }
        }
        sendOutPower();
    }

    private void generateEnergy(int fe){
        if (this.getCapability(CapabilityEnergy.ENERGY).map(IEnergyStorage::getEnergyStored).orElse(0) < Config.SOLAR_PANEL_MAX_POWER.get()){
            energy.ifPresent(e -> ((VEEnergyStorage)e).addEnergy(fe)); //Amount of energy to add per tick
        }
    }

    public static int receiveEnergy(TileEntity tileEntity, Direction from, int maxReceive){
        return tileEntity.getCapability(CapabilityEnergy.ENERGY, from).map(handler ->
                handler.receiveEnergy(maxReceive, false)).orElse(0);
    }

    private void sendOutPower() {
        energy.ifPresent(energy -> {
            for (Direction dir : Direction.values()){
                TileEntity tileEntity = level.getBlockEntity(getBlockPos().relative(dir));
                Direction opposite = dir.getOpposite();
                if(tileEntity != null){
                    // If less energy stored then max transfer send the all the energy stored rather than the max transfer amount
                    int smallest = Math.min(Config.SOLAR_PANEL_SEND.get(), energy.getEnergyStored());
                    int received = receiveEnergy(tileEntity, opposite, smallest);
                    ((VEEnergyStorage) energy).consumeEnergy(received);
                    if (energy.getEnergyStored() <=0){
                        break;
                    }
                }
            }
        });
    }

    @Override
    public void load(BlockState state, CompoundNBT tag) {
        CompoundNBT energyTag = tag.getCompound("energy");
        energy.ifPresent(h -> h.deserializeNBT(energyTag));
        super.load(state, tag);
    }

    @Override
    public CompoundNBT save(CompoundNBT tag) {
        energy.ifPresent(h -> h.serializeNBT(tag));
        return super.save(tag);
    }

    private VEEnergyStorage createEnergy(){
        return new VEEnergyStorage(Config.SOLAR_PANEL_MAX_POWER.get(),Config.SOLAR_PANEL_MAX_POWER.get()); // Config
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
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
        return new SolarPanelContainer(i, level, worldPosition, playerInventory, playerEntity);
    }

    public int getGeneration(){
        return this.generation;
    }

    @Override
    protected void uuidCleanup(){
        if(playerUuid.isEmpty() || level == null) return;
        if(level.getServer() == null) return;

        if(cleanupTick == 20){
            ArrayList<UUID> toRemove = new ArrayList<>();
            level.getServer().getPlayerList().getPlayers().forEach(player ->{
                if(player.containerMenu != null){
                    if(!(player.containerMenu instanceof SolarPanelContainer)){
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