package com.veteam.voluminousenergy.blocks.tiles;

import com.veteam.voluminousenergy.blocks.blocks.VEBlocks;
import com.veteam.voluminousenergy.blocks.containers.DistillationUnitContainer;
import com.veteam.voluminousenergy.recipe.DistillationRecipe;
import com.veteam.voluminousenergy.tools.Config;
import com.veteam.voluminousenergy.tools.VEEnergyStorage;
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
import java.util.concurrent.atomic.AtomicBoolean;

public class DistillationUnitTile extends VoluminousTileEntity implements ITickableTileEntity, INamedContainerProvider {
    private LazyOptional<IItemHandler> handler = LazyOptional.of(this::createHandler);
    private LazyOptional<IEnergyStorage> energy = LazyOptional.of(this::createEnergy);
    private LazyOptional<IFluidHandler> fluid = LazyOptional.of(this::createFluid);

    private int tankCapacity = 4000;

    private FluidTank inputTank = new FluidTank(tankCapacity);
    private FluidTank outputTank0 = new FluidTank(tankCapacity);
    private FluidTank outputTank1 = new FluidTank(tankCapacity);

    private int counter;
    private int length;


    private static final Logger LOGGER = LogManager.getLogger();


    public DistillationUnitTile() {
        super(VEBlocks.DISTILLATION_UNIT_TILE);
    }

