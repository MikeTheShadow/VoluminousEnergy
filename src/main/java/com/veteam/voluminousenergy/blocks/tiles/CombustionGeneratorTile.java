package com.veteam.voluminousenergy.blocks.tiles;

import com.veteam.voluminousenergy.blocks.blocks.VEBlocks;
import com.veteam.voluminousenergy.blocks.containers.CombustionGeneratorContainer;
import com.veteam.voluminousenergy.tools.Config;
import com.veteam.voluminousenergy.tools.VEEnergyStorage;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.fluid.Fluid;
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
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.concurrent.atomic.AtomicReference;

public class CombustionGeneratorTile extends TileEntity implements ITickableTileEntity, INamedContainerProvider {
    private LazyOptional<IItemHandler> handler = LazyOptional.of(this::createHandler);
    private LazyOptional<IEnergyStorage> energy = LazyOptional.of(this::createEnergy);
    private LazyOptional<IFluidHandler> fluid = LazyOptional.of(this::createFluid);

    private int tankCapacity = 4000;

    private FluidTank oxidizerTank = new FluidTank(tankCapacity);
    private FluidTank fuelTank = new FluidTank(tankCapacity);

    private int counter;
    private int length;
    private AtomicReference<ItemStack> oxidizerItemStack = new AtomicReference<ItemStack>(new ItemStack(Items.AIR, 0));
    private AtomicReference<FluidStack> updateOxidizerFluidStack = new AtomicReference<FluidStack>(new FluidStack(FluidStack.EMPTY, 0));

    private AtomicReference<ItemStack> fuelItemStack = new AtomicReference<ItemStack>(new ItemStack(Items.AIR, 0));
    private AtomicReference<FluidStack> updateFuelFluidStack = new AtomicReference<FluidStack>(new FluidStack(FluidStack.EMPTY, 0));

    public AtomicReference<FluidStack> tankOxidizer = new AtomicReference<FluidStack>(new FluidStack(FluidStack.EMPTY, 0));
    public AtomicReference<FluidStack> tankFuel = new AtomicReference<FluidStack>(new FluidStack(FluidStack.EMPTY, 0));

    private static final Logger LOGGER = LogManager.getLogger();


    public CombustionGeneratorTile() {
        super(VEBlocks.COMBUSTION_GENERATOR_TILE);
    }

