package com.veteam.voluminousenergy.blocks.tiles;

import com.veteam.voluminousenergy.blocks.blocks.VEBlocks;
import com.veteam.voluminousenergy.blocks.containers.AqueoulizerContainer;
import com.veteam.voluminousenergy.recipe.AqueoulizerRecipe;
import com.veteam.voluminousenergy.recipe.CentrifugalAgitatorRecipe;
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
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

public class AqueoulizerTile extends VoluminousTileEntity implements ITickableTileEntity, INamedContainerProvider {
    private LazyOptional<IItemHandler> handler = LazyOptional.of(this::createHandler);
    private LazyOptional<IEnergyStorage> energy = LazyOptional.of(this::createEnergy);
    private LazyOptional<IFluidHandler> fluid = LazyOptional.of(this::createFluid);

    private int tankCapacity = 4000;

    private FluidTank inputTank = new FluidTank(tankCapacity);
    private FluidTank outputTank = new FluidTank(tankCapacity);

    private int counter;
    private int length;

    private static final Logger LOGGER = LogManager.getLogger();


    public AqueoulizerTile() {
        super(VEBlocks.AQUEOULIZER_TILE);
    }

    @Override
    public void tick() {
        updateClients();

        handler.ifPresent(h -> {
            ItemStack input = h.getStackInSlot(0).copy(); // Input insert
            ItemStack input1 = h.getStackInSlot(1).copy(); // Input extract
            ItemStack output0 = h.getStackInSlot(2).copy(); // Output extract
            ItemStack inputItem = h.getStackInSlot(3).copy(); // Repurpose to Item input

            /* Input fluid into the input fluid tank
            if (input.copy() != null || input.copy() != ItemStack.EMPTY) {
                if (input.copy().getItem() instanceof BucketItem && input.getCount() == 1) {
                    Fluid fluid = ((BucketItem) input.copy().getItem()).getFluid();
                    //FluidStack fluidStack = new FluidStack(fluid, 1000);
                    if (inputTank.isEmpty() || inputTank.getFluid().isFluidEqual(new FluidStack(fluid, 1000)) && inputTank.getFluidAmount() + 1000 <= tankCapacity) {
                        inputTank.fill(new FluidStack(fluid, 1000), IFluidHandler.FluidAction.EXECUTE);
                        h.extractItem(0, 1, false);
                        h.insertItem(0, new ItemStack(Items.BUCKET, 1), false);
                    }
                }
            }*/

            if (input.copy() != null || input.copy() != ItemStack.EMPTY && input1.copy() == ItemStack.EMPTY) {
                if (input.copy().getItem() instanceof BucketItem && input.getCount() == 1) {
                    Fluid fluid = ((BucketItem) input.copy().getItem()).getFluid();
                    if (inputTank.isEmpty() || inputTank.getFluid().isFluidEqual(new FluidStack(fluid, 1000)) && inputTank.getFluidAmount() + 1000 <= tankCapacity) {
                        inputTank.fill(new FluidStack(fluid, 1000), IFluidHandler.FluidAction.EXECUTE);
                        h.extractItem(0, 1, false);
                        h.insertItem(1, new ItemStack(Items.BUCKET, 1), false);
                    }
                }
            }

            /* Extract fluid from the input tank
            if (input1.copy().getItem() != null || input1.copy() != ItemStack.EMPTY){
                if (input1.getItem() == Items.BUCKET && inputTank.getFluidAmount() >= 1000 && input1.getCount() == 1){
                    ItemStack bucketStack = new ItemStack(inputTank.getFluid().getRawFluid().getFilledBucket(),1);
                    inputTank.drain(1000, IFluidHandler.FluidAction.EXECUTE);
                    h.extractItem(1,1,false);
                    h.insertItem(1, bucketStack, false);

                }
            }*/

            if(input.copy().getItem() == Items.BUCKET && input1.copy() == ItemStack.EMPTY) {
                if(inputTank.getFluidAmount() >= 1000) {
                    ItemStack bucketStack = new ItemStack(inputTank.getFluid().getRawFluid().getFilledBucket(), 1);
                    inputTank.drain(1000, IFluidHandler.FluidAction.EXECUTE);
                    h.extractItem(0, 1, false);
                    h.insertItem(1, bucketStack, false);
                }
            }

            // Extract fluid from the first output tank
            if (output0.copy().getItem() != null || output0.copy() != ItemStack.EMPTY){
                if (output0.getItem() == Items.BUCKET && outputTank.getFluidAmount() >= 1000 && output0.getCount() == 1){
                    ItemStack bucketStack = new ItemStack(outputTank.getFluid().getRawFluid().getFilledBucket(), 1);
                    outputTank.drain(1000, IFluidHandler.FluidAction.EXECUTE);
                    h.extractItem(2,1,false);
                    h.insertItem(2, bucketStack, false);
                }
            }

            // Main Fluid Processing occurs here:
            AqueoulizerRecipe recipe = world.getRecipeManager().getRecipe(AqueoulizerRecipe.RECIPE_TYPE, new Inventory(inputItem.copy()),world).orElse(null);

            if (inputTank != null && !inputTank.isEmpty() && recipe != null) {
                ItemStack inputFluidStack = new ItemStack(inputTank.getFluid().getRawFluid().getFilledBucket(),1);
                //AqueoulizerRecipe recipe = world.getRecipeManager().getRecipe(AqueoulizerRecipe.RECIPE_TYPE, new Inventory(inputFluidStack), world).orElse(null);
                if (inputTank.getFluid().getRawFluid().getFilledBucket() == recipe.inputFluid.getItem()) {
                    if (outputTank != null) {

                        // Tank fluid amount check + tank cap checks
                        if (inputTank.getFluidAmount() >= recipe.inputAmount && outputTank.getFluidAmount() + recipe.outputAmount <= tankCapacity){
                            // Check for power
                            if (this.getCapability(CapabilityEnergy.ENERGY).map(IEnergyStorage::getEnergyStored).orElse(0) > 0){
                                if (counter == 1){

                                    // Drain Input
                                    inputTank.drain(recipe.inputAmount, IFluidHandler.FluidAction.EXECUTE);

                                    // Output Tank
                                    if (outputTank.getFluid().getRawFluid() != recipe.getOutputFluid().getRawFluid()){
                                        outputTank.setFluid(recipe.getOutputFluid());
                                    } else {
                                        outputTank.fill(recipe.getOutputFluid(), IFluidHandler.FluidAction.EXECUTE);
                                    }

                                    h.extractItem(3, recipe.ingredientCount,false);

                                    counter--;
                                    energy.ifPresent(e -> ((VEEnergyStorage)e).consumeEnergy(Config.AQUEOULIZER_POWER_USAGE.get()));
                                    this.markDirty();
                                } else if (counter > 0){
                                    counter--;
                                    energy.ifPresent(e -> ((VEEnergyStorage)e).consumeEnergy(Config.AQUEOULIZER_POWER_USAGE.get()));
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
            //LOGGER.debug("Fluid: " + inputTank.getFluid().getRawFluid().getFilledBucket().getTranslationKey() + " amount: " + inputTank.getFluid().getAmount());
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
            CompoundNBT outputTank = tag.getCompound("outputTank");

            this.inputTank.readFromNBT(inputTank);
            this.outputTank.readFromNBT(outputTank);
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
            CompoundNBT outputNBT = new CompoundNBT();

            this.inputTank.writeToNBT(inputNBT);
            this.outputTank.writeToNBT(outputNBT);

            tag.put("inputTank", inputNBT);
            tag.put("outputTank", outputNBT);
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
                return 2;
            }

            @Nonnull
            @Override
            public FluidStack getFluidInTank(int tank) {
                if (tank == 0) {
                    return inputTank == null ? FluidStack.EMPTY : inputTank.getFluid();
                } else if (tank == 1) {
                    return outputTank == null ? FluidStack.EMPTY : outputTank.getFluid();
                }
                LOGGER.debug("Invalid tankId in Aqueoulizer Tile for getFluidInTank");
                return FluidStack.EMPTY;
            }

            @Override
            public int getTankCapacity(int tank) {
                if (tank == 0) {
                    return inputTank == null ? 0 : inputTank.getCapacity();
                } else if (tank == 1) {
                    return outputTank == null ? 0 : outputTank.getCapacity();
                }
                LOGGER.debug("Invalid tankId in Aqueoulizer Tile for getTankCapacity");
                return 0;
            }

            @Override
            public boolean isFluidValid(int tank, @Nonnull FluidStack stack) {
                if (tank == 0) {
                    ItemStack bucketStack = new ItemStack(stack.getRawFluid().getFilledBucket());
                    AqueoulizerRecipe recipe = world.getRecipeManager().getRecipe(AqueoulizerRecipe.RECIPE_TYPE, new Inventory(bucketStack), world).orElse(null);

                    return recipe!= null && inputTank != null && inputTank.isFluidValid(stack);
                } else if (tank == 1) {
                    AtomicBoolean recipeHit = new AtomicBoolean(false);

                    AqueoulizerRecipe.ingredientList.forEach(item -> {
                        AqueoulizerRecipe recipe = world.getRecipeManager().getRecipe(AqueoulizerRecipe.RECIPE_TYPE, new Inventory(new ItemStack(item)), world).orElse(null);
                        if (recipe != null && recipe.getOutputFluid().getFluid().isEquivalentTo(stack.getFluid())){ // In theory should never be null
                            recipeHit.set(true);
                        }
                    });

                    return recipeHit.get() && outputTank != null && outputTank.isFluidValid(stack);
                }
                return false;
            }

            @Override
            public int fill(FluidStack resource, FluidAction action) {
                if (isFluidValid(0, resource) && inputTank.isEmpty() || resource.isFluidEqual(inputTank.getFluid())) {
                    return inputTank.fill(resource, action);
                } else if (isFluidValid(1, resource) && outputTank.isEmpty() || resource.isFluidEqual(outputTank.getFluid())) {
                    return outputTank.fill(resource, action);
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
                } else if (resource.isFluidEqual(outputTank.getFluid())) {
                    return outputTank.drain(resource, action);
                }
                return FluidStack.EMPTY;
            }

            @Nonnull
            @Override
            public FluidStack drain(int maxDrain, FluidAction action) {
                if (inputTank.getFluidAmount() > 0) {
                    inputTank.drain(maxDrain, action);
                } else if (outputTank.getFluidAmount() > 0) {
                    outputTank.drain(maxDrain, action);
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
        return new VEEnergyStorage(Config.AQUEOULIZER_MAX_POWER.get(), Config.AQUEOULIZER_TRANSFER.get());
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
        return new AqueoulizerContainer(i, world, pos, playerInventory, playerEntity);
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
            return outputTank.getFluid();
        }
        return FluidStack.EMPTY;
    }

    public int getTankCapacity(){
        return tankCapacity;
    }
}
