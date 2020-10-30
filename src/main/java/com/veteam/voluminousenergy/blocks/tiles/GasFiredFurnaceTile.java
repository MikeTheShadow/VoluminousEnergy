package com.veteam.voluminousenergy.blocks.tiles;

import com.veteam.voluminousenergy.VoluminousEnergy;
import com.veteam.voluminousenergy.blocks.blocks.VEBlocks;
import com.veteam.voluminousenergy.blocks.containers.GasFiredFurnaceContainer;
import com.veteam.voluminousenergy.recipe.CombustionGenerator.CombustionGeneratorFuelRecipe;
import com.veteam.voluminousenergy.recipe.VEFluidRecipe;
import com.veteam.voluminousenergy.util.RelationalTank;
import com.veteam.voluminousenergy.util.TankType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.crafting.BlastingRecipe;
import net.minecraft.item.crafting.FurnaceRecipe;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.item.crafting.RecipeManager;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.util.Direction;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

public class GasFiredFurnaceTile extends VEFluidTileEntity {

    private LazyOptional<ItemStackHandler> handler = LazyOptional.of(() -> this.inventory);
    private LazyOptional<IFluidHandler> fluid = LazyOptional.of(this::createFluid);

    RelationalTank fuelTank = new RelationalTank(new FluidTank(TANK_CAPACITY),0,null,null, TankType.INPUT);

    private int fuelCounter;
    private int fuelLength;
    private int counter;
    private int length;

    private AtomicReference<ItemStack> inputItemStack = new AtomicReference<ItemStack>(new ItemStack(Items.AIR,0));

    public GasFiredFurnaceTile() {
        super(VEBlocks.GAS_FIRED_FURNACE_TILE);
    }

    public final ItemStackHandler inventory = createHandler();

    @Override
    public ItemStackHandler getItemStackHandler() {
        return inventory;
    }

    @Override
    public void tick() {
        updateClients();
        ItemStack input = inventory.getStackInSlot(0).copy();
        ItemStack input1 = inventory.getStackInSlot(1).copy();
        ItemStack furnaceInput = inventory.getStackInSlot(2).copy();
        ItemStack furnaceOutput = inventory.getStackInSlot(3).copy();

        fuelTank.setInput(input.copy());
        fuelTank.setOutput(input1.copy());

        if(this.inputFluid(fuelTank,0,1)) return;
        if(this.outputFluid(fuelTank,0,1)) return;

        inputItemStack.set(furnaceInput.copy()); // Atomic Reference, use this to query recipes FOR OUTPUT SLOT

        // Main Processing occurs here
        if (fuelTank.getTank() != null && !fuelTank.getTank().isEmpty()) {
            FurnaceRecipe furnaceRecipe = world.getRecipeManager().getRecipe(IRecipeType.SMELTING, new Inventory(furnaceInput.copy()), world).orElse(null);
            BlastingRecipe blastingRecipe = world.getRecipeManager().getRecipe(IRecipeType.BLASTING, new Inventory(furnaceInput.copy()), world).orElse(null);

            if ((furnaceRecipe != null || blastingRecipe != null) && countChecker(furnaceRecipe,blastingRecipe,furnaceInput.copy()) && itemChecker(furnaceRecipe,blastingRecipe,furnaceOutput.copy())){
                if (counter == 1) {
                    //LOGGER.debug("What is in the output slot? " + furnaceOutput);
                    // Extract item
                    inventory.extractItem(2,1,false);

                    // Set output based on recipe
                    ItemStack newOutputStack;
                    if (furnaceRecipe != null) {
                        newOutputStack = furnaceRecipe.getRecipeOutput().copy();
                    } else {
                        newOutputStack = blastingRecipe.getRecipeOutput().copy();
                    }
                    //LOGGER.debug("NewOutputStack: " + newOutputStack);

                    // Output Item
                    if (furnaceOutput.getItem() != newOutputStack.getItem() || furnaceOutput.getItem() == Items.AIR) {
                        //LOGGER.debug("The output is not equal to the new output Stack");
                        if(furnaceOutput.getItem() == Items.AIR){ // Fix air >1 jamming slots
                            furnaceOutput.setCount(1);
                        }
                        if (furnaceRecipe != null){
                            newOutputStack.setCount(furnaceRecipe.getRecipeOutput().getCount());
                        } else {
                            newOutputStack.setCount(blastingRecipe.getRecipeOutput().getCount());
                        }
                        //LOGGER.debug("About to insert in pt1: " + newOutputStack);
                        inventory.insertItem(3, newOutputStack.copy(),false); // CRASH the game if this is not empty!

                    } else { // Assuming the recipe output item is already in the output slot
                        // Simply change the stack to equal the output amount
                        if (furnaceRecipe != null){
                            furnaceOutput.setCount(furnaceRecipe.getRecipeOutput().getCount());
                        } else {
                            furnaceOutput.setCount(blastingRecipe.getRecipeOutput().getCount());
                        }
                        //LOGGER.debug("About to insert in pt2: " + furnaceOutput);
                        inventory.insertItem(3, furnaceOutput.copy(),false); // Place the new output stack on top of the old one
                    }

                    counter--;
                    this.markDirty();
                } else if (counter > 0) {
                    counter--;
                } else {
                    counter = 200;
                    length = counter;
                }

                // Fuel Management
                if (fuelCounter == 1){
                    fuelCounter--;
                } else if (fuelCounter > 0){
                    fuelCounter--;
                } else {
                    VEFluidRecipe recipe = world.getRecipeManager().getRecipe(CombustionGeneratorFuelRecipe.RECIPE_TYPE, new Inventory(new ItemStack (fuelTank.getTank().getFluid().getRawFluid().getFilledBucket(), 1)), world).orElse(null);
                    if (recipe != null){
                        // Drain Input
                        fuelTank.getTank().drain(250, IFluidHandler.FluidAction.EXECUTE);
                        fuelCounter = recipe.getProcessTime()/4;
                        fuelLength = fuelCounter;
                        this.markDirty();
                    }
                }

            } else counter = 0;


        } else counter = 0;
    }

