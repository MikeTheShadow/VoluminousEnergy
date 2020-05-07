package com.veteam.voluminousenergy.blocks.tiles;

import com.veteam.voluminousenergy.blocks.blocks.VEBlocks;
import com.veteam.voluminousenergy.blocks.containers.PrimitiveBlastFurnaceContainer;
import com.veteam.voluminousenergy.items.VEItems;
import com.veteam.voluminousenergy.recipe.PrimitiveBlastFurnaceRecipe;
import com.veteam.voluminousenergy.tools.Config;
import com.veteam.voluminousenergy.tools.VEEnergyStorage;
import net.minecraft.client.renderer.tileentity.ItemStackTileEntityRenderer;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
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

import static java.lang.Math.abs;


public class PrimitiveBlastFurnaceTile extends TileEntity implements ITickableTileEntity, INamedContainerProvider {

    private LazyOptional<IItemHandler> handler = LazyOptional.of(this::createHandler);

    private int counter;
    private static final Logger LOGGER = LogManager.getLogger();

    public PrimitiveBlastFurnaceTile() {
        super(VEBlocks.PRIMITIVE_BLAST_FURNACE_TILE);
    }

    @Override
    public void tick() { //Tick method to run every tick
        if (world == null || world.isRemote()){
            return;
        }

        handler.ifPresent(h -> {
            ItemStack input = h.getStackInSlot(0);
            ItemStack output = h.getStackInSlot(1);

            PrimitiveBlastFurnaceRecipe recipe = world.getRecipeManager().getRecipe(PrimitiveBlastFurnaceRecipe.recipeType, new Inventory(input), world).orElse(null);


            LOGGER.debug(recipe.ingredient + " " + recipe.getResult());
            if(output.getCount() < 64 && input.getCount() > 0){
                if(counter == 1){ // To remove inserted item and create output item
                    h.extractItem(0,1,false); // Extracts the input item from the insert slot
                    int newOutputCount = output.getCount();// Get amount of items currently in the output slot
                    ItemStack nOut = recipe.getResult();// Creates a new ItemStack based on the expected result that will replace the one in the output slot
                    nOut.setCount(++newOutputCount);//Set the amount of items that should now be in the output slot
                    h.extractItem(1,64,false);// Extract the current ItemStack in the output slot
                    h.insertItem(1, nOut,false); // Insert the new ItemStack into the output slot
                    --counter;
                    markDirty();
                } else if (counter > 0){
                    --counter;
                    LOGGER.debug(counter + " %: " + progressCounter() + " px: " + progressCounterPX(23));
                } else {
                    if (!input.isEmpty()){
                        counter = 200;
                        //counter = recipe.getProcessTime();//Gets the processing time from the recipe -> NULL_POINTER_EXCEPTION
                    }
                }
            } else if (input.getCount() == 0) {
                counter = 0;
            }

            /*
            if(output.getCount() < 64 && input.getCount() > 0){
                if(counter == 1){ // To remove inserted item and create output item
                    h.extractItem(0,1,false); // Extracts the input item from the insert slot
                    int newOutputCount = output.getCount(); // Get amount of items currently in the output slot
                    ItemStack nOut = new ItemStack(VEItems.COALCOKE); // Create a new ItemStack that will replace the one in the output slot
                    nOut.setCount(++newOutputCount); //Set the amount of items that should now be in the output slot
                    h.extractItem(1,64,false);// Extract the current ItemStack in the output slot
                    h.insertItem(1, nOut,false); // Insert the new ItemStack into the output slot
                    --counter;
                    markDirty();
                } else if (counter > 0 && counter != 1){
                    --counter;
                    LOGGER.debug(counter + " %: " + progressCounter() + " px: " + progressCounterPX(23));
                } else {
                    if (!input.isEmpty()){
                        counter = 200;
                    }
                }
            } else if (input.getCount() == 0) {
                counter = 0;
            }
             */
        });
    }

    @Override
    public void read(CompoundNBT tag) {
        CompoundNBT inv = tag.getCompound("inv");
        handler.ifPresent(h -> ((INBTSerializable<CompoundNBT>)h).deserializeNBT(inv));
        createHandler().deserializeNBT(inv);
        super.read(tag);
    }

    @Override
    public CompoundNBT write(CompoundNBT tag) {
        handler.ifPresent(h -> {
            CompoundNBT compound = ((INBTSerializable<CompoundNBT>)h).serializeNBT();
            tag.put("inv",compound);
        });
        return super.write(tag);
    }

    private ItemStackHandler createHandler() {
        return new ItemStackHandler(2) {
            @Override
            public boolean isItemValid(int slot, @Nonnull ItemStack stack) { //IS ITEM VALID PLEASE DO THIS PER SLOT TO SAVE DEBUG HOURS!!!!
                if (slot == 0){
                    return stack.getItem() == Items.COAL;
                } else if (slot == 1){
                    return stack.getItem() == VEItems.COALCOKE;
                }
                return false;
            }

            @Nonnull
            @Override
            public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) //ALSO DO THIS PER SLOT BASIS TO SAVE DEBUG HOURS!!!
            {
                if(stack.getItem() == Items.COAL && slot == 0) {
                    LOGGER.debug("Inserting Coal to Slot 0.");
                    return super.insertItem(slot, stack, simulate);
                } else if (stack.getItem() == VEItems.COALCOKE && slot == 1){
                    LOGGER.debug("Inserting Coal Coke to Slot 1.");
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
        //return MathHelper.intFloorDiv(counter*100,200);
        return 100-((counter*100)/200);
    }

    public int progressCounterPX(int px){
        if (counter == 0){
            return 0;
        } else {
            return (px*(100-((counter*100)/200)))/100;
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