package com.veteam.voluminousenergy.blocks.tiles;

import com.veteam.voluminousenergy.VoluminousEnergy;
import com.veteam.voluminousenergy.blocks.blocks.VEBlocks;
import com.veteam.voluminousenergy.blocks.containers.AqueoulizerContainer;
import com.veteam.voluminousenergy.blocks.containers.PumpContainer;
import com.veteam.voluminousenergy.tools.Config;
import com.veteam.voluminousenergy.tools.VEEnergyStorage;
import com.veteam.voluminousenergy.tools.networking.VENetwork;
import com.veteam.voluminousenergy.tools.networking.packets.BoolButtonPacket;
import com.veteam.voluminousenergy.tools.networking.packets.DirectionButtonPacket;
import com.veteam.voluminousenergy.tools.networking.packets.TankBoolPacket;
import com.veteam.voluminousenergy.tools.networking.packets.TankDirectionPacket;
import com.veteam.voluminousenergy.tools.sidemanager.VESlotManager;
import com.veteam.voluminousenergy.util.IntToDirection;
import com.veteam.voluminousenergy.util.RelationalTank;
import com.veteam.voluminousenergy.util.TankType;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.ITickableTileEntity;
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
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import net.minecraftforge.fml.network.PacketDistributor;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.UUID;

public class PumpTile extends VoluminousTileEntity implements ITickableTileEntity, INamedContainerProvider {

    private static final Logger LOGGER = LogManager.getLogger();

    private LazyOptional<IItemHandler> handler = LazyOptional.of(this::createHandler);
    private LazyOptional<IEnergyStorage> energy = LazyOptional.of(this::createEnergy);
    private LazyOptional<IFluidHandler> fluid = LazyOptional.of(this::createFluid);

    public VESlotManager slotManager = new VESlotManager(0,Direction.UP,true,"slot.voluminousenergy.input_slot");

    private final int tankCapacity = 4000;

    // Working data
    private boolean initDone = false;
    private int lX = 0;
    private int lY = 0;
    private int lZ = 0;

    private RelationalTank fluidTank = new RelationalTank(new FluidTank(tankCapacity), 0, null, null, TankType.OUTPUT);
    private Fluid pumpingFluid = Fluids.EMPTY;

    public PumpTile() {
        super(VEBlocks.PUMP_TILE);
    }

    @Override
    public void tick(){
        updateClients();
        handler.ifPresent(h -> {
            ItemStack slotStack = h.getStackInSlot(0).copy();

            // Check item in the slot to see if it's a bucket. If it is--and there is fluid for it--fill it.
            if (slotStack.copy().getItem() != null || slotStack.copy() != ItemStack.EMPTY) { // TODO: Consider 2 slot system like the Combustion Generator/Distillation Unit
                if (slotStack.getItem() == Items.BUCKET && fluidTank.getTank().getFluidAmount() >= 1000 && slotStack.getCount() == 1) {
                    ItemStack bucketStack = new ItemStack(fluidTank.getTank().getFluid().getRawFluid().getFilledBucket(), 1);
                    fluidTank.getTank().drain(1000, IFluidHandler.FluidAction.EXECUTE);
                    h.extractItem(0, 1, false);
                    h.insertItem(0, bucketStack, false);
                }
            }

            if (fluidTank.getTank() != null && (fluidTank.getTank().getFluidAmount() + 1000) <= tankCapacity && this.getCapability(CapabilityEnergy.ENERGY).map(IEnergyStorage::getEnergyStored).orElse(0) > 0){
                fluidPumpMethod();
                markDirty();
            }
        });
    }

    public void addFluidToTank() {
        if ((fluidTank.getTank().getFluidAmount() + 1000) <= tankCapacity) {
            energy.ifPresent(e -> ((VEEnergyStorage)e).consumeEnergy(Config.PUMP_POWER_USAGE.get()));
            fluidTank.getTank().fill(new FluidStack(this.pumpingFluid.getFluid(), 1000), IFluidHandler.FluidAction.EXECUTE);
        }
    }

