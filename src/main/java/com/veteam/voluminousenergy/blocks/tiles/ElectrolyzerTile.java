package com.veteam.voluminousenergy.blocks.tiles;

import com.veteam.voluminousenergy.blocks.blocks.VEBlocks;
import com.veteam.voluminousenergy.blocks.containers.ElectrolyzerContainer;
import com.veteam.voluminousenergy.recipe.ElectrolyzerRecipe;
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
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Random;

import static net.minecraft.util.math.MathHelper.abs;

public class ElectrolyzerTile extends TileEntity implements ITickableTileEntity, INamedContainerProvider {
    private LazyOptional<IItemHandler> handler = LazyOptional.of(this::createHandler);
    private LazyOptional<IEnergyStorage> energy = LazyOptional.of(this::createEnergy);

    private int counter;
    private int length;
    private static final Logger LOGGER = LogManager.getLogger();


    public ElectrolyzerTile(){
        super(VEBlocks.ELECTROLYZER_TILE);
    }

    @Override
    public void tick(){
        handler.ifPresent(h -> {
            ItemStack input = h.getStackInSlot(0).copy();
            ItemStack bucket = h.getStackInSlot(1).copy();
            ItemStack output = h.getStackInSlot(2).copy();
            ItemStack rngOne = h.getStackInSlot(3).copy();
            ItemStack rngTwo = h.getStackInSlot(4).copy();
            ItemStack rngThree = h.getStackInSlot(5).copy();

            ElectrolyzerRecipe recipe = world.getRecipeManager().getRecipe(ElectrolyzerRecipe.recipeType, new Inventory(input), world).orElse(null);

            if (usesBucket(recipe,bucket.copy())){
                if (!areSlotsFull(recipe,output.copy(),rngOne.copy(),rngTwo.copy(),rngThree.copy()) && this.getCapability(CapabilityEnergy.ENERGY).map(IEnergyStorage::getEnergyStored).orElse(0) > 0) {
                    if (counter == 1){ //The processing is about to be complete
                        // Extract the inputted item
                        h.extractItem(0,recipe.ingredientCount,false);
                        // Extract bucket if it uses a bucket
                        if (recipe.isUsesBucket()){
                            h.extractItem(1,1,false);
                        }

                        // Get output stack from the recipe
                        ItemStack newOutputStack = recipe.getResult().copy();

                        //LOGGER.debug("output: " + output + " rngOne: " + rngOne + " rngTwo: " + rngTwo + " rngThree: " + rngThree + " newOutputStack: "  + newOutputStack);

                        // Manipulating the Output slot
                        if (output.getItem() != newOutputStack.getItem() || output.getItem() == Items.AIR) {
                            if(output.getItem() == Items.AIR){ // Fix air >1 jamming slots
                                output.setCount(1);
                            }
                            newOutputStack.setCount(recipe.getOutputAmount());
                            //LOGGER.debug(" Stack to output: " + newOutputStack.copy());
                            h.insertItem(2,newOutputStack.copy(),false); // CRASH the game if this is not empty!
                            //LOGGER.debug(" in output slot: " + h.getStackInSlot(2).copy());
                        } else { // Assuming the recipe output item is already in the output slot
                            output.setCount(recipe.getOutputAmount()); // Simply change the stack to equal the output amount
                            h.insertItem(2,output.copy(),false); // Place the new output stack on top of the old one
                        }

                        // Manipulating the RNG 0 slot
                        if (recipe.getChance0() != 0){ // If the chance is ZERO, this functionality won't be used
                            ItemStack newRngStack = recipe.getRngItemSlot0().copy();

                            // Generate Random floats
                            Random r = new Random();
                            float random = abs(0 + r.nextFloat() * (0 - 1));
                            //LOGGER.debug("Random: " + random);
                            // ONLY manipulate the slot if the random float is under or is identical to the chance float
                            if(random <= recipe.getChance0()){
                                //LOGGER.debug("Chance HIT!");
                                if (rngOne.getItem() != recipe.getRngItemSlot0().getItem()){
                                    if (rngOne.getItem() == Items.AIR){
                                        rngOne.setCount(1);
                                    }
                                    newRngStack.setCount(recipe.getOutputRngAmount0());
                                    h.insertItem(3, newRngStack.copy(),false); // CRASH the game if this is not empty!
                                } else { // Assuming the recipe output item is already in the output slot
                                    rngOne.setCount(recipe.getOutputRngAmount0()); // Simply change the stack to equal the output amount
                                    h.insertItem(3,rngOne.copy(),false); // Place the new output stack on top of the old one
                                }
                            }
                        }

                        // Manipulating the RNG 1 slot
                        if (recipe.getChance1() != 0){ // If the chance is ZERO, this functionality won't be used
                            ItemStack newRngStack = recipe.getRngItemSlot1().copy();

                            // Generate Random floats
                            Random r = new Random();
                            float random = abs(0 + r.nextFloat() * (0 - 1));
                            //LOGGER.debug("Random: " + random);
                            // ONLY manipulate the slot if the random float is under or is identical to the chance float
                            if(random <= recipe.getChance1()){
                                //LOGGER.debug("Chance HIT!");
                                if (rngTwo.getItem() != recipe.getRngItemSlot1().getItem()){
                                    if (rngTwo.getItem() == Items.AIR){
                                        rngTwo.setCount(1);
                                    }
                                    newRngStack.setCount(recipe.getOutputRngAmount1());
                                    h.insertItem(4, newRngStack.copy(),false); // CRASH the game if this is not empty!
                                } else { // Assuming the recipe output item is already in the output slot
                                    rngTwo.setCount(recipe.getOutputRngAmount1()); // Simply change the stack to equal the output amount
                                    h.insertItem(4,rngTwo.copy(),false); // Place the new output stack on top of the old one
                                }
                            }
                        }

                        // Manipulating the RNG 2 slot
                        if (recipe.getChance1() != 0){ // If the chance is ZERO, this functionality won't be used
                            ItemStack newRngStack = recipe.getRngItemSlot2().copy();

                            // Generate Random floats
                            Random r = new Random();
                            float random = abs(0 + r.nextFloat() * (0 - 1));
                            //LOGGER.debug("Random: " + random);
                            // ONLY manipulate the slot if the random float is under or is identical to the chance float
                            if(random <= recipe.getChance2()){
                                //LOGGER.debug("Chance HIT!");
                                if (rngThree.getItem() != recipe.getRngItemSlot2().getItem()){
                                    if (rngThree.getItem() == Items.AIR){
                                        rngThree.setCount(1);
                                    }
                                    newRngStack.setCount(recipe.getOutputRngAmount2());
                                    h.insertItem(5, newRngStack.copy(),false); // CRASH the game if this is not empty!
                                } else { // Assuming the recipe output item is already in the output slot
                                    rngThree.setCount(recipe.getOutputRngAmount2()); // Simply change the stack to equal the output amount
                                    h.insertItem(5,rngThree.copy(),false); // Place the new output stack on top of the old one
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
                        if (areSlotsEmptyOrHaveCurrentItems(recipe,output,rngOne,rngTwo,rngThree)){
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
        });
    }

    private boolean areSlotsFull(ElectrolyzerRecipe recipe, ItemStack one, ItemStack two, ItemStack three, ItemStack four){

        if (one.getCount() + recipe.getOutputAmount() > one.getItem().getItemStackLimit(one.copy())){ // Main output slot
            return true;
        } else if (two.getCount() + recipe.getOutputRngAmount0() > two.getItem().getItemStackLimit(two.copy())){ // Rng Slot 0
            return true;
        } else if (three.getCount() + recipe.getOutputRngAmount1() > three.getItem().getItemStackLimit(three.copy())){ // Rng Slot 1
            return true;
        } else if (four.getCount() + recipe.getOutputRngAmount2() > four.getItem().getItemStackLimit(four.copy())){ // Rng Slot 2
            return true;
        } else {
            return false;
        }
    }

    private boolean usesBucket(ElectrolyzerRecipe recipe,ItemStack bucket){
        if (recipe != null){ // If the recipe is null, don't bother processing
            if (recipe.isUsesBucket()){ // If it doesn't use a bucket, we know that it must have a valid recipe, return true
                if (!bucket.isEmpty() && bucket.getItem() == Items.BUCKET){
                    return true; // Needs a bucket, has a bucket. Return true.
                } else {
                    return false; // Needs a bucket, doesn't have a bucket. Return false.
                }
            } else {
                return true; // Doesn't need a bucket, likely valid recipe. Return true.
            }
        }
        return false; // Likely empty slot, don't bother
    }

    private boolean areSlotsEmptyOrHaveCurrentItems(ElectrolyzerRecipe recipe, ItemStack one, ItemStack two, ItemStack three, ItemStack four){
        ArrayList<ItemStack> outputList = new ArrayList<>();
        outputList.add(one.copy());
        outputList.add(two.copy());
        outputList.add(three.copy());
        outputList.add(four.copy());
        boolean isEmpty = true;
        boolean matchesRecipe = true;
        for (ItemStack x : outputList){
            if (!x.isEmpty()){
                //LOGGER.debug("Not Empty Slot!");
                isEmpty = false;
                if (one.getItem() != recipe.getResult().getItem() && one.getItem() != Items.AIR){
                    return false;
                } else if (two.getItem() != recipe.getRngItemSlot0().getItem() && two.getItem() != Items.AIR){
                    return false;
                } else if (three.getItem() != recipe.getRngItemSlot1().getItem() && three.getItem() != Items.AIR){
                    return false;
                } else if (four.getItem() != recipe.getRngItemSlot2().getItem() && four.getItem() != Items.AIR){
                    return false;
                } else {
                    return true;
                }
            }
        }
        return isEmpty;
    }

    /*
        Read and Write on World save
     */

    @Override
    public void read(CompoundNBT tag){
        CompoundNBT inv = tag.getCompound("inv");
        handler.ifPresent(h -> ((INBTSerializable<CompoundNBT>)h).deserializeNBT(inv));
        createHandler().deserializeNBT(inv);
        CompoundNBT energyTag = tag.getCompound("energy");
        energy.ifPresent(h -> ((INBTSerializable<CompoundNBT>)h).deserializeNBT(energyTag));
        super.read(tag);
    }

    @Override
    public CompoundNBT write(CompoundNBT tag) {
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

    private ItemStackHandler createHandler() {
        return new ItemStackHandler(6) {
            @Override
            protected void onContentsChanged(int slot) {
                markDirty();
            }

            @Override
            public boolean isItemValid(int slot, @Nonnull ItemStack stack) { //IS ITEM VALID PLEASE DO THIS PER SLOT TO SAVE DEBUG HOURS!!!!
                ElectrolyzerRecipe recipe = world.getRecipeManager().getRecipe(ElectrolyzerRecipe.recipeType, new Inventory(stack), world).orElse(null);
                if (slot == 0 && recipe != null){
                    recipe.ingredient.test(stack);
                    return true;
                } else if (slot == 1 && stack.getItem() == Items.BUCKET){
                    return true;
                } else if (slot >= 2 && slot <= 5){
                    return true;
                }
                return false;
            }

            @Nonnull
            @Override
            public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate){ //ALSO DO THIS PER SLOT BASIS TO SAVE DEBUG HOURS!!!
                ElectrolyzerRecipe recipe = world.getRecipeManager().getRecipe(ElectrolyzerRecipe.recipeType, new Inventory(stack), world).orElse(null);
                if(slot == 0 && recipe != null) {
                    for (ItemStack testStack : recipe.ingredient.getMatchingStacks()){
                        if(stack.getItem() == testStack.getItem()){
                            return super.insertItem(slot, stack, simulate);
                        }
                    }
                } else if ( slot == 1 && stack.getItem() == Items.BUCKET) {
                    return super.insertItem(slot, stack, simulate);
                } else if (slot >= 2 && slot <= 5){
                    return super.insertItem(slot, stack, simulate);
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
        return new ElectrolyzerContainer(i,world,pos,playerInventory,playerEntity);
    }

    public int progressCounterPX(int px){
        if (counter == 0){
            return 0;
        } else {
            return (px*(100-((counter*100)/length)))/100;
        }
    }
}