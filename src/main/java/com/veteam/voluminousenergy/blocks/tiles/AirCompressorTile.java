package com.veteam.voluminousenergy.blocks.tiles;

import com.veteam.voluminousenergy.blocks.blocks.VEBlocks;
import com.veteam.voluminousenergy.blocks.containers.AirCompressorContainer;
import com.veteam.voluminousenergy.fluids.VEFluids;
import com.veteam.voluminousenergy.tools.Config;
import com.veteam.voluminousenergy.tools.VEEnergyStorage;
import net.minecraft.block.Blocks;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
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
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class AirCompressorTile extends VoluminousTileEntity implements ITickableTileEntity, INamedContainerProvider {

    private static final Logger LOGGER = LogManager.getLogger();

    private LazyOptional<IItemHandler> handler = LazyOptional.of(this::createHandler);
    private LazyOptional<IEnergyStorage> energy = LazyOptional.of(this::createEnergy);
    private LazyOptional<IFluidHandler> fluid = LazyOptional.of(this::createFluid);

    private final int tankCapacity = 4000;
    private byte counter;

    private FluidTank airTank = new FluidTank(tankCapacity);

    public AirCompressorTile() {
        super(VEBlocks.AIR_COMPRESSOR_TILE);
    }

    @Override
    public void tick(){
        updateClients();
        handler.ifPresent(h -> {
            ItemStack slotStack = h.getStackInSlot(0).copy();

            // Check item in the slot to see if it's a bucket. If it is--and there is fluid for it--fill it.
            if (slotStack.copy().getItem() != null || slotStack.copy() != ItemStack.EMPTY) {
                if (slotStack.getItem() == Items.BUCKET && airTank.getFluidAmount() >= 1000 && slotStack.getCount() == 1) {
                    ItemStack bucketStack = new ItemStack(airTank.getFluid().getRawFluid().getFilledBucket(), 1);
                    airTank.drain(1000, IFluidHandler.FluidAction.EXECUTE);
                    h.extractItem(0, 1, false);
                    h.insertItem(0, bucketStack, false);
                }
            }

            if (airTank != null && (airTank.getFluidAmount() + 250) <= tankCapacity && counter == 20 && this.getCapability(CapabilityEnergy.ENERGY).map(IEnergyStorage::getEnergyStored).orElse(0) > 0){
                // Check blocks around the Air Compressor to see if it's air
                int x = this.pos.getX();
                int y = this.pos.getY();
                int z = this.pos.getZ();

                // Sanity check
                if (Blocks.AIR == world.getBlockState(new BlockPos(x,y,z)).getBlock()){
                    addAirToTank();
                }

                // Check X offsets
                if (Blocks.AIR == world.getBlockState(new BlockPos(x+1,y,z)).getBlock()){
                    //LOGGER.debug("HIT! x+1: " + (x+1) + " y: " + y + " z: " + z + " is AIR!");
                    addAirToTank();
                }
                if (Blocks.AIR == world.getBlockState(new BlockPos(x-1,y,z)).getBlock()){
                    //LOGGER.debug("HIT! x-1: " + (x-1) + " y: " + y + " z: " + z + " is AIR!");
                    addAirToTank();
                }

                // Check Y offsets
                if (Blocks.AIR == world.getBlockState(new BlockPos(x,y+1,z)).getBlock()){
                    //LOGGER.debug("HIT! x: " + x + " y+1: " + (y+1) + " z: " + z + " is AIR!");
                    addAirToTank();
                }
                if (Blocks.AIR == world.getBlockState(new BlockPos(x,y-1,z)).getBlock()){
                    //LOGGER.debug("HIT! x: " + x + " y-1: " + (y-1) + " z: " + z + " is AIR!");
                    addAirToTank();
                }

                if (Blocks.AIR == world.getBlockState(new BlockPos(x,y,z+1)).getBlock()){
                    //LOGGER.debug("HIT! x: " + x + " y: " + y + " z+1: " + (z+1) + " is AIR!");
                    addAirToTank();
                }
                if (Blocks.AIR == world.getBlockState(new BlockPos(x,y,z-1)).getBlock()){
                    //LOGGER.debug("HIT! x: " + x + " y: " + y + " z-1: " + (z-1) + " is AIR!");
                    addAirToTank();
                }

            } else if (airTank != null && (airTank.getFluidAmount() + 250) <= tankCapacity && this.getCapability(CapabilityEnergy.ENERGY).map(IEnergyStorage::getEnergyStored).orElse(0) > 0){
                energy.ifPresent(e -> ((VEEnergyStorage)e).consumeEnergy(Config.AIR_COMPRESSOR_POWER_USAGE.get()));
            }
        });
        if(counter == 20) counter = 0;
        else counter++;
    }

    public void addAirToTank() {
        if ((airTank.getFluidAmount() + 250) <= tankCapacity) {
            airTank.fill(new FluidStack(VEFluids.COMPRESSED_AIR_REG.get(), 250), IFluidHandler.FluidAction.EXECUTE);
        }
    }

    @Override
    public void read(CompoundNBT tag){
        CompoundNBT inv = tag.getCompound("inv");
        handler.ifPresent(h -> ((INBTSerializable<CompoundNBT>) h).deserializeNBT(inv));
        createHandler().deserializeNBT(inv);
        CompoundNBT energyTag = tag.getCompound("energy");
        energy.ifPresent(h -> ((INBTSerializable<CompoundNBT>) h).deserializeNBT(energyTag));

        fluid.ifPresent(f -> {
            CompoundNBT airNBT = tag.getCompound("airTank");
            airTank.readFromNBT(airNBT);
        });

        super.read(tag);
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

        // Tanks
        fluid.ifPresent(f -> {
            CompoundNBT tankNBT = new CompoundNBT();
            airTank.writeToNBT(tankNBT);
            tag.put("airTank", tankNBT);
        });

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
        this.read(pkt.getNbtCompound());
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
                return airTank == null ? FluidStack.EMPTY : airTank.getFluid();
            }

            @Override
            public int getTankCapacity(int tank) {
                return airTank == null ? 0 : airTank.getCapacity();
            }

            @Override
            public boolean isFluidValid(int tank, @Nonnull FluidStack stack) {
                return airTank != null && airTank.isFluidValid(stack);
            }

            @Override
            public int fill(FluidStack resource, FluidAction action) {
                if (isFluidValid(0, resource) && airTank.isEmpty() || resource.isFluidEqual(airTank.getFluid())) {
                    return airTank.fill(resource.copy(), action);
                }
                return 0;
            }

            @Nonnull
            @Override
            public FluidStack drain(FluidStack resource, FluidAction action) {
                if (resource.isEmpty()) {
                    return FluidStack.EMPTY;
                }
                if (resource.isFluidEqual(airTank.getFluid())) {
                    return airTank.drain(resource.copy(), action);
                }
                return FluidStack.EMPTY;
            }

            @Nonnull
            @Override
            public FluidStack drain(int maxDrain, FluidAction action) {
                if (airTank.getFluidAmount() > 0) {
                    airTank.drain(maxDrain, action);
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
        return new VEEnergyStorage(Config.AIR_COMPRESSOR_MAX_POWER.get(), Config.AIR_COMPRESSOR_TRANSFER.get());
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
        return new AirCompressorContainer(i, world, pos, playerInventory, playerEntity);
    }

    public FluidStack getAirTankFluid(){
        return this.airTank.getFluid();
    }

    public int getTankCapacity(){
        return tankCapacity;
    }
}