    @Override
    public void tick() {

        updateClients();

        handler.ifPresent(h -> {
            ItemStack inputTop = h.getStackInSlot(0).copy();
            ItemStack inputBottom = h.getStackInSlot(1).copy();
            ItemStack firstOutputTop = h.getStackInSlot(2).copy();
            ItemStack firstOutputBottom = h.getStackInSlot(3).copy();
            ItemStack secondOutputTop = h.getStackInSlot(4).copy();
            ItemStack secondOutputBottom = h.getStackInSlot(5).copy();
            ItemStack thirdOutput = h.getStackInSlot(6).copy();

            // Input fluid into the input fluid tank (NEW)
            if (inputTop.copy() != null || inputTop.copy() != ItemStack.EMPTY && inputBottom.copy() == ItemStack.EMPTY) {
                if (inputTop.copy().getItem() instanceof BucketItem && inputTop.getCount() == 1) {
                    Fluid fluid = ((BucketItem) inputTop.copy().getItem()).getFluid();
                    if (inputTank.isEmpty() || inputTank.getFluid().isFluidEqual(new FluidStack(fluid, 1000)) && inputTank.getFluidAmount() + 1000 <= tankCapacity) {
                        inputTank.fill(new FluidStack(fluid, 1000), IFluidHandler.FluidAction.EXECUTE);
                        h.extractItem(0, 1, false);
                        h.insertItem(1, new ItemStack(Items.BUCKET, 1), false);
                    }
                }
            }

            // Extract fluid from the input tank (NEW)
            if(inputTop.copy().getItem() == Items.BUCKET && inputBottom.copy() == ItemStack.EMPTY) {
                if(inputTank.getFluidAmount() >= 1000) {
                    ItemStack bucketStack = new ItemStack(inputTank.getFluid().getRawFluid().getFilledBucket(), 1);
                    inputTank.drain(1000, IFluidHandler.FluidAction.EXECUTE);
                    h.extractItem(0, 1, false);
                    h.insertItem(1, bucketStack, false);
                }
            }

            // Input fluid into the first output fluid tank (NEW)
            if (firstOutputTop.copy() != null || firstOutputTop.copy() != ItemStack.EMPTY && firstOutputBottom.copy() == ItemStack.EMPTY) {
                if (firstOutputTop.copy().getItem() instanceof BucketItem && firstOutputTop.getCount() == 1) {
                    Fluid fluid = ((BucketItem) firstOutputTop.copy().getItem()).getFluid();
                    if (outputTank0.isEmpty() || outputTank0.getFluid().isFluidEqual(new FluidStack(fluid, 1000)) && outputTank0.getFluidAmount() + 1000 <= tankCapacity) {
                        outputTank0.fill(new FluidStack(fluid, 1000), IFluidHandler.FluidAction.EXECUTE);
                        h.extractItem(2, 1, false);
                        h.insertItem(3, new ItemStack(Items.BUCKET, 1), false);
                    }
                }
            }

            // Extract fluid from the first output tank (NEW)
            if(firstOutputTop.copy().getItem() == Items.BUCKET && firstOutputBottom.copy() == ItemStack.EMPTY) {
                if(outputTank0.getFluidAmount() >= 1000) {
                    ItemStack bucketStack = new ItemStack(outputTank0.getFluid().getRawFluid().getFilledBucket(), 1);
                    outputTank0.drain(1000, IFluidHandler.FluidAction.EXECUTE);
                    h.extractItem(2, 1, false);
                    h.insertItem(3, bucketStack, false);
                }
            }

            // Input fluid into the second output fluid tank
            if (secondOutputTop.copy() != null || secondOutputTop.copy() != ItemStack.EMPTY && secondOutputBottom.copy() == ItemStack.EMPTY) {
                if (secondOutputTop.copy().getItem() instanceof BucketItem && secondOutputTop.getCount() == 1) {
                    Fluid fluid = ((BucketItem) secondOutputTop.copy().getItem()).getFluid();
                    if (outputTank1.isEmpty() || outputTank1.getFluid().isFluidEqual(new FluidStack(fluid, 1000)) && outputTank1.getFluidAmount() + 1000 <= tankCapacity) {
                        outputTank1.fill(new FluidStack(fluid, 1000), IFluidHandler.FluidAction.EXECUTE);
                        h.extractItem(4, 1, false);
                        h.insertItem(5, new ItemStack(Items.BUCKET, 1), false);
                    }
                }
            }

            // Extract fluid from the second output tank
            if(secondOutputTop.copy().getItem() == Items.BUCKET && secondOutputBottom.copy() == ItemStack.EMPTY) {
                if(outputTank1.getFluidAmount() >= 1000) {
                    ItemStack bucketStack = new ItemStack(outputTank1.getFluid().getRawFluid().getFilledBucket(), 1);
                    outputTank1.drain(1000, IFluidHandler.FluidAction.EXECUTE);
                    h.extractItem(4, 1, false);
                    h.insertItem(5, bucketStack, false);
                }
            }

            // Main Fluid Processing occurs here:
            if (inputTank != null || !inputTank.isEmpty()) {
                ItemStack inputFluidStack = new ItemStack(inputTank.getFluid().getRawFluid().getFilledBucket(),1);
                DistillationRecipe recipe = world.getRecipeManager().getRecipe(DistillationRecipe.RECIPE_TYPE, new Inventory(inputFluidStack), world).orElse(null);
                if (recipe != null) {
                    if (outputTank0 != null && outputTank1 != null) {

                        // Tank fluid amount check + tank cap checks
                        if (inputTank.getFluidAmount() >= recipe.getInputAmount() && outputTank0.getFluidAmount() + recipe.getOutputAmount() <= tankCapacity && outputTank1.getFluidAmount() + recipe.getSecondAmount() <= tankCapacity){
                            // Check for power
                            if (this.getCapability(CapabilityEnergy.ENERGY).map(IEnergyStorage::getEnergyStored).orElse(0) > 0){
                                if (counter == 1){

                                    // Drain Input
                                    inputTank.drain(recipe.getInputAmount(), IFluidHandler.FluidAction.EXECUTE);

                                    // First Output Tank
                                    if (outputTank0.getFluid().getRawFluid() != recipe.getOutputFluid().getRawFluid()){
                                        outputTank0.setFluid(recipe.getOutputFluid());
                                    } else {
                                        outputTank0.fill(recipe.getOutputFluid(), IFluidHandler.FluidAction.EXECUTE);
                                    }

                                    // Second Output Tank
                                    if (outputTank1.getFluid().getRawFluid() != recipe.getSecondFluid().getRawFluid()){
                                        outputTank1.setFluid(recipe.getSecondFluid());
                                    } else {
                                        outputTank1.fill(recipe.getSecondFluid(), IFluidHandler.FluidAction.EXECUTE);
                                    }

                                    counter--;
                                    energy.ifPresent(e -> ((VEEnergyStorage)e).consumeEnergy(Config.CENTRIFUGAL_AGITATOR_POWER_USAGE.get())); // TODO: Config for Distillation Unit
                                    this.markDirty();
                                } else if (counter > 0){
                                    counter--;
                                    energy.ifPresent(e -> ((VEEnergyStorage)e).consumeEnergy(Config.CENTRIFUGAL_AGITATOR_POWER_USAGE.get())); // TODO: Config for Distillation Unit
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
            }

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
        fluid.ifPresent(f -> {
            CompoundNBT inputTank = tag.getCompound("inputTank");
            CompoundNBT outputTank0 = tag.getCompound("outputTank0");
            CompoundNBT outputTank1 = tag.getCompound("outputTank1");

            this.inputTank.readFromNBT(inputTank);
            this.outputTank0.readFromNBT(outputTank0);
            this.outputTank1.readFromNBT(outputTank1);
        });

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
        fluid.ifPresent(f -> {
            CompoundNBT inputNBT = new CompoundNBT();
            CompoundNBT outputNBT0 = new CompoundNBT();
            CompoundNBT outputNBT1 = new CompoundNBT();

            this.inputTank.writeToNBT(inputNBT);
            this.outputTank0.writeToNBT(outputNBT0);
            this.outputTank1.writeToNBT(outputNBT1);

            tag.put("inputTank", inputNBT);
            tag.put("outputTank0", outputNBT0);
            tag.put("outputTank1", outputNBT1);
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
        this.read(pkt.getNbtCompound());
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
                    return inputTank == null ? FluidStack.EMPTY : inputTank.getFluid();
                } else if (tank == 1) {
                    return outputTank0 == null ? FluidStack.EMPTY : outputTank0.getFluid();
                } else if (tank == 2) {
                    return outputTank1 == null ? FluidStack.EMPTY : outputTank1.getFluid();
                }
                LOGGER.debug("Invalid tankId in Distillation Unit Tile for getFluidInTank");
                return FluidStack.EMPTY;
            }

            @Override
            public int getTankCapacity(int tank) {
                if (tank == 0) {
                    return inputTank == null ? 0 : inputTank.getCapacity();
                } else if (tank == 1) {
                    return outputTank0 == null ? 0 : outputTank0.getCapacity();
                } else if (tank == 2) {
                    return outputTank1 == null ? 0 : outputTank1.getCapacity();
                }
                LOGGER.debug("Invalid tankId in Distillation Unit Tile for getTankCapacity");
                return 0;
            }

            @Override
            public boolean isFluidValid(int tank, @Nonnull FluidStack stack) {
                if (tank == 0) {
                    ItemStack bucketStack = new ItemStack(stack.getRawFluid().getFilledBucket());
                    DistillationRecipe recipe = world.getRecipeManager().getRecipe(DistillationRecipe.RECIPE_TYPE, new Inventory(bucketStack), world).orElse(null);
                    return recipe != null && inputTank != null && inputTank.isFluidValid(stack);
                } else if (tank == 1) {
                    AtomicBoolean recipeHit = new AtomicBoolean(false);
                    DistillationRecipe.ingredientList.forEach(item -> {
                        DistillationRecipe recipe = world.getRecipeManager().getRecipe(DistillationRecipe.RECIPE_TYPE, new Inventory(new ItemStack(item)), world).orElse(null);
                        if (recipe != null && recipe.getOutputFluid().getFluid().isEquivalentTo(stack.getFluid())){ // In theory should never be null
                            recipeHit.set(true);
                        }
                    });

                    return recipeHit.get() && outputTank0 != null && outputTank0.isFluidValid(stack);
                } else if (tank == 2) {
                    AtomicBoolean recipeHit = new AtomicBoolean(false);
                    DistillationRecipe.ingredientList.forEach(item -> {
                        DistillationRecipe recipe = world.getRecipeManager().getRecipe(DistillationRecipe.RECIPE_TYPE, new Inventory(new ItemStack(item)),world).orElse(null);
                        if (recipe != null && recipe.getSecondFluid().getFluid().isEquivalentTo(stack.getFluid())){
                            recipeHit.set(true);
                        }
                    });

                    return recipeHit.get() && outputTank1 != null && outputTank1.isFluidValid(stack);
                }
                return false;
            }

            @Override
            public int fill(FluidStack resource, FluidAction action) {
                if (isFluidValid(0, resource) && inputTank.isEmpty() || resource.isFluidEqual(inputTank.getFluid())) {
                    return inputTank.fill(resource, action);
                } else if (isFluidValid(1, resource) && outputTank0.isEmpty() || resource.isFluidEqual(outputTank0.getFluid())) {
                    return outputTank0.fill(resource, action);
                } else if (isFluidValid(2, resource) && outputTank1.isEmpty() || resource.isFluidEqual(outputTank1.getFluid())) {
                    return outputTank1.fill(resource, action);
                }
                return 0;
            }

            @Nonnull
            @Override
            public FluidStack drain(FluidStack resource, FluidAction action) {
                if (resource.isEmpty()) {
                    return FluidStack.EMPTY;
                }
                if (resource.isFluidEqual(inputTank.getFluid())) {
                    return inputTank.drain(resource, action);
                } else if (resource.isFluidEqual(outputTank0.getFluid())) {
                    return outputTank0.drain(resource, action);
                } else if (resource.isFluidEqual(outputTank1.getFluid())) {
                    return outputTank1.drain(resource, action);
                }
                return FluidStack.EMPTY;
            }

            @Nonnull
            @Override
            public FluidStack drain(int maxDrain, FluidAction action) {
                if (inputTank.getFluidAmount() > 0) {
                    inputTank.drain(maxDrain, action);
                } else if (outputTank0.getFluidAmount() > 0) {
                    outputTank0.drain(maxDrain, action);
                } else if (outputTank1.getFluidAmount() > 0) {
                    outputTank1.drain(maxDrain, action);
                }
                return FluidStack.EMPTY;
            }
        };
    }


    private ItemStackHandler createHandler() {
        return new ItemStackHandler(7) {
            @Override
            protected void onContentsChanged(int slot) {
                markDirty();
            }

            @Override
            public boolean isItemValid(int slot, @Nonnull ItemStack stack) { //IS ITEM VALID PLEASE DO THIS PER SLOT TO SAVE DEBUG HOURS!!!!
                if (slot == 0 || slot == 1){
                    DistillationRecipe recipe = world.getRecipeManager().getRecipe(DistillationRecipe.RECIPE_TYPE,new Inventory(stack),world).orElse(null);
                    return recipe != null || stack.getItem() == Items.BUCKET;
                } else if (slot == 2 || slot == 3 && stack.getItem() instanceof BucketItem) {
                    if (stack.getItem() == Items.BUCKET) return true;

                    AtomicBoolean recipeHit = new AtomicBoolean(false);
                    DistillationRecipe.ingredientList.forEach(item -> {
                        DistillationRecipe recipe = world.getRecipeManager().getRecipe(DistillationRecipe.RECIPE_TYPE, new Inventory(new ItemStack(item)), world).orElse(null);
                        if (recipe != null && recipe.getOutputFluid().getRawFluid() == ((BucketItem) stack.getItem()).getFluid() ){
                            recipeHit.set(true);
                        }
                    });
                    return recipeHit.get();
                } else if (slot == 4 || slot == 5 && stack.getItem() instanceof BucketItem) {
                    if (stack.getItem() == Items.BUCKET) return true;
                    AtomicBoolean recipeHit = new AtomicBoolean(false);
                    DistillationRecipe.ingredientList.forEach(item -> {
                        DistillationRecipe recipe = world.getRecipeManager().getRecipe(DistillationRecipe.RECIPE_TYPE, new Inventory(new ItemStack(item)), world).orElse(null);
                        if (recipe != null && recipe.getSecondFluid().getRawFluid() == ((BucketItem) stack.getItem()).getFluid() ){
                            recipeHit.set(true);
                        }
                    });
                    return recipeHit.get();
                } else if (slot == 6){
                    AtomicBoolean recipeHit = new AtomicBoolean(false);
                    DistillationRecipe.ingredientList.forEach(item -> {
                        DistillationRecipe recipe = world.getRecipeManager().getRecipe(DistillationRecipe.RECIPE_TYPE, new Inventory(new ItemStack(item)), world).orElse(null);
                        if (recipe != null && recipe.getThirdResult().getItem() == stack.getItem()){
                            recipeHit.set(true);
                        }
                    });
                    return recipeHit.get();
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

    private IEnergyStorage createEnergy() { // TODO: Config for Distillation Unit
        return new VEEnergyStorage(Config.CENTRIFUGAL_AGITATOR_MAX_POWER.get(), Config.CENTRIFUGAL_AGITATOR_TRANSFER.get()); // Max Power Storage, Max transfer
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
        return new DistillationUnitContainer(i, world, pos, playerInventory, playerEntity);
    }

    public int progressCounterPX(int px) {
        if (counter == 0) {
            return 0;
        } else {
            return (px * (100 - ((counter * 100) / length))) / 100;
        }
    }

    public FluidStack getFluidStackFromTank(int num){
        if (num == 0){
            return inputTank.getFluid();
        } else if (num == 1){
            return outputTank0.getFluid();
        } else if (num == 2){
            return outputTank1.getFluid();
        }
        return FluidStack.EMPTY;
    }

    public int getTankCapacity(){
        return tankCapacity;
    }
}