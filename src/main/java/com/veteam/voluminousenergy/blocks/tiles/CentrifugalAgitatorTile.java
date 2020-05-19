package com.veteam.voluminousenergy.blocks.tiles;

import com.veteam.voluminousenergy.blocks.blocks.VEBlocks;
import com.veteam.voluminousenergy.blocks.containers.CentrifugalAgitatorContainer;
import com.veteam.voluminousenergy.blocks.containers.CrusherContainer;
import com.veteam.voluminousenergy.blocks.containers.PrimitiveBlastFurnaceContainer;
import com.veteam.voluminousenergy.fluids.VEFluids;
import com.veteam.voluminousenergy.recipe.CrusherRecipe;
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
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.ItemFluidContainer;
import net.minecraftforge.fluids.capability.templates.FluidHandlerItemStack;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Random;
import java.util.concurrent.atomic.AtomicReference;

import static net.minecraft.util.math.MathHelper.abs;

public class CentrifugalAgitatorTile extends TileEntity implements ITickableTileEntity, INamedContainerProvider {
    private LazyOptional<IItemHandler> handler = LazyOptional.of(this::createHandler);
    private LazyOptional<IEnergyStorage> energy = LazyOptional.of(this::createEnergy);
    private LazyOptional<IFluidHandler> fluid = LazyOptional.of(this::createFluid);

    private int tankCapacity = 4000;

    private FluidTank inputTank = new FluidTank(tankCapacity);
    private FluidTank outputTank0 = new FluidTank(tankCapacity);
    private FluidTank outputTank1 = new FluidTank(tankCapacity);

    private int counter;
    private int length;
    private AtomicReference<ItemStack> inputItemStack = new AtomicReference<ItemStack>(new ItemStack(Items.AIR,0));
    private AtomicReference<FluidStack> inputFluidStack = new AtomicReference<FluidStack>(new FluidStack(FluidStack.EMPTY, 0));
    private static final Logger LOGGER = LogManager.getLogger();


    public CentrifugalAgitatorTile(){
        super(VEBlocks.CENTRIFUGAL_AGITATOR_TILE);
    }

    @Override
    public void tick(){
        handler.ifPresent(h -> {
            ItemStack input = h.getStackInSlot(0).copy(); // TODO: Refactor to make this truly bucket input
            ItemStack output0 = h.getStackInSlot(1).copy(); // TODO: Refactor to make this to extract fluid to bucket
            ItemStack output1 = h.getStackInSlot(2).copy(); // TODO: Same as line above


            inputItemStack.set(input.copy()); // Atomic Reference, use this to query recipes
            fluid.ifPresent(f -> {
                //LOGGER.debug(" FLUID HANDLER PRESENT!");
                //TODO: Fluid manipulation should go here

                // Input fluid into the input fluid tank
                if (input.copy() != null || input.copy() != ItemStack.EMPTY){
                    if (input.copy().getItem() instanceof BucketItem){
                        Fluid fluid = ((BucketItem) input.copy().getItem()).getFluid();
                        //FluidStack fluidStack = new FluidStack(fluid, 1000);
                        if (inputTank.isEmpty() || inputTank.getFluid().isFluidEqual(new FluidStack(fluid, 1000)) && inputTank.getFluidAmount() + 1000 <= tankCapacity){
                            inputFluidStack.set(new FluidStack(fluid, 1000));
                            inputTank.fill(new FluidStack(fluid, 1000), IFluidHandler.FluidAction.EXECUTE);
                            h.extractItem(0,1,false);
                            h.insertItem(0,new ItemStack(Items.BUCKET, 1),false);
                        }
                    }
                }
                LOGGER.debug("Fluid: " + inputTank.getFluid());
                // End of Fluid Handler
            });

            // End of item handler
        });
    }

    /*
        Read and Write on World save
     */

    @Override
    public void read(CompoundNBT tag){ // TODO: Make these tags actually work (JSON), add fluid
        CompoundNBT inv = tag.getCompound("inv");
        handler.ifPresent(h -> ((INBTSerializable<CompoundNBT>)h).deserializeNBT(inv));
        createHandler().deserializeNBT(inv);
        CompoundNBT energyTag = tag.getCompound("energy");
        energy.ifPresent(h -> ((INBTSerializable<CompoundNBT>)h).deserializeNBT(energyTag));
        super.read(tag);
    }

    @Override
    public CompoundNBT write(CompoundNBT tag) { // TODO: Make these tags actually work (JSON), add fluid
        handler.ifPresent(h -> {
            CompoundNBT compound = ((INBTSerializable<CompoundNBT>) h).serializeNBT();
            tag.put("inv", compound);
        });
        energy.ifPresent(h -> {
            CompoundNBT compound = ((INBTSerializable<CompoundNBT>)h).serializeNBT();
            tag.put("energy",compound);
        });
        return super.write(tag);
    }

