package com.veteam.voluminousenergy.blocks.tiles;

import com.veteam.voluminousenergy.blocks.blocks.VEBlocks;
import com.veteam.voluminousenergy.blocks.containers.CombustionGeneratorContainer;
import com.veteam.voluminousenergy.recipe.CombustionGenerator.CombustionGeneratorFuelRecipe;
import com.veteam.voluminousenergy.recipe.CombustionGenerator.CombustionGeneratorOxidizerRecipe;
import com.veteam.voluminousenergy.recipe.VEFluidRecipe;
import com.veteam.voluminousenergy.tools.Config;
import com.veteam.voluminousenergy.tools.VEEnergyStorage;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.fluid.Fluid;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.BucketItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
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

public class CombustionGeneratorTile extends VoluminousTileEntity implements ITickableTileEntity, INamedContainerProvider {
    private LazyOptional<IItemHandler> handler = LazyOptional.of(this::createHandler);
    private LazyOptional<IEnergyStorage> energy = LazyOptional.of(this::createEnergy);
    private LazyOptional<IFluidHandler> fluid = LazyOptional.of(this::createFluid);

    private int tankCapacity = 4000;

    private FluidTank oxidizerTank = new FluidTank(tankCapacity);
    private FluidTank fuelTank = new FluidTank(tankCapacity);

    private int counter;
    private int length;
    private int energyRate;

    private static final Logger LOGGER = LogManager.getLogger();

    public CombustionGeneratorTile() {
        super(VEBlocks.COMBUSTION_GENERATOR_TILE);
    }

