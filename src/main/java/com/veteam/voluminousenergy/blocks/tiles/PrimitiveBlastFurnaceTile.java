package com.veteam.voluminousenergy.blocks.tiles;

import com.veteam.voluminousenergy.blocks.blocks.VEBlocks;
import com.veteam.voluminousenergy.blocks.containers.PrimitiveBlastFurnaceContainer;
import com.veteam.voluminousenergy.recipe.PrimitiveBlastFurnaceRecipe;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
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
import java.util.concurrent.atomic.AtomicReference;


public class PrimitiveBlastFurnaceTile extends TileEntity implements ITickableTileEntity, INamedContainerProvider {

    private LazyOptional<IItemHandler> handler = LazyOptional.of(this::createHandler);

    private int counter;
    private int length;
    private AtomicReference<ItemStack> inputItemStack = new AtomicReference<ItemStack>(new ItemStack(Items.AIR,0));
    private static final Logger LOGGER = LogManager.getLogger();

    public PrimitiveBlastFurnaceTile() {
        super(VEBlocks.PRIMITIVE_BLAST_FURNACE_TILE);
    }

    @Override
    public void tick() { //Tick method to run every tick
        handler.ifPresent(h -> {
            ItemStack input = h.getStackInSlot(0).copy();
            ItemStack output = h.getStackInSlot(1).copy();

            PrimitiveBlastFurnaceRecipe recipe = world.getRecipeManager().getRecipe(PrimitiveBlastFurnaceRecipe.RECIPE_TYPE, new Inventory(input), world).orElse(null);
            inputItemStack.set(input.copy()); // Atomic Reference, use this to query recipes

            if (!input.isEmpty()){
                if (output.getCount() + recipe.getOutputAmount() < 64) {
                    if (this.counter == 1){ //The processing is about to be complete
                        // Extract the inputted item
                        h.extractItem(0,recipe.ingredientCount,false);

                        // Get output stack and RNG stack from the recipe
                        ItemStack newOutputStack = recipe.getResult().copy();

                        // Manipulating the Output slot
                        if (output.getItem() != newOutputStack.getItem()) {
                            if (output.getItem() == Items.AIR){ // To prevent the slot from being jammed by air
                                output.setCount(1);
                            }
                            newOutputStack.setCount(recipe.getOutputAmount());
                            h.insertItem(1,newOutputStack.copy(),false); // CRASH the game if this is not empty!
                        } else { // Assuming the recipe output item is already in the output slot
                            output.setCount(recipe.getOutputAmount()); // Simply change the stack to equal the output amount
                            h.insertItem(1,output.copy(),false); // Place the new output stack on top of the old one
                        }

                        this.counter--;
                        markDirty();
                    } else if (this.counter > 0){ //In progress
                        this.counter--;
                    } else { // Check if we should start processing
                        if (output.isEmpty() || output.getItem() == recipe.getResult().getItem()){
                            this.counter = recipe.getProcessTime();
                            this.length = this.counter;
                        } else {
                            this.counter = 0;
                        }
                    }
                } else { // This is if we reach the maximum in the slots
                    this.counter = 0;
                }
            } else { // This is if the input slot is empty
                this.counter = 0;
            }
        });
    }

    @Override
    public void read(BlockState state, CompoundNBT tag) {
        CompoundNBT inv = tag.getCompound("inv");
        handler.ifPresent(h -> ((INBTSerializable<CompoundNBT>)h).deserializeNBT(inv));
        createHandler().deserializeNBT(inv);
        super.read(state, tag);
    }

    @Override
    public CompoundNBT write(CompoundNBT tag) {
        handler.ifPresent(h -> {
            CompoundNBT compound = ((INBTSerializable<CompoundNBT>)h).serializeNBT();
            tag.put("inv",compound);
        });
        return super.write(tag);
    }


    /*
        Sync on block update
     */

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
        this.read(this.getBlockState(), pkt.getNbtCompound());
        super.onDataPacket(net, pkt);
    }

    private ItemStackHandler createHandler() {
        return new ItemStackHandler(2) {
            @Override
            public boolean isItemValid(int slot, @Nonnull ItemStack stack) { //IS ITEM VALID PLEASE DO THIS PER SLOT TO SAVE DEBUG HOURS!!!!
                ItemStack referenceStack = stack.copy();
                referenceStack.setCount(64);
                PrimitiveBlastFurnaceRecipe recipeOutput = world.getRecipeManager().getRecipe(PrimitiveBlastFurnaceRecipe.RECIPE_TYPE, new Inventory(inputItemStack.get().copy()), world).orElse(null);
                PrimitiveBlastFurnaceRecipe recipe = world.getRecipeManager().getRecipe(PrimitiveBlastFurnaceRecipe.RECIPE_TYPE, new Inventory(referenceStack),world).orElse(null);

                if (slot == 0 && recipe != null){
                    return recipe.ingredient.test(stack);
                } else if (slot == 1 && recipeOutput != null){
                    return stack.getItem() == recipeOutput.result.getItem();
                }
                return false;
            }

            @Nonnull
            @Override
            public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate){ //ALSO DO THIS PER SLOT BASIS TO SAVE DEBUG HOURS!!!
                ItemStack referenceStack = stack.copy();
                referenceStack.setCount(64);
                PrimitiveBlastFurnaceRecipe recipeOut = world.getRecipeManager().getRecipe(PrimitiveBlastFurnaceRecipe.RECIPE_TYPE, new Inventory(inputItemStack.get().copy()), world).orElse(null);
                PrimitiveBlastFurnaceRecipe recipe = world.getRecipeManager().getRecipe(PrimitiveBlastFurnaceRecipe.RECIPE_TYPE, new Inventory(referenceStack),world).orElse(null);

                if(slot == 0 && recipe != null) {
                    for (ItemStack testStack : recipe.ingredient.getMatchingStacks()){
                        if(stack.getItem() == testStack.getItem()){
                            return super.insertItem(slot, stack, simulate);
                        }
                    }
                    //LOGGER.debug("Inserting to Slot 0.");

                } else if (slot == 1 && recipeOut != null){
                    //LOGGER.debug("Inserting to Slot 1.");
                    if (stack.getItem() == recipeOut.result.getItem()){
                        return super.insertItem(slot, stack, simulate);
                    }
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
    public ITextComponent getDisplayName() {
        return new TranslationTextComponent("voluminousenergy:primitiveblastfurnace");
    }

    @Nullable
    @Override
    public Container createMenu(int i, @Nonnull PlayerInventory playerInventory, @Nonnull PlayerEntity playerEntity)
    {
        return new PrimitiveBlastFurnaceContainer(i,world,pos,playerInventory,playerEntity);
    }

    public int counter(){
        return counter;
    }

    public int progressCounter(){
        //return 100-((counter*100)/200);
        return 100-((counter*100)/length);
    }

    public int progressCounterPX(int px){
        if (counter == 0){
            return 0;
        } else {
            //return (px*(100-((counter*100)/200)))/100;
            return (px*(100-((counter*100)/length)))/100;
        }
    }

    /*
    private PrimitiveBlastFurnaceRecipe getRecipe(final ItemStack input){
        return getRecipe(new Inventory(input));
    }

    private PrimitiveBlastFurnaceRecipe getRecipe(final IInventory inventory){
        return world == null ? null: world.getRecipeManager().getRecipe(PrimitiveBlastFurnaceRecipe.recipeType,inventory,world).orElse(null);
    }

     */
}