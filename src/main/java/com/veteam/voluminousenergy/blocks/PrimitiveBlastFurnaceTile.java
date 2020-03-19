package com.veteam.voluminousenergy.blocks;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.ItemStackHandler;


public class PrimitiveBlastFurnaceTile extends TileEntity implements ITickableTileEntity {

    private ItemStackHandler handler;

    public PrimitiveBlastFurnaceTile() {
        super(VEBlocks.PRIMITIVE_BLAST_FURNACE_TILE);
    }

    @Override
    public void tick() {

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

    @Override
    public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side) {
        if(cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
            return LazyOptional.of(()-> (T) getHandler());
        }
        return super.getCapability(cap, side);
    }
}
