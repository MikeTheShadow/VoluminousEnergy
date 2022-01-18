package com.veteam.voluminousenergy.blocks.tiles;

import com.veteam.voluminousenergy.blocks.blocks.VEBlocks;
import com.veteam.voluminousenergy.blocks.containers.PumpContainer;
import com.veteam.voluminousenergy.tools.Config;
import com.veteam.voluminousenergy.tools.energy.VEEnergyStorage;
import com.veteam.voluminousenergy.tools.networking.VENetwork;
import com.veteam.voluminousenergy.tools.networking.packets.TankBoolPacket;
import com.veteam.voluminousenergy.tools.networking.packets.TankDirectionPacket;
import com.veteam.voluminousenergy.tools.sidemanager.VESlotManager;
import com.veteam.voluminousenergy.util.IntToDirection;
import com.veteam.voluminousenergy.util.RelationalTank;
import com.veteam.voluminousenergy.util.SlotType;
import com.veteam.voluminousenergy.util.TankType;
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
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.network.PacketDistributor;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.UUID;

public class PumpTile extends VoluminousTileEntity implements MenuProvider {
    private LazyOptional<IItemHandler> handler = LazyOptional.of(() -> this.inventory);
    private LazyOptional<VEEnergyStorage> energy = LazyOptional.of(this::createEnergy);
    private LazyOptional<IFluidHandler> fluid = LazyOptional.of(this::createFluid);

    public VESlotManager slotManager = new VESlotManager(0,Direction.UP,true,"slot.voluminousenergy.input_slot", SlotType.INPUT);

    private final int tankCapacity = 4000;

    // Working data
    private boolean initDone = false;
    private int lX = 0;
    private int lY = 0;
    private int lZ = 0;

    private RelationalTank fluidTank = new RelationalTank(new FluidTank(tankCapacity), 0, null, null, TankType.OUTPUT);
    private Fluid pumpingFluid = Fluids.EMPTY;
    private ItemStackHandler inventory = this.createHandler();

    public PumpTile(BlockPos pos, BlockState state) {
        super(VEBlocks.PUMP_TILE, pos, state);
    }

    @Deprecated
    public PumpTile(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(VEBlocks.PUMP_TILE, pos, state);
    }

    @Override
    public void tick(){
        updateClients();
        handler.ifPresent(h -> {
            ItemStack slotStack = h.getStackInSlot(0).copy();

            // Check item in the slot to see if it's a bucket. If it is--and there is fluid for it--fill it.
            if (slotStack.copy().getItem() != null || slotStack.copy() != ItemStack.EMPTY) { // TODO: Consider 2 slot system like the Combustion Generator/Distillation Unit
                if (slotStack.getItem() == Items.BUCKET && fluidTank.getTank().getFluidAmount() >= 1000 && slotStack.getCount() == 1) {
                    ItemStack bucketStack = new ItemStack(fluidTank.getTank().getFluid().getRawFluid().getBucket(), 1);
                    fluidTank.getTank().drain(1000, IFluidHandler.FluidAction.EXECUTE);
                    h.extractItem(0, 1, false);
                    h.insertItem(0, bucketStack, false);
                }
            }

            if (fluidTank.getTank() != null && (fluidTank.getTank().getFluidAmount() + 1000) <= tankCapacity && this.getCapability(CapabilityEnergy.ENERGY).map(IEnergyStorage::getEnergyStored).orElse(0) > 0){
                fluidPumpMethod();
                setChanged();
            }
        });
    }

    public void addFluidToTank() {
        if ((fluidTank.getTank().getFluidAmount() + 1000) <= tankCapacity) {
            energy.ifPresent(e -> e.consumeEnergy(Config.PUMP_POWER_USAGE.get()));
            fluidTank.getTank().fill(new FluidStack(this.pumpingFluid, 1000), IFluidHandler.FluidAction.EXECUTE);
        }
    }

    @Override
    public void load(CompoundTag tag){
        CompoundTag inv = tag.getCompound("inv");
        handler.ifPresent(h -> ((INBTSerializable<CompoundTag>) h).deserializeNBT(inv));
        createHandler().deserializeNBT(inv);
        energy.ifPresent(h -> h.deserializeNBT(tag));

        CompoundTag airNBT = tag.getCompound("tank");
        fluidTank.getTank().readFromNBT(airNBT);

        pumpingFluid = fluidTank.getTank().getFluid().getRawFluid();

        lX = tag.getInt("lx");
        lY = tag.getInt("ly");
        lZ = tag.getInt("lz");
        initDone = tag.getBoolean("init_done");

        slotManager.read(tag, "slot_manager");
        fluidTank.readGuiProperties(tag, "tank_gui");

        super.load(tag);
    }