    @Override
    public void tick() {

        updateClients();

        handler.ifPresent(h -> {
            ItemStack oxidizerInput = h.getStackInSlot(0).copy();
            ItemStack oxidizerOutput = h.getStackInSlot(1).copy();
            ItemStack fuelInput = h.getStackInSlot(2).copy();
            ItemStack fuelOutput = h.getStackInSlot(3).copy();
            /*
             *  Manipulate tanks based on input from buckets in slots
             */

            // Input fluid into the oxidizer tank
            if (oxidizerInput.copy() != null || oxidizerInput.copy() != ItemStack.EMPTY && oxidizerOutput.copy() == ItemStack.EMPTY) {
                if (oxidizerInput.copy().getItem() instanceof BucketItem && oxidizerInput.getCount() == 1) {
                    Fluid fluid = ((BucketItem) oxidizerInput.copy().getItem()).getFluid();
                    //FluidStack fluidStack = new FluidStack(fluid, 1000);
                    if (oxidizerTank.isEmpty() || oxidizerTank.getFluid().isFluidEqual(new FluidStack(fluid, 1000)) && oxidizerTank.getFluidAmount() + 1000 <= tankCapacity) {
                        oxidizerTank.fill(new FluidStack(fluid, 1000), IFluidHandler.FluidAction.EXECUTE);
                        h.extractItem(0, 1, false);
                        h.insertItem(1, new ItemStack(Items.BUCKET, 1), false);
                    }
                }
            }

            // Extract fluid from the oxidizer tank
            if(oxidizerInput.copy().getItem() == Items.BUCKET && oxidizerOutput.copy() == ItemStack.EMPTY) {
                if(oxidizerTank.getFluidAmount() >= 1000) {
                    ItemStack bucketStack = new ItemStack(oxidizerTank.getFluid().getRawFluid().getFilledBucket(), 1);
                    oxidizerTank.drain(1000, IFluidHandler.FluidAction.EXECUTE);
                    h.extractItem(0, 1, false);
                    h.insertItem(1, bucketStack, false);
                }
            }


            // Input fluid to the fuel tank
            if (fuelInput.copy() != null || fuelInput.copy() != ItemStack.EMPTY && fuelOutput.copy() == ItemStack.EMPTY) {
                if (fuelInput.copy().getItem() instanceof BucketItem && fuelInput.getCount() == 1) {
                    Fluid fluid = ((BucketItem) fuelInput.copy().getItem()).getFluid();
                    //FluidStack fluidStack = new FluidStack(fluid, 1000);
                    if (fuelTank.isEmpty() || fuelTank.getFluid().isFluidEqual(new FluidStack(fluid, 1000)) && fuelTank.getFluidAmount() + 1000 <= tankCapacity) {
                        fuelTank.fill(new FluidStack(fluid, 1000), IFluidHandler.FluidAction.EXECUTE);
                        h.extractItem(2, 1, false);
                        h.insertItem(3, new ItemStack(Items.BUCKET, 1), false);
                    }
                }
            }

            // Extract fluid from the fuel tank
            if(fuelInput.copy().getItem() == Items.BUCKET && fuelOutput.copy() == ItemStack.EMPTY) {
                if(fuelTank.getFluidAmount() >= 1000) {
                    ItemStack bucketStack = new ItemStack(fuelTank.getFluid().getRawFluid().getFilledBucket(), 1);
                    fuelTank.drain(1000, IFluidHandler.FluidAction.EXECUTE);
                    h.extractItem(2, 1, false);
                    h.insertItem(3, bucketStack, false);
                }
            }

            // Main Combustion Generator tick logic
            if (counter > 0) {
                if (this.getCapability(CapabilityEnergy.ENERGY).map(IEnergyStorage::getEnergyStored).orElse(0) + energyRate <= Config.COMBUSTION_GENERATOR_MAX_POWER.get()){
                    counter--;
                    energy.ifPresent(e -> ((VEEnergyStorage)e).addEnergy(energyRate)); //Amount of energy to add per tick
                }
                markDirty();
            } else if ((oxidizerTank != null || !oxidizerTank.isEmpty()) && (fuelTank != null || !fuelTank.isEmpty())){
                ItemStack oxidizerStack = new ItemStack(oxidizerTank.getFluid().getRawFluid().getFilledBucket(),1);
                ItemStack fuelStack = new ItemStack(fuelTank.getFluid().getRawFluid().getFilledBucket(),1);

                CombustionGeneratorOxidizerRecipe oxidizerRecipe = world.getRecipeManager().getRecipe(CombustionGeneratorOxidizerRecipe.RECIPE_TYPE, new Inventory(oxidizerStack), world).orElse(null);
                VEFluidRecipe fuelRecipe = world.getRecipeManager().getRecipe(CombustionGeneratorFuelRecipe.RECIPE_TYPE, new Inventory(fuelStack), world).orElse(null);

                if (oxidizerRecipe != null && fuelRecipe != null){
                    int amount = 250;
                    if (oxidizerTank.getFluidAmount() >= amount && fuelTank.getFluidAmount() >= amount){
                        oxidizerTank.drain(amount, IFluidHandler.FluidAction.EXECUTE);
                        fuelTank.drain(amount, IFluidHandler.FluidAction.EXECUTE);
                        if (Config.COMBUSTION_GENERATOR_BALANCED_MODE.get()){
                            counter = (oxidizerRecipe.getProcessTime())/4;
                        } else {
                            counter = Config.COMBUSTION_GENERATOR_FIXED_TICK_TIME.get()/4;
                        }
                        energyRate = fuelRecipe.getProcessTime()/oxidizerRecipe.getProcessTime(); // Process time in fuel recipe is really volumetric energy
                        length = counter;
                        markDirty();
                    }
                }
            }

            if (counter == 0){
                energyRate = 0;
            }
            sendOutPower();
            // End of item handler
        });
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
                    int smallest = Math.min(Config.COMBUSTION_GENERATOR_SEND.get(), energy.getEnergyStored());
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
    public void read(BlockState state, CompoundNBT tag) {
        CompoundNBT inv = tag.getCompound("inv");
        handler.ifPresent(h -> ((INBTSerializable<CompoundNBT>) h).deserializeNBT(inv));
        createHandler().deserializeNBT(inv);
        CompoundNBT energyTag = tag.getCompound("energy");
        energy.ifPresent(h -> ((INBTSerializable<CompoundNBT>) h).deserializeNBT(energyTag));

        fluid.ifPresent(f -> {
            CompoundNBT oxidizerNBT = tag.getCompound("oxidizerTank");
            CompoundNBT fuelNBT = tag.getCompound("fuelTank");
            oxidizerTank.readFromNBT(oxidizerNBT);
            fuelTank.readFromNBT(fuelNBT);
        });

        counter = tag.getInt("counter");
        length = tag.getInt("length");
        energyRate = tag.getInt("energy_rate");

        super.read(state, tag);
    }

    @Nonnull
    @Override
    public CompoundNBT write(CompoundNBT tag) {
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
            CompoundNBT oxidizerNBT = new CompoundNBT();
            oxidizerTank.writeToNBT(oxidizerNBT);
            tag.put("oxidizerTank", oxidizerNBT);

            CompoundNBT fuelNBT = new CompoundNBT();
            fuelTank.writeToNBT(fuelNBT);
            tag.put("fuelTank", fuelNBT);
        });

