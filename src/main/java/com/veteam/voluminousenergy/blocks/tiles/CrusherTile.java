package com.veteam.voluminousenergy.blocks.tiles;

import com.veteam.voluminousenergy.blocks.blocks.VEBlocks;
import com.veteam.voluminousenergy.blocks.containers.CrusherContainer;
import com.veteam.voluminousenergy.items.VEItems;
import com.veteam.voluminousenergy.recipe.CrusherRecipe;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.Item;
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
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Random;

import static net.minecraft.util.math.MathHelper.abs;

public class CrusherTile extends TileEntity implements ITickableTileEntity, INamedContainerProvider {
    private LazyOptional<IItemHandler> handler = LazyOptional.of(this::createHandler);

    private int counter;
    private int length;
    private static final Logger LOGGER = LogManager.getLogger();


    public CrusherTile(){
        super(VEBlocks.CRUSHER_TILE);
    }

    @Override
    public void tick(){
        handler.ifPresent(h -> {
            ItemStack input = h.getStackInSlot(0);
            ItemStack output = h.getStackInSlot(1);
            ItemStack rng = h.getStackInSlot(2);

            CrusherRecipe recipe = world.getRecipeManager().getRecipe(CrusherRecipe.recipeType, new Inventory(input), world).orElse(null);

            if (!input.isEmpty()){
                if (output.getCount() + recipe.getOutputAmount() < 64 && rng.getCount() + recipe.getOutputRngAmount() < 64) {
                    if (counter == 1){ //The processing is about to be complete
                        // Extract the inputted item
                        h.extractItem(0,1,false);

                        // Get output stack and RNG stack from the recipe
                        ItemStack newOutputStack = recipe.getResult();

                        //LOGGER.debug("output: " + output + " rng: " + rng + " newOutputStack: "  + newOutputStack);

                        // Manipulating the Output slot
                        if (output.getItem() != newOutputStack.getItem() || output.getItem() == Items.AIR) {
                            if(output.getItem() == Items.AIR){ // Fix air >1 jamming slots
                                output.setCount(1);
                            }
                            newOutputStack.setCount(recipe.getOutputAmount());
                            h.insertItem(1,newOutputStack,false); // CRASH the game if this is not empty!
                        } else { // Assuming the recipe output item is already in the output slot
                            int newOutputCount = output.getCount();
                            newOutputCount = newOutputCount + recipe.getOutputAmount();
                            output.setCount(newOutputCount);
                        }

                        // Manipulating the RNG slot
                        if (recipe.getChance() != 0){ // If the chance is ZERO, this functionality won't be used
                            ItemStack newRngStack = recipe.getRngItem();

                            // Generate Random floats
                            Random r = new Random();
                            float random = abs(0 + r.nextFloat() * (0 - 1));

                            // ONLY manipulate the slot if the random float is under or is identical to the chance float
                            if(random <= recipe.getChance()){

                                if (rng.getItem() != recipe.getRngItem().getItem()){
                                    if (rng.getItem() == Items.AIR){
                                        rng.setCount(1);
                                    }
                                    newRngStack.setCount(recipe.getOutputRngAmount());
                                    h.insertItem(2, newRngStack,false); // CRASH the game if this is not empty!
                                } else { // Assuming the recipe output item is already in the output slot
                                    int newRngCount = rng.getCount();
                                    newRngCount = newRngCount + recipe.getOutputRngAmount();
                                    rng.setCount(newRngCount);
                                }
                            }
                        }
                        counter--;
                        markDirty();
                    } else if (counter > 0){ //In progress
                        counter--;
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
        });
    }

    /*
        Read and Write on World save
     */

    @Override
    public void read(CompoundNBT tag){
        CompoundNBT inv = tag.getCompound("inv");
        handler.ifPresent(h -> ((INBTSerializable<CompoundNBT>)h).deserializeNBT(inv));
        createHandler().deserializeNBT(inv);
        super.read(tag);
    }

    @Override
    public CompoundNBT write(CompoundNBT tag) {
        handler.ifPresent(h -> {
            CompoundNBT compound = ((INBTSerializable<CompoundNBT>) h).serializeNBT();
            tag.put("inv", compound);
        });
        return super.write(tag);
    }

    /*
        Sync on block update

    @Override
    public SUpdateTileEntityPacket getUpdatePacket(){
        CompoundNBT tag = new CompoundNBT();
        //Write data into the tag
        handler.ifPresent(h -> {
            CompoundNBT compound = ((INBTSerializable<CompoundNBT>) h).serializeNBT();
            tag.put("inv", compound);
        });
        return new SUpdateTileEntityPacket(getPos(), -1, tag);
    }

    @Override
    public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket pkt){
        CompoundNBT tag = pkt.getNbtCompound();
        //Handle Data from tag
        CompoundNBT inv = tag.getCompound("inv");
        handler.ifPresent(h -> ((INBTSerializable<CompoundNBT>)h).deserializeNBT(inv));
        createHandler().deserializeNBT(inv);
    }

     */
    private ItemStackHandler createHandler() {
        return new ItemStackHandler(3) {
            @Override
            protected void onContentsChanged(int slot) {
                markDirty();
            }

            @Override
            public boolean isItemValid(int slot, @Nonnull ItemStack stack) { //IS ITEM VALID PLEASE DO THIS PER SLOT TO SAVE DEBUG HOURS!!!!
                CrusherRecipe recipe = world.getRecipeManager().getRecipe(CrusherRecipe.recipeType, new Inventory(stack), world).orElse(null);
                if (slot == 0){
                    try{
                        recipe.ingredient.test(stack);
                        return true;
                    } catch (Exception e){
                        return false;
                    }
                } else if (slot == 1 || slot == 2){
                    return true;
                }
                return false;
            }

            @Nonnull
            @Override
            public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) //ALSO DO THIS PER SLOT BASIS TO SAVE DEBUG HOURS!!!
            {
                CrusherRecipe recipe = world.getRecipeManager().getRecipe(CrusherRecipe.recipeType, new Inventory(stack), world).orElse(null);
                if(slot == 0) {
                    for (ItemStack testStack : recipe.ingredient.getMatchingStacks()){
                        if(stack.getItem() == testStack.getItem()){
                            return super.insertItem(slot, stack, simulate);
                        }
                    }
                } else if (slot == 1 || slot == 2){
                    return super.insertItem(slot, stack, simulate);
                }
                return stack;
            }
        };
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
        if(cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
            return handler.cast();
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
        return new CrusherContainer(i,world,pos,playerInventory,playerEntity);
    }

    public int progressCounterPX(int px){
        if (counter == 0){
            return 0;
        } else {
            return (px*(100-((counter*100)/length)))/100;
        }
    }

}
