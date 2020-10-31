package com.veteam.voluminousenergy.blocks.tiles;

import com.veteam.voluminousenergy.blocks.blocks.VEBlocks;
import com.veteam.voluminousenergy.blocks.containers.ElectricFurnaceContainer;
import com.veteam.voluminousenergy.tools.Config;
import com.veteam.voluminousenergy.tools.VEEnergyStorage;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.crafting.BlastingRecipe;
import net.minecraft.item.crafting.FurnaceRecipe;
import net.minecraft.item.crafting.IRecipeType;
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
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.concurrent.atomic.AtomicReference;

public class ElectricFurnaceTile extends TileEntity implements ITickableTileEntity, INamedContainerProvider {
    private LazyOptional<ItemStackHandler> handler = LazyOptional.of(() -> this.inventory);
    private LazyOptional<IEnergyStorage> energy = LazyOptional.of(this::createEnergy);

    private int counter;
    private int length;
    private AtomicReference<ItemStack> inputItemStack = new AtomicReference<ItemStack>(new ItemStack(Items.AIR,0));
    private static final Logger LOGGER = LogManager.getLogger();

    public ElectricFurnaceTile(){
        super(VEBlocks.ELECTRIC_FURNACE_TILE);
    }

    public final ItemStackHandler inventory = createHandler();

    @Override
    public void tick(){
        ItemStack furnaceInput = inventory.getStackInSlot(0).copy();
        ItemStack furnaceOutput = inventory.getStackInSlot(1).copy();

        inputItemStack.set(furnaceInput.copy()); // Atomic Reference, use this to query recipes FOR OUTPUT SLOT

        // Main Processing occurs here
        if (this.getCapability(CapabilityEnergy.ENERGY).map(IEnergyStorage::getEnergyStored).orElse(0) > 0){
            FurnaceRecipe furnaceRecipe = world.getRecipeManager().getRecipe(IRecipeType.SMELTING, new Inventory(furnaceInput.copy()), world).orElse(null);
            BlastingRecipe blastingRecipe = world.getRecipeManager().getRecipe(IRecipeType.BLASTING, new Inventory(furnaceInput.copy()), world).orElse(null);

            if ((furnaceRecipe != null || blastingRecipe != null) && countChecker(furnaceRecipe,blastingRecipe,furnaceOutput.copy()) && itemChecker(furnaceRecipe,blastingRecipe,furnaceOutput.copy())){
                if (counter == 1) {
                    // Extract item
                    inventory.extractItem(0,1,false);

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
                        inventory.insertItem(1, newOutputStack.copy(),false); // CRASH the game if this is not empty!

                    } else { // Assuming the recipe output item is already in the output slot
                        // Simply change the stack to equal the output amount
                        if (furnaceRecipe != null){
                            furnaceOutput.setCount(furnaceRecipe.getRecipeOutput().getCount());
                        } else {
                            furnaceOutput.setCount(blastingRecipe.getRecipeOutput().getCount());
                        }
                        //LOGGER.debug("About to insert in pt2: " + furnaceOutput);
                        inventory.insertItem(1, furnaceOutput.copy(),false); // Place the new output stack on top of the old one
                    }

                    energy.ifPresent(e -> ((VEEnergyStorage)e).consumeEnergy(Config.ELECTRIC_FURNACE_POWER_USAGE.get()));
                    counter--;
                    this.markDirty();
                } else if (counter > 0) {
                    energy.ifPresent(e -> ((VEEnergyStorage)e).consumeEnergy(Config.ELECTRIC_FURNACE_POWER_USAGE.get()));
                    counter--;
                } else {
                    counter = 200;
                    length = counter;
                }

            } else counter = 0;
        } else counter = 0;
    }

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

    @Override
    public void onDataPacket(final NetworkManager net, final SUpdateTileEntityPacket pkt){
        energy.ifPresent(e -> ((VEEnergyStorage)e).setEnergy(pkt.getNbtCompound().getInt("energy")));
    }

    private ItemStackHandler createHandler() {
        return new ItemStackHandler(2) {
            @Override
            protected void onContentsChanged(int slot) {
                markDirty();
            }

            @Override
            public boolean isItemValid(int slot, @Nonnull ItemStack stack) { //IS ITEM VALID PLEASE DO THIS PER SLOT TO SAVE DEBUG HOURS!!!!
                if (slot == 0) {
                    return world.getRecipeManager().getRecipe(IRecipeType.SMELTING, new Inventory(stack), world).orElse(null) != null
                            || world.getRecipeManager().getRecipe(IRecipeType.BLASTING, new Inventory(stack), world).orElse(null) != null;
                } else if (slot == 1) {
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
            public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate){ //ALSO DO THIS PER SLOT BASIS TO SAVE DEBUG HOURS!!!
                if (slot == 0){
                    ItemStack referenceStack = stack.copy();
                    referenceStack.setCount(64);
                    FurnaceRecipe recipe = world.getRecipeManager().getRecipe(IRecipeType.SMELTING, new Inventory(referenceStack), world).orElse(null);
                    BlastingRecipe blastingRecipe = world.getRecipeManager().getRecipe(IRecipeType.BLASTING, new Inventory(referenceStack),world).orElse(null);

                    if (recipe != null || blastingRecipe != null){
                        return super.insertItem(slot, stack, simulate);
                    }

                } else if (slot == 1){
                    return super.insertItem(slot, stack, simulate);
                }
                return stack;
            }
        };
    }

    private IEnergyStorage createEnergy(){
        return new VEEnergyStorage(Config.ELECTRIC_FURNACE_MAX_POWER.get(), Config.ELECTRIC_FURNACE_TRANSFER.get()); // Max Power Storage, Max transfer
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
        return new ElectricFurnaceContainer(i,world,pos,playerInventory,playerEntity);
    }

    public int progressCounterPX(int px){
        if (counter == 0){
            return 0;
        } else {
            return (px*(100-((counter*100)/length)))/100;
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

    public int getCounter(){
        return counter;
    }

}