        tag.putInt("counter", counter);
        tag.putInt("length", length);
        tag.putInt("energy_rate", energyRate);

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
        this.read(this.getBlockState(), pkt.getNbtCompound());
        super.onDataPacket(net, pkt);
    }

    private IFluidHandler createFluid() {
        return new IFluidHandler() {
            @Override
            public int getTanks() {
                return 2;
            }

            @Nonnull
            @Override
            public FluidStack getFluidInTank(int tank) {
                if (tank == 0) {
                    return oxidizerTank == null ? FluidStack.EMPTY : oxidizerTank.getFluid();
                } else if (tank == 1) {
                    return fuelTank == null ? FluidStack.EMPTY : fuelTank.getFluid();
                }
                LOGGER.debug("Invalid tankId in Combustion Generator Tile for getFluidInTank");
                return FluidStack.EMPTY;
            }
            @Override
            public int getTankCapacity(int tank) {
                if (tank == 0) {
                    return oxidizerTank == null ? 0 : oxidizerTank.getCapacity();
                } else if (tank == 1) {
                    return fuelTank == null ? 0 : fuelTank.getCapacity();
                }
                LOGGER.debug("Invalid tankId in Combustion Generator Tile for getTankCapacity");
                return 0;
            }

            @Override
            public boolean isFluidValid(int tank, @Nonnull FluidStack stack) {
                if (tank == 0) {
                    ItemStack oxidizerStack = new ItemStack(stack.getFluid().getFilledBucket(),1);
                    CombustionGeneratorOxidizerRecipe oxidizerRecipe = world.getRecipeManager().getRecipe(CombustionGeneratorOxidizerRecipe.RECIPE_TYPE, new Inventory(oxidizerStack), world).orElse(null);
                    if (oxidizerRecipe == null){
                        return false;
                    }
                    return oxidizerTank != null && oxidizerTank.isFluidValid(stack);
                } else if (tank == 1) {
                    ItemStack fuelStack = new ItemStack(stack.getFluid().getFilledBucket(),1);
                    VEFluidRecipe fuelRecipe = world.getRecipeManager().getRecipe(CombustionGeneratorFuelRecipe.RECIPE_TYPE, new Inventory(fuelStack), world).orElse(null);
                    if (fuelRecipe == null){
                        return false;
                    }
                    return fuelTank != null && fuelTank.isFluidValid(stack);
                }
                return false;
            }

            @Override
            public int fill(FluidStack resource, FluidAction action) {
                if (isFluidValid(0, resource) && oxidizerTank.isEmpty() || resource.isFluidEqual(oxidizerTank.getFluid())) {
                    return oxidizerTank.fill(resource.copy(), action);
                } else if (isFluidValid(1, resource) && fuelTank.isEmpty() || resource.isFluidEqual(fuelTank.getFluid())) {
                    return fuelTank.fill(resource.copy(), action);
                }
                return 0;
            }

            @Nonnull
            @Override
            public FluidStack drain(FluidStack resource, FluidAction action) {
                if (resource.isEmpty()) {
                    return FluidStack.EMPTY;
                }
                if (resource.isFluidEqual(oxidizerTank.getFluid())) {
                    return oxidizerTank.drain(resource.copy(), action);
                } else if (resource.isFluidEqual(fuelTank.getFluid())) {
                    return fuelTank.drain(resource.copy(), action);
                }
                return FluidStack.EMPTY;
            }

            @Nonnull
            @Override
            public FluidStack drain(int maxDrain, FluidAction action) {
                if (oxidizerTank.getFluidAmount() > 0) {
                    return oxidizerTank.drain(maxDrain, action);
                } else if (fuelTank.getFluidAmount() > 0) {
                    return fuelTank.drain(maxDrain, action);
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
                if(slot == 0 || slot == 1) {
                    CombustionGeneratorOxidizerRecipe recipe = world.getRecipeManager().getRecipe(CombustionGeneratorOxidizerRecipe.RECIPE_TYPE,new Inventory(stack),world).orElse(null);
                    return recipe != null || stack.getItem() == Items.BUCKET;
                } else if(slot == 2 || slot == 3) {
                    VEFluidRecipe recipe = world.getRecipeManager().getRecipe(CombustionGeneratorFuelRecipe.RECIPE_TYPE,new Inventory(stack),world).orElse(null);
                    return recipe != null || stack.getItem() == Items.BUCKET;
                }
                return false;
            }

            @Nonnull
            @Override
            public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) { //ALSO DO THIS PER SLOT BASIS TO SAVE DEBUG HOURS!!!
                return super.insertItem(slot, stack, simulate);
            }
        };
    }

    private IEnergyStorage createEnergy() {
        return new VEEnergyStorage(Config.COMBUSTION_GENERATOR_MAX_POWER.get(), Config.COMBUSTION_GENERATOR_SEND.get());
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
        return new CombustionGeneratorContainer(i, world, pos, playerInventory, playerEntity);
    }

    public int progressCounterPX(int px) {
        if (counter == 0) {
            return 0;
        } else {
            return (px*(((counter*100)/length)))/100;
        }
    }

    public FluidStack getFluidStackFromTank(int num){
        if (num == 0){
            return oxidizerTank.getFluid();
        } else if (num == 1){
            return fuelTank.getFluid();
        }
        return FluidStack.EMPTY;
    }

    public int getTankCapacity(){
        return tankCapacity;
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
        return energyRate;
    }
}
