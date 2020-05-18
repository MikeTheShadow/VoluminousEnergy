package com.veteam.voluminousenergy.blocks.tiles;

import com.veteam.voluminousenergy.blocks.blocks.VEBlocks;
import com.veteam.voluminousenergy.blocks.containers.CentrifugalAgitatorContainer;
import com.veteam.voluminousenergy.blocks.containers.CrusherContainer;
import com.veteam.voluminousenergy.blocks.containers.PrimitiveBlastFurnaceContainer;
import com.veteam.voluminousenergy.recipe.CrusherRecipe;
import com.veteam.voluminousenergy.tools.Config;
import com.veteam.voluminousenergy.tools.VEEnergyStorage;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
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

    private FluidTank inputTank;
    private FluidTank outputTank0;
    private FluidTank outputTank1;

    private int counter;
    private int length;
    private AtomicReference<ItemStack> inputItemStack = new AtomicReference<ItemStack>(new ItemStack(Items.AIR,0));
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

            fluid.ifPresent(f -> {
                //LOGGER.debug(" FLUID HANDLER PRESENT!");
                //TODO: Fluid manipulation should go here
            });

            /* //TODO: Wipe this code and rewrite for Centrifugal Agitator
            CrusherRecipe recipe = world.getRecipeManager().getRecipe(CrusherRecipe.recipeType, new Inventory(input), world).orElse(null);
            inputItemStack.set(input.copy()); // Atomic Reference, use this to query recipes

            if (!input.isEmpty()){
                if (output.getCount() + recipe.getOutputAmount() < 64 && rng.getCount() + recipe.getOutputRngAmount() < 64 && this.getCapability(CapabilityEnergy.ENERGY).map(IEnergyStorage::getEnergyStored).orElse(0) > 0) {
                    if (counter == 1){ //The processing is about to be complete
                        // Extract the inputted item
                        h.extractItem(0,recipe.ingredientCount,false);

                        // Get output stack from the recipe
                        ItemStack newOutputStack = recipe.getResult().copy();

                        //LOGGER.debug("output: " + output + " rng: " + rng + " newOutputStack: "  + newOutputStack);

                        // Manipulating the Output slot
                        if (output.getItem() != newOutputStack.getItem() || output.getItem() == Items.AIR) {
                            if(output.getItem() == Items.AIR){ // Fix air >1 jamming slots
                                output.setCount(1);
                            }
                            newOutputStack.setCount(recipe.getOutputAmount());
                            h.insertItem(1,newOutputStack.copy(),false); // CRASH the game if this is not empty!
                        } else { // Assuming the recipe output item is already in the output slot
                            output.setCount(recipe.getOutputAmount()); // Simply change the stack to equal the output amount
                            h.insertItem(1,output.copy(),false); // Place the new output stack on top of the old one
                        }

                        // Manipulating the RNG slot
                        if (recipe.getChance() != 0){ // If the chance is ZERO, this functionality won't be used
                            ItemStack newRngStack = recipe.getRngItem().copy();

                            // Generate Random floats
                            Random r = new Random();
                            float random = abs(0 + r.nextFloat() * (0 - 1));
                            //LOGGER.debug("Random: " + random);
                            // ONLY manipulate the slot if the random float is under or is identical to the chance float
                            if(random <= recipe.getChance()){
                                //LOGGER.debug("Chance HIT!");
                                if (rng.getItem() != recipe.getRngItem().getItem()){
                                    if (rng.getItem() == Items.AIR){
                                        rng.setCount(1);
                                    }
                                    newRngStack.setCount(recipe.getOutputRngAmount());
                                    h.insertItem(2, newRngStack.copy(),false); // CRASH the game if this is not empty!
                                } else { // Assuming the recipe output item is already in the output slot
                                    rng.setCount(recipe.getOutputRngAmount()); // Simply change the stack to equal the output amount
                                    h.insertItem(2,rng.copy(),false); // Place the new output stack on top of the old one
                                }
                            }
                        }
                        counter--;
                        energy.ifPresent(e -> ((VEEnergyStorage)e).consumeEnergy(Config.CRUSHER_POWER_USAGE.get()));
                        markDirty();
                    } else if (counter > 0){ //In progress
                        counter--;
                        energy.ifPresent(e -> ((VEEnergyStorage)e).consumeEnergy(Config.CRUSHER_POWER_USAGE.get()));
                    } else { // Check if we should start processing
                        if (output.isEmpty() && rng.isEmpty() || output.isEmpty() && rng.getItem() == recipe.getRngItem().getItem() || output.getItem() == recipe.getResult().getItem() && rng.getItem() == recipe.getRngItem().getItem() || output.getItem() == recipe.getResult().getItem() && rng.isEmpty()){
                            counter = recipe.getProcessTime();
                            length = counter;
                        } else {
                            counter = 0;
                        }
                    }
                } else { // This is if we reach the maximum in the slots
                    counter = 0;
                }
            } else { // this is if the input slot is empty
                counter = 0;
            }
            */
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
                return 2; // TODO: Implement secondary tank
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
                CrusherRecipe recipe = world.getRecipeManager().getRecipe(CrusherRecipe.recipeType, new Inventory(stack), world).orElse(null);
                CrusherRecipe recipe1 = world.getRecipeManager().getRecipe(CrusherRecipe.recipeType, new Inventory(inputItemStack.get().copy()),world).orElse(null);

                if (slot == 0 && recipe != null){
                    return recipe.ingredient.test(stack);
                } else if (slot == 1 && recipe1 != null){
                    return stack.getItem() == recipe1.result.getItem();
                } else if (slot == 2 && recipe1 != null){
                    return stack.getItem() == recipe1.getRngItem().getItem();
                }
                return false;
            }

            @Nonnull
            @Override
            public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate){ //ALSO DO THIS PER SLOT BASIS TO SAVE DEBUG HOURS!!!
                CrusherRecipe recipe = world.getRecipeManager().getRecipe(CrusherRecipe.recipeType, new Inventory(stack), world).orElse(null);
                CrusherRecipe recipe1 = world.getRecipeManager().getRecipe(CrusherRecipe.recipeType, new Inventory(inputItemStack.get().copy()),world).orElse(null);

                if(slot == 0 && recipe != null) {
                    for (ItemStack testStack : recipe.ingredient.getMatchingStacks()){
                        if(stack.getItem() == testStack.getItem()){
                            return super.insertItem(slot, stack, simulate);
                        }
                    }
                } else if (slot == 1 && recipe1 != null){
                    if (stack.getItem() == recipe1.result.getItem()){
                        return super.insertItem(slot, stack, simulate);
                    }
                } else if (slot == 2 && recipe1 != null){
                    if (stack.getItem() == recipe1.getRngItem().getItem()){
                        return super.insertItem(slot, stack, simulate);
                    }
                }
                return stack;
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
