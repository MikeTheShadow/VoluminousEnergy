package com.veteam.voluminousenergy.persistence;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.fluids.FluidStack;

public class ChunkFluid {

    private final int id;
    private final ChunkPos chunkPos;
    private final ServerLevel level;
    private int fluidRemaining;
    private FluidStack fluidStack;

    public ChunkFluid(ServerLevel serverLevel, CompoundTag compoundTag) {
        this.level = serverLevel;
        this.id = compoundTag.getInt("Id");
        this.chunkPos = new ChunkPos(compoundTag.getInt("CX"),compoundTag.getInt("CZ"));
    }

    public ChunkFluid(int id, ServerLevel serverLevel, ChunkPos chunkPos) {
        this.id = id;
        this.level = serverLevel;
        this.chunkPos = chunkPos;
    }

    public CompoundTag save(CompoundTag compoundTag) {
        compoundTag.putInt("Id", this.id);
        compoundTag.putInt("CX", this.chunkPos.x);
        compoundTag.putInt("CZ", this.chunkPos.z);
        compoundTag.putInt("FS",this.fluidRemaining);
        fluidStack.writeToNBT(compoundTag);
        return compoundTag;
    }

    public ChunkPos getChunkPos() {
        return this.chunkPos;
    }

    public int getId() {
        return id;
    }

    public ServerLevel getLevel() {
        return level;
    }

    public FluidStack getFluid() {
        return fluidStack;
    }

    public int getFluidRemaining() {
        return fluidStack.getAmount();
    }
}