    @Override
    public void tick() {
        handler.ifPresent(h -> {
            ItemStack oxidizerInput = h.getStackInSlot(0).copy();
            ItemStack oxidizerOutput = h.getStackInSlot(1).copy();
            ItemStack fuelInput = h.getStackInSlot(2).copy();
            ItemStack fuelOutput = h.getStackInSlot(3).copy();


            oxidizerItemStack.set(oxidizerInput.copy()); // Atomic Reference, use this to query recipes
            fluid.ifPresent(f -> {
                // Input fluid into the oxidizer tank
                if (oxidizerInput.copy() != null || oxidizerInput.copy() != ItemStack.EMPTY) {
                    if (oxidizerInput.copy().getItem() instanceof BucketItem && oxidizerInput.getCount() == 1) {
                        Fluid fluid = ((BucketItem) oxidizerInput.copy().getItem()).getFluid();
                        //FluidStack fluidStack = new FluidStack(fluid, 1000);
                        if (oxidizerTank.isEmpty() || oxidizerTank.getFluid().isFluidEqual(new FluidStack(fluid, 1000)) && oxidizerTank.getFluidAmount() + 1000 <= tankCapacity) {
                            updateOxidizerFluidStack.set(new FluidStack(fluid, 1000));
                            oxidizerTank.fill(new FluidStack(fluid, 1000), IFluidHandler.FluidAction.EXECUTE);
                            h.extractItem(0, 1, false);
                            h.insertItem(0, new ItemStack(Items.BUCKET, 1), false);
                        }
                    }
                }

                // Extract fluid from the oxidizer tank
                if (oxidizerOutput.copy().getItem() != null || oxidizerOutput.copy() != ItemStack.EMPTY){
                    if (oxidizerOutput.getItem() == Items.BUCKET && oxidizerTank.getFluidAmount() >= 1000 && oxidizerOutput.getCount() == 1){
                        ItemStack bucketStack = new ItemStack(oxidizerTank.getFluid().getRawFluid().getFilledBucket(),1);
                        oxidizerTank.drain(1000, IFluidHandler.FluidAction.EXECUTE);
                        h.extractItem(1,1,false);
                        h.insertItem(1, bucketStack, false);

                    }
                }

                // Input fluid to the fuel tank
                if (fuelInput.copy() != null || fuelInput.copy() != ItemStack.EMPTY) {
                    if (fuelInput.copy().getItem() instanceof BucketItem && fuelInput.getCount() == 1) {
                        Fluid fluid = ((BucketItem) fuelInput.copy().getItem()).getFluid();
                        //FluidStack fluidStack = new FluidStack(fluid, 1000);
                        if (fuelTank.isEmpty() || fuelTank.getFluid().isFluidEqual(new FluidStack(fluid, 1000)) && fuelTank.getFluidAmount() + 1000 <= tankCapacity) {
                            updateFuelFluidStack.set(new FluidStack(fluid, 1000));
                            fuelTank.fill(new FluidStack(fluid, 1000), IFluidHandler.FluidAction.EXECUTE);
                            h.extractItem(2, 1, false);
                            h.insertItem(2, new ItemStack(Items.BUCKET, 1), false);
                        }
                    }
                }

                // Extract fluid from the fuel tank
                if (fuelOutput.copy().getItem() != null || fuelOutput.copy() != ItemStack.EMPTY){
                    if (fuelOutput.getItem() == Items.BUCKET && fuelTank.getFluidAmount() >= 1000 && fuelOutput.getCount() == 1){
                        ItemStack bucketStack = new ItemStack(fuelTank.getFluid().getRawFluid().getFilledBucket(),1);
                        fuelTank.drain(1000, IFluidHandler.FluidAction.EXECUTE);
                        h.extractItem(3,1,false);
                        h.insertItem(3, bucketStack, false);
                    }
                }


                // TODO: Main Fluid Processing occurs here:
                /*
                if (inputTank0 != null || !inputTank0.isEmpty()) {
                    ItemStack inputFluidStack = new ItemStack(inputTank0.getFluid().getRawFluid().getFilledBucket(),1);
                    CentrifugalAgitatorRecipe recipe = world.getRecipeManager().getRecipe(CentrifugalAgitatorRecipe.RECIPE_TYPE, new Inventory(inputFluidStack), world).orElse(null);
                    if (recipe != null) {
                        if (outputTank0 != null && inputTank1 != null) {

                            // Tank fluid amount check + tank cap checks
                            if (inputTank0.getFluidAmount() >= recipe.inputAmount && outputTank0.getFluidAmount() + recipe.outputAmount <= tankCapacity && inputTank1.getFluidAmount() + recipe.secondAmount <= tankCapacity){
                                // Check for power
                                if (this.getCapability(CapabilityEnergy.ENERGY).map(IEnergyStorage::getEnergyStored).orElse(0) > 0){
                                    if (counter == 1){

                                        // Drain Input
                                        inputTank0.drain(recipe.inputAmount, IFluidHandler.FluidAction.EXECUTE);

                                        // First Output Tank
                                        if (outputTank0.getFluid().getRawFluid() != recipe.getOutputFluid().getRawFluid()){
                                            outputTank0.setFluid(recipe.getOutputFluid());
                                        } else {
                                            outputTank0.fill(recipe.getOutputFluid(), IFluidHandler.FluidAction.EXECUTE);
                                        }

                                        // Second Output Tank
                                        if (inputTank1.getFluid().getRawFluid() != recipe.getSecondFluid().getRawFluid()){
                                            inputTank1.setFluid(recipe.getSecondFluid());
                                        } else {
                                            inputTank1.fill(recipe.getSecondFluid(), IFluidHandler.FluidAction.EXECUTE);
                                        }

                                        counter--;
                                        energy.ifPresent(e -> ((VEEnergyStorage)e).consumeEnergy(Config.CENTRIFUGAL_AGITATOR_POWER_USAGE.get()));
                                        this.markDirty();
                                    } else if (counter > 0){
                                        counter--;
                                        energy.ifPresent(e -> ((VEEnergyStorage)e).consumeEnergy(Config.CENTRIFUGAL_AGITATOR_POWER_USAGE.get()));
                                    } else {
                                        counter = recipe.getProcessTime();
                                        length = counter;
                                    }
                                } // Energy Check
                            } else { // If fluid tank empty set counter to zero
                                counter = 0;
                            }
                        }
                    }
                }*/

                if (oxidizerTank != null) {
                    tankOxidizer.set(oxidizerTank.getFluid().copy());
                }

                if (fuelTank != null) {
                    tankFuel.set(fuelTank.getFluid().copy());
                }

                //LOGGER.debug("Fluid: " + inputTank.getFluid().toString() + " amount: " + inputTank.getFluid().toString());
                // End of Fluid Handler
            });

            // End of item handler
        });
    }

    /*
        Read and Write on World save
     */

