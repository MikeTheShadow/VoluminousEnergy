package com.veteam.voluminousenergy.blocks.tiles;


import com.veteam.voluminousenergy.blocks.blocks.VEBlocks;
import com.veteam.voluminousenergy.blocks.containers.SolarPanelContainer;
import com.veteam.voluminousenergy.tools.Config;
import com.veteam.voluminousenergy.tools.energy.VEEnergyStorage;
import com.veteam.voluminousenergy.tools.sidemanager.VESlotManager;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class SolarPanelTile extends VESolarTile {

    private final LazyOptional<VEEnergyStorage> energy = LazyOptional.of(this::createEnergy);
    private int generation;

    public SolarPanelTile(BlockPos pos, BlockState state) {
        super(VEBlocks.SOLAR_PANEL_TILE, pos, state);
    }

    @Deprecated
    public SolarPanelTile(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(VEBlocks.SOLAR_PANEL_TILE, pos, state);
    }

    @Override
    public void tick() {
        updateClients();
        if (this.level != null){
            if (level.dimensionType().hasSkyLight() && isClear()) {
                this.generation = this.getGeneration();
                generateEnergy(this.generation);
                setChanged();
            }
        }
        sendOutPower();
    }

    private void generateEnergy(int fe){
        if (this.getCapability(CapabilityEnergy.ENERGY).map(IEnergyStorage::getEnergyStored).orElse(0) < Config.SOLAR_PANEL_MAX_POWER.get()){
            energy.ifPresent(e -> e.addEnergy(fe)); //Amount of energy to add per tick
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
                    int smallest = Math.min(Config.SOLAR_PANEL_SEND.get(), energy.getEnergyStored());
                    int received = receiveEnergy(tileEntity, opposite, smallest);
                    energy.consumeEnergy(received);
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
        energy.ifPresent(h -> h.deserializeNBT(energyTag));
        tag.putInt("generation_rate", this.generation);
        super.load(tag);
    }

    @Override
    public void saveAdditional(@NotNull CompoundTag tag) {
        energy.ifPresent(h -> h.serializeNBT(tag));
        this.generation = tag.getInt("generation_rate");
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

    private @Nonnull VEEnergyStorage createEnergy(){
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

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int i, Inventory playerInventory, Player playerEntity){
        return new SolarPanelContainer(i, level, worldPosition, playerInventory, playerEntity);
    }

    @Nullable
    @Override
    public ItemStackHandler getInventoryHandler() {
        return null;
    }

    @NotNull
    @Override
    public List<VESlotManager> getSlotManagers() {
        return new ArrayList<>();
    }

    @Nullable
    @Override
    public LazyOptional<VEEnergyStorage> getEnergy() {
        return energy;
    }

    public int getGeneration(){
        return (int)(Config.SOLAR_PANEL_GENERATE.get()*this.solarIntensity());
    }
}