    /*
        Read and Write on World save
     */

    @Override
    public void read(CompoundNBT tag) {
        CompoundNBT inv = tag.getCompound("inv");
        handler.ifPresent(h -> ((INBTSerializable<CompoundNBT>) h).deserializeNBT(inv));
        createHandler().deserializeNBT(inv);

        // Tanks
        fluid.ifPresent(f -> {
            CompoundNBT fuelTank = tag.getCompound("fuelTank");

            this.fuelTank.getTank().readFromNBT(fuelTank);

        });

        counter = tag.getInt("counter");
        length = tag.getInt("length");
        fuelCounter = tag.getInt("fuel_counter");
        fuelLength = tag.getInt("fuel_length");


        super.read(tag);
    }

    @Override
    public CompoundNBT write(CompoundNBT tag) {
        handler.ifPresent(h -> {
            CompoundNBT compound = ((INBTSerializable<CompoundNBT>) h).serializeNBT();
            tag.put("inv", compound);
        });

        // Tanks
        fluid.ifPresent(f -> {
            CompoundNBT fuelTank = new CompoundNBT();

            this.fuelTank.getTank().writeToNBT(fuelTank);

            tag.put("fuelTank", fuelTank);
        });

        tag.putInt("counter", counter);
        tag.putInt("length", length);
        tag.putInt("fuel_counter", fuelCounter);
        tag.putInt("fuel_length", fuelLength);

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
        return createFluidHandler(new CombustionGeneratorFuelRecipe(), fuelTank);
    }


