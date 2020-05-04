package com.veteam.voluminousenergy.blocks.tiles;

import com.veteam.voluminousenergy.blocks.blocks.VEBlocks;
import com.veteam.voluminousenergy.blocks.containers.PrimitiveBlastFurnaceContainer;
import com.veteam.voluminousenergy.items.VEItems;
import com.veteam.voluminousenergy.tools.Config;
import com.veteam.voluminousenergy.tools.VEEnergyStorage;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
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
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;


public class PrimitiveBlastFurnaceTile extends TileEntity implements ITickableTileEntity, INamedContainerProvider {

    private LazyOptional<IItemHandler> handler = LazyOptional.of(this::createHandler);

    private int counter;
    private static final Logger LOGGER = LogManager.getLogger();

    public PrimitiveBlastFurnaceTile() {
        super(VEBlocks.PRIMITIVE_BLAST_FURNACE_TILE);
    }

    @Override
    public void tick() {
        /*
        if (counter == 1){
            counter--;
            handler.ifPresent(h -> {
                ItemStack output = new ItemStack(VEItems.COALCOKE);
                h.extractItem(0, 1, false);
                h.insertItem(1,output,false);
                LOGGER.debug("Item Extraction complete! counter: " + counter);
            });
            markDirty();
        } else if (counter > 0){
            counter--;
            LOGGER.debug("Counting down! counter: " + counter);
            markDirty();
        } else {
            handler.ifPresent(h -> {
                ItemStack stack = h.getStackInSlot(0);
                if (stack.getItem() == Items.COAL) { //TODO: Change it to allow JSON recipes (tags) instead of static
                    //h.extractItem(0, 1, false);
                    counter = 200;
                    LOGGER.debug("COAL INSERTED! counter: " + counter);
                    markDirty();
                }
            });
        }
         */
        handler.ifPresent(h -> {

            ItemStack input = h.getStackInSlot(0);
            ItemStack output = h.getStackInSlot(1);
            /*
            if (output.getCount() < 64 && input.getCount() > 0){
                if (counter == 1) {
                    ItemStack newOut = new ItemStack(VEItems.COALCOKE);
                    newOut.setCount(output.getCount()+1);
                    h.extractItem(0,1,false);
                    h.insertItem(1,input,false);
                    counter--;
                    LOGGER.debug("Item Extraction complete! counter: " + counter + ", input: " + input.getCount() + ", output: " + output.getCount());
                    markDirty();
                } else if (counter > 0 && counter != 1){
                    counter--;
                    LOGGER.debug("Counting down! counter: " + counter);
                    markDirty();
                } else {
                    if (input.getItem() == Items.COAL){
                        counter = 200;
                        LOGGER.debug("COAL INSERTED! counter: " + counter);
                        markDirty();
                    }
                }
            }
             */
            if(output.getCount() < 64 && input.getCount() > 0){
                h.extractItem(0,1,false);
                if(output.isEmpty() == true){
                    output = new ItemStack(VEItems.COALCOKE);
                    output.setCount(1);
                } else {
                    int outCount = output.getCount();
                    outCount++;
                    output.setCount(outCount);
                }
                LOGGER.debug(output.getCount() + " " + output.getItem());
                h.insertItem(1,output,false);
                markDirty();
            }
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
            public boolean isItemValid(int slot, @Nonnull ItemStack stack) {
                return stack.getItem() == Items.COAL;
            }

            @Nonnull
            @Override
            public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate)
            {
                if(stack.getItem() != Items.COAL) {
                    return stack;
                }
                return super.insertItem(slot, stack, simulate);
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
}
