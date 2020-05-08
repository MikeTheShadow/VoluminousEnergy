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
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Random;

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

            if(!input.isEmpty()){
                if(output.getCount() + recipe.getOutputAmount() < 64 && rng.getCount() + recipe.getOutputRngAmount() < 64){
                    if(counter == 1){
                        h.extractItem(0,1,false);
                        //Determine if random item is generated
                        Random r = new Random();
                        float random = 0 + r.nextFloat() * (0 - 1);
                        if(random <= recipe.getChance()){
                            int currentRngCount = rng.getCount();
                            ItemStack newRngStack = recipe.getRngItem();
                            newRngStack.setCount(currentRngCount + recipe.getOutputRngAmount());
                            h.extractItem(2,64, false);
                            h.insertItem(2,newRngStack,false);
                        }
                        int newOutputCount = output.getCount();
                        ItemStack newOutput = recipe.getResult();
                        newOutput.setCount(newOutputCount + recipe.getOutputAmount());
                        h.extractItem(1,64,false);
                        h.insertItem(1,newOutput,false);
                        --counter;
                        markDirty();
                    } else if (counter > 0){
                        --counter;
                    } else {
                        counter = recipe.getProcessTime();
                        length = counter;
                    }
                }
            } else {
                counter = 0;
            }

        });
    }

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