    private ItemStackHandler createHandler() {
        return new ItemStackHandler(4) {
            @Override
            protected void onContentsChanged(int slot) {
                markDirty();
            }

            @Override
            public boolean isItemValid(int slot, @Nonnull ItemStack stack) { //IS ITEM VALID PLEASE DO THIS PER SLOT TO SAVE DEBUG HOURS!!!!
                if (slot == 0 || slot == 1){
                    return world.getRecipeManager().getRecipe(CombustionGeneratorFuelRecipe.RECIPE_TYPE, new Inventory(stack),world).orElse(null) != null
                            || stack.getItem() == Items.BUCKET;
                } else if (slot == 2) {
                    return world.getRecipeManager().getRecipe(IRecipeType.SMELTING, new Inventory(stack), world).orElse(null) != null
                            || world.getRecipeManager().getRecipe(IRecipeType.BLASTING, new Inventory(stack), world).orElse(null) != null;
                } else if (slot == 3) {
                    FurnaceRecipe furnaceRecipe = world.getRecipeManager().getRecipe(IRecipeType.SMELTING, new Inventory(inputItemStack.get()), world).orElse(null);
                    BlastingRecipe blastingRecipe = world.getRecipeManager().getRecipe(IRecipeType.BLASTING, new Inventory(inputItemStack.get()), world).orElse(null);

                    // If both recipes are null, then don't bother
                    if (blastingRecipe == null && furnaceRecipe == null) return false;

                    if (furnaceRecipe != null) {
                        return stack.getItem() == furnaceRecipe.getRecipeOutput().getItem();
                    }

                    return stack.getItem() == blastingRecipe.getRecipeOutput().getItem();
                }
                return false;
            }

            @Nonnull
            @Override
            public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) { //ALSO DO THIS PER SLOT BASIS TO SAVE DEBUG HOURS!!!

                if (slot == 0 || slot == 1) {
                    return super.insertItem(slot, stack, simulate);
                }

                if (slot == 2){
                    ItemStack referenceStack = stack.copy();
                    referenceStack.setCount(64);
                    FurnaceRecipe recipe = world.getRecipeManager().getRecipe(IRecipeType.SMELTING, new Inventory(referenceStack), world).orElse(null);
                    BlastingRecipe blastingRecipe = world.getRecipeManager().getRecipe(IRecipeType.BLASTING, new Inventory(referenceStack),world).orElse(null);

                    if (recipe != null || blastingRecipe != null){
                        return super.insertItem(slot, stack, simulate);
                    }

                } else if (slot == 3){
                    return super.insertItem(slot, stack, simulate);
                }
                return ItemStack.EMPTY;
            }
        };
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
        if (cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY){
            return handler.cast();
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
        return new GasFiredFurnaceContainer(i, world, pos, playerInventory, playerEntity);
    }

    public int progressCounterPX(int px) {
        if (counter == 0) {
            return 0;
        } else {
            return (px * (100 - ((counter * 100) / length))) / 100;
        }
    }

    public int progressFuelCounterPX(int px) {
        if (fuelCounter == 0){
            return 0;
        } else {
            return (px*(((fuelCounter*100)/fuelLength)))/100;
        }
    }

    @Deprecated // Use method that doesn't take in an int instead
    public FluidStack getFluidStackFromTank(int num){
        if (num == 0) {
            return fuelTank.getTank().getFluid();
        }
        return FluidStack.EMPTY;
    }

    public FluidStack getFluidFromTank(){
        return fuelTank.getTank().getFluid();
    }

    public int getTankCapacity(){
        return TANK_CAPACITY;
    }

    public int getFuelCounter(){return fuelCounter;}

    public int getCounter(){return counter;}


    public int progressFuelCounterPercent(){
        if (length != 0){
            return (int)(100-(((float)fuelCounter/(float)fuelLength)*100));
        } else {
            return 0;
        }
    }

    public int progressCounterPercent(){
        if (length != 0){
            return (int)(100-(((float)counter/(float)length)*100));
        } else {
            return 0;
        }
    }

    public boolean countChecker(FurnaceRecipe furnaceRecipe, BlastingRecipe blastingRecipe, ItemStack itemStack){
        if(furnaceRecipe != null){
            return (itemStack.getCount() + furnaceRecipe.getRecipeOutput().getCount()) <= 64;
        } else if (blastingRecipe != null){
            return (itemStack.getCount() + blastingRecipe.getRecipeOutput().getCount()) <= 64;
        }
        return false;
    }

    public boolean itemChecker(FurnaceRecipe furnaceRecipe, BlastingRecipe blastingRecipe, ItemStack itemStack){
        if(furnaceRecipe != null){
            if (itemStack.getItem() == Items.AIR || itemStack.isEmpty()) return true;
            return furnaceRecipe.getRecipeOutput().getItem() == itemStack.getItem();
        } else if (blastingRecipe != null){
            if (itemStack.getItem() == Items.AIR || itemStack.isEmpty()) return true;
            return blastingRecipe.getRecipeOutput().getItem() == itemStack.getItem();
        }
        return false;
    }
}
