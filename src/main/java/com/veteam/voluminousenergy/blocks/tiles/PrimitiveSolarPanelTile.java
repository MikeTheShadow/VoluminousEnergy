package com.veteam.voluminousenergy.blocks.tiles;


import com.veteam.voluminousenergy.blocks.blocks.VEBlocks;
import com.veteam.voluminousenergy.blocks.containers.PrimitiveSolarPanelContainer;
import com.veteam.voluminousenergy.tools.Config;
import com.veteam.voluminousenergy.tools.energy.VEEnergyStorage;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.UUID;

public class PrimitiveSolarPanelTile extends VESolarTile implements MenuProvider {

    private LazyOptional<IEnergyStorage> energy = LazyOptional.of(this::createEnergy);
    private int generation;

    public PrimitiveSolarPanelTile(BlockPos pos, BlockState state) {
        super(VEBlocks.PRIMITIVE_SOLAR_PANEL_TILE, pos, state);
    }

    @Deprecated
    public PrimitiveSolarPanelTile(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(VEBlocks.PRIMITIVE_SOLAR_PANEL_TILE, pos, state);
    }

    @Override
    public void tick() {
        updateClients();
        if (this.level != null){
            if (level.dimensionType().hasSkyLight() && isClear()) {
                this.generation = (int)(Config.PRIMITIVE_SOLAR_PANEL_GENERATE.get()*this.solarIntensity());
                generateEnergy(this.generation);
                setChanged();
            }
        }
        sendOutPower();
    }

    private void generateEnergy(int fe){
        if (this.getCapability(CapabilityEnergy.ENERGY).map(IEnergyStorage::getEnergyStored).orElse(0) < Config.PRIMITIVE_SOLAR_PANEL_MAX_POWER.get()){
            energy.ifPresent(e -> ((VEEnergyStorage)e).addEnergy(fe)); //Amount of energy to add per tick
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
                    int smallest = Math.min(Config.PRIMITIVE_SOLAR_PANEL_SEND.get(), energy.getEnergyStored());
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
    public void load(CompoundTag tag) {
        CompoundTag energyTag = tag.getCompound("energy");
        energy.ifPresent(h -> ((INBTSerializable<CompoundTag>)h).deserializeNBT(energyTag));
        super.load(tag);
    }

    @Override
    public CompoundTag save(CompoundTag tag) {
        energy.ifPresent(h -> {
            CompoundTag compound = ((INBTSerializable<CompoundTag>)h).serializeNBT();
            tag.put("energy",compound);
        });
        return super.save(tag);
    }

    private IEnergyStorage createEnergy(){
        return new VEEnergyStorage(Config.PRIMITIVE_SOLAR_PANEL_MAX_POWER.get(),Config.PRIMITIVE_SOLAR_PANEL_MAX_POWER.get());
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
    public Component getDisplayName(){
        return new TextComponent(getType().getRegistryName().getPath());
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int i, Inventory playerInventory, Player playerEntity){
        return new PrimitiveSolarPanelContainer(i, level, worldPosition, playerInventory, playerEntity);
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
                    if(!(player.containerMenu instanceof PrimitiveSolarPanelContainer)){
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