    @Override
    public void read(BlockState state, CompoundNBT tag){
        CompoundNBT inv = tag.getCompound("inv");
        handler.ifPresent(h -> ((INBTSerializable<CompoundNBT>) h).deserializeNBT(inv));
        createHandler().deserializeNBT(inv);
        CompoundNBT energyTag = tag.getCompound("energy");
        energy.ifPresent(h -> ((INBTSerializable<CompoundNBT>) h).deserializeNBT(energyTag));

        CompoundNBT airNBT = tag.getCompound("tank");
        fluidTank.getTank().readFromNBT(airNBT);

        pumpingFluid = fluidTank.getTank().getFluid().getRawFluid();

        lX = tag.getInt("lx");
        lY = tag.getInt("ly");
        lZ = tag.getInt("lz");
        initDone = tag.getBoolean("init_done");

        slotManager.read(tag, "slot_manager");
        fluidTank.readGuiProperties(tag, "tank_gui");

        super.read(state, tag);
    }

    @Override
    public CompoundNBT write(CompoundNBT tag){
        handler.ifPresent(h -> {
            CompoundNBT compound = ((INBTSerializable<CompoundNBT>) h).serializeNBT();
            tag.put("inv", compound);
        });
        energy.ifPresent(h -> {
            CompoundNBT compound = ((INBTSerializable<CompoundNBT>) h).serializeNBT();
            tag.put("energy", compound);
        });

        tag.putInt("lx", lX);
        tag.putInt("ly", lY);
        tag.putInt("lz", lZ);
        tag.putBoolean("init_done", initDone);

        // Tanks
        CompoundNBT tankNBT = new CompoundNBT();
        fluidTank.getTank().writeToNBT(tankNBT);
        tag.put("tank", tankNBT);

        slotManager.write(tag, "slot_manager");
        fluidTank.writeGuiProperties(tag, "tank_gui");

        return super.write(tag);
    }


    @Override
    public CompoundNBT getUpdateTag() {
        return this.write(new CompoundNBT());
    }

    @Nullable
    @Override
    public SUpdateTileEntityPacket getUpdatePacket() {
        return new SUpdateTileEntityPacket(this.pos, 0, this.getUpdateTag());
    }

