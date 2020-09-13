package com.veteam.voluminousenergy.blocks.tiles;

import com.veteam.voluminousenergy.blocks.blocks.VEBlocks;
import com.veteam.voluminousenergy.blocks.containers.AirCompressorContainer;
import com.veteam.voluminousenergy.blocks.containers.CombustionGeneratorContainer;
import com.veteam.voluminousenergy.fluids.VEFluids;
import com.veteam.voluminousenergy.items.VEItems;
import com.veteam.voluminousenergy.recipe.CombustionGenerator.CombustionGeneratorFuelRecipe;
import com.veteam.voluminousenergy.recipe.CombustionGenerator.CombustionGeneratorOxidizerRecipe;
import com.veteam.voluminousenergy.tools.Config;
import com.veteam.voluminousenergy.tools.VEEnergyStorage;
import jdk.nashorn.internal.ir.BlockStatement;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.IFluidState;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.common.capabilities.Capability;
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

public class AirCompressorTile extends TileEntity implements ITickableTileEntity, INamedContainerProvider {

    private static final Logger LOGGER = LogManager.getLogger();

    private LazyOptional<IItemHandler> handler = LazyOptional.of(this::createHandler);
    private LazyOptional<IEnergyStorage> energy = LazyOptional.of(this::createEnergy);
    private LazyOptional<IFluidHandler> fluid = LazyOptional.of(this::createFluid);

    private final int tankCapacity = 4000;
    private int counter;
    private int length;
    private int energyRate;

    private FluidTank airTank = new FluidTank(tankCapacity);

    public AirCompressorTile() {
        super(VEBlocks.AIR_COMPRESSOR_TILE);
    }

    @Override
    public void tick(){
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

            // TODO: Check power
            if (airTank != null && (airTank.getFluidAmount() + 250) <= tankCapacity){
                LOGGER.debug("airTank amount: " + airTank.getFluidAmount());
                // Check blocks around the Air Compressor to see if it's air
                for (final BlockPos blockPos : BlockPos.getAllInBoxMutable(pos.add(-1,-1,-1),pos.add(1,1,1))){
                    final BlockState blockState = world.getBlockState(blockPos);
                    LOGGER.debug("Found block: " + blockState.getBlock());
                    if (blockState.getBlock() == Blocks.AIR && (airTank.getFluidAmount() + 250) <= tankCapacity){ // If air, fill by 250 mB
                        LOGGER.debug("Adding 250 mB of compressed air!");
                        airTank.fill(new FluidStack(VEFluids.COMPRESSED_AIR_REG.get(), 250), IFluidHandler.FluidAction.EXECUTE);
                    }
                }
            }
        });
    }

    public static int recieveEnergy(TileEntity tileEntity, Direction from, int maxReceive){
        return tileEntity.getCapability(CapabilityEnergy.ENERGY, from).map(handler ->
                handler.receiveEnergy(maxReceive, false)).orElse(0);
    }

    @Override
    public void read(CompoundNBT tag){

    }

    @Override
    public CompoundNBT write(CompoundNBT tag){
        return tag;
    }

    // TODO: Fluid handler
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
        return new ItemStackHandler(4) {
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
            public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) { //ALSO DO THIS PER SLOT BASIS TO SAVE DEBUG HOURS!!!
                return super.insertItem(slot, stack, simulate);
            }
        };
    }

    private IEnergyStorage createEnergy() {
        return new VEEnergyStorage(Config.COMBUSTION_GENERATOR_MAX_POWER.get(), Config.COMBUSTION_GENERATOR_SEND.get()); //TODO: Configs
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

}
