package com.veteam.voluminousenergy.blocks;

import com.veteam.voluminousenergy.VoluminousEnergy;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.ItemStackHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class PrimitiveBlastFurnaceTile extends TileEntity implements ITickableTileEntity {

    private ItemStackHandler handler;

    public PrimitiveBlastFurnaceTile() {
        super(VEBlocks.PRIMITIVE_BLAST_FURNACE_TILE);
    }

    @Override
    public void tick() {
            VoluminousEnergy.LOGGER.info("WORKING!!!!");
    }

    @Override
    public void read(CompoundNBT tag) {
        CompoundNBT inv = tag.getCompound("inv");
        getHandler().deserializeNBT(inv);
        super.read(tag);
    }

    @Override
    public CompoundNBT write(CompoundNBT tag) {
        CompoundNBT compound = getHandler().serializeNBT();
        tag.put("inv",compound);
        return super.write(tag);
    }

    private ItemStackHandler getHandler() {
        if(handler == null) {
            handler = new ItemStackHandler(1);
        }
        return handler;
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
        if(cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
            return LazyOptional.of(()-> (T) getHandler());
        }
        return super.getCapability(cap, side);
    }
}