    @Override
    public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket pkt) {
        energy.ifPresent(e -> ((VEEnergyStorage)e).setEnergy(pkt.getNbtCompound().getInt("energy")));
        this.read(this.getBlockState(), pkt.getNbtCompound());
        super.onDataPacket(net, pkt);
    }

    private IFluidHandler createFluid() {
        return new IFluidHandler() {
            @Override
            public int getTanks() {
                return 1;
            }

            @Nonnull
            @Override
            public FluidStack getFluidInTank(int tank) {
                return fluidTank == null ? FluidStack.EMPTY : fluidTank.getTank().getFluid();
            }

            @Override
            public int getTankCapacity(int tank) {
                return fluidTank.getTank() == null ? 0 : fluidTank.getTank().getCapacity();
            }

            @Override
            public boolean isFluidValid(int tank, @Nonnull FluidStack stack) {
                return fluidTank != null && fluidTank.getTank().isFluidValid(stack);
            }

            @Override
            public int fill(FluidStack resource, FluidAction action) {
                if (isFluidValid(0, resource) && fluidTank.getTank().isEmpty() || resource.isFluidEqual(fluidTank.getTank().getFluid())) {
                    return fluidTank.getTank().fill(resource.copy(), action);
                }
                return 0;
            }

            @Nonnull
            @Override
            public FluidStack drain(FluidStack resource, FluidAction action) {
                if (resource.isEmpty()) {
                    return FluidStack.EMPTY;
                }
                if (resource.isFluidEqual(fluidTank.getTank().getFluid())) {
                    return fluidTank.getTank().drain(resource.copy(), action);
                }
                return FluidStack.EMPTY;
            }

            @Nonnull
            @Override
            public FluidStack drain(int maxDrain, FluidAction action) {
                if (fluidTank.getTank().getFluidAmount() > 0) {
                    return fluidTank.getTank().drain(maxDrain, action);
                }
                return FluidStack.EMPTY;
            }
        };
    }

    private ItemStackHandler createHandler() {
        return new ItemStackHandler(1) {
            @Override
            protected void onContentsChanged(int slot) {
                markDirty();
            }

            @Override
            public boolean isItemValid(int slot, @Nonnull ItemStack stack) { //IS ITEM VALID PLEASE DO THIS PER SLOT TO SAVE DEBUG HOURS!!!!
                return true;
            }

            @Nonnull
            @Override
            public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
                return super.insertItem(slot, stack, simulate);
            }
        };
    }

    private IEnergyStorage createEnergy() {
        return new VEEnergyStorage(Config.PUMP_MAX_POWER.get(), Config.PUMP_TRANSFER.get());
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
        if (cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
            return handler.cast();
        }
        if (cap == CapabilityEnergy.ENERGY) {
            return energy.cast();
        }
        if (cap == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY){
            return fluid.cast();
        }
        return super.getCapability(cap, side);
    }

    @Override
    public ITextComponent getDisplayName() {
        return new StringTextComponent(getType().getRegistryName().getPath());
    }

    @Nullable
    @Override
    public Container createMenu(int i, @Nonnull PlayerInventory playerInventory, @Nonnull PlayerEntity playerEntity) {
        return new PumpContainer(i, world, pos, playerInventory, playerEntity);
    }

    public FluidStack getAirTankFluid(){
        return this.fluidTank.getTank().getFluid();
    }

    public int getTankCapacity(){
        return tankCapacity;
    }

    public void fluidPumpMethod() {
        if (!(initDone)) {
            lX = -22;
            lY = -1;
            lZ = -22;

            try{
                this.pumpingFluid = this.world.getBlockState(this.getPos().add(0, -1, 0)).getFluidState().getFluid();
                initDone = true;
            } catch (Exception e){
                return;
            }
        }

        if (this.pumpingFluid == Fluids.EMPTY || this.pumpingFluid.isEquivalentTo(Fluids.EMPTY) || this.pumpingFluid == null){ // Sanity check to prevent mass destruction
            initDone = false;
            return;
        }

        if (lX < 22){
            lX++;
            if(this.pumpingFluid.isEquivalentTo(this.world.getBlockState(this.getPos().add(lX,lY,lZ)).getFluidState().getFluid())){
                this.world.setBlockState(this.getPos().add(lX,lY,lZ),Blocks.AIR.getDefaultState());
                addFluidToTank();
            }

        } else if (lX >= 22 && lZ < 22){
            lZ++;
            lX = -22;
            if(this.pumpingFluid.isEquivalentTo(this.world.getBlockState(this.getPos().add(lX,lY,lZ)).getFluidState().getFluid())){
                this.world.setBlockState(this.getPos().add(lX,lY,lZ),Blocks.AIR.getDefaultState());
                addFluidToTank();
            }
        } else if (lX >= 22 && lZ >= 22 && this.getPos().add(0,lY,0).getY() > 1){
            lY--;
            lX = -22;
            lZ = -22;
            if(this.pumpingFluid.isEquivalentTo(this.world.getBlockState(this.getPos().add(lX,lY,lZ)).getFluidState().getFluid())){
                this.world.setBlockState(this.getPos().add(lX,lY,lZ),Blocks.AIR.getDefaultState());
                addFluidToTank();
            }
        }
    }

    public RelationalTank getTank(){
        return this.fluidTank;
    }

    public void updatePacketFromGui(boolean status, int slotId){
        if(slotId == slotManager.getSlotNum()) slotManager.setStatus(status);
    }

    public void updatePacketFromGui(int direction, int slotId){
        if(slotId == slotManager.getSlotNum()) slotManager.setDirection(direction);
    }

    public void updateTankPacketFromGui(boolean status, int id){
        if(id == this.fluidTank.getId()) this.fluidTank.setSideStatus(status);
    }

    public void updateTankPacketFromGui(int direction, int id){
        if(id == this.fluidTank.getId()) this.fluidTank.setSideDirection(IntToDirection.IntegerToDirection(direction));
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
                        VENetwork.channel.send(PacketDistributor.PLAYER.with(() -> s), new TankBoolPacket(fluidTank.getSideStatus(), fluidTank.getId()));

                        // Direction Buttons
                        VENetwork.channel.send(PacketDistributor.PLAYER.with(() -> s), new DirectionButtonPacket(slotManager.getDirection().getIndex(),slotManager.getSlotNum()));
                        VENetwork.channel.send(PacketDistributor.PLAYER.with(() -> s), new TankDirectionPacket(fluidTank.getSideDirection().getIndex(), fluidTank.getId()));
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
            VENetwork.channel.send(PacketDistributor.NEAR.with(() -> targetPoint), new TankBoolPacket(fluidTank.getSideStatus(), fluidTank.getId()));

            // Direction Buttons
            VENetwork.channel.send(PacketDistributor.NEAR.with(() -> targetPoint), new DirectionButtonPacket(slotManager.getDirection().getIndex(),slotManager.getSlotNum()));
            VENetwork.channel.send(PacketDistributor.NEAR.with(() -> targetPoint), new TankDirectionPacket(fluidTank.getSideDirection().getIndex(), fluidTank.getId()));
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
                    if(!(player.openContainer instanceof PumpContainer)){
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