    @Override
    public void read(CompoundNBT tag) {
        CompoundNBT inv = tag.getCompound("inv");
        handler.ifPresent(h -> ((INBTSerializable<CompoundNBT>) h).deserializeNBT(inv));
        createHandler().deserializeNBT(inv);
        CompoundNBT energyTag = tag.getCompound("energy");
        energy.ifPresent(h -> ((INBTSerializable<CompoundNBT>) h).deserializeNBT(energyTag));

        // Tanks
        int inputAmount = tag.getInt("inputAmount");
        CompoundNBT inputBucket = tag.getCompound("inputTank").copy();
        //LOGGER.debug("Input " + ItemStack.read(inputBucket).copy() + " amount: " + inputAmount);
        oxidizerTank.fill(readFluidStackFromNBT(inputAmount, inputBucket.copy()), IFluidHandler.FluidAction.EXECUTE);

        int output1Amount = tag.getInt("input1Amount");
        CompoundNBT output1Bucket = tag.getCompound("input1Tank").copy();
        fuelTank.fill(readFluidStackFromNBT(output1Amount, output1Bucket.copy()), IFluidHandler.FluidAction.EXECUTE);

        super.read(tag);
    }

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
        // Oxidizer Tank
        int inputAmount = oxidizerTank.getFluidAmount();
        //LOGGER.debug("InputAmount: " + inputAmount);
        tag.put("inputTank", tankToNBT(oxidizerTank).copy());
        tag.putInt("inputAmount", inputAmount);


        // Fuel Tank
        int output1Amount = fuelTank.getFluidAmount();
        tag.put("output1Tank", tankToNBT(fuelTank).copy());
        tag.putInt("output1Amount", output1Amount);

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
        this.read(pkt.getNbtCompound());
    }

    private CompoundNBT tankToNBT(FluidTank tank){
        CompoundNBT nbt;
        ItemStack itemStack = new ItemStack(tank.getFluid().getRawFluid().getFilledBucket(), 1);
        nbt = itemStack.serializeNBT();
        return nbt;
    }

    private FluidStack readFluidStackFromNBT(int amount, CompoundNBT itemStackNBT){
        ItemStack itemStack = ItemStack.read(itemStackNBT);
        if (itemStack == null || itemStack == ItemStack.EMPTY){
            LOGGER.debug("ITEMSTACK FROM NBT EMPTY!");
        }
        if (itemStack.getItem() instanceof BucketItem){
            return new FluidStack(((BucketItem) itemStack.getItem()).getFluid(), amount);
        }
        return FluidStack.EMPTY;
    }

    private IFluidHandler createFluid() {
        return new IFluidHandler() {
            @Override
            public int getTanks() {
                return 3;
            }

            @Nonnull
            @Override
            public FluidStack getFluidInTank(int tank) {
                if (tank == 0) {
                    return oxidizerTank == null ? FluidStack.EMPTY : oxidizerTank.getFluid();
                } else if (tank == 2) {
                    return fuelTank == null ? FluidStack.EMPTY : fuelTank.getFluid();
                }
                LOGGER.debug("Invalid tankId in Combustion Generator Tile for getFluidInTank");
                return FluidStack.EMPTY;
            }

            @Override
            public int getTankCapacity(int tank) {
                if (tank == 0) {
                    return oxidizerTank == null ? 0 : oxidizerTank.getCapacity();
                } else if (tank == 2) {
                    return fuelTank == null ? 0 : fuelTank.getCapacity();
                }
                LOGGER.debug("Invalid tankId in Combustion Generator Tile for getTankCapacity");
                return 0;
            }

            @Override
            public boolean isFluidValid(int tank, @Nonnull FluidStack stack) {
                if (tank == 0) {
                    return oxidizerTank != null && oxidizerTank.isFluidValid(stack);
                } else if (tank == 2) {
                    return fuelTank != null && fuelTank.isFluidValid(stack);
                }
                return false;
            }

            @Override
            public int fill(FluidStack resource, FluidAction action) {
                if (isFluidValid(0, resource) && oxidizerTank.isEmpty() || resource.isFluidEqual(oxidizerTank.getFluid())) {
                    return oxidizerTank.fill(resource, action);
                } else if (isFluidValid(2, resource) && fuelTank.isEmpty() || resource.isFluidEqual(fuelTank.getFluid())) {
                    return fuelTank.fill(resource, action);
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
                    return oxidizerTank.drain(resource, action);
                } else if (resource.isFluidEqual(fuelTank.getFluid())) {
                    return fuelTank.drain(resource, action);
                }
                return FluidStack.EMPTY;
            }

            @Nonnull
            @Override
            public FluidStack drain(int maxDrain, FluidAction action) {
                if (oxidizerTank.getFluidAmount() > 0) {
                    oxidizerTank.drain(maxDrain, action);
                } else if (fuelTank.getFluidAmount() > 0) {
                    fuelTank.drain(maxDrain, action);
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
        return new VEEnergyStorage(Config.CENTRIFUGAL_AGITATOR_MAX_POWER.get(), Config.CENTRIFUGAL_AGITATOR_TRANSFER.get()); // TODO: Max Power Storage, Max transfer configs
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
            return (px * (100 - ((counter * 100) / length))) / 100;
        }
    }

    @Deprecated
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
}
