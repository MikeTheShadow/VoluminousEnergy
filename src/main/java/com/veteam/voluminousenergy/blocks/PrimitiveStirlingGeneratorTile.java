package com.veteam.voluminousenergy.blocks;


import com.sun.istack.internal.Nullable;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.ItemStackHandler;

public class PrimitiveStirlingGeneratorTile extends TileEntity implements ITickableTileEntity
{

    private ItemStackHandler handler;

    public PrimitiveStirlingGeneratorTile() {
        super(VEBlocks.PRIMITIVE_STIRLING_GENERATOR_TILE);
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

    @Nullable
    @Override
    public <T> LazyOptional<T> getCapability(@Nullable Capability<T> cap, @Nullable Direction side) {
        if(cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
            return LazyOptional.of(()-> (T) getHandler());
        }
        return super.getCapability(cap, side);
    }
}