    private IFluidHandler createFluid(){
        return new IFluidHandler() {
            @Override
            public int getTanks() {
                return 3; // TODO: Implement secondary tank
            }

            @Nonnull
            @Override
            public FluidStack getFluidInTank(int tank) {
                if (tank == 0){
                    return inputTank == null ? FluidStack.EMPTY : inputTank.getFluid();
                } else if (tank == 1){
                    return outputTank0 == null ? FluidStack.EMPTY : outputTank0.getFluid();
                } else if (tank == 2){
                    return outputTank1 == null ? FluidStack.EMPTY : outputTank1.getFluid();
                }
                return FluidStack.EMPTY;
            }

            @Override
            public int getTankCapacity(int tank) {
                if (tank == 0){
                    return inputTank == null ? 0 : inputTank.getCapacity();
                } else if (tank == 1){
                    return outputTank0 == null ? 0 : outputTank0.getCapacity();
                } else if (tank == 2){
                    return outputTank1 == null ? 0 : outputTank1.getCapacity();
                }
                return 0;
            }

            @Override
            public boolean isFluidValid(int tank, @Nonnull FluidStack stack) {
                if (tank == 0){
                    return inputTank != null && inputTank.isFluidValid(stack);
                } else if (tank == 1){
                    return outputTank0 != null && outputTank0.isFluidValid(stack);
                } else if (tank == 2){
                    return outputTank1 != null && outputTank1.isFluidValid(stack);
                }
                return false;
            }

            @Override
            public int fill(FluidStack resource, FluidAction action) {
                if (isFluidValid(0, resource) && inputTank.isEmpty() || resource.isFluidEqual(inputTank.getFluid())){
                    return inputTank.fill(resource, action);
                } else if (isFluidValid(1, resource) && outputTank0.isEmpty() || resource.isFluidEqual(outputTank0.getFluid())){
                    return outputTank0.fill(resource, action);
                } else if (isFluidValid(2, resource) && outputTank1.isEmpty() || resource.isFluidEqual(outputTank1.getFluid())){
                    return outputTank1.fill(resource, action);
                }
                return 0;
            }

            @Nonnull
            @Override
            public FluidStack drain(FluidStack resource, FluidAction action) {
                if(resource.isEmpty()){
                    return FluidStack.EMPTY;
                }
                if (resource.isFluidEqual(inputTank.getFluid())){
                    return inputTank.drain(resource, action);
                } else if (resource.isFluidEqual(outputTank0.getFluid())){
                    return outputTank0.drain(resource, action);
                } else if (resource.isFluidEqual(outputTank1.getFluid())){
                    return outputTank1.drain(resource, action);
                }
                return FluidStack.EMPTY;
            }

            @Nonnull
            @Override
            public FluidStack drain(int maxDrain, FluidAction action) {
                if (inputTank.getFluidAmount() > 0){
                    inputTank.drain(maxDrain, action);
                } else if (outputTank0.getFluidAmount() > 0){
                    outputTank0.drain(maxDrain, action);
                } else if (outputTank1.getFluidAmount() > 0){
                    outputTank1.drain(maxDrain, action);
                }
                return FluidStack.EMPTY;
            }
        };
    }


    private ItemStackHandler createHandler() {
        return new ItemStackHandler(3) {
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
            public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate){ //ALSO DO THIS PER SLOT BASIS TO SAVE DEBUG HOURS!!!

                return super.insertItem(slot, stack, simulate);
            }
        };
    }

    private IEnergyStorage createEnergy(){
        return new VEEnergyStorage(Config.CRUSHER_MAX_POWER.get(),Config.CRUSHER_TRANSFER.get()); // Max Power Storage, Max transfer
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
        if(cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
            return handler.cast();
        }
        if (cap == CapabilityEnergy.ENERGY){
            return energy.cast();
        }
        return super.getCapability(cap, side);
    }

    @Override
    public ITextComponent getDisplayName(){
        return new StringTextComponent(getType().getRegistryName().getPath());
    }

    @Nullable
    @Override
    public Container createMenu(int i, @Nonnull PlayerInventory playerInventory, @Nonnull PlayerEntity playerEntity)
    {
        return new CentrifugalAgitatorContainer(i,world,pos,playerInventory,playerEntity);
    }

    public int progressCounterPX(int px){
        if (counter == 0){
            return 0;
        } else {
            return (px*(100-((counter*100)/length)))/100;
        }
    }

}