    @Override
    public void saveAdditional(CompoundTag tag){
        handler.ifPresent(h -> {
            CompoundTag compound = ((INBTSerializable<CompoundTag>) h).serializeNBT();
            tag.put("inv", compound);
        });
        energy.ifPresent(h -> h.serializeNBT(tag));

        tag.putInt("lx", lX);
        tag.putInt("ly", lY);
        tag.putInt("lz", lZ);
        tag.putBoolean("init_done", initDone);

        // Tanks
        CompoundTag tankNBT = new CompoundTag();
        fluidTank.getTank().writeToNBT(tankNBT);
        tag.put("tank", tankNBT);

        slotManager.write(tag, "slot_manager");
        fluidTank.writeGuiProperties(tag, "tank_gui");
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
    public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket pkt) {
        energy.ifPresent(e -> e.setEnergy(pkt.getTag().getInt("energy")));
        this.load(pkt.getTag());
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
                setChanged();
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

    private VEEnergyStorage createEnergy() {
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
    public Component getDisplayName() {
        return new TextComponent(getType().getRegistryName().getPath());
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int i, @Nonnull Inventory playerInventory, @Nonnull Player playerEntity) {
        return new PumpContainer(i, level, worldPosition, playerInventory, playerEntity);
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
                this.pumpingFluid = this.level.getBlockState(this.getBlockPos().offset(0, -1, 0)).getFluidState().getType();
                initDone = true;
            } catch (Exception e){
                return;
            }
        }

        if (this.pumpingFluid == Fluids.EMPTY || this.pumpingFluid.isSame(Fluids.EMPTY) || this.pumpingFluid == null){ // Sanity check to prevent mass destruction
            initDone = false;
            return;
        }

        if (lX < 22){
            lX++;
            if(this.pumpingFluid.isSame(this.level.getBlockState(this.getBlockPos().offset(lX,lY,lZ)).getFluidState().getType())){
                this.level.setBlockAndUpdate(this.getBlockPos().offset(lX,lY,lZ),Blocks.AIR.defaultBlockState()); // setBlockAndUpdate is the replacement for setBlockState in MCP mappings. This is obvious because of the flag of 3.
                addFluidToTank();
            }

        } else if (lX >= 22 && lZ < 22){
            lZ++;
            lX = -22;
            if(this.pumpingFluid.isSame(this.level.getBlockState(this.getBlockPos().offset(lX,lY,lZ)).getFluidState().getType())){
                this.level.setBlockAndUpdate(this.getBlockPos().offset(lX,lY,lZ),Blocks.AIR.defaultBlockState());
                addFluidToTank();
            }
        } else if (lX >= 22 && lZ >= 22 && this.getBlockPos().offset(0,lY,0).getY() > -63){
            lY--;
            lX = -22;
            lZ = -22;
            if(this.pumpingFluid.isSame(this.level.getBlockState(this.getBlockPos().offset(lX,lY,lZ)).getFluidState().getType())){
                this.level.setBlockAndUpdate(this.getBlockPos().offset(lX,lY,lZ),Blocks.AIR.defaultBlockState());
                addFluidToTank();
            }
        }
    }

    public RelationalTank getTank(){
        return this.fluidTank;
    }

    @Override
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
        if(level == null || getLevel() == null) return;
        if(getLevel().getServer() != null) {
            this.playerUuid.forEach(u -> {
                level.getServer().getPlayerList().getPlayers().forEach(s -> {
                    if (s.getUUID().equals(u)){
                        bulkSendSMPacket(s, slotManager);

                        // Boolean Buttons
                        VENetwork.channel.send(PacketDistributor.PLAYER.with(() -> s), new TankBoolPacket(fluidTank.getSideStatus(), fluidTank.getId()));

                        // Direction Buttons
                        VENetwork.channel.send(PacketDistributor.PLAYER.with(() -> s), new TankDirectionPacket(fluidTank.getSideDirection().get3DDataValue(), fluidTank.getId()));
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
                    if(!(player.containerMenu instanceof PumpContainer)){
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
